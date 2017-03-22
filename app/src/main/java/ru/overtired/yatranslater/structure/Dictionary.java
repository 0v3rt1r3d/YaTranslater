package ru.overtired.yatranslater.structure;

import java.util.ArrayList;
import java.util.List;

import ru.overtired.yatranslater.structure.dictionary.Definition;

public class Dictionary
{
    private String mDirection;
    private List<Definition> mDefinitions;
    private String mText;

    public Dictionary(String text, String direction)
    {
        mText = text;
        mDirection = direction;
        mDefinitions = new ArrayList<>();
    }

    public void addDefinition(Definition definition)
    {
        mDefinitions.add(definition);
    }

    public List<Definition> getDefinitions()
    {
        return mDefinitions;
    }

    @Override
    public String toString()
    {
        return mText;
    }
}
