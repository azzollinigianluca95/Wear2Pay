package com.example.wear2pay;


import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.Wear2Pay.R;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;


public class Purchase_Done_6a extends WearableActivity {

    private TextView string_price;
    Timer timer;
    TextView string_price1;

    private static DecimalFormat REAL_FORMATTER = new DecimalFormat("0.00"); //Formattazione Reale (double -> String)
    String user_id;
    double price;
    String price_s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        user_id = getIntent().getStringExtra("user_id");
        price_s = getIntent().getStringExtra("price");
        price = Double.valueOf(price_s);
        setContentView(R.layout.purchase_done_6a);
        string_price = (TextView) findViewById(R.id.purchase_completed1);

        string_price1 = (TextView) findViewById(R.id.purchase_completed1); //associo all'oggetto presentation_text l'id del testo

        string_price1.setText("- "+REAL_FORMATTER.format(price) + '€');



        Animation a = AnimationUtils.loadAnimation( Purchase_Done_6a.this, R.anim.zoomin); //Genero animazione
        Animation b = AnimationUtils.loadAnimation( Purchase_Done_6a.this, R.anim.fadein); //Genero animazione

        string_price1.startAnimation(b);
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent1 = new Intent(Purchase_Done_6a.this, Main_Activity_2.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent1.putExtra("user_id",user_id);
                startActivity(intent1);
                finish();
            }
        },10000);
    }


    public void onDestroy() { super.onDestroy(); }
}
