package ru.overtired.yatranslater.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import ru.overtired.yatranslater.Translation;

/**
 * Created by overtired on 17.03.17.
 */

public class HistoryCursorWrapper extends CursorWrapper
{
    public HistoryCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    public Translation getTranslation()
    {
        String shortFrom = getString(getColumnIndex(DataBaseScheme.HistoryTable.Cols.SHORT_FROM));
        String shortTo = getString(getColumnIndex(DataBaseScheme.HistoryTable.Cols.SHORT_TO));
        String wordFrom = getString(getColumnIndex(DataBaseScheme.HistoryTable.Cols.WORD_FROM));
        String wordTo = getString(getColumnIndex(DataBaseScheme.HistoryTable.Cols.WORD_TO));
        boolean isFavorite = getInt(getColumnIndex(DataBaseScheme.HistoryTable.Cols.IS_FAVORITE))!=0;

        return new Translation(shortFrom,shortTo,wordFrom,wordTo,isFavorite);
        //как-то бул надо тоже считать
    }
}
