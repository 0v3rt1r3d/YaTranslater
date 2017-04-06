package ru.overtired.yatranslater.structure.dictionary;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.spec.ECField;

//Один из элементов словаря - пример перевода

public class Example implements Parcelable
{
    private String mText;
    private String mTranslation;

    public static final Parcelable.Creator<Example> CREATOR
            = new Parcelable.Creator<Example>()
    {
        @Override
        public Example createFromParcel(Parcel source)
        {
            return new Example(source);
        }

        @Override
        public Example[] newArray(int size)
        {
            return new Example[size];
        }
    };

    public Example(String text, String translation)
    {
        mText = text;
        mTranslation = translation;
    }

    private Example(Parcel source)
    {
        mText = source.readString();
        mTranslation = source.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mText);
        dest.writeString(mTranslation);
    }

    public String getText()
    {
        return mText;
    }

    public String getTranslation()
    {
        return mTranslation;
    }
}
