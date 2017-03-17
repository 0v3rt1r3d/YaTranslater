package ru.overtired.yatranslater;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by overtired on 16.03.17.
 */

public class MiddleFragment extends Fragment
{
    private static final String TAG = "MiddleFragment: ";

    private FavoriteFragment mFavoriteFragment;
    private HistoryFragment mHistoryFragment;

    private ImageButton mClearHistoryButton;

    private TextView mHistoryTextView;
    private TextView mFavoriteTextView;

    private ViewPager mViewPager;

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

        mViewPager = (ViewPager) v.findViewById(R.id.middle_view_pager);
//        mViewPager.setAdapter(new FragmentPagerAdapter(getFragmentManager())
//        {
//            @Override
//            public Fragment getItem(int position)
//            {
//                if(position == 0)
//                {
//                    return mHistoryFragment;
//                } else
//                {
//                    return mFavoriteFragment;
//                }
//            }
//
//            @Override
//            public int getCount()
//            {
//                return 2;
//            }
//        });

        mFavoriteTextView = (TextView) v.findViewById(R.id.favorites_title);
        mFavoriteTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(),"Work! favorites",Toast.LENGTH_SHORT).show();
//                Log.d(TAG,Integer.toString(mViewPager.getCurrentItem()));
//                if (mViewPager.getCurrentItem()!=1)
//                {
//                    mViewPager.setCurrentItem(0);
//                }
            }
        });

        mHistoryTextView = (TextView) v.findViewById(R.id.history_title);
        mHistoryTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(),"Work! favorites",Toast.LENGTH_SHORT).show();
//                Log.d(TAG,Integer.toString(mViewPager.getCurrentItem()));
//                if (mViewPager.getCurrentItem()!=0)
//                {
//                    mViewPager.setCurrentItem(1);
//                }
            }
        });

        mClearHistoryButton = (ImageButton) v.findViewById(R.id.clear_history_button);
        mClearHistoryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(),"WORK!",Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
