package com.androidiot.dm;


public class ClientMessage {
	public String msgtype;
	public String name;
	public String timestamp;
	public String latitude;
	public String longitude;

	public String[] criteria;
	public String[] pairwiseComparisons;

	public ClientMessage() {
	}
	@Override
	public String toString() {
		return "Type: " + msgtype + ", Name: " + name +
			", Timestamp: " + timestamp + ", Latitude: " +
			latitude + ", Longitude: " + longitude;
	}
}

