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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.database.Downloader;
import ru.overtired.yatranslater.database.PreferencesScheme;
import ru.overtired.yatranslater.structure.Language;
import ru.overtired.yatranslater.database.Singleton;

//Эта активность загружает языки с сервера и сохраняет их в базе данных
//Языки загружает либо на русском, либо на английском в зависимости от конкретной локали

public class SplashActivity extends AppCompatActivity
{
    private String mCurrentLocale;
    private LanguagesDownloader mLanguagesDownloader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final Context context = SplashActivity.this;

        boolean isFirstStart = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PreferencesScheme.PREF_FIRST_START, true);

        String locale = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PreferencesScheme.PREF_LOCALE, "");

        mCurrentLocale = Locale.getDefault().toString().substring(0, 2);

//        Перезагружаются языки, если другая локаль или первый запуск
        if (isFirstStart || !mCurrentLocale.equals(locale))
        {
            PreferenceManager.getDefaultSharedPreferences(SplashActivity.this)
                    .edit()
                    .putBoolean(PreferencesScheme.PREF_FIRST_START, false)
                    .putString(PreferencesScheme.PREF_LOCALE, mCurrentLocale)
                    .apply();
            tryToDownloadLanguages();
        }
        else
        {
            startMainActivity();
        }
    }

    private class LanguagesDownloader extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            Downloader downloader = new Downloader();
            return downloader.getLanguagesResponse(params[0]);
        }

        @Override
        protected void onPostExecute(String jsonResponse)
        {
            List<Language> languages = Downloader.getLanguages(SplashActivity.this, jsonResponse);

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
                Singleton.get(SplashActivity.this).addLanguage(languages.get(i));
            }

            List<String> directions = Downloader.getDirections(jsonResponse);

            for (int i = 0; i < directions.size(); i++)
            {
                Singleton.get(SplashActivity.this).addDirection(directions.get(i));
            }

            startMainActivity();
        }
    }

    private void startMainActivity()
    {
        Intent intent = MainActivity.newIntent(SplashActivity.this);
        startActivity(intent);
        finish();
    }

    private void tryToDownloadLanguages()
    {
        if (Downloader.hasInternetConnection(SplashActivity.this))
        {
            Singleton.get(SplashActivity.this).removeAllLanguages();
            if(mLanguagesDownloader!=null)
            {
                mLanguagesDownloader.cancel(true);
            }
            mLanguagesDownloader = new LanguagesDownloader();
            mLanguagesDownloader.execute(mCurrentLocale);
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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(mLanguagesDownloader!=null)
        {
            mLanguagesDownloader.cancel(true);
        }
    }
}