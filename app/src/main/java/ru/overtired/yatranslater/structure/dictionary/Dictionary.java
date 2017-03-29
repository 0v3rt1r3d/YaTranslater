package ru.overtired.yatranslater.structure.dictionary;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.overtired.yatranslater.structure.dictionary.*;
import ru.overtired.yatranslater.structure.dictionary.Translation;

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
    private List<ru.overtired.yatranslater.structure.dictionary.Translation> mTranslations;

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mDirection);
        dest.writeString(mText);
        dest.writeString(mTranscription);
        dest.writeList(mTranslations);
    }

    private Dictionary(Parcel source)
    {
        mDirection = source.readString();
        mText = source.readString();
        mTranscription = source.readString();
        source.readTypedList(mTranslations,Translation.CREATOR);
    }

    public Dictionary(String text, String transcription, String direction)
    {
        mText = text;
        mDirection = direction;
        mTranscription = "["+transcription+"]";
        mTranslations = new ArrayList<>();
    }

    public List<Translation> getTranslations()
    {
        return mTranslations;
    }

    public void addTranslation(Translation translation)
    {
        mTranslations.add(translation);
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
