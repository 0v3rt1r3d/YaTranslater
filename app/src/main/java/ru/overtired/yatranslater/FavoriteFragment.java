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

public class FavoriteFragment extends Fragment
{
    public static FavoriteFragment newInstance()
    {

//        Bundle args = new Bundle();

        FavoriteFragment fragment = new FavoriteFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_favorite,container,false);
        return v;
    }
}