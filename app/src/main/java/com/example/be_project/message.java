package com.example.be_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class
message extends AppCompatActivity {
    private EditText pEdit;
    private EditText mEdit;
    textospeech tts;
    private boolean pshort=false;
    private boolean plong = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        pEdit =findViewById(R.id.number);
        mEdit = findViewById(R.id.emessage);
        getSupportActionBar().setTitle("MESSAGE");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tts= new textospeech(getBaseContext(),"Messaging Service",true);

            }
        },1500);
        Button send =findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                send(tts);

            }
        });


    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode){
            case KeyEvent.KEYCODE_HEADSETHOOK:
                event.startTracking();
                pshort = true;
                return true;


        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_HEADSETHOOK:
                event.startTracking();
                pshort=false;
                plong = true;
                return true;


        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_HEADSETHOOK:
                if(plong){
                    plong=false;
                    pshort=false;
                    speechmethod();
                    return true;}
                else{
                    event.startTracking();
                }


        }
        return super.onKeyUp(keyCode, event);
    }

    public void speechmethod(){
        // Toast.makeText(getApplicationContext(),"Entering clik mode",Toast.LENGTH_SHORT).show();
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        startActivityForResult(i,10);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && requestCode ==10){
            processResult((data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)).get(0));

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void processResult(String result) {

        result = result.toLowerCase();
        // Toast.makeText(this,"????/////////////////////////////////////",Toast.LENGTH_SHORT).show();


        if(result.indexOf("message")!=-1){
            //Toast.makeText(this,"/////////////////////////////////////",Toast.LENGTH_SHORT).show();

            if(result.indexOf("to")!=-1){
                //Toast.makeText(this,"to",Toast.LENGTH_SHORT).show();
                mEdit.setFocusable(false);
                pEdit.setFocusable(true);
                pEdit.setText(result.split("to")[1].toLowerCase().trim());
            }
            if(result.indexOf("is")!=-1){
               // Toast.makeText(this,"is",Toast.LENGTH_SHORT).show();
                pEdit.setFocusable(false);
                mEdit.setFocusable(true);
                mEdit.setText(result.split("is")[1].trim());
            }

            if(result.indexOf("send")!=-1){
                Cursor cursor = (Cursor) getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME  );
                String pNumber = pEdit.getText().toString().trim();
                String message = mEdit.getText().toString().trim();
                String num="";

                while(cursor.moveToNext()){
                    if(pNumber.toLowerCase().equals(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).toLowerCase())){
                        num = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        break;
                    }
                }

                if(num.length()<=0 ){
                    Toast.makeText(this,"Enter a Valid Name/Number!",Toast.LENGTH_SHORT);
                }else {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(num, null, message, null, null);

                    Toast.makeText(getBaseContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                    tts.Speak("Message Sent");
                    pEdit.setText("");
                    mEdit.setText("");

                }

            }

        }

        if(result.indexOf("go")!=-1){
            if(result.indexOf("home")!=-1){
                Intent i = new Intent(this,home.class);
                startActivity(i);
            }

        }
    }








    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void send(textospeech tts) {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if(permission == PackageManager.PERMISSION_GRANTED){
            message(tts);
        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void message(textospeech tts){

        boolean empty=false;
        if(pEdit.length()==0){
            empty=true;
            Toast.makeText(getBaseContext(),"Enter a Number", Toast.LENGTH_SHORT).show();
            pEdit.setFocusable(true);
            pEdit.setError("Required");
            tts.Speak("Enter a Valid Name or Number!");


        }
        else if(mEdit.length()==0){
            empty=true;
            Toast.makeText(getBaseContext(),"Enter a Message", Toast.LENGTH_SHORT).show();
            mEdit.setFocusable(true);
            mEdit.setError("Required");
            tts.Speak("Enter a Message");

        }

        if(!empty) {

            Cursor cursor = (Cursor) getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME  );
            String pNumber = pEdit.getText().toString().trim();
            String message = mEdit.getText().toString().trim();
            String num="";

            while(cursor.moveToNext()){
                if(pNumber.toLowerCase().equals(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).toLowerCase())){
                 num = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                 break;
                }
            }

            if(num.length()<=0 ){
                Toast.makeText(this,"Enter a Valid Name/Number!",Toast.LENGTH_SHORT);
            }else {
                fsend(num,message);

                Toast.makeText(getBaseContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                tts.Speak("Message Sent");
                pEdit.setText("");
                mEdit.setText("");

            }
        }
    }

    public void fsend(String num, String message){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(num, null, message, null, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            message(tts);
        }
        else{
            Toast.makeText(getBaseContext(), "Sorry.....you dont have permission", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onDestroy() {
        tts.Stop();
        super.onDestroy();
    }
}
