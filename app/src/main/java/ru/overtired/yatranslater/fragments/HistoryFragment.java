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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.structure.Translation;
import ru.overtired.yatranslater.database.Data;

/**
 * Created by overtired on 14.03.17.
 */

public class HistoryFragment extends HistoryFavoriteRecycler
{
    @BindView(R.id.searchview_middle) EditText mSearchEditText;
    private boolean mIsSearchActivated = false;

    public static HistoryFragment newInstance()
    {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

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
                    mIsSearchActivated = false;

                }else
                {
                    mIsSearchActivated = true;
                }
                updateRecycler();
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
        if(mIsSearchActivated)
        {
            return Data.get(getActivity()).getFindHistory(mSearchEditText.getText().toString());
        }
        return Data.get(getActivity()).getHistory();
    }

    @Override
    protected void updateOtherRecycler()
    {
        MiddleFragment parentFragment = (MiddleFragment)getParentFragment();
        parentFragment.updateFavoriteRecyclerView();
    }

    @Override
    protected void removeTranslation(Translation translation)
    {
        Data.get(getActivity()).removeFromHistory(translation);
    }
}
