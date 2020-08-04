package com.example.wear2pay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

public class DataPackager_user_passw {
    String user_name;
    String password;

    public DataPackager_user_passw(String user_name, String password){
        this.user_name = user_name;
        this.password = password;

    }

    public String packData()
    {
        JSONObject jo= new JSONObject();
        StringBuffer sb=new StringBuffer();

        try
        {
            jo.put("user_name", user_name);
            jo.put("password", password);


            Boolean firstValue=true;
            Iterator it= jo.keys();

            do //extract keys
            {
                String key = it.next().toString();
                String value= jo.get(key).toString();

                if(firstValue)
                {
                    firstValue=false;
                }
                else
                {
                    sb.append("&");
                }

                sb.append(URLEncoder.encode(key, "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(value, "UTF-8"));

            }while (it.hasNext());

            return sb.toString();

        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
