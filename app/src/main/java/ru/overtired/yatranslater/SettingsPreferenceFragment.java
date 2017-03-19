package ru.overtired.yatranslater;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by overtired on 19.03.17.
 */

public class SettingsPreferenceFragment extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        addPreferencesFromResource(R.xml.peferences);
    }

    public static SettingsPreferenceFragment newInstance()
    {
//        Bundle args = new Bundle();
        SettingsPreferenceFragment fragment = new SettingsPreferenceFragment();
//        fragment.setArguments(args);
        return fragment;
    }
}
