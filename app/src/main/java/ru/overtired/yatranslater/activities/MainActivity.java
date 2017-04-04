package ru.overtired.yatranslater.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;

import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @BindView(R.id.bottom_bar) com.roughike.bottombar.BottomBar mBottomBar;

//    Если перед пересозданием активности был активен TranslateFragment - просто восстановлю ссылку
    private static final String STATE_IS_TRANSLATE_FRAGMENT_ACTIVE = "is_translate_fragment_active";

    private static final String TAG_TRANSLATE_FRAGMENT = "tag_translate_fragment";
    private static final String TAG_MIDDLE_FRAGMENT = "tag_middle_fragment";

    private TranslateFragment mTranslateFragment;
    private MiddleFragment mMiddleFragment;

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
//        Сохраняю информацию о том, какой фрагмент активен
        outState.putBoolean(STATE_IS_TRANSLATE_FRAGMENT_ACTIVE,
                (mBottomBar.getCurrentTabPosition() == 0));
        if(mBottomBar.getCurrentTabPosition()!=0)
        {
//            В случае, если фрагмент перевода не активен, его все равно нужно сохранить
            Bundle translateFragmentState = new Bundle();
            mTranslateFragment.onSaveInstanceState(translateFragmentState);
            outState.putParcelable(TAG_TRANSLATE_FRAGMENT,translateFragmentState);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState != null)
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
                Bundle translateFragmentState = savedInstanceState.getBundle(TAG_TRANSLATE_FRAGMENT);
                mTranslateFragment = TranslateFragment.newInstance();
                mTranslateFragment.setState(translateFragmentState);
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
                    .replace(R.id.main_activity_frame_for_fragments,
                            mTranslateFragment,
                            TAG_TRANSLATE_FRAGMENT)
                    .commit();
        }

//        Инициализация нижнего бара
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
//        if(!mTranslateFragment.getTranslation().equals(translation))
//        {
//
//            mTranslateFragment = TranslateFragment.newInstance(translation);
//        } else
//        {
////            Возможно, что пользователь добавил перевод в избранное
//            mTranslateFragment.getTranslation().setFavorite(Data.get(this)
//                    .isTranslationFavorite(translation));
//            mTranslateFragment.setArguments(new Bundle());
//        }
        mTranslateFragment = TranslateFragment.newInstance(translation);
        mBottomBar.selectTabAtPosition(0);
    }
}
