package com.pasindu_ud.light_chat;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;

public class ReceivingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receiving_activity);

        if (OpenCVLoader.initDebug()) {
            Log.d("LOADED_OPENCV", "Successful");
        } else {
            Log.d("LOADED_OPENCV", "Unsuccessful");
        }
    }
}
