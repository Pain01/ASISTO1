package com.example.be_project;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class contact_adapter extends RecyclerView.Adapter<contact_adapter.contactViewHolder>{
    ArrayList<String> contacts;
    private Context context;
    ArrayList<String> numbers;
    textospeech tts;

    public contact_adapter(Context context,ArrayList<String> contacts,ArrayList<String> numbers,textospeech tts) {
        this.contacts = contacts;
        this.context = context;
        this.numbers = numbers;
        this.tts = tts;
    }

    @NonNull
    @Override
    public contactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact,parent,false);
        contactViewHolder contactViewHolder = new contactViewHolder(view,contacts,context,numbers,tts);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull contactViewHolder holder, int position) {
        for(String s: contacts)
        {
            Log.i("Contacts",s);
        }
        Log.i("break",contacts.size()+"---------------------------------------------------------------------");

        String s = contacts.get(position);
        holder.textView.setText(s);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setFilter(ArrayList<String> newList){
        contacts.clear();
        contacts.addAll(newList);

        notifyDataSetChanged();
    }

    public static class contactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       // textospeech tts;
        TextView textView;
        ArrayList<String> contacts;
        ArrayList<String> numbers;
        Context context;
        private static final  int REQUEST_CALL =1;

        public contactViewHolder(@NonNull View itemView, ArrayList<String> contacts,Context context,ArrayList<String> numbers,textospeech tts) {
            super(itemView);
            textView =itemView.findViewById(R.id.tcontact);
            this.contacts = contacts;
            this.context = context;
            this.numbers = numbers;
           // this.tts = tts;


            itemView.setOnClickListener(this);

        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {

           // Toast.makeText(context,contacts.get(getAdapterPosition()).split("->")[1].trim(),Toast.LENGTH_SHORT).show();

            final String num =contacts.get(getAdapterPosition()).split("\n")[1].trim();
            //tts.Speak("Calling "+contacts.get(getAdapterPosition()).split("->")[1].trim());

           // c.tts= new textospeech(context,"",false);
            Call.tts.Speak("Calling "+contacts.get(getAdapterPosition()).split("\n")[0].trim());


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    makecall(num);

              }
            },1200);

            //makecall(num);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void makecall(String n) {
            String n1 = n.trim();


            String dial = "tel:" + n1;

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 2);}

            context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));

        }







    }
}
