package com.example.group30.assignment3.data;

import android.provider.BaseColumns;


/**
 * Created by Dishank on 3/5/2018.
 */

public class SensorDataContract {



    public static final class SensorDataTable implements BaseColumns{

        public static String TABLE_NAME = "Test";
        public static final String COLUMN_ACTIVITY_ID = "Activity_ID";
        public static final String COLUMN_ACTIVITY_LABEL = "Activity_Label";


    }
}
