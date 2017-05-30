package com.skbuf.datagenerator;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class GenerateSampleFragment extends Fragment {

    Button generateStart, generateSafe;
    MapFragment mapFragment;
    Boolean generateStarted = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        return inflater.inflate(R.layout.fragment_generate_sample, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(getText(R.string.title_generate_sample));
    }


    private void createLogFile() {
        try {
            String logFile = SamplingData.createLogFile();
            Toast.makeText(this.getActivity().getApplicationContext(),
                    "Sample stored at " + logFile, Toast.LENGTH_SHORT).show();
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

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_generate_sample);


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
*/