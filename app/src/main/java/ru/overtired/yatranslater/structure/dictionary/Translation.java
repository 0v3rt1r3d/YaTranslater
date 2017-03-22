package ru.overtired.yatranslater.structure.dictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by overtired on 22.03.17.
 */

public class Translation
{
    private String mText;

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

    private List<String> mSynonyms;
    private List<String> mMeans;
    private List<Example> mExamples;

    public Translation(String text)
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
