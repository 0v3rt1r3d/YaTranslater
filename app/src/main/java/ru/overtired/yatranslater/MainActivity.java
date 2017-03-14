package ru.overtired.yatranslater;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

//Главная активность, хост для всех фрагментов

public class MainActivity extends AppCompatActivity
{
//    Кнопки из бара снизу
    private ImageButton mTranslateButton;
    private ImageButton mBookmarkButton;
    private ImageButton mSettingsButton;

//    Фрагменты
    private BookmarkFragment mBookmarkFragment;
    private HistoryFragment mHistoryFragment;
    private TranslateFragment mTranslateFragment;
    private SettingsFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Сразу инициализирую все фрагменты
        mBookmarkFragment = BookmarkFragment.newInstance();
        mTranslateFragment = TranslateFragment.newInstance();
        mSettingsFragment = SettingsFragment.newInstance();


//        Первым фрагментом выбираю перевод
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragment_container,mTranslateFragment).commit();

//        Нахожу кнопки нижнего бара и определяю для них слушателей
        mTranslateButton = (ImageButton)findViewById(R.id.nav_button_translate);
        mTranslateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,mTranslateFragment)
                        .commit();
            }
        });
        mBookmarkButton = (ImageButton)findViewById(R.id.nav_button_bookmark);
        mBookmarkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,mBookmarkFragment)
                        .commit();
            }
        });
        mSettingsButton = (ImageButton)findViewById(R.id.nav_button_settings);
        mSettingsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,mSettingsFragment)
                        .commit();
            }
        });

    }
}
