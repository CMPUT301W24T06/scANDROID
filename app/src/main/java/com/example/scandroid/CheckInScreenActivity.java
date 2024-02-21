package com.example.scandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;


public class CheckInScreenActivity extends AppCompatActivity {

    ImageView backgroundImageView; // declare the ImageView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in_screen_activity);
        ImageView backgroundImageView = findViewById(R.id.backgroundImageView);
        backgroundImageView.setBackgroundResource(R.drawable.img);
    }
}