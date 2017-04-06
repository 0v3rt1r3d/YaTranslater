package ru.overtired.yatranslater.structure.dictionary;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

//Хранит все данные, полученные из словаря

public class Dictionary implements Parcelable
{
    public static final Parcelable.Creator<Dictionary> CREATOR =
            new Parcelable.Creator<Dictionary>()
            {
                @Override
                public Dictionary createFromParcel(Parcel source)
                {
                    return new Dictionary(source);
                }

                @Override
                public Dictionary[] newArray(int size)
                {
                    return new Dictionary[size];
                }
            };

    private String mDirection;
    private String mText;
    private String mTranscription;
    private List<Variant> mVariants;

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mDirection);
        dest.writeString(mText);
        dest.writeString(mTranscription);
        dest.writeList(mVariants);
    }

    private Dictionary(Parcel source)
    {
        mDirection = source.readString();
        mText = source.readString();
        mTranscription = source.readString();
        source.readTypedList(mVariants, Variant.CREATOR);
    }

    public Dictionary(String text, String transcription, String direction)
    {
        mText = text;
        mDirection = direction;
        mTranscription = "["+transcription+"]";
        mVariants = new ArrayList<>();
    }

    public List<Variant> getVariants()
    {
        return mVariants;
    }

    public void addTranslation(Variant variant)
    {
        mVariants.add(variant);
    }

    @Override
    public String toString()
    {
        return mText;
    }

    public String getText()
    {
        return mText;
    }

    public String getTranscription()
    {
        return mTranscription;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }
}
