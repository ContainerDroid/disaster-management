package com.skbuf.datagenerator;

import android.location.Location;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class GlobalData {
    private static final String TAG = "DataGenerator-Sample";
    private static Location location = null;
    private static float linear_acceleration[] = new float[3];
    private static File logDir;

    /* data from login page */
    private static Integer serverPort;
    private static String serverAddress;
    private static String clientName;

    /* data from friends page */
    private static List<String> friends = new ArrayList<String>();

    /* data from settings page */
    private static List<String> criteria;
    private static List<Float> pref;

    public static List<String> getCriteria() {
        return criteria;
    }

    public static void setCriteria(List<String> criteria) {
        GlobalData.criteria = criteria;
    }

    public static List<Float> getPref() {
        return pref;
    }

    public static void setPref(List<Float> pref) {
        GlobalData.pref = pref;
    }

    public static List<String> getFriends() {
        return friends;
    }

    public static void setFriends(List<String> friends) {
        GlobalData.friends = friends;
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
            String update = String.format("%s %s %s %s\n",
                    System.currentTimeMillis(),
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
        GlobalData.location = location;
        logSample();
    }

    static Location getLocation() {
        return location;
    }

    static void setLinear_acceleration(float linear_acceleration[]) {
        GlobalData.linear_acceleration[0] = linear_acceleration[0];
        GlobalData.linear_acceleration[1] = linear_acceleration[1];
        GlobalData.linear_acceleration[2] = linear_acceleration[2];
        logSample();
    }

    static String getSamplesPath() {
        return "/sdcard/DMDataGenerator-Samples/";
    }

    static void alterDataFormat(String file) {

        try {
            FileInputStream fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            StringBuffer outputBuffer = new StringBuffer();

            while ((strLine = br.readLine()) != null)   {
                if (strLine.contains("beginning"))
                    continue;
                String columns[] = strLine.split(" ");
                outputBuffer.append(columns[6] + " ");
                outputBuffer.append(columns[7] + " ");
                outputBuffer.append(columns[8] + " ");
                outputBuffer.append(columns[9] + "\n");
            }
            br.close();

            FileOutputStream outStream = new FileOutputStream(file);
            BufferedWriter brWriter = new BufferedWriter(new OutputStreamWriter(outStream));
            brWriter.write(outputBuffer.toString());
            brWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
