package com.example.Vending_Machine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Connection_2 extends AppCompatActivity {
    private static final String TAG = Connection_2.class.getSimpleName();
    private BluetoothLEServer server;
    TextView connecting;
    ImageView image_connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_2);

        image_connection = (ImageView) findViewById(R.id.connection);
        connecting = (TextView) findViewById(R.id.connecting);

        Animation a = AnimationUtils.loadAnimation( Connection_2.this, R.anim.blink_anim); //Genero animazione
        image_connection.startAnimation(a);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        server = BluetoothLEServer.getIstance();
        server.BluetoothLEServer(Connection_2.this);
        server.UpdatePrice("2");

    }

        @Override
        protected void onStop()
        {
            super.onStop();
            Log.e("SERVER", "CLOSING");
            server.CloseServer();
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = new Intent(Connection_2.this, Main_Activity_1.class);
        startActivity(intent);
        finish();
        return super.onOptionsItemSelected(item);
    }


}
