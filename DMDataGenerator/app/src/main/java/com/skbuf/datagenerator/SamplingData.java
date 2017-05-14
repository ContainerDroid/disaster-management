package com.skbuf.datagenerator;

import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class SamplingData {
    private static final String TAG = "DataGenerator-Sample";
    private static Location location = null;
    private static float linear_acceleration[] = new float[3];
    private static TextView log;
    private static File logDir;

    static void logSample() {
        if (location != null) {
            String update = Float.toString(linear_acceleration[0]) + " " +
                    Float.toString(linear_acceleration[1]) + " " +
                    Float.toString(linear_acceleration[2]) + " " +
                    location.getLatitude() + " " +
                    location.getLongitude();
            Log.d(TAG, update);
            log.append(update + "\n");
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
            String filePath = Environment.getExternalStorageDirectory() + "/DMDataGenerator-Samples/sample-" + formattedDate;
            Runtime.getRuntime().exec(new String[]{"logcat", "-f", filePath, "DataGenerator-Sample:V", "*:S"});

            return filePath;
        }

        return new String("Could not save log file!");
    }
}
