package ru.overtired.yatranslater;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.overtired.yatranslater.database.Data;

/**
 * Created by overtired on 14.03.17.
 */

public class HistoryFragment extends HistoryFavoriteRecycler
{
    public static HistoryFragment newInstance()
    {

//        Bundle args = new Bundle();

        HistoryFragment fragment = new HistoryFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected List<Translation> getTranslations()
    {
        return Data.get(getActivity()).getHistory();
    }
}
