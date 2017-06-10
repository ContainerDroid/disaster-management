package com.skbuf.datagenerator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateCustomSampleFragment extends Fragment {

    private final String TAG = "CreateCustomSample";
    private List<String> filesSelected = new ArrayList<String>();

    private final Integer FILE_SELECT_CODE = 1;
    private final Integer FILE_CREATE_CODE = 2;

    private Button buttonSave, buttonBrowse;
    private ListView lv;
    private FileListAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_custom_sample, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonSave = (Button) getView().findViewById(R.id.button_create_save);
        buttonBrowse = (Button) getView().findViewById(R.id.button_create_browse);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/*");
                Intent cintent = Intent.createChooser(intent, "Choose files");
                startActivityForResult(cintent, FILE_CREATE_CODE);
            }
        });


        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                String samplesDir = SamplingData.getSamplesPath();
                Uri uri = Uri.parse(samplesDir);
                intent.setDataAndType(uri, "text/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                Intent cintent = Intent.createChooser(intent, "Choose files");
                startActivityForResult(cintent, FILE_SELECT_CODE);

            }
        });

        adapter = new FileListAdapter(getContext(), filesSelected);
        lv = (ListView) getView().findViewById(R.id.list);
        lv.setAdapter(adapter);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String sampleFile = data.getData().toString().replaceFirst("file:///storage/emulated/0/DMDataGenerator-Samples/", "");
                filesSelected.add(sampleFile);
                adapter.notifyDataSetChanged();
            }
        } else if (requestCode == FILE_CREATE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                String customSampleFile = uri.toString();

                Log.d(TAG, "Created custom file: " + customSampleFile);
                Toast.makeText(getContext(), customSampleFile, Toast.LENGTH_LONG).show();

                try {
                    ParcelFileDescriptor pfd = getActivity().getContentResolver().
                            openFileDescriptor(uri, "w");
                    FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());

                    fileOutputStream.write("TODO TODO TODO".getBytes());

                    fileOutputStream.close();
                    pfd.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }
}
