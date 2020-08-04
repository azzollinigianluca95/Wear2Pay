package com.example.Vending_Machine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class Buy_Product_4 extends Activity {

    private TextView euro_price;
    private TextView buy_prod;
    private String user_id;
    private String price;
    private Double price_negative;
    private String product_id;
    private Button yes;
    private Button no;
    private static DecimalFormat REAL_FORMATTER = new DecimalFormat("0.00"); //Formattazione Reale (double -> String)
    private String urlAddress_write = "http://a807bc61.ngrok.io/Write.php";
    private Timer timer;
    private double wallet;
    private double result;
    private BluetoothLEServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_product_4);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        server = BluetoothLEServer.getIstance();
        server.BluetoothLEServer(Buy_Product_4.this);
        server.UpdatePrice("-1.0");

        yes = (Button) findViewById(R.id.yes);
        no = (Button) findViewById(R.id.no);
        euro_price = (TextView) findViewById(R.id.euro_price);
        //buy_prod = (TextView) findViewById(R.id.buy_prod);

        user_id  = getIntent().getStringExtra("user_id");
        price  = getIntent().getStringExtra("price" );
        Log.d("PREZZO DEBUG !",price);
        product_id = getIntent().getStringExtra("product_id");
        wallet = getIntent().getDoubleExtra("wallet",wallet);

        euro_price.setText(REAL_FORMATTER.format(Double.valueOf(price) ) + '€');

        //YES BUY
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Buy_Product_4.this,"Acquisto in corso\nAttendere", Toast.LENGTH_LONG).show();
                yes.setEnabled(false);
                no.setEnabled(false);

                if ( wallet-Double.valueOf(price) >= 0)
                {
                    //Sufficient Credit
                    //Insert transaction in DB Backend
                    Log.d("CREDIT", "Sufficient");
                    price_negative = - Double.valueOf(price);
                    Sender s = new Sender(Buy_Product_4.this, urlAddress_write, user_id, String.valueOf(price_negative), product_id);
                    s.execute();
                    server.UpdatePrice(price);

                }
                else
                {
                    //UnSufficient Credit
                    Log.d("CREDIT", "Unsufficient");
                    result = -2;
                    server.UpdatePrice(String.valueOf(result));
                }

                //Waiting time for the transaction to occur
                timer=new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        openActivity3a1a();
                    }
                },25000);
            }
        });

        //NOT BUY
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //yes.setEnabled(false);
                //no.setEnabled(false);
                openActivity3a();
                server.CloseServer();
            }
        });

    }


    public void openActivity3a1a () {
        Log.d("FINISH", "END");
        Log.d("result",String.valueOf(result));

        if (result==-1 || result==-2)
        {
            server.CloseServer();
            //Unsuccessful purchase
            Intent intent = new Intent(Buy_Product_4.this, Purchase_Not_Completed_7.class);
            startActivity(intent);
            finish();
        }
        else
        {
            server.CloseServer();
            //Successful purchase
            Intent intent = new Intent(Buy_Product_4.this, Purchase_Done_6.class);
            startActivity(intent);
            finish();
        }
    }

    public void openActivity3a () {
        Intent intent = new Intent(Buy_Product_4.this, Main_Activity_1.class);
        intent.putExtra("nopressed",true);
        intent.putExtra("wallet",wallet);
        startActivity(intent);
        finish();
    }


    //Function to move in another Java File in future update
    //Connection to insert the new transaction
    public class Sender extends AsyncTask<Void, Void, String> {

        Context c;
        private String urlAddress;
        private String user_idTxt, costTxt, productTxt;
        private String user_id,cost,product_id;
        private final String trans_error = "Not saved successfully";
        private final String trans_ok = "Successfully Saved";


        public Sender (Context c, String urlAddress, String...strings){
            this.c = c;
            this.urlAddress = urlAddress;

            this.user_idTxt = strings[0];
            this.costTxt = strings[1];
            this.productTxt = strings[2];

            user_id = user_idTxt;
            cost= costTxt;
            product_id= productTxt;
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
            super.onPostExecute(s);

            if(s != null)
            {
                Log.e("s",s);
                user_idTxt = ("");
                costTxt = ("");
                productTxt = ("");

                if ( s.equals(trans_error) )
                {
                    Log.e("ERROR","TRANSAZIONE NON RIUSCITA");
                    result = -1;
                    server.UpdatePrice( String.valueOf(result));
                }

                else
                {
                    if (s.equals(trans_ok) )
                    {
                        Log.d("TRANSACTION", "OK");
                        result = Double.valueOf(price);
                    }
                    else
                    {
                        Log.e("ERRORE","CONNESSIONE NON RIUSCITA!");
                        result = -1;
                    }

                }



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

                bw.write(new DataPackager_insert_Trans(user_id, cost, product_id).packData());

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