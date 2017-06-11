package com.skbuf.datagenerator;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;

public class SecondActivity extends AppCompatActivity implements LocationListener{

    final static String TAG = "DataGenerator";

    TextView textViewLogcat;
    Switch switchActive;

    private SensorService mSensorService;
    private boolean mServiceBound = false;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SensorService.MyBinder myBinder = (SensorService.MyBinder) iBinder;
            mSensorService = myBinder.getService();
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBound = false;
        }
    };

    private SocketService mSocketService;
    private boolean socketServiceBound = false;
    private ServiceConnection mSocketServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SocketService.LocalBinder myBinder = (SocketService.LocalBinder) iBinder;
            mSocketService = myBinder.getService();
            SamplingData.setSocketService(mSocketService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            socketServiceBound = false;
        }
    };

    private void doBindService() {
        bindService(new Intent(SecondActivity.this, SocketService.class), mSocketServiceConnection, Context.BIND_AUTO_CREATE);
        socketServiceBound = true;
    }

    private void doUnbindService() {
        if (socketServiceBound) {
            unbindService(mSocketServiceConnection);
            socketServiceBound = false;
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String updateString = intent.getStringExtra("update");
            textViewLogcat.append(updateString + "\n");
        }
    };

    private static long LOCATION_REFRESH_TIME = 5;
    private static float LOCATION_REFRESH_DISTANCE = 0;
    LocationManager mLocationManager;
    private static final String[] PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private void startLocationRequests() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ActivityCompat.requestPermissions(this, PERMS, 1);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {

            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, this);

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, this);
            Log.d(TAG, "requestLocationUpdates");
        }
    }

    private void stopLocationRequests() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ActivityCompat.requestPermissions(this, PERMS, 1);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        textViewLogcat = (TextView) findViewById(R.id.logcat);
        textViewLogcat.setMovementMethod(new ScrollingMovementMethod());

        switchActive = (Switch) findViewById(R.id.activeSwitch);
        switchActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                Log.i(TAG, "active switch changed state to " + Boolean.toString(checked));
                if (checked) {

                    Log.d(TAG, "started SensorService");
                    Intent intent = new Intent(getApplicationContext(), SensorService.class);
                    startService(intent);
                    bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
                    mServiceBound = true;

                    try {
                        Runtime.getRuntime().exec(new String[]{"logcat", "-c"});
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startLocationRequests();

                } else {

                    Log.d(TAG, "stopped SensorService");
                    if (mServiceBound) {
                        unbindService(mServiceConnection);
                        mServiceBound = false;
                    }
                    Intent intent = new Intent(getApplicationContext(), SensorService.class);
                    stopService(intent);

                    stopLocationRequests();
                    String filePath = null;
                    try {
                        filePath = SamplingData.createLogFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Saved in " + filePath, Toast.LENGTH_LONG).show();
                }

            }
        });

        startService(new Intent(SecondActivity.this, SocketService.class));
        doBindService();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(SecondActivity.this).registerReceiver(broadcastReceiver, new IntentFilter("NOW"));
    }

    @Override
    public void onLocationChanged(Location location) {
       SamplingData.setLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
