package ru.overtired.yatranslater.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import ru.overtired.yatranslater.Language;
import ru.overtired.yatranslater.Translater;
import ru.overtired.yatranslater.Translation;
import ru.overtired.yatranslater.database.DataBaseScheme.HistoryTable;
import ru.overtired.yatranslater.database.DataBaseScheme.LanguageTable;

/**
 * Created by overtired on 17.03.17.
 */

public class Data
{
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private static Data sData;

    public static Data get(Context context)
    {
        if(sData == null)
        {
            sData = new Data(context);
        }
        return sData;
    }

    private Data(Context context)
    {
        mContext = context.getApplicationContext();
        mDatabase = new DataBaseHelper(mContext).getWritableDatabase();
//        Не так, но как-то нужно заполнять базу с языками, и не всегда
//        new LanguageTaker().execute("ru");
    }

    public List<Language> getLanguages()
    {
        List<Language> languages = new ArrayList<>();

        LanguageCursorWrapper cursor = queryLanguages();
        try
        {
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                languages.add(cursor.getLanguage());
                cursor.moveToNext();
            }
        }
        finally
        {
            cursor.close();
        }

        return languages;
    }

    public void removeAllLanguages()
    {
        mDatabase.delete(LanguageTable.NAME, null, null);
    }

    public void addLanguage(Language language)
    {
        ContentValues values = getLanguageContentValues(language);
        mDatabase.insert(LanguageTable.NAME,null,values);
    }

    public void addTranslationToHistory(Translation translation)
    {
        ContentValues values = getTranslationContentValues(translation);
        mDatabase.insert(HistoryTable.NAME,null,values);
    }

    private ContentValues getLanguageContentValues(Language language)
    {
        ContentValues values = new ContentValues();
        values.put(LanguageTable.Cols.FULL_NAME,language.getFullName());
        values.put(LanguageTable.Cols.SHORT_NAME,language.getShortName());

        return values;
    }

    private ContentValues getTranslationContentValues(Translation translation)
    {
        ContentValues values = new ContentValues();
        values.put(HistoryTable.Cols.SHORT_FROM,translation.getShortFrom());
        values.put(HistoryTable.Cols.SHORT_TO,translation.getShortTo());
        values.put(HistoryTable.Cols.WORD_FROM,translation.getWordFrom());
        values.put(HistoryTable.Cols.WORD_TO,translation.getWordTo());
        values.put(HistoryTable.Cols.IS_FAVORITE,translation.isFavorite() ? 1:0);

        return values;
    }

//    setFavorite() пока не думал, как это организовать
//    Видимо придется таки вводить UUID

    public void removeTranslationFromHistory(Translation translation)
    {

    }

    public void clearHistory()
    {

    }

    private LanguageCursorWrapper queryLanguages()
    {
        Cursor cursor = mDatabase.query(
                LanguageTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        return new LanguageCursorWrapper(cursor);
    }
}
