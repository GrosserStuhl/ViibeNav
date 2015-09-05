package com.example.indoorbeacon.app.model;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

/**
 * Created by TomTheBomb on 01.09.2015.
 */
public class TTS extends UtteranceProgressListener {

    private static final String TAG = "TTS";

    private static TTS singleton;
    private static TextToSpeech tts;

    private Context c;

    public static TTS createTTS(Context c){
        synchronized(TTS.class){
                if (singleton == null)
                    singleton = new TTS(c);

        }
        return singleton;
    }

    private TTS(Context c){
        this.c = c;
    }

    @Override
    public void onStart(String utteranceId) {
        Log.d(TAG, "startSpeaking");
    }

    @Override
    public void onDone(String utteranceId) {
        stop();
        Log.d(TAG, "speaking done.");
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


    public void stop(){
        tts.stop();
//        tts.shutdown();
    }

    public static TTS getTTS() {
        return singleton;
    }
}
