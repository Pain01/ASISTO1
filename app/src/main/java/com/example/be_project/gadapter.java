package com.example.be_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class gadapter extends BaseAdapter {
    private Context context;
    private final String[] names;

    public gadapter(Context context, String[] names) {
        this.context = context;
        this.names = names;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        if(convertView==null){
            v =new View(context);
            v = inflater.inflate(R.layout.home_mobile_actions,null);

            TextView textView= v.findViewById(R.id.textview);
            ImageView imageView =v.findViewById(R.id.imageView);
            String s =names[position];

            textView.setText(s);
            switch (s){
                case "CALL":
                    imageView.setImageResource(R.drawable.path);
                    break;
                case "MESSAGE":
                    imageView.setImageResource(R.drawable.m);
                    break;
                case "LOCATE":
                    imageView.setImageResource(R.drawable.l);
                    break;
                case "TIME/DATE":
                    imageView.setImageResource(R.drawable.ct);
                    break;
                case "SOS":
                    imageView.setImageResource(R.drawable.s);
                    break;
                case "CAMERA":
                    imageView.setImageResource(R.drawable.c);
                    break;
            }


        }else {
             v =convertView;
        }

        return v;
    }
}
