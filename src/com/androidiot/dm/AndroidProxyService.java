package com.androidiot.dm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
//import org.apache.spark.api.java;

class ConnectionWorker implements Runnable {
	private Socket socket = null;
	private Gson g;
	volatile Boolean isStopped = false;

	public ConnectionWorker(Socket socket) {
		this.socket = socket;
		this.g = new Gson();
	}

	public void stop() {
		isStopped = true;
	}

	private void sendSafeLocations(PrintStream stream, String clientName) {
		AndroidClientService acs = AndroidClientService.getInstance();
		SafeLocationService sls = SafeLocationService.getInstance();
		AndroidClient requester = acs.getClient(clientName);
		int locationCount = sls.getLocationCount();

		if (requester == null) {
			System.err.println("Cannot find client " + clientName);
			return;
		}
		stream.println("Your preference consistency ratio is:");
		stream.println(requester.preferences.getConsistencyRatio());

		ArrayList<AndroidClient> people = acs.getClients();
		stream.println("Size: " + people.size());
		acs.list(stream);
		sls.list(stream);


		double[] proximityPriorityVector =
			CriteriaScoreCalculator.getProximityPriorityVector(requester);
		double[] safetyPriorityVector =
			CriteriaScoreCalculator.getSafetyPriorityVector(requester);
		double[] friendsPriorityVector =
			CriteriaScoreCalculator.getFriendsPriorityVector(requester);
		double[] crowdedPriorityVector =
			CriteriaScoreCalculator.getCrowdednessPriorityVector(requester);

		double[] choices = new double[locationCount];
		for (int i = 0; i < locationCount; i++) {
			choices[i]  = crowdedPriorityVector[i] *
				requester.preferences.getCriteriaWeightByName("notCrowded");
			choices[i] += proximityPriorityVector[i] *
				requester.preferences.getCriteriaWeightByName("proximity");
			choices[i] += safetyPriorityVector[i] *
				requester.preferences.getCriteriaWeightByName("safety");
			choices[i] += friendsPriorityVector[i] *
				requester.preferences.getCriteriaWeightByName("closeToFriends");
			stream.println("Location " + i + ": " + choices[i]);
		}
	}

	private void parseClientMessage(ClientMessage cm, OutputStream output) {
		AndroidClientService acs = AndroidClientService.getInstance();
		SafeLocationService sls = SafeLocationService.getInstance();
		PrintStream stream = new PrintStream(output, true);

		if (cm.msgtype.compareTo("client-location") == 0) {
			acs.addLocation(cm.name, new Location(cm));
			acs.list(stream);
		} else if (cm.msgtype.compareTo("safe-location") == 0) {
			sls.add(new Location(cm));
			sls.list(System.out);
		} else if (cm.msgtype.compareTo("safe-location-preferences") == 0) {
			acs.setPreferences(cm.name, new ClientPreferences(cm));
		} else if (cm.msgtype.compareTo("safe-location-request") == 0) {
			sendSafeLocations(stream, cm.name);
		} else {
			System.err.println("Unknown msgtype received for " + cm);
		}
	}

	public void run() {
		try {
			OutputStream output = socket.getOutputStream();
			BufferedReader input = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));

			ClientMessage cm;
			String msg;

			while ((msg = input.readLine()) != null) {
				if (isStopped) {
					input.close();
					break;
				}
				cm = g.fromJson(msg, ClientMessage.class);
				parseClientMessage(cm, output);
				long time = System.currentTimeMillis();
				System.out.println("Request processed: " + time);
			}
		} catch (IOException e) {
			//report exception somewhere.
			e.printStackTrace();
		}
	}
}

public class AndroidProxyService implements Runnable {
	int port;
	volatile Boolean isStopped = false;
	ArrayList<ConnectionWorker> workers;

	public AndroidProxyService(int port) {
		this.port = port;
		workers = new ArrayList<ConnectionWorker>();
	}

	public void stop() {
		isStopped = true;
		for (ConnectionWorker t: workers) {
			t.stop();
		}
	}

	public void run() {
		ServerSocket ss;
		try {
			ss = new ServerSocket(port);
		} catch (java.io.IOException ex) {
			System.err.println("Failed to listen on port " + port);
			return;
		}

		while (isStopped == false) {
			try {
				Socket cs = ss.accept();
				ConnectionWorker w = new ConnectionWorker(cs);
				workers.add(w);
				new Thread(w).start();
			} catch (java.io.IOException ex) {
				System.err.println(
					"Failed to connect to client");
			}
		}
	}
}
