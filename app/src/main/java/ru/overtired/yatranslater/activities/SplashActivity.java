package ru.overtired.yatranslater.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

import com.facebook.stetho.Stetho;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.database.PreferencesScheme;
import ru.overtired.yatranslater.structure.Language;
import ru.overtired.yatranslater.database.Translater;
import ru.overtired.yatranslater.database.Data;

/**
 * Эта активность загружает языки с сервера и сохраняет их в базе данных
 * Языки загружает либо на русском, либо на английском в зависимости от конкретной локали
 */

public class SplashActivity extends AppCompatActivity
{
    private String mCurrentLocale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(getApplication());
        final Context context = SplashActivity.this;

        boolean isFirstStart = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PreferencesScheme.PREF_FIRST_START, true);

        String locale = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PreferencesScheme.PREF_LOCALE, "");
        mCurrentLocale = Locale.getDefault().toString().substring(0, 2);

//        Перезагружает языки, если другая локаль или первый запуск
        if (isFirstStart || !mCurrentLocale.equals(locale))
        {
            tryToDownloadLanguages();
        }
        else
        {
            startMainActivity();
        }
    }

    private class LangAndDirTaker extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            Translater translater = new Translater();
            return translater.getLangAndDirResponse(params[0]);
        }

        @Override
        protected void onPostExecute(String jsonResponse)
        {
            List<Language> languages = Translater.getLanguages(SplashActivity.this, jsonResponse);

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
            }

            List<String> directions = Translater.getDirections(jsonResponse);

            for (int i = 0; i < directions.size(); i++)
            {
                Data.get(SplashActivity.this).addDirection(directions.get(i));
            }

            PreferenceManager.getDefaultSharedPreferences(SplashActivity.this)
                    .edit()
                    .putBoolean(PreferencesScheme.PREF_FIRST_START, false)
                    .putString(PreferencesScheme.PREF_LOCALE, mCurrentLocale)
                    .apply();

            startMainActivity();
        }
    }

    private void startMainActivity()
    {
        Intent intent = MainActivity.newIntent(SplashActivity.this);
        startActivity(intent);
        finish();
    }

    public static boolean hasInternetConnection(Context context)
    {
//        Проверяет наличие сети
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected() && wifiInfo.isAvailable())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected() && wifiInfo.isAvailable())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected() && wifiInfo.isAvailable())
        {
            return true;
        }
        return false;
    }

    private void tryToDownloadLanguages()
    {
        if (hasInternetConnection(SplashActivity.this))
        {
            Data.get(SplashActivity.this).removeAllLanguages();
            new LangAndDirTaker().execute(mCurrentLocale);
        }
        else
        {
            new AlertDialog.Builder(SplashActivity.this)
                    .setMessage(R.string.need_internet)
                    .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            tryToDownloadLanguages();
                        }
                    })
                    .setNegativeButton(R.string.close_app, new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            System.exit(0);
                        }
                    })
                    .show();
        }
    }
}