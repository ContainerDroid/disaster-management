package com.androidiot.dm;

public class CriteriaScoreCalculator {

	public static double[] getCrowdednessPriorityVector(AndroidClient requester) {
		SafeLocationService sls = SafeLocationService.getInstance();

		int locationCount = sls.getLocationCount();
		double[] priorityVector = new double[locationCount];

		for (int i = 0; i < locationCount; i++) {
			priorityVector[i] = (double) (1.0 / locationCount);
		}
		return priorityVector;
	}

	public static double[] getSafetyPriorityVector(AndroidClient requester) {
		SafeLocationService sls = SafeLocationService.getInstance();

		int locationCount = sls.getLocationCount();
		double[] priorityVector = new double[locationCount];

		for (int i = 0; i < locationCount; i++) {
			priorityVector[i] = (double) (1.0 / locationCount);
		}
		return priorityVector;
	}

	public static double[] getProximityPriorityVector(AndroidClient requester) {
		SafeLocationService sls = SafeLocationService.getInstance();

		int locationCount = sls.getLocationCount();
		double[] priorityVector = new double[locationCount];

		for (int i = 0; i < locationCount; i++) {
			priorityVector[i] = (double) (1.0 / locationCount);
		}
		return priorityVector;
	}

	public static double[] getFriendsPriorityVector(AndroidClient requester) {
		SafeLocationService sls = SafeLocationService.getInstance();

		int locationCount = sls.getLocationCount();
		double[] priorityVector = new double[locationCount];

		for (int i = 0; i < locationCount; i++) {
			priorityVector[i] = (double) (1.0 / locationCount);
		}
		return priorityVector;
	}
}

