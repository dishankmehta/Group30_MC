package com.example.group30.assignment3;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.group30.assignment3.data.SensorDataHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Intent startSenseService;
    private RadioGroup rg_Activity;
    private RadioButton rb_walk, rb_jog, rb_run;
    private Button btn_collectData, btn_ml_algo;
    private SensorDataHelper sensorDataHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Delegate.theMainActivity = this;


        rg_Activity = findViewById(R.id.rg_Activity);
        rb_walk = findViewById(R.id.rb_walk);
        rb_jog = findViewById(R.id.rb_jog);
        rb_run = findViewById(R.id.rb_run);

        rg_Activity.check(rb_walk.getId());

        btn_collectData = findViewById(R.id.btn_collectData);
        btn_ml_algo = findViewById(R.id.btn_ml_activity);

        startSenseService = new Intent(MainActivity.this, SensorHandlerClass.class);

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 1);
        }

        try {
            String storage_folder = "/Android/Data/CSE535_ASSIGNMENT3";

            File f = new File(Environment.getExternalStorageDirectory(), storage_folder);
            if (!f.exists()) {
                f.mkdirs();
                Log.d("success", "file created");
            }


        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        sensorDataHelper = new SensorDataHelper(this);
        btn_collectData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton rb_selected = findViewById(rg_Activity.getCheckedRadioButtonId());
                String currentActivity = rb_selected.getText().toString();
                Bundle b = new Bundle();
                b.putString("label", currentActivity);
                startSenseService.putExtras(b);

                Toast.makeText(getApplicationContext(), "Starting data collection for " + currentActivity + " activity", Toast.LENGTH_SHORT).show();

                startService(startSenseService);


            }
        });




        btn_ml_algo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MLActivity.class);
                startActivity(intent);
            }
        });


    }


    public void storeToDatabase(ContentValues contentValues) {
        Log.d("Main", "store "+ contentValues.toString());
        sensorDataHelper.storeValues(contentValues);
    }

}
