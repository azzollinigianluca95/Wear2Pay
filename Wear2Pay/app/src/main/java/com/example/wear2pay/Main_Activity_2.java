package com.example.wear2pay;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Wear2Pay.R;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Main_Activity_2 extends WearableActivity {
    //Backend Call
    private String urlAddress = "http://a807bc61.ngrok.io/SW_retrieve_user_by_userID.php";
    private String nome_utente;
    private String user_id;

    private TextView mTextView;
    Button shopping_cart;
    Button list_transaction;
    TextView presentationtext; //Dichiaro oggetto di tipo testo
    TextView walletvalue;
    private static DecimalFormat REAL_FORMATTER = new DecimalFormat("0.00"); //Formattazione Reale (double -> String)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user_id = getIntent().getStringExtra("user_id");
        Sender s = new Sender(Main_Activity_2.this, urlAddress, user_id);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.main_activity_2);
        nome_utente = getIntent().getStringExtra("user_name");

        //Set Text
        presentationtext = (TextView) findViewById(R.id.presentation_text); //associo all'oggetto presentation_text l'id del testo
        walletvalue = (TextView) findViewById(R.id.wallet_value);


        //Set Animation
        Animation a1 = AnimationUtils.loadAnimation( Main_Activity_2.this, R.anim.fadein_stop); //Genero animazione
        presentationtext.startAnimation(a1);
        walletvalue.startAnimation(a1);

        shopping_cart = (Button) findViewById(R.id.shopping_cart);
        list_transaction = (Button) findViewById(R.id.list_transaction);

        shopping_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMain_Activity2();
                //Toast.makeText(Activity2.this, "si", Toast.LENGTH_SHORT).show(); //CREATE AND SHOW A TOAST
            }
        });

        list_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity3b();
                //Toast.makeText(Activity2.this, "connessione rifiutata", Toast.LENGTH_SHORT).show(); //CREATE AND SHOW A TOAST
            }
        });
    }

    @Override
    protected void onDestroy() { super.onDestroy(); }

    public void openMain_Activity2() {
            Intent intent = new Intent(Main_Activity_2.this, Select_Vendors_3.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra("user_id", user_id); //passo variabile alla successiva activity
            startActivity(intent);
            finish();
        }

    public void openActivity3b () {
        Intent intent = new Intent(Main_Activity_2.this, List_Transactions_4.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
        finish();
    }


    class User {
        String name;
        String surname;
        double amount;
    }

    //Function to move in another Java File in future update
    public class Sender extends AsyncTask<Void, Void, String> {

        Context c;
        String urlAddress;
        String user;

        public Sender (Context c, String urlAddress, String...strings){
            this.c = c;
            this.urlAddress = urlAddress;
            user=strings[0];

            execute();
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
                Main_Activity_2.User t = gson.fromJson(s, Main_Activity_2.User.class);

                presentationtext.setText("Ciao\n"+ t.name + " " + t.surname + "\nIl tuo credito residuo è:" );
                presentationtext.setTextSize(17);
                walletvalue.setText(REAL_FORMATTER.format(t.amount) + '€');
                walletvalue.setTextSize(40);

                return ;
            }
            else
            {
                Log.d("TOAST EX","EXCEPTION");
                Toast.makeText(c,"Credenziali \n non recuperate \n Connettersi alla rete", Toast.LENGTH_LONG).show();
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
