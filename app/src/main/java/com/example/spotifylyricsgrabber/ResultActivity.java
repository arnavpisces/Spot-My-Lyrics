package com.example.spotifylyricsgrabber;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //To set my custom image as the background of the action bar
        ActionBar actionBar=getSupportActionBar();
//        ColorDrawable colorDrawable=new ColorDrawable(Color.parseColor("black"));
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("");
        Drawable drawable=ContextCompat.getDrawable(getApplicationContext(), R.drawable.sml);
        actionBar.setBackgroundDrawable(drawable);
        TextView lyricsBox=findViewById(R.id.lyricsBox);
//        lyricsBox.setMovementMethod(new ScrollingMovementMethod());
        lyricsBox.setText(MainActivity.globalLyrics);


    }

}
