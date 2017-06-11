package com.androidiot.dm;

import java.lang.reflect.Array;
import java.util.ArrayList;

class ClientPreferences {
	/* AHP weights */
	float safety;
	float proximity;
	float notCrowded;
	float closeToFriends;
	float pairwiseComparisons[];
	float[][] pairwiseMatrix;

	public ClientPreferences() {
		safety = proximity = notCrowded = closeToFriends = 0;
	}

	public ClientPreferences(ClientMessage cm) {
		int criteriaCount = Array.getLength(cm.criteria);
		if (criteriaCount != 4) {
			System.err.println("Program hardcoded for 4 criteria");
			return;
		}
		for (String s: cm.criteria) {
			if (s.compareTo("safety") != 0 &&
			    s.compareTo("proximity") != 0 &&
			    s.compareTo("closeToFriends") != 0 &&
			    s.compareTo("notCrowded") != 0) {
				System.err.println("Unknown criterion " + s);
				return;
			}
		}
		pairwiseMatrix = new float[criteriaCount][criteriaCount];
		int index = 0;
		try {
			for (int i = 0; i < criteriaCount; i++) {
				for (int j = i + 1; j < criteriaCount; j++) {
					float comparison = Float.parseFloat(cm.pairwiseComparisons[index]);
					String itemA = cm.criteria[i];
					String itemB = cm.criteria[j];
					System.out.println(itemA + " score over " + itemB
						+ ": " + comparison);
					pairwiseMatrix[i][j] = pairwiseMatrix[j][i] = comparison;
					index++;
				}
			}
		} catch (NumberFormatException ex) {
			System.err.println("Invalid float for one of the following:");
			System.err.println("safety, proximity, notCrowded, closeToFriends");
		}
	}

	public ClientPreferences(ClientPreferences other) {
		this.safety         = other.safety;
		this.proximity      = other.proximity;
		this.notCrowded     = other.notCrowded;
		this.closeToFriends = other.closeToFriends;
	}

	@Override
	public String toString() {
		return "Preferences: Safety " + safety + ", Proximity " +
			proximity + ", Not Crowded " + notCrowded +
			", Close To Friends " + closeToFriends;
	}
}

public class AndroidClient {
	ClientPreferences preferences;
	ArrayList<Location> trajectory;

	public AndroidClient() {
		this.preferences = new ClientPreferences();
		this.trajectory = new ArrayList<Location>();
	}

	public void setPreferences(ClientPreferences pref) {
		this.preferences = new ClientPreferences(pref);
	}

	public String toString() {
		String str = preferences.toString() + "\n";
		str += "Trajectory:\n";
		for (Location l: trajectory) {
			str += l.toString();
		}
		return str;
	}
}

