package com.example.spotifylyricsgrabber;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class RetrieveLyricsBackup2 extends AsyncTask<Void,Void,Void> {

    LyricsListener listen;
    String trackName;
    String artistName;
    private Exception exception;
    static String lyricsOfSong;

    private static Pattern patternDomainName;
    private static Matcher matcher;
    private static final String DOMAIN_NAME_PATTERN
            = "([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}";
    static {
        patternDomainName = Pattern.compile(DOMAIN_NAME_PATTERN);
    }

    RetrieveLyricsBackup2(String trackName, String artistName, LyricsListener listen){
        this.trackName=trackName;
        this.artistName=artistName;
        this.listen=listen;
    }
    
    protected Void doInBackground(Void... anyVoid){
        try{
            String url="";
            String googleUrl="https://www.google.com/search?q=azlyrics+"+trackName+"+"+artistName+"+lyrics";
            Log.d("googleurl",googleUrl);
            Elements links = Jsoup.connect(googleUrl).userAgent("Mozilla/5.0")
                    .referrer("http://www.google.com")
                    .get().select("a[href]");
            Set<String> result = new HashSet<String>();
            for (Element link : links) {
//                Log.d("googlelinks",String.valueOf(link));
                String temp = link.attr("href");
                if(temp.startsWith("/url?q=")){
                    //use regex to get domain name
                    result.add(getDomainName(temp));
                    if(result.size()!=0){
                        break;
                    }
                    Log.d(TAG, "doInBackground: "+result.size());
                }
            }
            Log.d(TAG, "after background: "+result.size());
            for(String a:result){
                Log.d("results","the result is "+a);
                if (a!=null)
                    url=a;
            }

//            String url="https://genius.com/"+artistName+"-"+trackName+"-lyrics";
            Log.d("urllyrics",url);
            String urlForCustomServer="http://192.168.0.10:5001/lyricsaz?lyricsUrl="+url;
            Log.d("customUrl",urlForCustomServer);
            Document document = Jsoup.connect(urlForCustomServer).get();

            document.outputSettings(new Document.OutputSettings().prettyPrint(false));
            document.select("br").after("\\n");
            Log.d("document",String.valueOf(document));
//            Elements lyrics=document.select("body");
//            String lyricsText=lyrics.text().replaceAll("\\\\n", "\n");
            String lyricsText=String.valueOf(document);
//            Log.d("finalLyrics",lyricsText);
            lyricsOfSong=lyricsText;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getDomainName(String url) {
//        Log.d("urlis",url);
        String domainNameUrl = url.replace("/url?q=", "");
        int d = domainNameUrl.indexOf("&");
        domainNameUrl = domainNameUrl.substring(0, d);
        String domainName = "";
        matcher = patternDomainName.matcher(url);
        if (matcher.find()) {
            domainName = matcher.group(0).toLowerCase().trim();
//            Log.d("domain",domainName);
        }
        if(domainName.equals("www.azlyrics.com") && domainNameUrl.contains("lyrics")){
            Log.d(TAG, "getDomainName: is genius "+domainNameUrl);
            return domainNameUrl;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        listen.grabLyrics(lyricsOfSong);
        Log.d(TAG, "onPostExecute: lyrics have been grabbed");
    }
}
