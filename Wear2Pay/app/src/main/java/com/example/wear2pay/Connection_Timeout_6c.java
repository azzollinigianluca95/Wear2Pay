package com.example.wear2pay;


import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Wear2Pay.R;

import java.util.Timer;
import java.util.TimerTask;


public class Connection_Timeout_6c extends WearableActivity {

    Timer timer;
    ImageView time_exceeded; //Dichiaro oggetto di tipo testo
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_timeout_6c);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        user_id = getIntent().getStringExtra("user_id");
        time_exceeded = (ImageView) findViewById(R.id.timer_off);
        time_exceeded = (ImageView) findViewById(R.id.timer_off); //associo all'oggetto presentation_text l'id del testo

        Animation a = AnimationUtils.loadAnimation( Connection_Timeout_6c.this, R.anim.fadein); //Genero animazione

        time_exceeded.startAnimation(a);
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent1 = new Intent(Connection_Timeout_6c.this, Main_Activity_2.class);
                intent1.putExtra("user_id",user_id);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent1);
                finish();
            }
        },10000);
    }

    public void onDestroy() {

        super.onDestroy();

    }

}
