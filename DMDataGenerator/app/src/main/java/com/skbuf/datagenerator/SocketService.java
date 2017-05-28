package com.skbuf.datagenerator;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketService extends Service {
    private final String TAG = "SocketService";
    private final IBinder myBinder = new LocalBinder();
    private Socket serverSocket;
    private InetAddress serverAddr;
    private Integer serverPort = SamplingData.getServerPort();
    private PrintWriter outStreamServer;

    public SocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class LocalBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }

    public void sendMessage(String message){
        Runnable serverMessage = new ServerMessage(message);
        new Thread(serverMessage).start();
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        Runnable serverConnection = new ServerConnection();
        new Thread(serverConnection).start();
        return START_STICKY;
    }

    class ServerMessage implements Runnable {
        private String message;

        public ServerMessage(String message) {
            this.message = message;
        }
        @Override
        public void run() {
            if (outStreamServer != null && !outStreamServer.checkError()) {
                Log.d(TAG, "sent new message to server");
                outStreamServer.println(message);
                outStreamServer.flush();
            }
        }
    }

    class ServerConnection implements Runnable {

        @Override
        public void run() {
            try {
                serverAddr = InetAddress.getByName(SamplingData.getServerAddress());
                Log.d(TAG, "Connecting to " + SamplingData.getServerAddress());

                serverSocket = new Socket(serverAddr, serverPort);
                Log.d(TAG, "Created socket");

                outStreamServer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream())), true);
                Log.d(TAG, "Created output stream to server");

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        serverSocket = null;
    }
}
