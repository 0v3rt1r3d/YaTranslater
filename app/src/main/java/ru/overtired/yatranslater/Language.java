package ru.overtired.yatranslater;

/**
 * Created by overtired on 15.03.17.
 */

public class Language
{
    public Language(String FullName, String ShortName)
    {
        mFullName = FullName;
        mShortName = ShortName;
    }

    private String mFullName;

    public String getFullName()
    {
        return mFullName;
    }

    public String getShortName()
    {
        return mShortName;
    }

    private String mShortName;
}
