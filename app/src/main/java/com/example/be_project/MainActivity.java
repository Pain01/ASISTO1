package com.example.be_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo =findViewById(R.id.logo1);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.bubble_effect);
        bubbleInterpolator interpolator = new bubbleInterpolator(0.1,5);
        animation.setInterpolator(interpolator);
        logo.startAnimation(animation);

        new CountDownTimer(950,50){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                logo.animate().scaleXBy(100);
                logo.animate().scaleYBy(100);
                logo.animate().alpha(0).setDuration(1500);
            }
        }.start();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i =new Intent(getBaseContext(),home.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);


                finish();
            }
        },2201);

    }

}
