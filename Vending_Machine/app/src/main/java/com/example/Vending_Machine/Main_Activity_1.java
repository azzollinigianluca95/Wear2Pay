package com.example.Vending_Machine;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class Main_Activity_1 extends Activity {

    Button buy_product;
    Button info;
    private BluetoothLEServer server;
    TextView bt_name; //Dichiaro oggetto di tipo testo


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final BluetoothAdapter myBTAdapter = BluetoothAdapter.getDefaultAdapter();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_1);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        String sOldName = myBTAdapter.getName();
        bt_name = (TextView) findViewById(R.id.bt_name); //associo all'oggetto presentation_text l'id del testo
        bt_name.setText(sOldName);
        bt_name.setTextSize(18);

        buy_product = (Button) findViewById(R.id.buy_product);
        info = (Button) findViewById(R.id.info);


        buy_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_Activity_1.this, Connection_2.class);
                startActivity(intent);
                finish();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_Activity_1.this, Info_5.class);
                startActivity(intent);
                finish();
            }
        });



    }


}



