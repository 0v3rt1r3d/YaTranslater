package ru.overtired.yatranslater.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;

import com.roughike.bottombar.OnTabSelectListener;

import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.fragments.HistoryFavoriteRecycler;
import ru.overtired.yatranslater.fragments.MiddleFragment;
import ru.overtired.yatranslater.fragments.TranslateFragment;
import ru.overtired.yatranslater.structure.Translation;

/**
 * Главная активность, хост для всех фрагментов
 */

public class MainActivity extends AppCompatActivity implements HistoryFavoriteRecycler.Callbacks
{
    private com.roughike.bottombar.BottomBar mBottomBar;

    //    Если перед пересозданием активности был активен TranslateFragment - просто восстановлю ссылку
    private static final String STATE_IS_TRANSLATE_FRAGMENT_ACTIVE = "is_translate_fragment_active";

    private static final String TAG_TRANSLATE_FRAGMENT = "tag_translate_fragment";
    private static final String TAG_MIDDLE_FRAGMENT = "tag_translate_fragment";

    private TranslateFragment mTranslateFragment;
    private MiddleFragment mMiddleFragment;

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_TRANSLATE_FRAGMENT_ACTIVE, mBottomBar.getCurrentTabId() == 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ((savedInstanceState != null))
        {
            if(savedInstanceState.getBoolean(STATE_IS_TRANSLATE_FRAGMENT_ACTIVE))
            {
//            Если был открыт фрагмент перевода - восстанавливаю ссылку на него
                mTranslateFragment = (TranslateFragment) getSupportFragmentManager()
                        .findFragmentByTag(TAG_TRANSLATE_FRAGMENT);
                mMiddleFragment = MiddleFragment.newInstance();
            }else
            {
//                Если был открыт фрагмент с историей
                mMiddleFragment = (MiddleFragment) getSupportFragmentManager()
                        .findFragmentByTag(TAG_MIDDLE_FRAGMENT);
                mTranslateFragment = TranslateFragment.newInstance();
            }
//            И не надо ничего добавлять во FragmentManager, уже итак восстановилось все
        }
        else
        {
//            Если активность не пересоздается после поворота, а впервые инициализируется
            mTranslateFragment = TranslateFragment.newInstance();
            mMiddleFragment = MiddleFragment.newInstance();

//            И добавляю фрагмент перевода во FragmentManager
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_activity_frame_for_fragments,
                            mTranslateFragment,
                            TAG_TRANSLATE_FRAGMENT)
                    .commit();
        }

//        Инициализация нижнего бара
        mBottomBar = (com.roughike.bottombar.BottomBar) findViewById(R.id.bottom_bar);
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener()
        {
            @Override
            public void onTabSelected(@IdRes int tabId)
            {
                if (tabId == R.id.nav_button_translate)
                {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_activity_frame_for_fragments,
                                    mTranslateFragment,
                                    TAG_TRANSLATE_FRAGMENT)
                            .commit();
                }
                else if (tabId == R.id.nav_button_middle)
                {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_activity_frame_for_fragments,
                                    mMiddleFragment,
                                    TAG_MIDDLE_FRAGMENT)
                            .commit();
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
        mTranslateFragment = TranslateFragment.newInstance(translation);
        mBottomBar.selectTabAtPosition(0);
    }
}
