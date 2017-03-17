package ru.overtired.yatranslater;

import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.roughike.bottombar.OnTabSelectListener;

//Главная активность, хост для всех фрагментов

public class MainActivity extends AppCompatActivity
{
//    Кнопки из бара снизу
    private com.roughike.bottombar.BottomBar mBottomBar;
//    private com.roughike.bottombar.BottomBarTab mTranslateButton;
//    private com.roughike.bottombar.BottomBarTab mMiddleButton;
//    private com.roughike.bottombar.BottomBarTab mSettingsButton;

//    Фрагменты
    private MiddleFragment mMiddleFragment;
    private TranslateFragment mTranslateFragment;
    private SettingsFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
