package com.example.wear2pay;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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
import java.util.ArrayList;


public class Login_1 extends WearableActivity {
    EditText UsernameEt, PasswordEt;
    String urlAddress = "http://a807bc61.ngrok.io/login.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_1);
        UsernameEt = (EditText)findViewById(R.id.etUserName);
        PasswordEt = (EditText)findViewById(R.id.etPassword);

        if(SaveSharedPreference.getUserName(Login_1.this).length() == 0)
        {
            //Login & psw non inizializzate
        }
        else
        {
            UsernameEt.setText(SaveSharedPreference.getUserName(Login_1.this));
            PasswordEt.setText(SaveSharedPreference.getUserPassw(Login_1.this));

        }
    }

    public void OnLogin(View view) {
        String username = UsernameEt.getText().toString();
        String password = PasswordEt.getText().toString();

        SaveSharedPreference.setUser(Login_1.this, username, password);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Sender s = new Sender(Login_1.this, urlAddress, username, password);
        s.execute();
        Log.d("LOGIN", "calling backend...");
        Toast.makeText(this,"Login in corso", Toast.LENGTH_LONG).show();

    }

    class User
    {
        String id;
    }

    public class Sender extends AsyncTask<Void, Void, String>
    {

        Context c;
        String urlAddress;
        String user;
        String password;

        public Sender (Context c, String urlAddress, String...strings){
            this.c = c;
            this.urlAddress = urlAddress;
            user=strings[0];
            password=strings[1];
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
                if(s.equals("{\"success\":0,\"message\":\"no data\"}"))
                {
                    Toast.makeText(c,"Credenziali errate!\nRiprovare", Toast.LENGTH_LONG).show();
                }
                else
                {
                    ArrayList<String> arrayList = new ArrayList<>();
                    Log.d("OLD S", s);

                    s = s.replace("[", "");
                    s = s.replace("},{", "};{");
                    s = s.replace("]", "");

                    Log.d("NEW S", s);

                    Gson gson = new Gson();
                    Login_1.User t = gson.fromJson(s, Login_1.User.class);

                    Log.d("Final User Id",t.id);
                    Intent intent = new Intent(Login_1.this, Main_Activity_2.class);
                    intent.putExtra("user_id", t.id); //passo variabile alla successiva activity
                    startActivity(intent);
                    //finish();
                    return ;
                }

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
                bw.write(new DataPackager_user_passw(user,password).packData());

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

