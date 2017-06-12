package com.skbuf.datagenerator;

import android.content.ClipData;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsFragment extends Fragment {

    private final String TAG = "SettingsFragment";

    List<String> criteria = new ArrayList<>(Arrays.asList(
            "safety",
            "proximity",
            "closeToFriends",
            "notCrowded"));
    List<Pair<String, String>> pairwiseComparisons = createPairwiseComparisons(criteria);

    private ListView lv;
    private SettingsAdapter adapter;
    Handler updateConversationHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getText(R.string.title_settings));

        adapter = new SettingsAdapter(getContext(), pairwiseComparisons);
        lv = (ListView) getView().findViewById(R.id.pref_list);
        lv.setAdapter(adapter);

        GlobalData.setCriteria(criteria);
        updateConversationHandler = new Handler();
    }

    private List<Pair<String, String>> createPairwiseComparisons(List<String> criteria) {
        List<Pair<String, String>> comparisons = new ArrayList<Pair<String, String>>();
        Integer criteriaSize = criteria.size();

        for (Integer i = 0; i < criteriaSize; i++) {
            for (Integer j = i + 1; j < criteriaSize; j++) {
                String c1 = criteria.get(i);
                String c2 = criteria.get(j);
                comparisons.add(new Pair<String, String>(c1, c2));
            }
        }

        return  comparisons;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_check:
                CheckConThread checkThread = new CheckConThread();
                checkThread.start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    class CheckConThread extends Thread {

        Socket socket;
        BufferedOutputStream brOutputSocket;
        BufferedReader brInputSocket;
        String line;

        @Override
        public void run() {
            try {
                socket = new Socket(GlobalData.getServerAddress(), GlobalData.getServerPort());
                Log.d(TAG, "Created connection with server");
                brOutputSocket = new BufferedOutputStream(this.socket.getOutputStream());
                brInputSocket = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

                Message message = new Message(Message.MSG_TYPE_PREF,
                        GlobalData.getClientName(),
                        GlobalData.getCriteria(),
                        GlobalData.getPref());
                brOutputSocket.write(message.toString().getBytes());
                brOutputSocket.write('\n');
                brOutputSocket.flush();
                Log.d(TAG, "sent to server: " + message.toString());

                Log.d(TAG, "Waiting for reply from server");
                line = brInputSocket.readLine();
                Gson gson = new Gson();
                Message incommingMessage = gson.fromJson(line, Message.class);
                Log.d(TAG, "from server : " + incommingMessage.toString());

                updateConversationHandler.post(new updateUIThread(incommingMessage));

                brOutputSocket.close();
                brInputSocket.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class updateUIThread extends Thread {
        private Message incommingMessage;

        public updateUIThread(Message incommingMessage) {
            this.incommingMessage = incommingMessage;
        }

        @Override
        public void run() {
            if (incommingMessage.msgtype.equals(Message.MSG_TYPE_PREF_RESPONSE)) {
                if (incommingMessage.getValue() > 10.0f) {
                    Toast.makeText(getActivity(), "Preferences are not consistent!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Preferences are consistent!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
