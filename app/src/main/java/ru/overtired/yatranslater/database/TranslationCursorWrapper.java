package ru.overtired.yatranslater.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import ru.overtired.yatranslater.structure.Translation;

/**
 * Created by overtired on 17.03.17.
 */

public class TranslationCursorWrapper extends CursorWrapper
{
    public TranslationCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    public Translation getTranslation()
    {
        String uuid = getString(getColumnIndex(DataBaseScheme.HistoryTable.Cols.UUID));
        String shortFrom = getString(getColumnIndex(DataBaseScheme.HistoryTable.Cols.LANG_FROM));
        String shortTo = getString(getColumnIndex(DataBaseScheme.HistoryTable.Cols.LANG_TO));
        String wordFrom = getString(getColumnIndex(DataBaseScheme.HistoryTable.Cols.TEXT_FROM));
        String wordTo = getString(getColumnIndex(DataBaseScheme.HistoryTable.Cols.TEXT_TO));
        boolean isFavorite = getInt(getColumnIndex(DataBaseScheme.HistoryTable.Cols.IS_FAVORITE))!=0;

        return new Translation(shortFrom,shortTo,wordFrom,wordTo,uuid,isFavorite);
    }
}
