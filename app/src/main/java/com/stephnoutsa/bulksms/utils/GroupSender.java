package com.stephnoutsa.bulksms.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by stephnoutsa on 12/1/16.
 */

public class GroupSender extends AsyncTask<URL, String, String> {

    Context context;
    String phone;

    public GroupSender(Context context, String phone) {
        this.context = context;
        this.phone = phone;
    }

    @Override
    protected String doInBackground(URL... urls) {
        for (URL url : urls) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                int statusCode = urlConnection.getResponseCode();
                if (statusCode == 200) {
                    InputStream it = new BufferedInputStream(urlConnection.getInputStream());
                    InputStreamReader read = new InputStreamReader(it);
                    BufferedReader buff = new BufferedReader(read);
                    StringBuilder dta = new StringBuilder();
                    String chunks ;
                    while((chunks = buff.readLine()) != null)
                    {
                        dta.append(chunks);
                    }
                } else {
                    Toast.makeText(context, "Sending to " + phone + "failed. Try again later.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(context, "Message(s) sent", Toast.LENGTH_SHORT).show();
    }
}
