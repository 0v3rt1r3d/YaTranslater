package ru.overtired.yatranslater.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import ru.overtired.yatranslater.Language;

/**
 * Created by overtired on 17.03.17.
 */

public class LanguagesCursorWrapper extends CursorWrapper
{
    public LanguagesCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    public Language getLanguage()
    {
        String shortName = getString(getColumnIndex(DataBaseScheme.LanguageTable.Cols.SHORT_NAME));
        String fullName = getString(getColumnIndex(DataBaseScheme.LanguageTable.Cols.FULL_NAME));

        return new Language(fullName,shortName);
    }
}
