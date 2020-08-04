package com.example.wear2pay;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.WindowManager;
import com.example.Wear2Pay.R;
import java.util.Timer;
import java.util.TimerTask;


public class Logo_0 extends WearableActivity {

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.logo_0);

        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                    Intent intent1 = new Intent(Logo_0.this, Login_1.class);
                    startActivity(intent1);
                    finish();


            }
        },5000);

    }
    public void onDestroy() { super.onDestroy(); }
}
