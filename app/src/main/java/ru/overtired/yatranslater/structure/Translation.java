//refactored
package ru.overtired.yatranslater.structure;

import java.util.UUID;

//Структура данных для одного перевода

public class Translation
{
    private UUID mId;
    private boolean mIsFavorite;

    private String mLangFrom;
    private String mLangTo;
    private String mTextFrom;
    private String mTextTo;

    public Translation(String langFrom, String langTo, String textFrom, String textTo, String uuid, boolean isFavorite)
    {
        mLangFrom = langFrom;
        mLangTo = langTo;
        mTextFrom = textFrom;
        mTextTo = textTo;
        mIsFavorite = isFavorite;
        mId = UUID.fromString(uuid);

    }

    public Translation(String langFrom, String langTo, String textFrom, String textTo, boolean isFavorite)
    {
        mLangFrom = langFrom;
        mLangTo = langTo;
        mTextFrom = textFrom;
        mTextTo = textTo;
        mIsFavorite = isFavorite;
        mId = UUID.randomUUID();
    }

    public UUID getId()
    {
        return mId;
    }

    public boolean isFavorite()
    {
        return mIsFavorite;
    }

    public void setFavorite(boolean favorite)
    {
        mIsFavorite = favorite;
    }

    public String getLangFrom()
    {
        return mLangFrom;
    }

    public void setLangFrom(String langFrom)
    {
        mLangFrom = langFrom;
    }

    public String getLangTo()
    {
        return mLangTo;
    }

    public void setLangTo(String langTo)
    {
        mLangTo = langTo;
    }

    public String getTextFrom()
    {
        return mTextFrom;
    }

    public void setTextFrom(String textFrom)
    {
        mTextFrom = textFrom;
    }

    public String getTextTo()
    {
        return mTextTo;
    }

    public void setTextTo(String textTo)
    {
        mTextTo = textTo;
    }
}
