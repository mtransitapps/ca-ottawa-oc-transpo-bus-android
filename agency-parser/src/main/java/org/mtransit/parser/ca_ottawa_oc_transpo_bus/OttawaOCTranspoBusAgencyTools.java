package org.mtransit.parser.ca_ottawa_oc_transpo_bus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CharUtils;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.provider.OttawaOCTranspoProviderCommons;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;

import java.util.List;
import java.util.Locale;

// https://www.octranspo.com/en/plan-your-trip/travel-tools/developers/
// https://www.octranspo.com/fr/planifiez/outils-dinformation/developpeurs/
public class OttawaOCTranspoBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new OttawaOCTranspoBusAgencyTools().start(args);
	}

	@Nullable
	@Override
	public List<Locale> getSupportedLanguages() {
		return LANG_EN_FR;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "OC Transpo";
	}

	@Override
	public boolean excludeRoute(@NotNull GRoute gRoute) {
		final String rsn = gRoute.getRouteShortName();
		switch (rsn) {
		case "1": // Confederation Line
		case "2": // Bayview - Greenboro
		case "4": // South Keys - Airport
			return EXCLUDE; // wrongfully classified as bus
		}
		return super.excludeRoute(gRoute);
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public @Nullable String getServiceIdCleanupRegex() {
		return "^[A-Z]+\\d{2}\\-"; // starts with "MMMYY" (JAN26 or SEPT25)
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Nullable
	@Override
	public Long convertRouteIdPreviousChars(@NotNull String previousChars) {
		switch (previousChars) {
		case "SNO":
			return 506L;
		}
		return null;
	}

	@Nullable
	public String getRouteIdCleanupRegex() {
		return "\\-\\d+(\\-\\d+)?$";
	}

	@Override
	public boolean verifyRouteIdsUniqueness() {
		return false; // merge routes
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	private static final String AGENCY_COLOR = "C80D1A";

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.removeVia(tripHeadsign);
		return OttawaOCTranspoProviderCommons.cleanTripHeadsign(tripHeadsign);
	}

	@Override
	public @Nullable String getTripIdCleanupRegex() {
		return "^[A-Z]+\\d{2}\\-"; // starts with "MMMYY" (JAN26 or SEPT25)
	}

	@NotNull
	private String[] getIgnoredWords() {
		return new String[]{
				"TOH"
		};
	}

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.toLowerCaseUpperCaseWords(Locale.ENGLISH, gStopName, getIgnoredWords());
		gStopName = CleanUtils.fixMcXCase(gStopName);
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		return CleanUtils.cleanLabel(getFirstLanguageNN(), gStopName);
	}

	@Override
	public int getStopId(@NotNull GStop gStop) {
		String stopCode = getStopCode(gStop);
		if (!stopCode.isEmpty() && CharUtils.isDigitsOnly(stopCode)) {
			return Integer.parseInt(stopCode); // using stop code as stop ID
		}
		return super.getStopId(gStop); // good enough
	}
}
