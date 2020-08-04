package com.example.Vending_Machine;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Select_Product_3 extends AppCompatActivity {

    ImageButton french_fries;
    ImageButton chocolate;
    ImageButton bottle_water;
    ImageButton coke;
    String user_id;
    private String urlAddress_read = "http://a807bc61.ngrok.io/SW_retrieve_user_by_userID.php";
    private double wallet;
    private boolean nopressed=false;
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this,"Recupero del portafoglio in corso\n Attendere prego", Toast.LENGTH_LONG).show();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.select_product_3);
        french_fries = (ImageButton) findViewById(R.id.french_fries);
        chocolate = (ImageButton) findViewById(R.id.chocolate);
        bottle_water = (ImageButton) findViewById(R.id.bottle_water);
        coke = (ImageButton) findViewById(R.id.coke);

        french_fries.setEnabled(false);
        chocolate.setEnabled(false);
        bottle_water.setEnabled(false);
        coke.setEnabled(false);

        user_id  = getIntent().getStringExtra("user_id");
        nopressed = getIntent().getBooleanExtra("nopressed",false);

        if(nopressed)
        {
            wallet= getIntent().getDoubleExtra("wallet",0);
            Log.e("nopressed","TRUE con wallet");
            Log.e("nopressed",String.valueOf(wallet) );
            french_fries.setEnabled(true);
            chocolate.setEnabled(true);
            bottle_water.setEnabled(true);
            coke.setEnabled(true);
        }
        else
        {
            Sender2 s2 = new Sender2(Select_Product_3.this, urlAddress_read, user_id); //Rertrive user wallet
            s2.execute();
            Log.e("nopressed","else con wallet");
            Log.e("nopressed",String.valueOf(wallet) );

        }



        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        french_fries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity3a1();
            }
        });


        chocolate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity3a2();
            }
        });


        bottle_water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity3a3();
            }
        });

        coke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity3a4();
            }
        });

        //Timeout di connessione
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Log.e("TIMEOUT","END");
                Intent intent = new Intent(Select_Product_3.this, Connection_2.class);
                startActivity(intent);
                finish();
            }
        },30000); //togliere uno zero

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
            timer.cancel();
            Intent intent = new Intent(Select_Product_3.this, Main_Activity_1.class);
            startActivity(intent);
            finish();

        return super.onOptionsItemSelected(item);
    }

    public void openActivity3a1 () {
        timer.cancel();
        Intent intent = new Intent(Select_Product_3.this, Buy_Product_4.class);
        intent.putExtra("user_id", user_id); //passo variabile alla successiva activity
        intent.putExtra("price", "0.99"); //passo variabile alla successiva activity
        intent.putExtra("product_id", "1");
        intent.putExtra("wallet",wallet);
        startActivity(intent);
        finish();
    }

    public void openActivity3a2 () {
        timer.cancel();
        Intent intent = new Intent(Select_Product_3.this, Buy_Product_4.class);
        intent.putExtra("user_id", user_id); //passo variabile alla successiva activity
        intent.putExtra("price", "1.99"); //passo variabile alla successiva activity
        intent.putExtra("product_id", "2");
        intent.putExtra("wallet",wallet);
        startActivity(intent);
        finish();
    }

    public void openActivity3a3 () {
        timer.cancel();
        Intent intent = new Intent(Select_Product_3.this, Buy_Product_4.class);
        intent.putExtra("user_id", user_id); //passo variabile alla successiva activity
        intent.putExtra("price", "0.40"); //passo variabile alla successiva activity
        intent.putExtra("product_id", "3");
        intent.putExtra("wallet",wallet);
        startActivity(intent);
        finish();
    }

    public void openActivity3a4 () {
        timer.cancel();
        Intent intent = new Intent(Select_Product_3.this, Buy_Product_4.class);
        intent.putExtra("user_id", user_id); //passo variabile alla successiva activity
        intent.putExtra("price", "2.00"); //passo variabile alla successiva activity
        intent.putExtra("product_id", "4");
        intent.putExtra("wallet",wallet);
        startActivity(intent);
        finish();
    }

    class User {
        String name;
        String surname;
        double amount;
    }

    //Function to move in another Java File in future update
    //Connection to retrive user's wallet
    public class Sender2 extends AsyncTask<Void, Void, String> {

        Context c;
        String urlAddress;
        String user;

        public Sender2 (Context c, String urlAddress, String...strings){
            this.c = c;
            this.urlAddress = urlAddress;
            user=strings[0];

            //execute();
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params){
            return this.send();
        }

        @Override
        protected void onPostExecute (String s){

            if(s !=null)
            {
                ArrayList<String> arrayList = new ArrayList<>();
                Log.d("OLD S", s);

                s = s.replace("[", "");
                s = s.replace("},{", "};{");
                s = s.replace("]", "");

                Log.d("NEW S", s);
                Gson gson = new Gson();
                Select_Product_3.User t = gson.fromJson(s, Select_Product_3.User.class);
                wallet = t.amount;
                Log.d("WALLET", String.valueOf(wallet));
                Toast.makeText(c,"Portafoglio recuperato! \nSelezionare prodotto", Toast.LENGTH_LONG).show();
                french_fries.setEnabled(true);
                chocolate.setEnabled(true);
                bottle_water.setEnabled(true);
                coke.setEnabled(true);

                return ;
            }
            else
            {
                Log.d("TOAST EX","EXCEPTION");
                Toast.makeText(c,"Portafoglio \n non recuperato \n Connettersi alla rete", Toast.LENGTH_LONG).show();
            }
        }

        private  String send()
        {
            //CONNECT
            HttpURLConnection con=Connector.connect(urlAddress);
            if(con==null)
            {
                return null;
            }

            try
            {
                OutputStream os=con.getOutputStream();
                //WRITE
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, Charset.defaultCharset()));
                bw.write(new DataPackager_userID(user).packData());

                //RELEASE RES
                bw.flush();
                bw.close();
                os.close();

                //HAS IT BEEN SUCCESSFUL?
                int responseCode=con.getResponseCode();
                if(responseCode==con.HTTP_OK)
                {
                    //GET RESPONSE
                    BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuffer response = new StringBuffer();

                    String line=null;

                    while((line=br.readLine()) !=null)
                    {
                        response.append(line);
                    }
                    //RELEASE RES
                    br.close();

                    return response.toString();
                }
                else
                {

                }
            }

            catch (IOException e)
            {
                e.printStackTrace();

            }

            return null;
        }
    }
}
