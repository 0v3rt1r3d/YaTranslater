package ru.overtired.yatranslater.fragments;

import android.support.v7.util.DiffUtil;

import java.util.List;

import ru.overtired.yatranslater.structure.Translation;

public class TranslationsDiffCallback extends DiffUtil.Callback
{
    private final List<Translation> mOldList;
    private final List<Translation> mNewList;

    public TranslationsDiffCallback(List<Translation> oldList,List<Translation> newList)
    {
        mOldList = oldList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize()
    {
        return mOldList.size();
    }

    @Override
    public int getNewListSize()
    {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition)
    {
        return mOldList.get(oldItemPosition).equals(mNewList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
    {
        return true;
    }
}
