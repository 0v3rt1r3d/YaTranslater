package ru.overtired.yatranslater.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.LruCache;

import java.util.ArrayList;
import java.util.List;

import ru.overtired.yatranslater.structure.Language;
import ru.overtired.yatranslater.structure.Translation;
import ru.overtired.yatranslater.database.DataBaseScheme.DirectionsTable;
import ru.overtired.yatranslater.database.DataBaseScheme.HistoryTable;
import ru.overtired.yatranslater.database.DataBaseScheme.LanguagesTable;
import ru.overtired.yatranslater.structure.dictionary.Dictionary;

//Синглетон для доступа к базе данных и кэшу

public class Singleton
{
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private LruCache<String,Dictionary> mCache;
    int mCacheSize = 10*1024*1024; // 10 мб

    private static Singleton sSingleton;

    public static Singleton get(Context context)
    {
        if (sSingleton == null)
        {
            sSingleton = new Singleton(context);
        }
        return sSingleton;
    }

    private Singleton(Context context)
    {
        mContext = context.getApplicationContext();
        mDatabase = new DataBaseHelper(mContext).getWritableDatabase();
        mCache = new LruCache<>(mCacheSize);
    }

    public void putDictionaryInCache(Translation translation, Dictionary dictionary)
    {
        mCache.put(translation.getLangFrom()+translation.getLangTo()+
                        translation.getTextFrom().hashCode(),
                dictionary);
    }

    public Dictionary getDictionaryFromCache(Translation translation)
    {
        return mCache.get(translation.getLangFrom()+translation.getLangTo()+
                translation.getTextFrom().hashCode());
    }

    public List<Language> getLanguages()
    {
        List<Language> languages = new ArrayList<>();

        LanguageCursorWrapper cursor = queryLanguages(null);
        try
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
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

    public List<Translation> getHistory()
    {
        List<Translation> translations = new ArrayList<>();

        TranslationCursorWrapper cursor = queryHistory(HistoryTable.Cols.IS_IN_HISTORY + "=1");
        try
        {
            cursor.moveToLast();
            while (!cursor.isBeforeFirst())
            {
                translations.add(cursor.getTranslation());
                cursor.moveToPrevious();
            }
        }
        finally
        {
            cursor.close();
        }
        return translations;
    }

    public List<Translation> getFavorites()
    {
        List<Translation> translations = new ArrayList<>();

        TranslationCursorWrapper cursor =
                queryHistory(HistoryTable.Cols.IS_IN_FAVORITES + "=1");
        try
        {
            cursor.moveToLast();
            while (!cursor.isBeforeFirst())
            {
                translations.add(cursor.getTranslation());
                cursor.moveToPrevious();
            }
        }
        finally
        {
            cursor.close();
        }
        return translations;
    }

    public List<String> getDirections()
    {
        List<String> directions = new ArrayList<>();

        Cursor cursor = mDatabase.query(
                DirectionsTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        try
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                directions.add(cursor
                        .getString(cursor.getColumnIndex(DirectionsTable.Cols.DIRECTION)));
                cursor.moveToNext();
            }
        }
        finally
        {
            cursor.close();
        }
        return directions;
    }

    public void removeAllLanguages()
    {
        mDatabase.delete(LanguagesTable.NAME, null, null);
    }

    public void addLanguage(Language language)
    {
        ContentValues values = getLanguageContentValues(language);
        mDatabase.insert(LanguagesTable.NAME, null, values);
    }

    public void addTranslation(Translation translation)
    {
        if (hasTranslation(translation))
        {
            removeTranslation(translation);
        }
        ContentValues values = getTranslationContentValues(translation);
        mDatabase.insert(HistoryTable.NAME, null, values);
    }

    public void addDirection(String direction)
    {
        ContentValues values = getDirectionContentValues(direction);
        mDatabase.insert(DirectionsTable.NAME, null, values);
    }

    private ContentValues getLanguageContentValues(Language language)
    {
        ContentValues values = new ContentValues();

        values.put(LanguagesTable.Cols.FULL_NAME, language.getFullLang());
        values.put(LanguagesTable.Cols.SHORT_NAME, language.getShortLang());

        return values;
    }

    private ContentValues getTranslationContentValues(Translation translation)
    {
        ContentValues values = new ContentValues();

        values.put(HistoryTable.Cols.LANG_FROM, translation.getLangFrom());
        values.put(HistoryTable.Cols.LANG_TO, translation.getLangTo());
        values.put(HistoryTable.Cols.TEXT_FROM, translation.getTextFrom());
        values.put(HistoryTable.Cols.TEXT_TO, translation.getTextTo());
        values.put(HistoryTable.Cols.IS_IN_FAVORITES, translation.isFavorite() ? 1 : 0);
        values.put(HistoryTable.Cols.IS_IN_HISTORY, translation.isInHistory() ? 1 : 0);
        values.put(HistoryTable.Cols.IS_IN_DICTIONARY, translation.isInDictionary() ? 1 : 0);

        return values;
    }

