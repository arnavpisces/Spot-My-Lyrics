package com.example.spotifylyricsgrabber;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class RetrieveLyrics extends AsyncTask<Void,Void,Void> {

    LyricsListener listen;
    String trackName;
    String artistName;
    private Exception exception;
    static String lyricsOfSong;


    RetrieveLyrics(String trackName, String artistName, LyricsListener listen){
        this.trackName=trackName;
        this.artistName=artistName;
        this.listen=listen;
    }
    
    protected Void doInBackground(Void... anyVoid){
        try{

            String urlForCustomServer="http://192.168.0.10:5001/details?track="+trackName+"&artist="+artistName;
            Log.d("customUrl",urlForCustomServer);
            Document document = Jsoup.connect(urlForCustomServer).get();
            document.outputSettings(new Document.OutputSettings().prettyPrint(false));
            document.select("br").after("\\n");
            Log.d("document",String.valueOf(document));
            Elements lyrics=document.select("body");
            String lyricsText=lyrics.text().replaceAll("\\\\n", "\n");
//            String lyricsText=String.valueOf(document);
            Log.d("finalLyrics",lyricsText);
            lyricsOfSong=lyricsText;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        listen.grabLyrics(lyricsOfSong);
        Log.d(TAG, "onPostExecute: lyrics have been grabbed" + lyricsOfSong);
    }
}
