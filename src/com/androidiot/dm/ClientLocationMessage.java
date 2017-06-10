package com.androidiot.dm;


public class ClientLocationMessage {
	public String msgtype;
	public String name;
	public String timestamp;
	public String latitude;
	public String longitude;
	public ClientLocationMessage() {
	}
	@Override
	public String toString() {
		return "Type: " + msgtype + ", Name: " + name +
			", Timestamp: " + timestamp + ", Latitude: " +
			latitude + ", Longitude: " + longitude;
	}
}

