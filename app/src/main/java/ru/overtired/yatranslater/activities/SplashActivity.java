package ru.overtired.yatranslater.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.overtired.yatranslater.structure.Language;
import ru.overtired.yatranslater.database.Translater;
import ru.overtired.yatranslater.database.Data;
import ru.overtired.yatranslater.structure.Translation;

/**
 * Эта активность загружает языки с сервера и сохраняет их в базе данных
 */

public class SplashActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Context context = SplashActivity.this;

        boolean shouldUpdateLanguages = PreferenceManager
                .getDefaultSharedPreferences(SplashActivity.this)
                .getBoolean("should_update_languages",true);

        boolean hasInternetConnection = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            hasInternetConnection = true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            hasInternetConnection = true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            hasInternetConnection = true;
        }

        if (shouldUpdateLanguages && hasInternetConnection)
        {
            Data.get(SplashActivity.this).removeAllLanguages();
            new LangAndDirTaker().execute("ru");
        }
        else
        {
            startMainActivity(hasInternetConnection);
        }
    }

    private class LangAndDirTaker extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            Translater translater = new Translater();
            return translater.getLangAndDirResponse("ru");
        }

        @Override
        protected void onPostExecute(String jsonResponse)
        {
            List<Language> languages = Translater.getLanguages(SplashActivity.this,jsonResponse);

            Collections.sort(languages, new Comparator<Language>()
            {
                @Override
                public int compare(Language o1, Language o2)
                {
                    return o1.getFullLang().compareToIgnoreCase(o2.getFullLang());
                }
            });

            for (int i = 0; i < languages.size(); i++)
            {
                Data.get(SplashActivity.this).addLanguage(languages.get(i));
                Log.d("String: ",languages.get(i).getFullLang());
            }

            List<String> directions = Translater.getDirections(SplashActivity.this,jsonResponse);

            for (int i = 0; i < directions.size(); i++)
            {
                Data.get(SplashActivity.this).addDirection(directions.get(i));
            }

            startMainActivity(true);
        }
    }

    private void startMainActivity(boolean hasInternet)
    {
        Intent intent = MainActivity.newIntent(SplashActivity.this,hasInternet);
        startActivity(intent);
        finish();
    }
}
