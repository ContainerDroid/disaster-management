package com.skbuf.datagenerator;

import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class SamplingData {
    private static final String TAG = "DataGenerator-Sample";
    private static Location location = null;
    private static float linear_acceleration[] = new float[3];
    private static TextView log;
    private static File logDir;

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
            String update = String.format("%10s %10s %10s %10s %10s %10s\n",
                    clientName,
                    df.format(linear_acceleration[0]),
                    df.format(linear_acceleration[1]),
                    df.format(linear_acceleration[2]),
                    df.format(location.getLatitude()),
                    df.format(location.getLongitude()));
            Log.d(TAG, update);
            log.append(update + "\n");

            if (socketService != null)
                socketService.sendMessage(update);
            else
                Log.d(TAG, "NULLLLLLLLLLLLLLLLLLL");
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

    static void setTextView(TextView log) {
        SamplingData.log = log;
    }

    static String createLogFile() throws IOException {
        Boolean createdFolder;

        logDir = new File(Environment.getExternalStorageDirectory() + "/DMDataGenerator-Samples/");
        createdFolder = true;
        if (!logDir.exists()) {
            createdFolder = logDir.mkdirs();
        }

        if (createdFolder) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = df.format(c.getTime());
            String filePath = Environment.getExternalStorageDirectory() + "/DMDataGenerator-Samples/sample-" + formattedDate + "-" + clientName;
            Runtime.getRuntime().exec(new String[]{"logcat", "-f", filePath, "DataGenerator-Sample:V", "*:S"});

            return filePath;
        }

        return new String("Could not save log file!");
    }


}
