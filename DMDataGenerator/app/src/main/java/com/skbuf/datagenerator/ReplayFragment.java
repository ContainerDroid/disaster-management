package com.skbuf.datagenerator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class ReplayFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        LocationListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener  {
    private static String TAG = "ReplayFragment";

    MapView mapView;
    private final int[] MAP_TYPES = {
            GoogleMap.MAP_TYPE_NORMAL };
    private int curMapTypeIndex = 0;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private static final long INTERVAL = 300; // 300ms
    private static final long FASTEST_INTERVAL = 100; // 100ms
    private static final float SMALLEST_DISPLACEMENT = 0.25F;
    private final Integer FILE_SELECT_CODE = 13;

    FloatingActionButton uploadFile, nextButton;

    // drawing on the map
    List<Integer> colors = Arrays.asList(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED);
    Integer currentColor = 0;

    HashMap<String, List<LatLng>> usersLocations = new HashMap<String, List<LatLng>>();
    HashMap<String, Integer> usersColor = new HashMap<String, Integer>();

    BufferedReader br;
    Polyline line;
    Boolean simulationStarted = false;

    Handler updateConversationHandler;

    Socket socketGlobal;
    BufferedOutputStream brSocket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_replay, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getText(R.string.title_replay));

        mapView = (MapView) view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        if (mapView != null) {
            mGoogleApiClient = new GoogleApiClient.Builder( getActivity() )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
            mGoogleApiClient.connect();
            initListeners();
            createLocationRequest();
            Log.d(TAG, "onViewCreated");
        }

        uploadFile = (FloatingActionButton) view.findViewById(R.id.action_upload);
        nextButton = (FloatingActionButton) view.findViewById(R.id.action_next);

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                String samplesDir = GlobalData.getSamplesPath();
                Uri uri = Uri.parse(samplesDir);
                intent.setDataAndType(uri, "text/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                Intent cintent = Intent.createChooser(intent, "Choose files");
                startActivityForResult(cintent, FILE_SELECT_CODE);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReplayStep();
            }
        });

        updateConversationHandler = new Handler();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri sampleSelectedURI = data.getData();
                Log.d(TAG, "Selected file: " + sampleSelectedURI.getPath());

                // connect to server
                SocketThread socketThread = new SocketThread();
                socketThread.start();

                // should start to display
                replay(sampleSelectedURI);
            }
        }
    }

    private void replay(Uri uri) {
        ParcelFileDescriptor inputPfd = null;
        Message message;
        String line;
        Gson gson = new Gson();

        try {
            inputPfd = getActivity().getContentResolver().openFileDescriptor(uri, "r");
            FileInputStream fileInputStream = new FileInputStream(inputPfd.getFileDescriptor());
            br = new BufferedReader(new InputStreamReader(fileInputStream));

            while ((line = br.readLine()) != null) {
                // send line to server
                OutgoingThread outThread = new OutgoingThread(brSocket, line);
                outThread.start();

                Log.d(TAG, line);
                message = gson.fromJson(line, Message.class);

                if (message.msgtype.equals(Message.MSG_TYPE_LOCATION) == false){
                    String user = message.getUsername();
                    if (usersLocations.containsKey(user) == false) {
                        usersLocations.put(user, new ArrayList<LatLng>());
                        usersColor.put(user, colors.get(currentColor++));
                    }
                } else if (message.msgtype.equals(Message.MSG_TYPE_LOCATION)){
                    insertNewLocation(message);
                    break;
                } else {
                    break;
                }
            }

            simulationStarted = true;
            ReplayStep();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insertNewLocation(Message message) {
        String user = message.getUsername();
        List<LatLng> locations = usersLocations.get(user);
        LatLng newLocation = new LatLng(message.getLatitude(), message.getLongitude());
        locations.add(newLocation);
        usersLocations.put(user, locations);
    }

    private void ReplayStep() {
        StepThread thread = new StepThread();
        thread.run();
    }

    public class StepThread extends Thread {

        @Override
        public void run() {
            String line;
            Message message;
            Gson gson = new Gson();
            String username = "";
            Boolean replayStopped = false;

            try {
                if (simulationStarted == false) {
                    Toast.makeText(getActivity(), "No simulation active!", Toast.LENGTH_SHORT).show();
                    return;
                }

                while ((line = br.readLine()) != null) {
                    Log.d(TAG, line);
                    message = gson.fromJson(line, Message.class);

                    // send line to server
                    OutgoingThread outThread = new OutgoingThread(brSocket, line);
                    outThread.start();

                    if (message.msgtype.equals(Message.MSG_TYPE_LOCATION)) {
                        insertNewLocation(message);
                    } else if (message.msgtype.equals(Message.MSG_TYPE_REQUEST)){
                        username = message.getUsername();
                        replayStopped = true;
                        break;
                    }
                }

                if (replayStopped == false) {
                    br.close();
                    br = null;
                    simulationStarted = false;
                    Toast.makeText(getActivity(), "Simulation finished!", Toast.LENGTH_SHORT).show();
                    brSocket.close();
                    socketGlobal.close();
                } else {
                    redrawLines();
                    Toast.makeText(getActivity(), username + " made a request for location", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                try {
                    br.close();
                    br = null;
                    simulationStarted = false;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void redrawLines() {

        mapView.getMap().clear();

        for (String user : usersLocations.keySet()) {
            List<LatLng> locations = usersLocations.get(user);
            Integer size = locations.size();
            if (size == 0)
                continue;
            LatLng lastLocation = locations.get(size - 1);
            Integer color = usersColor.get(user);

            redrawLine(locations, color, lastLocation, user);
        }
    }

    private void redrawLine(List<LatLng> locations, Integer color, LatLng lastLocation, String user){

        PolylineOptions options = new PolylineOptions().width(10).color(color).geodesic(true);
        for (int i = 0; i < locations.size(); i++) {
            LatLng point = locations.get(i);
            options.add(point);
        }
        float colorMarker = 0.0f;
        if (color == Color.BLUE)
            colorMarker = BitmapDescriptorFactory.HUE_BLUE;
        else  if (color == Color.YELLOW)
            colorMarker = BitmapDescriptorFactory.HUE_YELLOW;
        else if (color == Color.GREEN)
            colorMarker = BitmapDescriptorFactory.HUE_GREEN;
        else if (color == Color.RED)
            colorMarker = BitmapDescriptorFactory.HUE_RED;

        addMarker(lastLocation, user, colorMarker);
        line = mapView.getMap().addPolyline(options);
    }

    private void addMarker(LatLng lastLocation, String label, float color) {
        MarkerOptions options = new MarkerOptions();
        options.position(lastLocation);
        options.title(label);
        options.icon(BitmapDescriptorFactory.defaultMarker(color));
        Marker mapMarker = mapView.getMap().addMarker(options);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private void initCamera( Location location ) {
        CameraPosition position = CameraPosition.builder()
                .target( new LatLng( location.getLatitude(),
                        location.getLongitude() ) )
                .zoom( 16f )
                .bearing( 0.0f )
                .tilt( 0.0f )
                .build();

        mapView.getMap().animateCamera( CameraUpdateFactory
                .newCameraPosition( position ), null );

        mapView.getMap().setMapType( MAP_TYPES[curMapTypeIndex] );
        mapView.getMap().setTrafficEnabled( true );
        mapView.getMap().setMyLocationEnabled( true );
        mapView.getMap().getUiSettings().setZoomControlsEnabled( false );
        mapView.getMap().setMyLocationEnabled(false);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private void initListeners() {
        mapView.getMap().setOnMarkerClickListener(this);
        mapView.getMap().setOnMapLongClickListener(this);
        mapView.getMap().setOnInfoWindowClickListener( this );
        mapView.getMap().setOnMapClickListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        mCurrentLocation = LocationServices
                .FusedLocationApi
                .getLastLocation( mGoogleApiClient );

        if (mCurrentLocation != null )
            initCamera( mCurrentLocation );
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    class SocketThread extends Thread {
        Socket socket = null;

        @Override
        public void run() {

            try {
                this.socket = new Socket(GlobalData.getServerAddress(), GlobalData.getServerPort());
                socketGlobal = this.socket;
                brSocket = new BufferedOutputStream(this.socket.getOutputStream());

                if (socket != null) {
                    IncommingThread incommingTh = new IncommingThread(socket);
                    incommingTh.start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class IncommingThread extends Thread {

        private Socket socket;
        private BufferedReader input;

        public IncommingThread(Socket socket) {

            try {
                this.socket = socket;
                this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {

                try {
                    String read = input.readLine();
                    updateConversationHandler.post(new updateUIThread(read));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class updateUIThread extends Thread {
        private String msg;

        public updateUIThread(String str) {
            this.msg = str;
        }

        @Override
        public void run() {
            Log.d(TAG, "De la server: " + msg);
        }
    }

    class OutgoingThread extends Thread {
        BufferedOutputStream brOut;
        String line;


        public OutgoingThread(BufferedOutputStream out, String line) {
            this.brOut = out;
            this.line = line;
        }


        @Override
        public void run() {

            try {
                brOut.write(line.getBytes());
                brOut.write('\n');
                brOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
