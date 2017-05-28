package com.skbuf.datagenerator;

import android.location.Location;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;


public class SamplingData {
    private static final String TAG = "DataGenerator-Sample";
    private static Location location = null;
    private static float linear_acceleration[] = new float[3];
    private static File logDir;

    /* data from login page */
    private static Integer serverPort;
    private static String serverAddress;
    private static String clientName;

    private static SocketService socketService;

    public static SocketService getSocketService() {
        return socketService;
    }

    public static void setSocketService(SocketService socketService) {
        SamplingData.socketService = socketService;
    }

    static public Integer getServerPort() {
        return serverPort;
    }

    static public void setServerPort(Integer serverPortP) {
        serverPort = serverPortP;
    }

    static public String getClientName() {
        return clientName;
    }

    static public void setClientName(String clientNameP) {
        clientName = clientNameP;
    }

    static public String getServerAddress() {
        return serverAddress;
    }

    static public void setServerAddress(String serverAddressP) {
        serverAddress = serverAddressP;
    }

    static void logSample() {
        if (location != null) {
            DecimalFormat df = new DecimalFormat("#.#####");
            df.setRoundingMode(RoundingMode.CEILING);
            String update = String.format("%10s %10s %10s\n",
                    clientName,
                    //f.format(linear_acceleration[0]),
                    //df.format(linear_acceleration[1]),
                    //df.format(linear_acceleration[2]),
                    df.format(location.getLatitude()),
                    df.format(location.getLongitude()));
            Log.d(TAG, update);

            /*
            if (socketService != null)
                socketService.sendMessage(update);
            else
                Log.d(TAG, "NULLLLLLLLLLLLLLLLLLL"); */
        }
    }


    static void setLocation(Location location) {
        SamplingData.location = location;
        logSample();
    }

    static void setLinear_acceleration(float linear_acceleration[]) {
        SamplingData.linear_acceleration[0] = linear_acceleration[0];
        SamplingData.linear_acceleration[1] = linear_acceleration[1];
        SamplingData.linear_acceleration[2] = linear_acceleration[2];
        logSample();
    }

    static String createLogFile() throws IOException {
        Boolean createdFolder;

        logDir = new File(Environment.getExternalStorageDirectory() + "/DMDataGenerator-Samples/");
        createdFolder = true;
        if (!logDir.exists()) {
            createdFolder = logDir.mkdirs();
        }

        if (createdFolder) {
            String filePath = Environment.getExternalStorageDirectory() + "/DMDataGenerator-Samples/sample-" +
                    System.currentTimeMillis() + "-" + clientName;
            Runtime.getRuntime().exec(new String[]{"logcat", "-f", filePath, "DataGenerator-Sample:V", "*:S"});
            Runtime.getRuntime().exec(new String[]{"sed", "-i", "/beginning/d", filePath});
            Runtime.getRuntime().exec(new String[]{"logcat", "-c"});
            return filePath;
        }

        return new String("Could not save log file!");
    }


}
