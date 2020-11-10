package com.iwantdraw.draw;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.preference.PreferenceManager;

import com.onesignal.OneSignal;
import com.scottyab.aescrypt.AESCrypt;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;

public class Check {
    private Context context;

    void check(Context context) {
        this.context = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        DownloadTask task = new DownloadTask();
        try {
            String result = "";
            result = task.execute(encr()).get();
            if (result.length() > 10){
                preferences.edit().putBoolean("isBot", false).apply();
                OneSignal.sendTag("nobot", "1");
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String encr(){
        String messageAfterDecrypt = null;
        try {
            messageAfterDecrypt = AESCrypt.decrypt("amayyy", "fPAh8n1BZ1da4I54pKY0vla0WZ0l1GQgWlQxABPHAc4=");
        } catch (GeneralSecurityException e) {
        }
        return messageAfterDecrypt;
    }

    private static class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL klek;
            String newnas = "";
            HttpURLConnection klekNmen = null;
            try {
                klek = new URL(strings[0]);
                klekNmen = (HttpURLConnection) klek.openConnection();
                newnas = klekNmen.getHeaderField("Location");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (klekNmen != null) {
                    klekNmen.disconnect();
                }
            }
            return newnas;
        }
    }
}
