package com.example.Vending_Machine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class Start_Activity_0 extends Activity {
    TextView title;
    ImageView logo;
    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.start_activity_0);
        title = (TextView) findViewById(R.id.title); //associo all'oggetto presentation_text l'id del testo
        logo = (ImageView) findViewById(R.id.logo);

        Animation a = AnimationUtils.loadAnimation( Start_Activity_0.this, R.anim.lefttoright); //Genero animazione
        title.startAnimation(a);
        Animation b = AnimationUtils.loadAnimation( Start_Activity_0.this, R.anim.lefttoright); //Genero animazione
        logo.startAnimation(b);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent1 = new Intent(Start_Activity_0.this, Main_Activity_1.class);
                startActivity(intent1);
                finish();
            }
        },3000);
    }
}
