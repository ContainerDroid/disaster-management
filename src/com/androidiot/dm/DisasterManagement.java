package com.androidiot.dm;

import com.google.gson.Gson;

public class DisasterManagement {

	public static void main(String[] args) {
		AndroidProxyService proxy =
			new AndroidProxyService(6970);
		new Thread(proxy).start();

		SafeLocationService locations = new SafeLocationService();
	}
}
