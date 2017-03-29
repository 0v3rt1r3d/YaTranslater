package ru.overtired.yatranslater.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.database.Data;

/**
 * Created by overtired on 16.03.17.
 */

public class MiddleFragment extends Fragment
{
    public static final String TAG = "MiddleFragment";

    private HistoryFragment mHistoryFragment;
    private FavoriteFragment mFavoriteFragment;

    private ImageButton mClearHistoryButton;

    private ViewPager mViewPager;

    private TabLayout mTabLayout;

    public static MiddleFragment newInstance()
    {
//        Bundle args = new Bundle();
        return new MiddleFragment();
//        fragment.setArguments(args);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_middle, container, false);

        mHistoryFragment = HistoryFragment.newInstance();
        mFavoriteFragment = FavoriteFragment.newInstance();

        mViewPager = (ViewPager) v.findViewById(R.id.middle_view_pager);
        mViewPager.setAdapter(new TabbedFragmentPagerAdapter(getActivity(),
                getChildFragmentManager()));

        mTabLayout = (TabLayout) v.findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

        mClearHistoryButton = (ImageButton) v.findViewById(R.id.clear_history_button);
        mClearHistoryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                mHistoryFragment.updateRecycler();
                if(mViewPager.getCurrentItem()==0)
                {
                    new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.dialog_delete_history)
                            .setPositiveButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            Data.get(getActivity()).clearHistory();
                                            mHistoryFragment.updateRecycler();
                                        }
                                    })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                }else
                {
                    new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.dialog_delete_favorite)
                            .setPositiveButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            Data.get(getActivity()).clearFavorites();
                                            mHistoryFragment.updateRecycler();
                                        }
                                    })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                }
            }
        });

        return v;
    }

    private class TabbedFragmentPagerAdapter extends FragmentPagerAdapter
    {
        final int pageCount = 2;

        private Context mContext;
        private String[] mTitles;

        public TabbedFragmentPagerAdapter(Context context, FragmentManager fm)
        {
            super(fm);
            mContext = context;
            mTitles = new String[2];
            mTitles[0] = context.getResources().getString(R.string.history);
            mTitles[1] = context.getResources().getString(R.string.favorites);

        }

        @Override
        public Fragment getItem(int position)
        {
            if(position == 0)
            {
//                    return HistoryFragment.newInstance();
                return mHistoryFragment;
            } else
            {
//                    return FavoriteFragment.newInstance();
                return mFavoriteFragment;
            }
        }

        @Override
        public int getCount()
        {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mTitles[position];
        }
    }

    public void updateFavoriteRecyclerView()
    {
        mFavoriteFragment.updateRecycler();
    }

    public void updateHistoryRecyclerView()
    {
        mHistoryFragment.updateRecycler();
    }
}
