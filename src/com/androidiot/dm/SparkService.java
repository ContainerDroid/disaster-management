package com.androidiot.dm;

import java.util.ArrayList;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class SparkService {
	private static JavaSparkContext sc;

	private static class LazyHolder {
		static final SparkService INSTANCE =
			new SparkService();
	}

	private SparkService() {
		SparkConf conf = new SparkConf()
			.setAppName(DisasterManagement.class.getName())
			.setMaster("local");
		sc = new JavaSparkContext(conf);
	}

	public static SparkService getInstance() {
		return LazyHolder.INSTANCE;
	}

	public double[] getCrowdednessScores() {
		AndroidClientService acs = AndroidClientService.getInstance();
		SafeLocationService sls = SafeLocationService.getInstance();
		ArrayList<AndroidClient> people = acs.getClients();
		int locationCount = sls.getLocationCount();
		double[] scores = new double[locationCount];
		/* XXX: eps */
		final double epsTime = 100;
		final double epsLocation = 100;

		JavaRDD<AndroidClient> safeClients = sc.parallelize(people).filter(
				client -> {return client.isSafe();});

		for (int i = 0; i < locationCount; i++) {
			Location l = sls.getLocation(i);
			JavaRDD<AndroidClient> safeClientsNearby = safeClients.filter(
					client -> {return client.getCurrentLocation().isInVicinityOf(l, epsLocation);});
			Double timesSpentNearby =
				safeClientsNearby.map(client -> client.getTimeSpentNearby(l, epsTime))
				.fold(0.0, ((accum, time) -> (accum + time)));
			Integer safeClientsCount = safeClientsNearby.map(client -> 1)
				.fold(0, ((accum, n) -> (accum + n)));
			System.out.println("timesSpentNearby " + timesSpentNearby);
			System.out.println("safeClientsCount " + safeClientsCount);
			double avgTimeSpentNearby;
			if (safeClientsCount == 0) {
				avgTimeSpentNearby = 0;
			} else {
				avgTimeSpentNearby = timesSpentNearby / safeClientsCount;
			}
			scores[i] = avgTimeSpentNearby;
		}
		return scores;
	}
}
