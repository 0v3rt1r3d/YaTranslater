//refactored
package ru.overtired.yatranslater.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "database.db";

    public DataBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + DataBaseScheme.LanguagesTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                DataBaseScheme.LanguagesTable.Cols.FULL_NAME + ", " +
                DataBaseScheme.LanguagesTable.Cols.SHORT_NAME + ")");

        db.execSQL("create table " + DataBaseScheme.HistoryTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                DataBaseScheme.HistoryTable.Cols.LANG_FROM + ", " +
                DataBaseScheme.HistoryTable.Cols.LANG_TO + ", " +
                DataBaseScheme.HistoryTable.Cols.TEXT_FROM + ", " +
                DataBaseScheme.HistoryTable.Cols.TEXT_TO + ", " +
                DataBaseScheme.HistoryTable.Cols.IS_IN_HISTORY+", "+
                DataBaseScheme.HistoryTable.Cols.IS_IN_DICTIONARY+", "+
                DataBaseScheme.HistoryTable.Cols.IS_FAVORITE + ")");

        db.execSQL("create table " + DataBaseScheme.DirectionsTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                DataBaseScheme.DirectionsTable.Cols.DIRECTION+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}