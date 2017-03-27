package ru.overtired.yatranslater.structure;

import java.util.ArrayList;
import java.util.List;

import ru.overtired.yatranslater.structure.dictionary.*;
import ru.overtired.yatranslater.structure.dictionary.Translation;

public class Dictionary
{
    private String mDirection;
    private List<ru.overtired.yatranslater.structure.dictionary.Translation> mTranslations;
    private String mText;
    private String mTranscription;

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
}
