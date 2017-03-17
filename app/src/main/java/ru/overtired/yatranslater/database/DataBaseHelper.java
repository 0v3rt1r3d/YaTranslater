package ru.overtired.yatranslater.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by overtired on 17.03.17.
 */

public class DataBaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "database.db";
    private static final int VERSION = 1;

    public DataBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + DataBaseScheme.LanguageTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                DataBaseScheme.LanguageTable.Cols.FULL_NAME + ", " +
                DataBaseScheme.LanguageTable.Cols.SHORT_NAME + ")");

        db.execSQL("create table " + DataBaseScheme.HistoryTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                DataBaseScheme.HistoryTable.Cols.SHORT_FROM + ", " +
                DataBaseScheme.HistoryTable.Cols.SHORT_TO + ", " +
                DataBaseScheme.HistoryTable.Cols.WORD_FROM + ", " +
                DataBaseScheme.HistoryTable.Cols.WORD_TO + ", " +
                DataBaseScheme.HistoryTable.Cols.IS_FAVORITE + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
