package ru.overtired.yatranslater;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by overtired on 16.03.17.
 */

public class MiddleFragment extends Fragment
{
    private FavoriteFragment mFavoriteFragment;
    private HistoryFragment mHistoryFragment;

    private TextView mHistoryTextView;
    private TextView mFavoriteTextView;

    public static MiddleFragment newInstance()
    {
//        Bundle args = new Bundle();
        MiddleFragment fragment = new MiddleFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_middle, container, false);

        mFavoriteFragment = FavoriteFragment.newInstance();
        mHistoryFragment = HistoryFragment.newInstance();

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.middle_fragment_container, mHistoryFragment)
                .commit();

        mFavoriteTextView = (TextView) v.findViewById(R.id.favorites_title);
        mFavoriteTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (getActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.middle_fragment_container) != mFavoriteFragment)
                {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.middle_fragment_container, mFavoriteFragment)
                            .commit();
                }
            }
        });

        mHistoryTextView = (TextView) v.findViewById(R.id.history_title);
        mHistoryTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (getActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.middle_fragment_container) != mHistoryFragment)
                {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.middle_fragment_container, mHistoryFragment)
                            .commit();
                }
            }
        });


        return v;
    }
}
