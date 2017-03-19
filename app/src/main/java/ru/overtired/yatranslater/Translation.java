package ru.overtired.yatranslater;

import java.util.UUID;

/**
 * Created by overtired on 17.03.17.
 */

public class Translation
{
    private String mShortFrom;
    private String mShortTo;
    private String mWordFrom;
    private String mWordTo;

    public UUID getId()
    {
        return mId;
    }

    private UUID mId;

    private boolean mIsFavorite;

    public void setShortFrom(String shortFrom)
    {
        mShortFrom = shortFrom;
    }

    public void setShortTo(String shortTo)
    {
        mShortTo = shortTo;
    }

    public void setWordFrom(String wordFrom)
    {
        mWordFrom = wordFrom;
    }

    public void setWordTo(String wordTo)
    {
        mWordTo = wordTo;
    }

    public void setFavorite(boolean favorite)
    {
        mIsFavorite = favorite;
    }

    public Translation(String shortFrom, String shortTo, String wordFrom, String wordTo, String uuid, boolean isFavorite )
    {
        mShortFrom = shortFrom;
        mShortTo = shortTo;
        mWordFrom = wordFrom;
        mWordTo = wordTo;
        mIsFavorite = isFavorite;
        mId = UUID.fromString(uuid);
    }

    public Translation(String shortFrom, String shortTo, String wordFrom, String wordTo, boolean isFavorite)
    {
        mShortFrom = shortFrom;
        mShortTo = shortTo;
        mWordFrom = wordFrom;
        mWordTo = wordTo;
        mIsFavorite = isFavorite;
        mId = UUID.randomUUID();
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
