//refactored
package ru.overtired.yatranslater.structure;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

//Структура данных для одного перевода

public class Translation implements Parcelable
{
    private boolean mIsFavorite;
    private boolean mInHistory;
    private boolean mIsInDictionary;

    private String mLangFrom;
    private String mLangTo;
    private String mTextFrom;
    private String mTextTo;

    public static final Parcelable.Creator<Translation> CREATOR = new Creator<Translation>()
    {
        @Override
        public Translation createFromParcel(Parcel source)
        {
            return new Translation(source);
        }

        @Override
        public Translation[] newArray(int size)
        {
            return new Translation[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeBooleanArray(new boolean[]{mIsFavorite,mInHistory});
        dest.writeString(mLangFrom);
        dest.writeString(mLangTo);
        dest.writeString(mTextFrom);
        dest.writeString(mTextTo);
    }

    private Translation(Parcel source)
    {
        boolean arr[] = new boolean[]{};
        source.readBooleanArray(arr);
        mIsFavorite = arr[0];
        mInHistory = arr[1];
        mLangFrom = source.readString();
        mLangTo = source.readString();
        mTextFrom = source.readString();
        mTextTo = source.readString();
    }

    public Translation(String langFrom,
                       String langTo,
                       String textFrom,
                       String textTo,
                       boolean isFavorite,
                       boolean inHistory,
                       boolean isInDictionary)
    {
        mLangFrom = langFrom;
        mLangTo = langTo;
        mTextFrom = textFrom;
        mTextTo = textTo;
        mIsFavorite = isFavorite;
        mInHistory = inHistory;
        mIsInDictionary = isInDictionary;
    }

    public boolean equals(Translation translation)
    {
        return (mTextFrom.equals(translation.getTextFrom()) &&
                mLangFrom.equals(translation.getLangFrom()) &&
                mLangTo.equals(translation.getLangTo()));
    }

//    Getters-setter

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

    public boolean isInDictionary()
    {
        return mIsInDictionary;
    }

    public void setInDictionary(boolean inDictionary)
    {
        mIsInDictionary = inDictionary;
    }
}
