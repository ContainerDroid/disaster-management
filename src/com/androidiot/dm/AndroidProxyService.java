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
		if (locationCount == 0) {
			System.err.println("No safe locations, cannot compute priorities");
			return;
		}

		ArrayList<AndroidClient> people = acs.getClients();
		int choice = 0;

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
			if (choices[choice] < choices[i]) {
				choice = i;
			}
		}
		ClientMessage cm = new ClientMessage();
		cm.msgtype   = "safe-location-response";
		cm.latitude  = Double.toString(sls.getLocation(choice).latitude);
		cm.longitude = Double.toString(sls.getLocation(choice).longitude);
		cm.score     = Double.toString(choices[choice]);
		String json  = cm.toString();
		System.out.println(json);
		stream.println(json);
	}

	private void sendConsistencyRatio(PrintStream stream, double value) {
		ClientMessage response = new ClientMessage();
		response.msgtype = "consistency-ratio";
		response.consistencyRatio = Double.toString(value);
		String json = response.toString();
		System.out.println(json);
		stream.println(json);
	}

	private void parseClientMessage(ClientMessage cm, OutputStream output) {
		AndroidClientService acs = AndroidClientService.getInstance();
		SafeLocationService sls = SafeLocationService.getInstance();
		PrintStream stream = new PrintStream(output, true);

		if (cm.msgtype.compareTo("client-location") == 0) {
			acs.addLocation(cm.name, cm.timestamp, new Location(cm));
		} else if (cm.msgtype.compareTo("safe-location") == 0) {
			sls.add(new Location(cm));
		} else if (cm.msgtype.compareTo("safe-location-preferences") == 0) {
			acs.setPreferences(cm.name, new ClientPreferences(cm));
			sendConsistencyRatio(stream, acs.getClient(cm.name).preferences.getConsistencyRatio());
		} else if (cm.msgtype.compareTo("safe-location-request") == 0) {
			sendSafeLocations(stream, cm.name);
		} else if (cm.msgtype.compareTo("delete-clients") == 0) {
			acs.deleteClients();
		} else if (cm.msgtype.compareTo("delete-safe-locations") == 0) {
			sls.deleteLocations();
		} else {
			System.err.println("Unknown msgtype received for " + cm);
		}
		System.out.println("Request processed: " + cm.msgtype + " at time " + cm.timestamp);
	}

	public void run() {
		String msg = "";
		try {
			OutputStream output = socket.getOutputStream();
			BufferedReader input = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));

			ClientMessage cm;

			while ((msg = input.readLine()) != null) {
				if (isStopped) {
					input.close();
					output.close();
					break;
				}
				System.out.println("\"" + msg + "\"");
				try {
					cm = g.fromJson(msg.trim(), ClientMessage.class);
					parseClientMessage(cm, output);
				} catch (com.google.gson.JsonSyntaxException e) {
					System.err.println("Malformed json at " + msg);
				}
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
