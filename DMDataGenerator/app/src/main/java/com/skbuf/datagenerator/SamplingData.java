package com.skbuf.datagenerator;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by pis on 14.05.2017.
 */

public class SamplingData {
    private static final String TAG = "DataGenerator-Sample";
    private static Location location = new Location(LocationManager.NETWORK_PROVIDER);
    private static float linear_acceleration[] = new float[3];
    private static TextView log;

    static void logSample() {
        String update = Float.toString(linear_acceleration[0]) + ":"  +
                Float.toString(linear_acceleration[1]) + ":" +
                Float.toString(linear_acceleration[2]) + ":" +
                location.getLatitude() + ":" +
                location.getLongitude();

        Log.d(TAG, update);
        log.append(update + "\n");
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
}
