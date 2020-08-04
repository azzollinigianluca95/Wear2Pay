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
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
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
import java.util.Collections;
import java.util.StringJoiner;


public class List_Transactions_4 extends WearableActivity {
    ListView TransactionList;
    TextView title;
    private ImageButton annulla2;

    String urlAddress = "http://a807bc61.ngrok.io/SW_retrieve_transactions_by_userID.php";
    String user_id;
    private static DecimalFormat REAL_FORMATTER = new DecimalFormat("0.00"); //Formattazione Reale (double -> String)


    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_transactions4);

        user_id = getIntent().getStringExtra("user_id");
        Sender s = new Sender(List_Transactions_4.this, urlAddress, user_id);
        s.execute();

        //Set Text
        title = (TextView) findViewById(R.id.TransactionList); //associo all'oggetto presentation_text l'id del testo
        TransactionList = (ListView) findViewById(R.id.listview);

        //Set Animation
        Animation a1 = AnimationUtils.loadAnimation( List_Transactions_4.this, R.anim.fadein_stop); //Genero animazione
        title.startAnimation(a1);
        TransactionList.startAnimation(a1);

        annulla2 = (ImageButton) findViewById(R.id.annulla);
        annulla2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();
            }
        });
    }

    public void openActivity2 () {
        Intent intent = new Intent(List_Transactions_4.this, Main_Activity_2.class);
        intent.putExtra("user_id",user_id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }


    class Transaction {
        int product_id;
        double cost;
        String created_at;
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
        int n=0;
            if(s !=null)
            {
                ArrayList<String> arrayList = new ArrayList<>();

                s = s.replace("[", "");
                s = s.replace("]", "");
                s = s.replace("},{", "};{");

                String[] words=s.split(";");//splits the string based on whitespace
                for(String w:words){

                    Gson gson = new Gson();
                    Transaction t = gson.fromJson(w, Transaction.class);

                    StringJoiner joiner = new StringJoiner(" - ");
                    StringJoiner joiner2 = new StringJoiner("");
                    joiner2.add(String.valueOf(REAL_FORMATTER.format(t.cost))).add("€");

                    joiner.add(String.valueOf(t.product_id)).add( joiner2.toString() ).add(String.valueOf(t.created_at));
                    n+=1;
                    String finalstring = joiner.toString();
                    Log.d("element", finalstring);

                    arrayList.add( finalstring );
                }
                Collections.reverse(arrayList);
                Log.d("SENDER arraylist", arrayList.toString() );
                if (n>5){n=5;} //Max 5 Transactions to visualize
                ArrayList<String> in = new ArrayList<>();
                for(int i = 0; i < (n); i++)
                    in.add(arrayList.get(i));


                TransactionList=(ListView)findViewById(R.id.listview);
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(List_Transactions_4.this, R.layout.custom_textview,in);
                TransactionList.setAdapter(arrayAdapter);

                return ;
            }
            else
            {
                Toast.makeText(c,"Transazioni \n non visualizzabili. \n Connettersi alla rete", Toast.LENGTH_LONG).show();
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
    public void onDestroy() {

        super.onDestroy();

    }

}


