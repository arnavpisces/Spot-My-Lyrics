package com.example.spotifylyricsgrabber;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class BubbleActivity extends AppCompatActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);
        TextView text=findViewById(R.id.bubbleText);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(MainActivity.globalLyrics);
    }
}
