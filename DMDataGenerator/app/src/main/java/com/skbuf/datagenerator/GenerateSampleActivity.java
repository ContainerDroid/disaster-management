package com.skbuf.datagenerator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;

public class GenerateSampleActivity extends AppCompatActivity {

    Button generateStart, generateSafe;
    MapFragment mapFragment;
    Boolean generateStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_sample);


        generateStart = (Button) findViewById(R.id.button_generate_start);
        generateSafe = (Button) findViewById(R.id.button_generate_safe);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);

        generateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateState(generateStarted);
            }
        });

        generateSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateState(generateStarted);
            }
        });
    }


    private void createLogFile() {
        try {
            String logFile = SamplingData.createLogFile();
            Toast.makeText(this, "Sample stored at " + logFile, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateState(boolean generateStarted) {
        if (!generateStarted) {
            this.generateStart.setText("STOP");
            this.generateSafe.setEnabled(true);
            this.generateStarted = true;
            this.mapFragment.startLocationUpdates();
        } else {
            this.generateStart.setText("START");
            this.generateSafe.setEnabled(false);
            this.generateStarted = false;
            this.mapFragment.stopLocationUpdates();
            createLogFile();
        }
    }
}
