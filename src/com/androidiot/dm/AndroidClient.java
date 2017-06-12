package com.androidiot.dm;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

class ClientPreferences implements java.io.Serializable {
	/* AHP weights */
	String[]   criteriaLabels;
	double[][] criteriaComparisonMatrix;
	double[]   criteriaWeights;
	int        criteriaCount;
	AHPHelper  ahp;

	/* Empty constructor */
	public ClientPreferences() {
		criteriaLabels = new String[0];
		criteriaWeights = new double[0];
		criteriaComparisonMatrix = new double[0][0];
		criteriaCount = 0;
	}

	/* Copy constructor */
	public ClientPreferences(ClientPreferences other) {
		/* XXX: Not sure about cloning arrays actually
		 * performing more than a shallow copy, or even whether
		 * a deeper copy is in fact needed */
		this.criteriaLabels = other.criteriaLabels.clone();
		this.criteriaWeights = other.criteriaWeights.clone();
		this.criteriaComparisonMatrix = other.criteriaComparisonMatrix.clone();
		//this.ahp = other.ahp.clone();
	}

	/* Constructor from ClientMessage */
	public ClientPreferences(ClientMessage cm) {
		int criteriaCount = Array.getLength(cm.criteria);
		if (criteriaCount != 4) {
			System.err.println("Program hardcoded for 4 criteria");
			return;
		}
		this.criteriaCount = criteriaCount;
		for (String s: cm.criteria) {
			if (s.compareTo("safety") != 0 &&
			    s.compareTo("proximity") != 0 &&
			    s.compareTo("closeToFriends") != 0 &&
			    s.compareTo("notCrowded") != 0) {
				System.err.println("Unknown criterion " + s);
				return;
			}
		}
		criteriaLabels = new String[criteriaCount];
		criteriaComparisonMatrix = new double[criteriaCount][criteriaCount];
		int index = 0;
		try {
			for (int i = 0; i < criteriaCount; i++) {
				for (int j = i + 1; j < criteriaCount; j++) {
					double comparison = Double.parseDouble(cm.pairwiseComparisons[index]);
					String itemA = cm.criteria[i];
					String itemB = cm.criteria[j];
					System.out.println(itemA + " score over " + itemB
						+ ": " + comparison);
					criteriaComparisonMatrix[i][j] = comparison;
					criteriaComparisonMatrix[j][i] = 1.0f / comparison;
					index++;
				}
				/* Criteria on the diagonal are always equal to themselves */
				criteriaComparisonMatrix[i][i] = 1;
				criteriaLabels[i] = cm.criteria[i];
			}
			ahp = new AHPHelper(criteriaComparisonMatrix, criteriaCount);
			criteriaWeights = ahp.getPriorityVector();
		} catch (NumberFormatException ex) {
			System.err.println("Invalid double for one of the following:");
			System.err.println("safety, proximity, notCrowded, closeToFriends");
		}
	}

	public int getCriteriaIndexFromLabel(String s) {
		if (criteriaLabels == null) {
			System.err.println("Criteria labels not initialized");
			return -1;
		}
		for (int i = 0; i < criteriaCount; i++) {
			if (criteriaLabels[i].compareTo(s) == 0) {
				return i;
			}
		}
		System.err.println("Could not find criterium " + s);
		return -1;
	}

	public double getCriteriaWeightByName(String s) {
		return criteriaWeights[getCriteriaIndexFromLabel(s)];
	}

	public double getConsistencyRatio() {
		return ahp.getConsistencyRatio();
	}

	@Override
	public String toString() {
		if (criteriaCount == 0) {
			System.out.println("ClientPreferences Not initialized");
		}
		String str = "";
		for (int i = 0; i < criteriaCount; i++) {
			str += String.format("%15s", criteriaLabels[i]);
		}
		str += "\n";
		for (int i = 0; i < criteriaCount; i++) {
			for (int j = 0; j < criteriaCount; j++) {
				str += String.format("%15s", criteriaComparisonMatrix[i][j]);
			}
			str += "\n";
		}
		return str;
	}
}

public class AndroidClient implements java.io.Serializable {
	ClientPreferences preferences;
	ArrayList<Location> trajectory;
	ArrayList<Double>   timestamps;
	String[] friends;
	boolean safe;

	public AndroidClient() {
		this.trajectory = new ArrayList<Location>();
		this.timestamps = new ArrayList<Double>();
		safe = false;
	}

	public void setFriends(String[] friends) {
		this.friends = friends;
	}

	public void setPreferences(ClientPreferences pref) {
		this.preferences = pref;
	}

	public Location getLocationAtTime(double time) {
		int pos = Arrays.binarySearch(timestamps.toArray(), time);
		return trajectory.get(pos);
	}

	public Location getCurrentLocation() {
		if (trajectory.size() == 0) {
			System.err.println("Cannot get current location if trajectory is empty");
			return null;
		}
		return trajectory.get(trajectory.size() - 1);
	}

	public String toString() {
		String str;
		if (preferences != null) {
			str = preferences.toString() + "\n";
		} else {
			str = "Preferences are not initialized\n";
		}
		if (friends != null) {
			str += "Friends:\n";
			for (String f: friends) {
				str += f + " ";
			}
			str += "\n";
		} else {
			str = "Friends are not initialized\n";
		}
		str += "Trajectory:\n";
		for (int i = 0; i < trajectory.size(); i++) {
			str += trajectory.get(i) +
				" at time " + timestamps.get(i);
		}
		return str;
	}

	public void addLocation(double timestamp, Location l) {
		trajectory.add(l);
		timestamps.add(timestamp);
	}

	public boolean isSafe() {
		return safe;
	}

	public void makeSafe() {
		safe = true;
	}

	public double getTimeSpentNearby(Location l, double eps) {
		int pos = trajectory.size() - 1;

		while (trajectory.get(pos).isInVicinityOf(l, eps) == false) {
			pos--;
		}
		int endPos = pos;
		while (trajectory.get(pos).isInVicinityOf(l, eps) == true) {
			pos--;
		}
		int startPos = pos;
		System.out.println("getTimeSpentNearby: " + startPos + ", " + endPos);
		return timestamps.get(endPos) - timestamps.get(startPos);
	}
}

