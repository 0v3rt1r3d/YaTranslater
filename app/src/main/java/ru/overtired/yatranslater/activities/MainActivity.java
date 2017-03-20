package ru.overtired.yatranslater.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.roughike.bottombar.OnTabSelectListener;

import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.database.Data;
import ru.overtired.yatranslater.fragments.HistoryFavoriteRecycler;
import ru.overtired.yatranslater.fragments.MiddleFragment;
import ru.overtired.yatranslater.fragments.SettingsFragment;
import ru.overtired.yatranslater.fragments.TranslateFragment;
import ru.overtired.yatranslater.structure.Translation;

//Главная активность, хост для всех фрагментов

public class MainActivity extends AppCompatActivity implements HistoryFavoriteRecycler.Callbacks
{
    private static final String EXTRA_INTERENET = "ru.overtired.yatranslater.mainactivity.internet";

    private com.roughike.bottombar.BottomBar mBottomBar;

//    Фрагменты
    private MiddleFragment mMiddleFragment;
    private TranslateFragment mTranslateFragment;
    private SettingsFragment mSettingsFragment;

    private boolean hasInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hasInternet = getIntent().getBooleanExtra(EXTRA_INTERENET,false);


//        Сразу инициализирую все фрагменты
        mTranslateFragment = TranslateFragment.newInstance();
        mSettingsFragment = SettingsFragment.newInstance();
        mMiddleFragment = MiddleFragment.newInstance();

//        Первым фрагментом выбираю перевод
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragment_container,mTranslateFragment).commit();

        mBottomBar = (com.roughike.bottombar.BottomBar) findViewById(R.id.bottom_bar);
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener()
        {
            @Override
            public void onTabSelected(@IdRes int tabId)
            {
                if(tabId == R.id.nav_button_translate)
                {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container,mTranslateFragment)
                            .commit();
                }else if(tabId == R.id.nav_button_middle)
                {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container,mMiddleFragment)
                            .commit();
                }else if(tabId == R.id.nav_button_settings)
                {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container,mSettingsFragment)
                            .commit();
                }
            }
        });
    }

    public static Intent newIntent(Context context, boolean hasInternet)
    {
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra(EXTRA_INTERENET,hasInternet);
        return intent;
    }

    @Override
    public void setTranslation(Translation translation)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,mTranslateFragment)
                .commit();
        mTranslateFragment.setTranslation(translation);
    }
}
