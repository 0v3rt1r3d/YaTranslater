//refactored
package ru.overtired.yatranslater.structure;

import java.util.UUID;

//Структура данных для одного перевода

public class Translation
{
    private UUID mId;
    private boolean mIsFavorite;
    private boolean mInHistory;

    private String mLangFrom;
    private String mLangTo;
    private String mTextFrom;
    private String mTextTo;

    public Translation(String langFrom,
                       String langTo,
                       String textFrom,
                       String textTo,
                       String uuid,
                       boolean isFavorite,
                       boolean inHistory)
    {
        mLangFrom = langFrom;
        mLangTo = langTo;
        mTextFrom = textFrom;
        mTextTo = textTo;
        mIsFavorite = isFavorite;
        mId = UUID.fromString(uuid);
        mInHistory = inHistory;
    }

    public Translation(String langFrom,
                       String langTo,
                       String textFrom,
                       String textTo,
                       boolean isFavorite,
                       boolean inHistory)
    {
        mLangFrom = langFrom;
        mLangTo = langTo;
        mTextFrom = textFrom;
        mTextTo = textTo;
        mIsFavorite = isFavorite;
        mId = UUID.randomUUID();
        mInHistory = inHistory;
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

    public boolean isInHistory()
    {
        return mInHistory;
    }

    public void setInHistory(boolean inHistory)
    {
        mInHistory = inHistory;
    }
}
