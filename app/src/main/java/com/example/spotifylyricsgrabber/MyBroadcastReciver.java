package com.example.spotifylyricsgrabber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReciver extends BroadcastReceiver {
    static final class BroadcastTypes{
        static final String SPOTIFY_PACKAGE = "com.spotify.music";
        static final String PLAYBACK_STATE_CHANGED = SPOTIFY_PACKAGE + ".playbackstatechanged";
        static final String QUEUE_CHANGED = SPOTIFY_PACKAGE + ".queuechanged";
        static final String METADATA_CHANGED = SPOTIFY_PACKAGE + ".metadatachanged";
    }

    @Override
    public void onReceive(Context context, Intent intent){
        long timeSentInMs= intent.getLongExtra("timeSent",0L);
        String action=intent.getAction();

        String trackId=intent.getStringExtra("id");
        String artistName=intent.getStringExtra("artist");
        String albumName=intent.getStringExtra("album");
        String trackName=intent.getStringExtra("track");
        int trackLengthInSec=intent.getIntExtra("length",0);

        if(action.equals(BroadcastTypes.METADATA_CHANGED)){
            Log.d("broadcast",trackName+ " metadata");
            //i have the information now
            sendInformation(context, trackName,artistName);

        }

        else if(action.equals(BroadcastTypes.PLAYBACK_STATE_CHANGED)){
            boolean playing = intent.getBooleanExtra("playing",false);
            int positionInMs=intent.getIntExtra("playbackPosition",0);
            Log.d("broadcast",trackName+ " "+artistName+" playback state");

        }

//        else if(action.equals(BroadcastTypes.QUEUE_CHANGED)){
//            //sent as a notification
//            Log.d("broadcast",trackName+ " reached here queue");
//        }

    }

    public void sendInformation(Context context, String track, String artist){
        Log.d("sendInformation","reached send information");
        Intent i = new Intent(context, RetrieveLyrics.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("songInfo",track+" "+artist);
        String[] songInformation=sanitizeNames(track, artist);
//        String[] songInformation=sanitizeNames("Bad decisions","the strokes" );
        String sanitizedTrack=songInformation[0];
        String sanitizedArtist=songInformation[1];
        //The asynctask was declared in mainactivity to send the information back to mainactivity
        LyricsListener referenceOfMain=MainActivity.asyncTaskRetrieveLyrics.listen; //This is a way to get a reference of the implemented listener of mainactivity and access it in this broadcastservice
        new RetrieveLyrics(sanitizedTrack,sanitizedArtist,referenceOfMain).execute();
//        new RetrieveLyrics("eternal+summer","the+strokes",referenceOfMain).execute();
    }

    public String[] sanitizeNames(String track, String artist){
//        Remove the word live from the song
//        track=track.replaceAll(" - Live","");
        //Convert & to and
        track = track.replaceAll("&", "and");
        artist = artist.replaceAll("&", "and");
//        //Remove all other characters
        track = track.replaceAll("[^a-zA-Z0-9- ]", "");
        artist = artist.replaceAll("[^a-zA-Z0-9- ]", "");
        //Replace spaces with hyphens
        track=track.replace(' ','+');
        artist=artist.replace(' ','+');
        //Replace hyphens with +
        track=track.replace('-','+');
        artist=artist.replace('-','+');
        String[] songInfo={track,artist};
        return songInfo;
//        Log.d("sanitize",track+" "+artist);
    }
}
