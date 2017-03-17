package ru.overtired.yatranslater;

import android.content.Intent;
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

        if(true)
        {
            new LanguageTaker().execute("ru");
        }else
        {
            startMainActivity();
        }
    }

    private class LanguageTaker extends AsyncTask<String,Void,List<Language>>
    {
        @Override
        protected List<Language> doInBackground(String... params)
        {
            Translater translater = new Translater();
            return translater.getLanguages(SplashActivity.this,params[0]);
        }

        @Override
        protected void onPostExecute(List<Language> languages)
        {
            for(int i=0;i<languages.size();i++)
            {
                Data.get(SplashActivity.this).addLanguage(languages.get(i));
            }
            startMainActivity();
        }
    }

    private void startMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Сюда можно вставить обновление языков
}
