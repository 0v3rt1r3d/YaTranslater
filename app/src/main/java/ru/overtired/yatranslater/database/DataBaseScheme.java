package ru.overtired.yatranslater.database;

import android.hardware.camera2.params.ColorSpaceTransform;

/**
 * Created by overtired on 17.03.17.
 */

public class DataBaseScheme
{
    public static final class LanguageTable
    {
        public static final String NAME = "language_table";

        public static final class Cols
        {
            public static final String SHORT_NAME = "short_name";
            public static final String FULL_NAME = "full_name";
        }
    }

    public static final class HistoryTable
    {
        public static final String NAME = "history_table";

        public static final class Cols
        {
            public static final String SHORT_FROM = "short_from";
            public static final String SHORT_TO = "short_to";
            public static final String WORD_FROM = "word_from";
            public static final String WORD_TO = "word_to";
            public static final String IS_FAVORITE = "is_favorite";
        }
    }
}
