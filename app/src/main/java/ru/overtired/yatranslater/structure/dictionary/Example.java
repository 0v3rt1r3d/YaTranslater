package ru.overtired.yatranslater.structure.dictionary;

/**
 * Created by overtired on 22.03.17.
 */

public class Example
{
    private String mText;
    private String mTranslation;

    public String getText()
    {
        return mText;
    }

    public String getTranslation()
    {
        return mTranslation;
    }

    public Example(String text, String translation)

    {
        mText = text;
        mTranslation = translation;
    }
}
