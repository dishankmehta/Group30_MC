package com.example.group30.assignment3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import au.com.bytecode.opencsv.CSVWriter;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import umich.cse.yctung.androidlibsvm.LibSVM;


/**
 * Created by Dishank on 4/16/2018.
 */

public class MLActivity extends AppCompatActivity {


    private Button btn_train;
    private Button btn_test;
    private TextView tv_score;
    private TextView tv_params;
    //private svm_parameter parameter;
    private double model_accuracy = 0;
    //private svm_problem model;
    private String trainDataPath;
    private String modelPath;
    private String dataPredictPath;
    private String parameters;
    private String processId = Integer.toString(android.os.Process.myPid());


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ml);
        new FileConvert().execute("");

        String storage_folder = "/Android/Data/CSE535_ASSIGNMENT3";
        parameters = "-s 0 -t 1 -d 3 -g 0.0067 -v 4 ";
        String appFolder = Environment.getExternalStorageDirectory().getPath()
                +storage_folder;
        initializeFilePaths(appFolder);

        tv_params = findViewById(R.id.tv_params);
        tv_params.setText("-:SVM Classifier:-\n" +
                        "svm_type = C-SVC\t kernel_type = polynomial\n" +
                        "degree = 3\t gamma = 0.0067\n" +
                        "n_fold = 4");

        btn_train = findViewById(R.id.btn_train);


        btn_train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TrainModel().execute(parameters, trainDataPath, modelPath);
            }
        });






    }


    public void initializeFilePaths(String appFolder){
        trainDataPath = appFolder+"/dataset.txt";
        modelPath = appFolder+"/model";
        dataPredictPath = appFolder+"/predict";
    }

    private class TrainModel extends AsyncTask<String, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "=========SVM Training Start==========", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(String... parameters) {
            LibSVM.getInstance().train(TextUtils.join(" ",parameters));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "=========SVM Training Done==========", Toast.LENGTH_LONG).show();

            try {
                Process process = Runtime.getRuntime().exec("logcat -d");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder log = new StringBuilder();
                String line = "";
                while ((line = bufferedReader.readLine()) != null){
                    if(line.contains(processId) && line.contains("LibSvm")){
                        if (line.contains("=======")){
                            log.append("==================\n");
                        } else if (line.contains("Start of SVM")){
                            log.append(line.substring(line.indexOf("Start"))).append("\n");
                        } else if (line.contains("End of SVM")) {
                            log.append(line.substring(line.indexOf("End"))).append("\n");
                        } else {
                            int indexOfProcessId = line.lastIndexOf(processId);
                            String newLine = line.substring(indexOfProcessId);
                            log.append(newLine).append("\n\n");
                        }
                    }
                }


                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View dialogView = inflater.inflate(R.layout.result_layout, null);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext())
                            .setTitle("SVM Train Results")
                            .setView(dialogView)
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                final TextView tv_result = dialogView.findViewById(R.id.tv_result_data);
                tv_result.setText(log.toString());
                final AlertDialog alertDialogCreater = alertDialog.create();
                alertDialogCreater.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    class FileConvert extends AsyncTask<String, Void, Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            String storage_folder = "/Android/Data/CSE535_ASSIGNMENT3";
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory().getPath()
                            +storage_folder+"/Group30.db", null);
            File csvDirectory = new File(Environment.getExternalStorageDirectory()+storage_folder);
            //File csvFile = new File(csvDirectory, "dataset.csv");
            File csvFile = new File(csvDirectory, "dataset.txt");
            //CSVWriter writer = null;
            Cursor cursor = null;
            FileWriter writer = null;


            try{

                cursor = db.rawQuery("SELECT COUNT(*) FROM Test", null);
                cursor.moveToFirst();
                int rows = cursor.getInt(0);
                writer = new FileWriter(csvFile);

                for(int i=0; i<rows; i++){
                    StringBuilder row;
                    Cursor rowCursor = db.rawQuery("SELECT * FROM Test WHERE id="+(i+1), null);
                    rowCursor.moveToFirst();
                    String label = rowCursor.getString(151);
                    row = new StringBuilder(label);
                    for(int j=1; j<=150; j++){
                        row.append(j).append(":").append(cursor.getString(j)).append(" ");
                    }

                    String finalRow = row.toString().trim();
                    finalRow += "\n";

                    writer.append(finalRow);
                    writer.flush();

                }
                /*csvFile.createNewFile();
                writer = new CSVWriter(new FileWriter(csvFile));
                cursor = db.rawQuery("SELECT * FROM Test", null);
                writer.writeNext(cursor.getColumnNames());
                while(cursor.moveToNext()){
                    String rows[] = new String[152];
                    rows[0] = cursor.getString(0);
                    for(int i=1; i<=150; i++){
                        rows[i] = cursor.getString(i);
                    }
                    rows[rows.length - 1] = cursor.getString(151);
                    writer.writeNext(rows);
                }*/

                writer.close();
            }catch(Exception e){
                e.printStackTrace();
            }finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Toast.makeText(getApplicationContext(), "File converted", Toast.LENGTH_LONG).show();
            }
        }
    }
}
