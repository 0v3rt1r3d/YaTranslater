//refactored
package ru.overtired.yatranslater.structure;

//Структура данных для одного языка

public class Language
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
}