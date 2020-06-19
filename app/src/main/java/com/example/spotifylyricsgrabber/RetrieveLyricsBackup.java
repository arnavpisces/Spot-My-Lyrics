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

public class RetrieveLyricsBackup extends AsyncTask<Void,Void,Void> {

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

    RetrieveLyricsBackup(String trackName, String artistName, LyricsListener listen){
        this.trackName=trackName;
        this.artistName=artistName;
        this.listen=listen;
    }

    protected Void doInBackground(Void... anyVoid){
        try{
            String url="";
            String googleUrl="https://www.google.com/search?q=genius+"+trackName+"+"+artistName+"+lyrics";
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
            Document document = Jsoup.connect(url).userAgent("Mozilla/5.0")
                    .referrer("http://www.google.com")
                    .timeout(5000)
                    .get();

//            try{
//                UserAgent userAgent = new UserAgent();
//                com.jaunt.Elements elements = userAgent.doc.findFirst("<div class=lyrics>").findEach("<div>");             //find all divs in the document
//                System.out.println("Meat search: " + elements.size() + " results");//report number of search results.
//            }
//            catch(JauntException e){
//                System.err.println(e);
//            }
            Log.d("document",String.valueOf(document));
            document.outputSettings(new Document.OutputSettings().prettyPrint(false));
            document.select("br").after("\\n");
            Elements lyrics=document.select("div[class=lyrics]");


            String lyricsText=lyrics.text().replaceAll("\\\\n", "\n");
            Log.d("finalLyrics",lyricsText);
            lyricsOfSong=lyricsText;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getDomainName(String url) {
        Log.d("urlis",url);
        String domainNameUrl = url.replace("/url?q=", "");
        int d = domainNameUrl.indexOf("&");
        domainNameUrl = domainNameUrl.substring(0, d);
        String domainName = "";
        matcher = patternDomainName.matcher(url);
        if (matcher.find()) {
            domainName = matcher.group(0).toLowerCase().trim();
        }
        if(domainName.equals("genius.com") && domainNameUrl.contains("lyrics")){
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
