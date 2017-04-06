package ru.overtired.yatranslater.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;

import butterknife.BindView;
import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.structure.Translation;
import ru.overtired.yatranslater.database.Singleton;

public class FavoritesFragment extends BaseRecyclerFragment
{
    @BindView(R.id.fragment_base_recycler_edit_text_search) EditText mSearchEditText;
    private boolean mIsSearchActivated = false;

    public static FavoritesFragment newInstance()
    {
        FavoritesFragment fragment = new FavoritesFragment();
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
            return Singleton.get(getActivity()).getFindFavorites(mSearchEditText.getText().toString());
        }
        return Singleton.get(getActivity()).getFavorites();
    }

    @Override
    protected void updateOtherRecycler()
    {
        RightFragment parentFragment = (RightFragment)getParentFragment();
        parentFragment.updateHistoryRecyclerView();
    }

    @Override
    protected void removeTranslation(Translation translation)
    {
        Singleton.get(getActivity()).removeFromFavorites(translation);
    }
}
