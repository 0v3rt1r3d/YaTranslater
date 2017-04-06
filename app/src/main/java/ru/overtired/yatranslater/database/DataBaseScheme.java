package ru.overtired.yatranslater.database;

//Схема базы данных

public class DataBaseScheme
{
    public static final class LanguagesTable
    {
        public static final String NAME = "languages_table";

        public static final class Cols
        {
            public static final String SHORT_NAME = "lang_short";
            public static final String FULL_NAME = "lang_full";
        }
    }

    public static final class HistoryTable
    {
        public static final String NAME = "history_table";

        public static final class Cols
        {
            public static final String LANG_FROM = "lang_from";
            public static final String LANG_TO = "lang_to";
            public static final String TEXT_FROM = "text_from";
            public static final String TEXT_TO = "text_to";
            public static final String IS_IN_FAVORITES = "is_in_favorites";
            public static final String IS_IN_HISTORY = "is_in_history";
            public static final String IS_IN_DICTIONARY = "is_in_dictionary";
        }
    }

    public static final class DirectionsTable
    {
        public static final String NAME = "directions_table";

        public static final class Cols
        {
            public static final String DIRECTION = "direction";
        }
    }
}
