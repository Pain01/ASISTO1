package com.example.be_project;


import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import androidx.annotation.RequiresApi;

public class textospeech {
    Context context;
    String text;
    boolean c;
    public TextToSpeech textToSpeech;

    public textospeech(final Context context, final String text, final boolean c) {
        this.context = context;
        this.text = text;
        this.c = c;

        textToSpeech =new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(Locale.UK);

                    if(result == textToSpeech.LANG_NOT_SUPPORTED|| result== textToSpeech.LANG_MISSING_DATA){
                        Toast.makeText(context,"Error while setting up textToSpeech", Toast.LENGTH_SHORT).show();
                        Log.e("lang","::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

                    }
                    Log.e("con","::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

                }else{
                    Toast.makeText(context,"Error while setting up textToSpeech..................", Toast.LENGTH_SHORT).show();

                }
                if(c) {
                    Log.e("YES","::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    SpeakOnce(text);
                }
            }
        });

    }

    public void SpeakOnce(String text){
        textToSpeech.speak("Assisto "+text+" is ready to use.", TextToSpeech.QUEUE_FLUSH,null);

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void Speak(String text){
        Log.e("Speak","::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        textToSpeech.setVoice(textToSpeech.getVoice());
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH,null);

    }

    public  void Stop(){
        Log.e("Stop","::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

        textToSpeech.stop();
        textToSpeech.shutdown();
    }



}
