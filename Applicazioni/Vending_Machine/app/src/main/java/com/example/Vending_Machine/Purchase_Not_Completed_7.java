package com.example.Vending_Machine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class Purchase_Not_Completed_7 extends Activity {

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.purchase_not_completed_7);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Intent intent1 = new Intent(Purchase_Not_Completed_7.this, Main_Activity_1.class);
                startActivity(intent1);
                finish();
            }
        },10000);
    }
}
