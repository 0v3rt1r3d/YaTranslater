package ru.overtired.yatranslater.structure.dictionary;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

//Один из элементов словаря - вариант перевода

public class Variant implements Parcelable
{
    private String mText;

    private List<String> mSynonyms;
    private List<String> mMeans;
    private List<Example> mExamples;

    public static final Parcelable.Creator<Variant> CREATOR =
            new Parcelable.Creator<Variant>()
            {
                @Override
                public Variant createFromParcel(Parcel source)
                {
                    return new Variant(source);
                }

                @Override
                public Variant[] newArray(int size)
                {
                    return new Variant[size];
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
        dest.writeString(mText);
        dest.writeList(mSynonyms);
        dest.writeList(mMeans);
        dest.writeList(mExamples);
    }

    private Variant(Parcel source)
    {
        mText = source.readString();
        source.readStringList(mSynonyms);
        source.readStringList(mMeans);
        source.readTypedList(mExamples,Example.CREATOR);
    }


    public String getText()
    {
        return mText;
    }

    public List<String> getSynonyms()

    {
        return mSynonyms;
    }

    public List<String> getMeans()
    {
        return mMeans;
    }

    public List<Example> getExamples()
    {
        return mExamples;
    }

    public Variant(String text)
    {
        mText = text;
        mSynonyms = new ArrayList<>();
        mExamples = new ArrayList<>();
        mMeans = new ArrayList<>();
    }

    public void addSynonym(String synonim)
    {
        mSynonyms.add(synonim);
    }

    public void addExample(Example example)
    {
        mExamples.add(example);
    }

    public void addMean(String mean)
    {
        mMeans.add(mean);
    }
}
