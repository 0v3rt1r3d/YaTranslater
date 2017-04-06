package ru.overtired.yatranslater.structure;

//Структура данных для одного языка

import android.support.annotation.NonNull;

public class Language implements Comparable<Language>
{
    public Language(String shortLang,String fullLang)
    {
        mShortLang = shortLang;
        mFullLang = fullLang;
    }

    private String mFullLang;
    private String mShortLang;

    public String getFullLang()
    {
        return mFullLang;
    }

    public String getShortLang()
    {
        return mShortLang;
    }

    @Override
    public int compareTo(@NonNull Language o)
    {
//        Сравнение нужно для сортировки языков по алфавиту
        return mFullLang.compareToIgnoreCase(o.getFullLang());
    }
}