package com.androidiot.dm;

public class CriteriaScoreCalculator {

	public static double[] getCrowdednessPriorityVector(AndroidClient requester) {
		SafeLocationService sls = SafeLocationService.getInstance();
		AndroidClientService acs = AndroidClientService.getInstance();
		SparkService ss = SparkService.getInstance();

		int locationCount = sls.getLocationCount();
		double[][] comparisonMatrix = new double[locationCount][locationCount];
		double[] priorityVector = new double[locationCount];
		double[] scores = ss.getCrowdednessScores();

		for (int i = 0; i < locationCount; i++) {
			for (int j = 0; j < locationCount; j++) {
				comparisonMatrix[i][j] = scores[i] / scores[j];
				System.out.println("ComparisonMatrix[" + i + "][" + j + "] = " + comparisonMatrix[i][j]);
			}
		}
		AHPHelper ahp = new AHPHelper(comparisonMatrix, locationCount);
		return ahp.getPriorityVector();
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
		double[] distances = new double[locationCount];
		double[][] comparisonMatrix = new double[locationCount][locationCount];
		Location clientPosition = requester.getCurrentLocation();

		for (int i = 0; i < locationCount; i++) {
			distances[i] = clientPosition.getDistanceKmTo(sls.getLocation(i));
		}
		for (int i = 0; i < locationCount; i++) {
			for (int j = 0; j < locationCount; j++) {
				comparisonMatrix[i][j] = distances[i] / distances[j];
				System.out.println("ComparisonMatrix[" + i + "][" + j + "] = " + comparisonMatrix[i][j]);
			}
		}
		AHPHelper ahp = new AHPHelper(comparisonMatrix, locationCount);
		return ahp.getPriorityVector();
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

