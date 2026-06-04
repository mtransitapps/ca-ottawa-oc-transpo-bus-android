package org.mtransit.parser.ca_ottawa_oc_transpo_bus;

import org.jetbrains.annotations.NotNull;
import org.mtransit.commons.CharUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.gtfs.data.GStop;

public class OttawaOCTranspoBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new OttawaOCTranspoBusAgencyTools().start(args);
	}

	@Override
	public int getStopId(@NotNull GStop gStop) {
		String stopCode = getStopCode(gStop);
		if (!stopCode.isEmpty() && CharUtils.isDigitsOnly(stopCode)) {
			return Integer.parseInt(stopCode); // using stop code as stop ID
		}
		if ("DT032".equals(gStop.getStopId())) {
			return 10449; // LAVAL / LAURIER
		}
		if ("DT033".equals(gStop.getStopId())) {
			return 10712; // DU PORTAGE / DE L' HÔTEL-DE-VILLE
		}
		if ("DT035".equals(gStop.getStopId())) {
			return 10766; // LAURIER / EDDY
		}
		return super.getStopId(gStop); // good enough
	}
}
