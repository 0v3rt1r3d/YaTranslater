package ru.overtired.yatranslater.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;

import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.structure.Translation;
import ru.overtired.yatranslater.database.Data;

/**
 * Created by overtired on 14.03.17.
 */

public class HistoryFragment extends HistoryFavoriteRecycler
{
    private EditText mSearchEditText;

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
        super.onCreateView(inflater, container, savedInstanceState);

        mSearchEditText = (EditText) mView.findViewById(R.id.searchview_middle);
        mSearchEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.equals(""))
                {
                    mRecyclerView.setAdapter(new HistoryFavoriteAdapter(Data.get(getActivity()).getHistory()));
                }else
                {
                    mRecyclerView.setAdapter(new HistoryFavoriteAdapter(Data.get(getActivity()).getFindHistory(s.toString())));
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        return mView;
    }

    @Override
    protected List<Translation> getTranslations()
    {
        return Data.get(getActivity()).getHistory();
    }
}
