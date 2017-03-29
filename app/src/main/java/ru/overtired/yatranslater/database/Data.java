package ru.overtired.yatranslater.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.overtired.yatranslater.structure.Language;
import ru.overtired.yatranslater.structure.Translation;
import ru.overtired.yatranslater.database.DataBaseScheme.DirectionsTable;
import ru.overtired.yatranslater.database.DataBaseScheme.HistoryTable;
import ru.overtired.yatranslater.database.DataBaseScheme.LanguagesTable;

//Этот класс - синглетон. Нужен для общего доступа к языкам и истории перевода из разных фрагментов

public class Data
{
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private static Data sData;

    public static Data get(Context context)
    {
        if (sData == null)
        {
            sData = new Data(context);
        }
        return sData;
    }

    private Data(Context context)
    {
        mContext = context.getApplicationContext();
        mDatabase = new DataBaseHelper(mContext).getWritableDatabase();
    }

    public List<Language> getLanguages()
    {
        List<Language> languages = new ArrayList<>();

        LanguageCursorWrapper cursor = queryLanguages();
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
                queryHistory(HistoryTable.Cols.IS_FAVORITE + "=1");
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

        values.put(HistoryTable.Cols.UUID, translation.getId().toString());
        values.put(HistoryTable.Cols.LANG_FROM, translation.getLangFrom());
        values.put(HistoryTable.Cols.LANG_TO, translation.getLangTo());
        values.put(HistoryTable.Cols.TEXT_FROM, translation.getTextFrom());
        values.put(HistoryTable.Cols.TEXT_TO, translation.getTextTo());
        values.put(HistoryTable.Cols.IS_FAVORITE, translation.isFavorite() ? 1 : 0);
        values.put(HistoryTable.Cols.IS_IN_HISTORY, translation.isInHistory() ? 1 : 0);

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
                        HistoryTable.Cols.LANG_FROM + "=\"" + translation.getLangFrom() + "\" and " +
                        HistoryTable.Cols.LANG_TO + "=\"" + translation.getLangTo() + "\" and " +
                        HistoryTable.Cols.TEXT_FROM + "=\"" + translation.getTextFrom() + "\""
                , null);
        mDatabase.delete(HistoryTable.NAME,
                HistoryTable.Cols.IS_IN_HISTORY + "=0 and " +
                        HistoryTable.Cols.IS_FAVORITE+"=0 and "+
                        HistoryTable.Cols.LANG_FROM + "=\"" + translation.getLangFrom() + "\" and " +
                        HistoryTable.Cols.LANG_TO + "=\"" + translation.getLangTo() + "\" and " +
                        HistoryTable.Cols.TEXT_FROM + "=\"" + translation.getTextFrom() + "\""
                , null);
    }

    public void removeFromFavorites(Translation translation)
    {
        ContentValues values = new ContentValues();
        values.put(HistoryTable.Cols.IS_FAVORITE, 0);
        mDatabase.update(HistoryTable.NAME, values,
                HistoryTable.Cols.IS_FAVORITE + "=1 and " +
                        HistoryTable.Cols.LANG_FROM + "=\"" + translation.getLangFrom() + "\" and " +
                        HistoryTable.Cols.LANG_TO + "=\"" + translation.getLangTo() + "\" and " +
                        HistoryTable.Cols.TEXT_FROM + "=\"" + translation.getTextFrom() + "\""
                , null);
        mDatabase.delete(HistoryTable.NAME,
                HistoryTable.Cols.IS_IN_HISTORY + "=0 and " +
                        HistoryTable.Cols.IS_FAVORITE+"=0 and "+
                        HistoryTable.Cols.LANG_FROM + "=\"" + translation.getLangFrom() + "\" and " +
                        HistoryTable.Cols.LANG_TO + "=\"" + translation.getLangTo() + "\" and " +
                        HistoryTable.Cols.TEXT_FROM + "=\"" + translation.getTextFrom() + "\""
                , null);
    }

    public void removeTranslation(Translation translation)
    {
        mDatabase.delete(HistoryTable.NAME,
                HistoryTable.Cols.LANG_FROM + "=\"" + translation.getLangFrom() + "\" and " +
                        HistoryTable.Cols.LANG_TO + "=\"" + translation.getLangTo() + "\" and " +
                        HistoryTable.Cols.TEXT_FROM + "=\"" + translation.getTextFrom() + "\""
                , null);
    }

    public void clearFavorites()
    {
        mDatabase.delete(HistoryTable.NAME, HistoryTable.Cols.IS_FAVORITE + "=1 and " +
                HistoryTable.Cols.IS_IN_HISTORY + "=0", null);

        ContentValues values = new ContentValues();
        values.put(HistoryTable.Cols.IS_FAVORITE, 0);
        mDatabase.update(HistoryTable.NAME, values, HistoryTable.Cols.IS_FAVORITE + "=1", null);
    }

    public void clearHistory()
    {
        mDatabase.delete(HistoryTable.NAME, HistoryTable.Cols.IS_IN_HISTORY + "=1 and " +
                HistoryTable.Cols.IS_FAVORITE + "=0", null);
        ContentValues values = new ContentValues();
        values.put(HistoryTable.Cols.IS_IN_HISTORY, 0);
        mDatabase.update(HistoryTable.NAME, values, HistoryTable.Cols.IS_IN_HISTORY + "=1", null);
    }

    private LanguageCursorWrapper queryLanguages()
    {
        Cursor cursor = mDatabase.query(
                LanguagesTable.NAME,
                null,
                null,
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
        mDatabase.update(HistoryTable.NAME, values, HistoryTable.Cols.UUID + "=\"" + translation.getId().toString() + "\"", null);
    }

    public Translation getTranslation(String id)
    {
        TranslationCursorWrapper cursorWrapper =
                queryHistory(HistoryTable.Cols.UUID + "=\"" + id + "\"");
        try
        {
            cursorWrapper.moveToFirst();
            return cursorWrapper.getTranslation();
        }
        catch (Exception e)
        {
            Log.d("Exception: ", e.getMessage());
        }
        return null;
    }

    public boolean hasTranslation(Translation translation)
    {
        TranslationCursorWrapper cursorWrapper =
                queryHistory(HistoryTable.Cols.TEXT_FROM + "=\"" + translation.getTextFrom() + "\" and " +
                        HistoryTable.Cols.LANG_FROM + "=\"" + translation.getLangFrom() + "\" and " +
                        HistoryTable.Cols.LANG_TO + "=\"" + translation.getLangTo() + "\"");
        return cursorWrapper.getCount() > 0;
    }

    public boolean hasTranslation(String id)
    {
        TranslationCursorWrapper cursorWrapper =
                queryHistory(HistoryTable.Cols.UUID + "=\"" + id + "\"");
        return cursorWrapper.getCount() > 0;
    }

    public List<Translation> getFindHistory(String text)
    {
        List<Translation> translations = new ArrayList<>();

        TranslationCursorWrapper cursorWrapper =
                queryHistory(HistoryTable.Cols.IS_IN_HISTORY + "=1 and (" +
                        HistoryTable.Cols.TEXT_FROM + " like \"%" + text + "%\" or " +
                        HistoryTable.Cols.TEXT_TO + " like\"%" + text + "%\")");

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
                queryHistory(HistoryTable.Cols.IS_FAVORITE + "=1 and (" +
                        HistoryTable.Cols.TEXT_FROM + " like \"%" + text + "%\" or " +
                        HistoryTable.Cols.TEXT_TO + " like\"%" + text + "%\")");

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


}
