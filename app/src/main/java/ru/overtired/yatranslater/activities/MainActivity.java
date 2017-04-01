package ru.overtired.yatranslater.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import com.roughike.bottombar.OnTabSelectListener;

import java.util.List;

import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.database.Data;
import ru.overtired.yatranslater.fragments.HistoryFavoriteRecycler;
import ru.overtired.yatranslater.fragments.MiddleFragment;
import ru.overtired.yatranslater.fragments.SettingsFragment;
import ru.overtired.yatranslater.fragments.TranslateFragment;
import ru.overtired.yatranslater.structure.Translation;

/**
 * Главная активность, хост для всех фрагментов
 */

public class MainActivity extends AppCompatActivity implements HistoryFavoriteRecycler.Callbacks
{
    private com.roughike.bottombar.BottomBar mBottomBar;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        mViewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager()));c

//        Первым фрагментом выбираю перевод
        mBottomBar = (com.roughike.bottombar.BottomBar) findViewById(R.id.bottom_bar);
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener()
        {
            @Override
            public void onTabSelected(@IdRes int tabId)
            {
                if (tabId == R.id.nav_button_translate)
                {
                    mViewPager.setCurrentItem(0);
                }
                else if (tabId == R.id.nav_button_middle)
                {
                    mViewPager.setCurrentItem(1);
                }
                else if (tabId == R.id.nav_button_settings)
                {
                    mViewPager.setCurrentItem(2);
                }
            }
        });
    }

    public static Intent newIntent(Context context)
    {
        return new Intent(context, MainActivity.class);
    }

    @Override
    public void setTranslation(Translation translation)
    {
        ((MainViewPagerAdapter) mViewPager.getAdapter()).setTranslation(translation);
        mViewPager.getAdapter().notifyDataSetChanged();
        mBottomBar.selectTabAtPosition(0);
    }
}
