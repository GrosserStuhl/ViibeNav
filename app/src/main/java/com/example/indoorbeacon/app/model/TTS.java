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
    private Context c;

    private ArrayList<String> instructionList;
    private int instructionCounter;
    private boolean hadErrors;

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

    @Deprecated
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
//                    tts.setOnUtteranceProgressListener(singleton);
                }
            }
        });
    }

    public void speak(final String toSpeak, final UtteranceProgressListener utt){

        if(tts != null)
            if(tts.isSpeaking()) {
                stop();
                Log.d(TAG, "speaking: RETURN!");
                return;
            }

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

    private Handler h = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            if(tts.isSpeaking()) {
                onError("Unique");
                tts.stop();

                instructionCounter = 0;
//                h.removeCallbacksAndMessages(msg);
//                Message send = Message.obtain();
//                send.obj = message;
//                h.sendMessage(send);
//                 VERSION 2
//                speakList(instructionList,instructionCounter);
                speakListItem(instructionList.get(instructionCounter));
            } else {
                if(instructionCounter == 0)
                    instructionCounter++;
                speak(message, TTS.this);
            }
        }
    };

    @Override
    public void onStart(String utteranceId) {
        Log.d(TAG, "startSpeaking");
    }

    @Override
    public void onDone(String utteranceId) {
        Log.d(TAG, "speaking done." + tts.isSpeaking());
            if (!hadErrors && instructionCounter < instructionList.size()) {
                Message msg = Message.obtain();
                Log.d(TAG, "onDone : countr"+instructionCounter);
                msg.obj = instructionList.get(instructionCounter++);
                h.sendMessage(msg);
            }
            hadErrors = false;
    }

    @Override
    public void onError(String utteranceId) {
        Log.e(TAG, "Speaking error.");
        hadErrors = true;
    }



    public void speakList(final ArrayList<String> strings,final int counter){
        instructionList = strings;
        instructionCounter = counter;
        hadErrors = false;
        speakListItem(instructionList.get(instructionCounter));

    }

    private void speakListItem(String item){
        Message msg = Message.obtain();
        msg.obj = item;
        h.sendMessage(msg);
    }

    public void stop(){
//        if(tts.stop() == -1);
            stop();
    }

    public TextToSpeech getTextToSpeech(){
        return tts;
    }
}
