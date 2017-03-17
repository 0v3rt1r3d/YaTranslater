package ru.overtired.yatranslater;

/**
 * Created by overtired on 15.03.17.
 */

public class Language
{
    public Language(String FullName, String ShortName)
    {
        mShortName = ShortName;
        mFullName = FullName;
    }

    private String mFullName;
    private String mShortName;

    public String getFullName()
    {
        return mFullName;
    }

    public String getShortName()
    {
        return mShortName;
    }
}
