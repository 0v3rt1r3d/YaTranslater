package ru.overtired.yatranslater;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by overtired on 14.03.17.
 */

public class FavoriteFragment extends HistoryFavoriteRecycler
{
    public static FavoriteFragment newInstance()
    {

//        Bundle args = new Bundle();

        FavoriteFragment fragment = new FavoriteFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected List<Translation> getTranslations()
    {
        return new ArrayList<>();
    }
}
