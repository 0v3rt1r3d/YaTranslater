package ru.overtired.yatranslater.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.overtired.yatranslater.fragments.MiddleFragment;
import ru.overtired.yatranslater.fragments.SettingsFragment;
import ru.overtired.yatranslater.fragments.TranslateFragment;
import ru.overtired.yatranslater.structure.Translation;


public class MainViewPagerAdapter extends FragmentPagerAdapter
{
    private TranslateFragment mTranslateFragment;
    private MiddleFragment mMiddleFragment;
    private SettingsFragment mSettingsFragment;

    public MainViewPagerAdapter(FragmentManager fm)
    {
        super(fm);

        mTranslateFragment = TranslateFragment.newInstance();
        mMiddleFragment = MiddleFragment.newInstance();
        mSettingsFragment = SettingsFragment.newInstance();
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0: return mTranslateFragment;
            case 1: return mMiddleFragment;
            case 2: return mSettingsFragment;
            default: return null;
        }
    }

    @Override
    public int getCount()
    {
        return 3;
    }

    public void setTranslation(Translation translation)
    {
        mTranslateFragment.setTranslation(translation);
        notifyDataSetChanged();
    }
}
