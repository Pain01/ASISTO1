package com.example.be_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class home extends AppCompatActivity {
    private Intent i;
    private GridView gridView;
    private String[] names ={"CALL","MESSAGE","LOCATE","TIME/DATE","SOS","CAMERA"};
    private textospeech tts;
    private boolean pshort=false;
    private boolean plong = false;
    FusedLocationProviderClient fl;
    Double x = 0.0, y = 0.0;
    String add = null;


    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        gridView =findViewById(R.id.grid);
        gridView.setAdapter(new gadapter(this,names));

        tts= new textospeech(this," ", true);
        locate(tts);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (names[position]){
                    case "CALL":
                        Toast.makeText(getBaseContext(),"CALL", Toast.LENGTH_SHORT).show();
                        call(tts);
                        break;
                    case "MESSAGE":
                        Toast.makeText(getBaseContext(),"MESSAGE", Toast.LENGTH_SHORT).show();
                        message(tts);
                        break;
                    case "LOCATE":
                        locate(tts);
                        //Toast.makeText(getBaseContext(),"LOCATE", Toast.LENGTH_SHORT).show();
                        break;
                    case "TIME/DATE":
                        ct(tts,true);
                        break;
                    case "SOS":
                        Toast.makeText(getBaseContext(),"SOS", Toast.LENGTH_SHORT).show();
                        break;
                    case "CAMERA":
                        camera(tts);
                        break;
                }

            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (names[position]=="TIME/DATE"){
                ct(tts,false);
                return true;
                }
                return false;
            }
        });

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


        if(result.indexOf("open")!=-1){

            if(result.indexOf("call")!=-1){
                call(tts);
            }
            else if( result.indexOf("message")!=-1){
                message(tts);
            }
            else if(result.indexOf("camera")!=-1){
                Toast.makeText(this,"camera opening",Toast.LENGTH_SHORT).show();

                camera(tts);
            }
        }
        if(result.indexOf("what")!=-1){
             if(result.indexOf("date")!=-1){
                ct(tts,true);
            }
            else if(result.indexOf("time")!=-1){
                ct(tts,false);
            }else if(result.indexOf("location")!=-1){
                locate(tts);
             }else if (result.indexOf("battery")!=-1){
                 final IntentFilter inf = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                 final Intent bS = getApplicationContext().registerReceiver(null,inf);
                 int l = bS.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
                 int s = bS.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
                 final double bp = l*100 /s;
                 tts.Speak(String.valueOf(bp));
                 Toast.makeText(getApplicationContext(),"Battery percentage is"+String.valueOf(bp),Toast.LENGTH_SHORT).show();
             }
        }
        if(result.indexOf("sos")!=-1){
            sos();
        }


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



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void call(textospeech tts){
        i =new Intent(this,Call.class);
        tts.Speak("Opening Call Service.");

        startActivity(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void message(textospeech tts){
        i =new Intent(this,message.class);
        tts.Speak("Opening Messaging Service.");
        startActivity(i);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    public void locate(textospeech tts){
        fl = LocationServices.getFusedLocationProviderClient(this);

        int locationRequestCode = 1000;

        //Toast.makeText(getApplicationContext(),"]]]]]]]]]]",Toast.LENGTH_SHORT).show();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},locationRequestCode);

        }else{
            fl.getLastLocation().addOnSuccessListener(this,new OnSuccessListener<Location>(){
                public void onSuccess(Location l){
                    //Toast.makeText(getApplicationContext(),"]]]]]]]]]]",Toast.LENGTH_SHORT).show();

                    System.out.println(l);
                    if(l!=null){

                        x = l.getLatitude();
                        y = l.getLongitude();
                    }else{
                      //  Toast.makeText(getApplicationContext(),"@@@@@@@@@@",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        //Toast.makeText(getApplicationContext(),String.valueOf(x)+" "+String.valueOf(y),Toast.LENGTH_SHORT).show();

        Geocoder geocoder = new Geocoder(getApplicationContext(),Locale.getDefault());

        try{
            List<Address> addresses = geocoder.getFromLocation(x, y, 1);
            if(addresses != null&&addresses.size()>0){
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);
            //add = add + "\n" + obj.getCountryName();
            //add = add + "\n" + obj.getCountryCode();
            //add = add + "\n" + obj.getAdminArea();
            //add = add + "\n" + obj.getPostalCode();
            //add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            //add = add + "\n" + obj.getSubThoroughfare();
            }

            else{
                Toast.makeText(getApplicationContext(), "Fetching Location...", Toast.LENGTH_SHORT).show();

            }

        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getApplicationContext(), add, Toast.LENGTH_SHORT).show();

        tts.Speak(add);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void ct(textospeech tts, boolean f){
        if(f){
        final Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH)+1;
        int d = calendar.get(Calendar.DAY_OF_MONTH);

        Toast.makeText(this,"TIME/CALENDER --->"+d+"/"+m+"/"+y, Toast.LENGTH_SHORT).show();
        tts.Speak("Today is "+d+"/"+m+"/"+y);}

        else {
            Date date = new Date();
            String time = DateUtils.formatDateTime(this,date.getTime(),DateUtils.FORMAT_SHOW_TIME);
            Toast.makeText(this,"TIME/CALENDER --->"+time, Toast.LENGTH_SHORT).show();
            tts.Speak("The Time is "+time);
        }

    }

    public void sos(){

        String num="+91 9664869629";
        String message = "HELP!!! My location is:"+String.valueOf(x)+" "+String.valueOf(y);
        message m = new message();
        m.fsend(num,message);
        Toast.makeText(getBaseContext(),"SOS", Toast.LENGTH_SHORT).show();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void camera(textospeech tts){

        Intent img_pick = new Intent(this,DetectorActivity.class);
        tts.Speak("Opening Camera Service.");

        startActivity(img_pick);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1000:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fl.getLastLocation().addOnSuccessListener(this,new OnSuccessListener< Location >(){
                        public void onSuccess(Location l){
                            //Toast.makeText(getApplicationContext(),"]]]]]]]]]]",Toast.LENGTH_SHORT).show();

                            System.out.println(l);
                            if(l!=null){
                                //Toast.makeText(getApplicationContext(), (CharSequence) l,Toast.LENGTH_SHORT).show();

                                x = l.getLatitude();
                                y = l.getLongitude();
                            }else{
                                //Toast.makeText(getApplicationContext(),"@@@@@@@@@@",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });}
                break;
        }
    }

}