    private ContentValues getDirectionContentValues(String direction)
    {
        ContentValues values = new ContentValues();

        values.put(DirectionsTable.Cols.DIRECTION, direction);

        return values;
    }

    public void removeFromHistory(Translation translation)
    {
        ContentValues values = new ContentValues();
        values.put(HistoryTable.Cols.IS_IN_HISTORY, 0);
        mDatabase.update(HistoryTable.NAME, values,
                HistoryTable.Cols.IS_IN_HISTORY + "=1 and " +
                        HistoryTable.Cols.LANG_FROM + "=\'" + translation.getLangFrom() + "\' and " +
                        HistoryTable.Cols.LANG_TO + "=\'" + translation.getLangTo() + "\' and " +
                        HistoryTable.Cols.TEXT_FROM + "=\'" + translation.getTextFrom() + "\'"
                , null);
        mDatabase.delete(HistoryTable.NAME,
                HistoryTable.Cols.IS_IN_HISTORY + "=0 and " +
                        HistoryTable.Cols.IS_IN_FAVORITES + "=0 and " +
                        HistoryTable.Cols.LANG_FROM + "=\'" + translation.getLangFrom() + "\' and " +
                        HistoryTable.Cols.LANG_TO + "=\'" + translation.getLangTo() + "\' and " +
                        HistoryTable.Cols.TEXT_FROM + "=\'" + translation.getTextFrom() + "\'"
                , null);
    }

    public void removeFromFavorites(Translation translation)
    {
        ContentValues values = new ContentValues();
        values.put(HistoryTable.Cols.IS_IN_FAVORITES, 0);
        mDatabase.update(HistoryTable.NAME, values,
                HistoryTable.Cols.IS_IN_FAVORITES + "=1 and " +
                        HistoryTable.Cols.LANG_FROM + "=\'" + translation.getLangFrom() + "\' and " +
                        HistoryTable.Cols.LANG_TO + "=\'" + translation.getLangTo() + "\' and " +
                        HistoryTable.Cols.TEXT_FROM + "=\'" + translation.getTextFrom() + "\'"
                , null);
        mDatabase.delete(HistoryTable.NAME,
                HistoryTable.Cols.IS_IN_HISTORY + "=0 and " +
                        HistoryTable.Cols.IS_IN_FAVORITES + "=0 and " +
                        HistoryTable.Cols.LANG_FROM + "=\'" + translation.getLangFrom() + "\' and " +
                        HistoryTable.Cols.LANG_TO + "=\'" + translation.getLangTo() + "\' and " +
                        HistoryTable.Cols.TEXT_FROM + "=\'" + translation.getTextFrom() + "'"
                , null);
    }

    public void removeTranslation(Translation translation)
    {
        mDatabase.delete(HistoryTable.NAME,
                HistoryTable.Cols.LANG_FROM + "=\'" + translation.getLangFrom() + "\' and " +
                        HistoryTable.Cols.LANG_TO + "=\'" + translation.getLangTo() + "\' and " +
                        HistoryTable.Cols.TEXT_FROM + "=\'" + translation.getTextFrom() + "\'"
                , null);
    }

    public void clearFavorites()
    {
        mDatabase.delete(HistoryTable.NAME, HistoryTable.Cols.IS_IN_FAVORITES + "=1 and " +
                HistoryTable.Cols.IS_IN_HISTORY + "=0", null);

        ContentValues values = new ContentValues();
        values.put(HistoryTable.Cols.IS_IN_FAVORITES, 0);
        mDatabase.update(HistoryTable.NAME, values, HistoryTable.Cols.IS_IN_FAVORITES + "=1", null);
    }

    public void clearHistory()
    {
        mDatabase.delete(HistoryTable.NAME, HistoryTable.Cols.IS_IN_HISTORY + "=1 and " +
                HistoryTable.Cols.IS_IN_FAVORITES + "=0", null);
        ContentValues values = new ContentValues();
        values.put(HistoryTable.Cols.IS_IN_HISTORY, 0);
        mDatabase.update(HistoryTable.NAME, values, HistoryTable.Cols.IS_IN_HISTORY + "=1", null);
    }

