package com.example.group30.assignment3;

/**
 * Created by ayan_ on 4/10/2018.
 */
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.group30.assignment3.data.SensorDataContract;

public class SensorHandlerClass extends Service implements SensorEventListener {

    private SensorManager accelManage;
    private int index = 1;
    private Boolean serviceFlag = true;
    private ContentValues contentValues;


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // TODO Auto-generated method stub
        if(index<51 && serviceFlag) {
            contentValues.put("X" + index, sensorEvent.values[0]);
            contentValues.put("Y" + index, sensorEvent.values[1]);
            contentValues.put("Z" + index, sensorEvent.values[2]);
            index++;
            Log.d("Data", ""+index);
        }else if(serviceFlag){
            index =1;
            serviceFlag = false;
            storeSensorValues(contentValues);
            stopSelf();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCreate() {
        Log.d("Start S", "started");
        contentValues = new ContentValues();
        contentValues.put(SensorDataContract.SensorDataTable.COLUMN_ACTIVITY_LABEL, "walk");
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        accelManage = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor senseAccel = accelManage.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManage.registerListener(this, senseAccel, 100000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        index = 1;
        contentValues = new ContentValues();
        String label = intent.getExtras().getString("label");
        contentValues.put(SensorDataContract.SensorDataTable.COLUMN_ACTIVITY_LABEL, label);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        //k = 0;
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        serviceFlag = false;
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }


    public void storeSensorValues(ContentValues contentValues){

        Delegate.theMainActivity.storeToDatabase(contentValues);
    }




}
