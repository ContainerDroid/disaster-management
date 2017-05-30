package com.skbuf.datagenerator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText editServerAdrress, editServerPort, editClientName;
    Button butonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editClientName = (EditText) findViewById(R.id.input_name);
        editServerAdrress = (EditText) findViewById(R.id.input_server);
        editServerPort = (EditText) findViewById(R.id.input_port);

        butonLogin = (Button) findViewById(R.id.btn_login);
        butonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SamplingData.setServerAddress(editServerAdrress.getText().toString());
                SamplingData.setClientName(editClientName.getText().toString());
                SamplingData.setServerPort(Integer.parseInt(editServerPort.getText().toString()));

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        requestPermissionIfNeeded(Manifest.permission.ACCESS_FINE_LOCATION);
        requestPermissionIfNeeded(Manifest.permission.ACCESS_COARSE_LOCATION);
        requestPermissionIfNeeded(Manifest.permission.INTERNET);
        requestPermissionIfNeeded(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }


    private void requestPermissionIfNeeded(String perm) {
        if (checkSelfPermission(perm)
                != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{perm},
                        0);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (grantResults.length <= 0 &&
                grantResults[0] != PackageManager.PERMISSION_GRANTED) {

            finish();
            System.exit(0);
        }
    }

}