    private LanguageCursorWrapper queryLanguages(String clause)
    {
        Cursor cursor = mDatabase.query(
                LanguagesTable.NAME,
                null,
                clause,
                null,
                null,
                null,
                null
        );

        return new LanguageCursorWrapper(cursor);
    }

    private TranslationCursorWrapper queryHistory(String selectClause)
    {
        Cursor cursor = mDatabase.query(
                HistoryTable.NAME,
                null,
                selectClause,
                null,
                null,
                null,
                null
        );

        return new TranslationCursorWrapper(cursor);
    }

    public void updateTranslation(Translation translation)
    {
        ContentValues values = getTranslationContentValues(translation);
        mDatabase.update(HistoryTable.NAME, values,
                HistoryTable.Cols.LANG_FROM + "=\'" + translation.getLangFrom() + "\' and " +
                        HistoryTable.Cols.LANG_TO + "=\'" + translation.getLangTo() + "\' and " +
                        HistoryTable.Cols.TEXT_FROM + "=\'" + translation.getTextFrom() + "\'"
                , null);
    }

    public Translation getTranslation(Translation translation)
    {
        TranslationCursorWrapper cursorWrapper =
                queryHistory(HistoryTable.Cols.TEXT_FROM + "=\'" + translation.getTextFrom() + "\' and " +
                        HistoryTable.Cols.LANG_FROM + "=\'" + translation.getLangFrom() + "\' and " +
                        HistoryTable.Cols.LANG_TO + "=\'" + translation.getLangTo() + "\'");
        if (cursorWrapper.getCount() == 0)
        {
            return null;
        }
        cursorWrapper.moveToFirst();
        return cursorWrapper.getTranslation();
    }

    public boolean hasTranslation(Translation translation)
    {
        TranslationCursorWrapper cursorWrapper =
                queryHistory(HistoryTable.Cols.TEXT_FROM + "=\'" + translation.getTextFrom() + "\' and " +
                        HistoryTable.Cols.LANG_FROM + "=\'" + translation.getLangFrom() + "\' and " +
                        HistoryTable.Cols.LANG_TO + "=\'" + translation.getLangTo() + "\'");
        return cursorWrapper.getCount() > 0;
    }

    public List<Translation> getFindHistory(String text)
    {
        List<Translation> translations = new ArrayList<>();

        TranslationCursorWrapper cursorWrapper =
                queryHistory(HistoryTable.Cols.IS_IN_HISTORY + "=1 and (" +
                        HistoryTable.Cols.TEXT_FROM + " like \'%" + text + "%\' or " +
                        HistoryTable.Cols.TEXT_TO + " like\'%" + text + "%\')");

        try
        {
            cursorWrapper.moveToLast();
            while (!cursorWrapper.isBeforeFirst())
            {
                translations.add(cursorWrapper.getTranslation());
                cursorWrapper.moveToPrevious();
            }
        }
        catch (Exception e)
        {
            Log.d("Error", e.getMessage());
        }

        return translations;
    }

    public List<Translation> getFindFavorites(String text)
    {
        List<Translation> translations = new ArrayList<>();

        TranslationCursorWrapper cursorWrapper =
                queryHistory(HistoryTable.Cols.IS_IN_FAVORITES + "=1 and (" +
                        HistoryTable.Cols.TEXT_FROM + " like \'%" + text + "%\' or " +
                        HistoryTable.Cols.TEXT_TO + " like\'%" + text + "%\')");

        try
        {
            cursorWrapper.moveToLast();
            while (!cursorWrapper.isBeforeFirst())
            {
                translations.add(cursorWrapper.getTranslation());
                cursorWrapper.moveToPrevious();
            }
        }
        catch (Exception e)
        {
            Log.d("Error", e.getMessage());
        }

        return translations;
    }

    public boolean hasDirection(String direction)
    {
        List<String> directions = getDirections();
        return directions.contains(direction);
    }

    public boolean isTranslationFavorite(Translation translation)
    {
        if (!hasTranslation(translation))
        {
            return false;
        }
        TranslationCursorWrapper cursor = queryHistory(
                HistoryTable.Cols.LANG_FROM + "=\'" + translation.getLangFrom() + "\' and " +
                        HistoryTable.Cols.LANG_TO + "=\'" + translation.getLangTo() + "\' and " +
                        HistoryTable.Cols.TEXT_FROM + "=\'" + translation.getTextFrom() + "\'");

        cursor.moveToFirst();
        return cursor.getTranslation().isFavorite();
    }

    public String getFullLanguageByShort(String shortLang)
    {
        for (Language language : getLanguages())
        {
            if (language.getShortLang().equals(shortLang))
            {
                return language.getFullLang();
            }
        }
        return null;
    }
}
