package ru.overtired.yatranslater;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by overtired on 17.03.17.
 */

public class Data
{
    private List<Language> mLanguages;
    private Context mContext;

    private static Data ourInstance;

    public static Data getInstance(Context context)
    {
        if(ourInstance == null)
        {
            ourInstance = new Data(context);
        }
        return ourInstance;
    }

    private Data(Context context)
    {
        mContext = context;
        new LanguageTaker().execute("ru");
    }

    public List<Language> getLanguages()
    {
        return mLanguages;
    }

    private class LanguageTaker extends AsyncTask<String,Void,List<Language>>
    {
        @Override
        protected List<Language> doInBackground(String... params)
        {
            Translater translater = new Translater();
            return translater.getLanguages(mContext,params[0]);
        }

        @Override
        protected void onPostExecute(List<Language> languages)
        {
            //Нерационально беру элементы, нужен хешмап или что-то вроде того
            mLanguages = languages;
        }
    }
}
