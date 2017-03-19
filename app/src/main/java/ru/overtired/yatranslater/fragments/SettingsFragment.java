package ru.overtired.yatranslater.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.overtired.yatranslater.R;

/**
 * Created by overtired on 14.03.17.
 */

public class SettingsFragment extends Fragment
{
    public static SettingsFragment newInstance()
    {
//        Bundle args = new Bundle();

        SettingsFragment fragment = new SettingsFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);
        getFragmentManager().beginTransaction()
                .add(R.id.settings_container, SettingsPreferenceFragment.newInstance())
                .commit();

        return view;
    }
}
