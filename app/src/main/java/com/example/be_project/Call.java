package com.example.be_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import java.util.ArrayList;

public class Call extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ArrayList<String> contacts = new ArrayList<>();
    ArrayList<String> numbers = new ArrayList<>();
    contact_adapter adapter;
    static textospeech tts;
    SearchView searchView;
    private boolean pshort=false;
    private boolean plong = false;
    private boolean s =false;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }

         else {
            contacts = getContact();
            RecyclerView recyclerView = findViewById(R.id.rc);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tts= new textospeech(getBaseContext(),"Call Service",true);

                }
            },1500);

            adapter=new contact_adapter (this, contacts, numbers,tts);
            recyclerView.setAdapter(adapter);

        }
         getSupportActionBar().setTitle("CALL");



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


        if(result.indexOf("search")!=-1){
            s=true;
            onQueryTextChange(result.split(" ")[1]);

    }
        if(result.indexOf("go")!=-1){
            if(result.indexOf("back")!=-1){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                adapter.setFilter(getContact());


            }}
            else if(result.indexOf("home")!=-1){
                Toast.makeText(this,"going back to home",Toast.LENGTH_SHORT).show();

                Intent i = new Intent(this,home.class);
                startActivity(i);

                }
            }
        if(result.indexOf("call")!=-1){
          //  Toast.makeText(this,"Without check"+result.split(" ")[1],Toast.LENGTH_SHORT).show();
            String name =result.split(" ")[1].trim();
            ArrayList<String> c = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                c = getContact();
            }

            for(final String i : c){

              //  Toast.makeText(this,"With check"+i.split("->")[0].toLowerCase(),Toast.LENGTH_SHORT).show();

                if(i.split("\n")[0].toLowerCase().trim().equals(name)){
                    //Toast.makeText(this,"}}}}}",Toast.LENGTH_SHORT).show();
                    tts.Speak("Calling "+name);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((Activity) getBaseContext(), new String[]{Manifest.permission.CALL_PHONE}, 2);}
                            startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+i.split("\n")[1].trim())));

                        }
                    },1200);

                }

            }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        searchView=(SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                adapter.setFilter(getContact());
                return true;
            }
        });
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<String> getContact() {
        ArrayList<String> c = new ArrayList<>();


        Cursor cursor = (Cursor) getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME  );

        while (cursor.moveToNext()) {

            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)) + "\n";
            String number = (cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))).trim().replaceAll(" ", "");

            c.add(name + " " + number);
            numbers.add(number);
        }


        return c;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                contacts=getContact();
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();

        ArrayList<String> newList = new ArrayList<>();

        for (String c: contacts){

            String c1 = c.toLowerCase();

            if(c1.contains(newText)){
                newList.add(c);

            }
        }
        adapter.setFilter(newList);
        return true;
    }


}
