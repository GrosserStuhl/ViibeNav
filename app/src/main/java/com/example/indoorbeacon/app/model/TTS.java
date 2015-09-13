package com.example.indoorbeacon.app.model;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by TomTheBomb on 01.09.2015.
 */
public class TTS extends UtteranceProgressListener {

    private static final String TAG = "TTS";

    private static TTS singleton;
    private static TextToSpeech tts;

    private ArrayList<String> stringList;
    private int stringListCounter = 0;

    private Context c;

    public static TTS getTTS(Context c){
        synchronized(TTS.class){
                if (singleton == null)
                    singleton = new TTS(c);

        }
        return singleton;
    }

    private TTS(Context c){
        this.c = c;
        tts = new TextToSpeech(c, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.GERMANY);
                    Bundle params = new Bundle();
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "Unique");
                }
            }
        });
    }

    @Override
    public void onStart(String utteranceId) {
        Log.d(TAG, "startSpeaking");
    }

    @Override
    public void onDone(String utteranceId) {
        stop();
        Log.d(TAG, "speaking done."+tts.isSpeaking());
    }

    @Override
    public void onError(String utteranceId) {
        Log.e(TAG, "Speaking error.");
    }

    public void speak(final String toSpeak){

        if(tts != null)
            if(tts.isSpeaking())
                stop();

        tts = new TextToSpeech(c, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.GERMANY);
                    Bundle params = new Bundle();
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "Unique");
                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, params, "UniqueID");
                    tts.setOnUtteranceProgressListener(singleton);
                }
            }
        });
    }

    public void speak(final String toSpeak, final UtteranceProgressListener utt){

        if(tts != null)
            if(tts.isSpeaking())
                stop();

        tts = new TextToSpeech(c, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.GERMANY);
                    Bundle params = new Bundle();
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "Unique");
                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, params, "UniqueID");
                    tts.setOnUtteranceProgressListener(utt);
                }
            }
        });
    }

    public void speakList(final ArrayList<String> strings,final int counter){
            stringList = strings;
            stringListCounter = counter;
//            for (String s : strings)
        if(tts.isSpeaking()) {
            Log.d(TAG, "speakList "+ "try to start new List");
            tts.stop();
            h.removeCallbacks(runnable);
//            speakList(strings, counter);
//            runnable = new MyRunnable();
        } else {
            runnable = new MyRunnable();
            h.sendEmptyMessage(0);
        }
    }

    private Handler h = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            postDelayed(runnable, 500);
        }
    };

    private MyRunnable runnable = new MyRunnable();

    private class MyRunnable extends UtteranceProgressListener implements Runnable {
        private boolean started;

        public MyRunnable(){
            started = false;
        }

        @Override
        public void run() {
            started = true;
            if(stringListCounter < stringList.size()-1 && started)
                speak(stringList.get(stringListCounter++),this);
        }

        @Override
        public void onDone(String utteranceId) {
            if(!tts.isSpeaking())
                h.sendEmptyMessage(0);
        }

        @Override
        public void onStart(String utteranceId) {}

        @Override
        public void onError(String utteranceId) {}
    }
//    private Handler betweenHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            speakListCounter++;
//            h.postDelayed(runnable,500);
//        }
//    };


    public void stop(){
        tts.stop();
//        tts.shutdown();
    }

    public TextToSpeech getTextToSpeech(){
        return tts;
    }

}
