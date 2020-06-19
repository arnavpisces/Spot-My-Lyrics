package com.example.spotifylyricsgrabber;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.spotify.android.appremote.api.SpotifyAppRemote;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

//Spotify SDK dependencies
//JSOUP Imports

public class MainActivityBackup extends AppCompatActivity implements LyricsListener {

    //Spotify CREDS
    private static final String CLIENT_ID="1031a8a66bdd45be9e1f9316a6e13217";
    private static final String REDIRECT_URI="com.spotifylyricsgrabber://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    static final String CHANNEL_ID="spotifyLyricsNotifChannel";
    BroadcastReceiver myBroadcastReceiver;
    Notification.Builder notifBuilder;
    NotificationChannel channel;
    NotificationManagerCompat  notificationManager;
    public static RetrieveLyrics asyncTaskRetrieveLyrics=new RetrieveLyrics(null,null,null);
    static String globalLyrics="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //On the RetrieveLyrics async task, on postExecute
        asyncTaskRetrieveLyrics.listen=this;

        IntentFilter intentFilter=new IntentFilter("com.spotify.music.playbackstatechanged");
        intentFilter.addAction("com.spotify.music.metadatachanged");
        intentFilter.addAction("com.spotify.music.queuechanged");

        myBroadcastReceiver = new MyBroadcastReciver();
        registerReceiver(myBroadcastReceiver,intentFilter);

        createNotificationChannel();

//        Button bubbleButton=findViewById(R.id.createBubble);
//        bubbleButton.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                //This intent is for the activity inside the bubble intent
//                Intent target = new Intent(MainActivity.this, BubbleActivity.class);
//                PendingIntent bubbleIntent=PendingIntent.getActivity(MainActivity.this, 0, target, PendingIntent.FLAG_UPDATE_CURRENT);
//                Notification.BubbleMetadata bubbleData= new Notification.BubbleMetadata.Builder()
//                        .setDesiredHeight(600)
//                        .setIcon(Icon.createWithResource(MainActivity.this, R.mipmap.ic_launcher))
//                        .setIntent(bubbleIntent)
//                        .setAutoExpandBubble(true)
//                        .build();
//                notifBuilder = new Notification.Builder(MainActivity.this, channel.getId())
//                        .setSmallIcon(R.mipmap.ic_launcher)
////                        .setAutoCancel(true)
//                        .setBubbleMetadata(bubbleData);
//
////                notificationManager.createNotificationChannel(channel);
//
//                notificationManager.createNotificationChannel(channel);
//                Log.d("createNotificationChannel", "createNotificationChannel: channel created");
//                notificationManager.notify(1, notifBuilder.build());
//                /*------------------------------------*/
//            }
//        });

        /*This code is for creating an intent for the activity that would open when the notification is tapped*/
        Intent resultIntent=new Intent(this,ResultActivity.class);
        TaskStackBuilder stackBuilder=TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent=stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,resultIntent,0);
        /*--------------------------------------------*/
//
//
//
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        builder.setContentIntent(resultPendingIntent);

        notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(123, builder.build());


    }

    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    public void grabLyrics(String lyrics){
        //The lyrics of the song are updated after the asynctask is ran, and are stored in the variable lyrics
        Log.d("listener","lyrics are \n"+lyrics);
        globalLyrics=lyrics;
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
//            channel.setAllowBubbles(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    public void getLyrics(String trackName,String artistName) throws Exception{
        trackName="The Strokes";
        artistName="Bad Decisions";
        String formattedTrackName=trackName.replace(' ','-');
        String formattedArtistName=artistName.replace(' ','-');
        String url="https://genius.com/"+formattedArtistName+"-"+formattedTrackName+"-lyrics";

        Document document = Jsoup.connect(url).get();
        Elements lyrics=document.select("div[class=lyircs]");
        Log.d("lyricsSong", "getLyrics: "+lyrics);
    }
}