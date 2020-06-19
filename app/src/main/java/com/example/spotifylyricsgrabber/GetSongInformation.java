package com.example.spotifylyricsgrabber;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import static android.content.ContentValues.TAG;

public class GetSongInformation extends Service {
    BroadcastReceiver spotifyBroadcastReceiver;

    @Override
    public void onCreate(){
        Log.d(TAG,"service started");
        spotifyBroadcastReceiver = new MyBroadcastReciver();
        IntentFilter spotifyIntentFilter=new IntentFilter("com.spotify.music.playbackstatechanged");
        spotifyIntentFilter.addAction("com.spotify.music.metadatachanged");
        spotifyIntentFilter.addAction("com.spotify.music.queuechanged");
        registerReceiver(spotifyBroadcastReceiver, spotifyIntentFilter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy(){
        unregisterReceiver(spotifyBroadcastReceiver);

    }
}
