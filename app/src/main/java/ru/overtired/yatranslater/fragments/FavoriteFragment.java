package ru.overtired.yatranslater.fragments;

import java.util.List;

import ru.overtired.yatranslater.structure.Translation;
import ru.overtired.yatranslater.database.Data;

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
        return Data.get(getActivity()).getFavorites();
    }
}
