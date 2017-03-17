package ru.overtired.yatranslater;

/**
 * Created by overtired on 17.03.17.
 */

public class Translation
{
    private String mShortFrom;
    private String mShortTo;
    private String mWordFrom;

    private String mWordTo;

    private boolean mIsFavorite;

    public Translation(String shortFrom,String shortTo,String wordFrom,String wordTo,boolean isFavorite)
    {
        mShortFrom = shortFrom;
        mShortTo = shortTo;
        mWordFrom = wordFrom;
        mWordTo = wordTo;
        mIsFavorite = isFavorite;
    }

    public String getShortFrom()
    {
        return mShortFrom;
    }

    public String getShortTo()
    {
        return mShortTo;
    }

    public String getWordFrom()
    {
        return mWordFrom;
    }

    public String getWordTo()
    {
        return mWordTo;
    }

    public boolean isFavorite()
    {
        return mIsFavorite;
    }
}
