package ru.overtired.yatranslater.structure.dictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by overtired on 22.03.17.
 */

public class Definition
{
    private String mTranscription;
    private List<Translation> mTranslations;

    public Definition(String transcription)
    {
        mTranscription = transcription;
        mTranslations = new ArrayList<>();
    }

    public String getTranscription()
    {
        return mTranscription;
    }

    public List<Translation> getTranslations()
    {
        return mTranslations;
    }

    public void addTranslation(Translation translation)

    {
        mTranslations.add(translation);
    }
}
