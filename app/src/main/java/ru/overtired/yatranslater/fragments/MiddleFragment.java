package ru.overtired.yatranslater.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private static final String TAG = "MiddleFragment: ";

    private HistoryFragment mHistoryFragment;
    private FavoriteFragment mFavoriteFragment;

    private ImageButton mClearHistoryButton;

    private TextView mHistoryTextView;
    private TextView mFavoriteTextView;

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

//        mFavoriteTextView = (TextView) v.findViewById(R.id.favorites_title);
//        mFavoriteTextView.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                if (mViewPager.getCurrentItem()!=1)
//                {
//                    mViewPager.setCurrentItem(1);
//                    mViewPager.getAdapter().notifyDataSetChanged();
//                }
//            }
//        });

    //        mHistoryTextView = (TextView) v.findViewById(R.id.history_title);
    //        mHistoryTextView.setOnClickListener(new View.OnClickListener()
    //        {
    //            @Override
    //            public void onClick(View v)
    //            {
    //                if (mViewPager.getCurrentItem()!=0)
    //                {
    //                    mViewPager.setCurrentItem(0);
    //                    mViewPager.getAdapter().notifyDataSetChanged();
    //                }
    //            }
    //        });

        mClearHistoryButton = (ImageButton) v.findViewById(R.id.clear_history_button);
        mClearHistoryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(),Integer.toString(Data.get(getActivity()).getHistory().size()),Toast.LENGTH_SHORT).show();
                Data.get(getActivity()).clearHistory();
                mHistoryFragment.updateRecycler();
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
}
