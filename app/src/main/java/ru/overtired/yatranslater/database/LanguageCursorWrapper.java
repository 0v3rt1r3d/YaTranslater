package ru.overtired.yatranslater.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import ru.overtired.yatranslater.structure.Language;

/**
 * Created by overtired on 17.03.17.
 */

public class LanguageCursorWrapper extends CursorWrapper
{
    public LanguageCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    public Language getLanguage()
    {
        String shortName = getString(getColumnIndex(DataBaseScheme.LanguagesTable.Cols.SHORT_NAME));
        String fullName = getString(getColumnIndex(DataBaseScheme.LanguagesTable.Cols.FULL_NAME));

        return new Language(shortName,fullName);
    }
}
