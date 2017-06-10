package com.androidiot.dm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class ConnectionWorker implements Runnable {
	private Socket cs = null;
	private Gson g;
	volatile Boolean isStopped = false;

	public ConnectionWorker(Socket cs) {
		this.cs = cs;
		this.g = new Gson();
	}

	public void stop() {
		isStopped = true;
	}

	public void run() {
		ClientLocationService cls = ClientLocationService.getInstance();
		SafeLocationService sls = SafeLocationService.getInstance();

		try {
			OutputStream output = cs.getOutputStream();
			BufferedReader input = new BufferedReader(
				new InputStreamReader(cs.getInputStream()));

			ClientMessage cm;
			String msg;
			
			while ((msg = input.readLine()) != null) {
				if (isStopped) {
					input.close();
					break;
				}
				cm = g.fromJson(msg, ClientMessage.class);
				if (cm.msgtype.compareTo("client-location") == 0) {
					cls.add(new ClientLocation(cm));
					cls.list();
				} else if (cm.msgtype.compareTo("safe-location") == 0) {
					sls.add(new SafeLocation(cm));
					sls.list();
				} else if (cm.msgtype.compareTo("safe-location-preferences") == 0) {

				} else if (cm.msgtype.compareTo("safe-location-request") == 0) {

				}
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
