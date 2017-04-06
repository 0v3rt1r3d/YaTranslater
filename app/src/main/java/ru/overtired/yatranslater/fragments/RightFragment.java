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
import android.widget.ImageButton;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.database.Singleton;

//Фрагмент - родитель для истории и избранного

public class RightFragment extends Fragment
{
    private Unbinder mUnbinder;

    public static final String TAG = "RightFragment";
    private static final String ARG_PAGE = "arg_page";

    private HistoryFragment mHistoryFragment;
    private FavoritesFragment mFavoritesFragment;

    @BindView(R.id.fragment_right_button_clear) ImageButton mClearHistoryButton;

    @BindView(R.id.fragment_right_view_pager) ViewPager mViewPager;
    @BindView(R.id.fragment_right_tab_layout) TabLayout mTabLayout;

    public static RightFragment newInstance()
    {
//        Bundle args = new Bundle();
        return new RightFragment();
//        fragment.setArguments(args);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_right, container, false);
        mUnbinder = ButterKnife.bind(this,view);

        mHistoryFragment = HistoryFragment.newInstance();
        mFavoritesFragment = FavoritesFragment.newInstance();

        mViewPager.setAdapter(new TabbedFragmentPagerAdapter(getActivity(),
                getChildFragmentManager()));

        mTabLayout.setupWithViewPager(mViewPager);

        mClearHistoryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mViewPager.getCurrentItem()==0)
                {
                    if(!mHistoryFragment.isRecyclerEmpty())
                    {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(R.string.dialog_delete_history)
                                .setPositiveButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                Singleton.get(getActivity()).clearHistory();
                                                updateFavoriteRecyclerView();
                                                updateHistoryRecyclerView();
                                                Toast.makeText(getActivity(),R.string.history_empty,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                .setNegativeButton(android.R.string.cancel, null)
                                .show();
                    }else
                    {
                        Toast.makeText(getActivity(),R.string.history_empty,Toast.LENGTH_SHORT).show();
                    }

                }else
                {
                    if(!mFavoritesFragment.isRecyclerEmpty())
                    {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(R.string.dialog_delete_favorite)
                                .setPositiveButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                Singleton.get(getActivity()).clearFavorites();
                                                updateFavoriteRecyclerView();
                                                updateHistoryRecyclerView();
                                                Toast.makeText(getActivity(),R.string.favorites_empty,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                .setNegativeButton(android.R.string.cancel, null)
                                .show();
                    }else
                    {
                        Toast.makeText(getActivity(),R.string.favorites_empty,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if(savedInstanceState!=null)
        {
            int page = savedInstanceState.getInt(ARG_PAGE);
            mViewPager.setCurrentItem(page);
        }

        return view;
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
//                    return FavoritesFragment.newInstance();
                return mFavoritesFragment;
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
        mFavoritesFragment.updateRecycler();
    }

    public void updateHistoryRecyclerView()
    {
        mHistoryFragment.updateRecycler();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_PAGE,mViewPager.getCurrentItem());
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
