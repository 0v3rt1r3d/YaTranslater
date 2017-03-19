package ru.overtired.yatranslater;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import ru.overtired.yatranslater.database.Data;

/**
 * Эта активность загружает языки с сервера и сохраняет их в базе данных
 */

public class SplashActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        boolean hasInternetConnection = false;
        Context context = SplashActivity.this;

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

        if (hasInternetConnection)
        {
//            Data.get(SplashActivity.this).removeAllLanguages();
//            new LanguageTaker().execute("ru");
            startMainActivity(hasInternetConnection);
        }
        else
        {
            startMainActivity(hasInternetConnection);
        }
    }

    private class LanguageTaker extends AsyncTask<String, Void, List<Language>>
    {
        @Override
        protected List<Language> doInBackground(String... params)
        {
            Translater translater = new Translater();
            return translater.getLanguages(SplashActivity.this, params[0]);
        }

        @Override
        protected void onPostExecute(List<Language> languages)
        {
            for (int i = 0; i < languages.size(); i++)
            {
                Data.get(SplashActivity.this).addLanguage(languages.get(i));
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


    //Сюда можно вставить обновление языков
}
