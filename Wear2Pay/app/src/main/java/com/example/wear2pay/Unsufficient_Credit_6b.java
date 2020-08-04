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

public class Unsufficient_Credit_6b extends WearableActivity {

    Timer timer;
    TextView t ;
    ImageView money_off; //Dichiaro oggetto di tipo testo
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.unsufficient_credit_6b);

        user_id = getIntent().getStringExtra("user_id");
        money_off = (ImageView) findViewById(R.id.money_off);

        money_off = (ImageView) findViewById(R.id.money_off); //associo all'oggetto presentation_text l'id del testo

        Animation anim_money = AnimationUtils.loadAnimation( Unsufficient_Credit_6b.this, R.anim.fadein); //Genero animazione

        money_off.startAnimation(anim_money);
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent1 = new Intent(Unsufficient_Credit_6b.this, Main_Activity_2.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent1.putExtra("user_id",user_id);
                startActivity(intent1);
                finish();
            }
        },10000);
    }
    public void onDestroy() {

        super.onDestroy();

    }

}
