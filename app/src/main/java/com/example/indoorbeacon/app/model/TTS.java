package com.example.indoorbeacon.app.model;

import android.content.Context;
import android.os.Bundle;
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
    private boolean newStartedOldNotFinshed;

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

    public void speakList(final ArrayList<String> strings,final int counter){
        if(tts != null)
            if (tts.isSpeaking()) {
                stop();
                tts = null;
            }

        tts = new TextToSpeech(c, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.GERMANY);
                    Bundle params = new Bundle();
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "Unique");
                    tts.speak(strings.get(counter), TextToSpeech.QUEUE_FLUSH, params, "UniqueID");
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onStart(String utteranceId) {

                            }

                            @Override
                            public void onDone(String utteranceId) {
                                if (counter < strings.size() - 1 && !tts.isSpeaking())
                                    TTS.this.speakList(strings, counter + 1);
                            }

                            @Override
                            public void onError(String utteranceId) {

                            }
                        });


                }
            }
        });
    }

//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            if(speakListCounter < speakList.size()-1)
//                TTS.this.speakList(speakList,speakListCounter);
//        }
//    };
//
//    private Handler h = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            new Handler().postDelayed(runnable,500);
//        }
//    };
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
