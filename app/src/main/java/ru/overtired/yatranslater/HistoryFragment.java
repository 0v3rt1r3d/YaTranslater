package ru.overtired.yatranslater;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by overtired on 14.03.17.
 */

public class HistoryFragment extends Fragment
{
    public static HistoryFragment newInstance()
    {

//        Bundle args = new Bundle();

        HistoryFragment fragment = new HistoryFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_history,container,false);

        return v;
    }
}
