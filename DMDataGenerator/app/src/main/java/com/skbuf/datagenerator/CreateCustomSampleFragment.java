package com.skbuf.datagenerator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CreateCustomSampleFragment extends Fragment {

    private final String TAG = "CreateCustomSampleFragment";
    private Button buttonSave, buttonBrowse;

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

            }
        });


        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                String samplesDir = SamplingData.getSamplesPath();
                Uri uri = Uri.parse(samplesDir);
                intent.setDataAndType(uri, "*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                Intent cintent = Intent.createChooser(intent, "Choose files");
                startActivity(cintent);

            }
        });
    }

}
