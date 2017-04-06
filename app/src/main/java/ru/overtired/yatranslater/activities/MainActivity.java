package ru.overtired.yatranslater.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.fragments.BaseRecyclerFragment;
import ru.overtired.yatranslater.fragments.LeftFragment;
import ru.overtired.yatranslater.fragments.RightFragment;
import ru.overtired.yatranslater.structure.Translation;

//Главная активность - родительская для всех фрагментов

public class MainActivity extends AppCompatActivity implements BaseRecyclerFragment.Callbacks
{
    @BindView(R.id.activity_main_bottom_bar)
    com.roughike.bottombar.BottomBar mBottomBar;

//    Флаг для восстановления состояния фрагментов при повороте
    private static final String STATE_IS_TRANSLATE_FRAGMENT_ACTIVE = "is_translate_fragment_active";

//    Метки фрагментов
    public static final String TAG_TRANSLATE_FRAGMENT = "tag_translate_fragment";
    public static final String TAG_MIDDLE_FRAGMENT = "tag_middle_fragment";

    private LeftFragment mLeftFragment;
    private RightFragment mRightFragment;

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

//        Сохраняется информацию о том, какой фрагмент активен
        outState.putBoolean(STATE_IS_TRANSLATE_FRAGMENT_ACTIVE,
                (mBottomBar.getCurrentTabPosition() == 0));
        if (mBottomBar.getCurrentTabPosition() != 0)
        {
//            В случае, если фрагмент перевода не активен, его все равно нужно сохранить
            Bundle translateFragmentState = new Bundle();
            mLeftFragment.onSaveInstanceState(translateFragmentState);
            outState.putParcelable(TAG_TRANSLATE_FRAGMENT, translateFragmentState);
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
//            Действия после поворота
            if (savedInstanceState.getBoolean(STATE_IS_TRANSLATE_FRAGMENT_ACTIVE))
            {
//            Если был открыт фрагмент перевода - восстанавливается ссылка на него
                mLeftFragment = (LeftFragment) getSupportFragmentManager()
                        .findFragmentByTag(TAG_TRANSLATE_FRAGMENT);
                mRightFragment = RightFragment.newInstance();
            }
            else
            {
//                Если был открыт фрагмент с историей
                mRightFragment = (RightFragment) getSupportFragmentManager()
                        .findFragmentByTag(TAG_MIDDLE_FRAGMENT);
                Bundle translateFragmentState = savedInstanceState.getBundle(TAG_TRANSLATE_FRAGMENT);
                mLeftFragment = LeftFragment.newInstance();
                mLeftFragment.setState(translateFragmentState);
            }
        }
        else
        {
//            Если активность не пересоздается после поворота, а впервые инициализируется
            mLeftFragment = LeftFragment.newInstance();
            mRightFragment = RightFragment.newInstance();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_for_fragments,
                            mLeftFragment,
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
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    transaction.replace(R.id.activity_main_frame_for_fragments,
                            mLeftFragment,
                            TAG_TRANSLATE_FRAGMENT);
                    transaction.commit();
                }
                else if (tabId == R.id.nav_button_middle)
                {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    transaction.replace(R.id.activity_main_frame_for_fragments,
                            mRightFragment,
                            TAG_MIDDLE_FRAGMENT);
                    transaction.commit();
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
//        Этот метод вызывается из правого фрагмента для восстановления данных перевода
        mLeftFragment = LeftFragment.newInstance(translation);
        mBottomBar.selectTabAtPosition(0);
    }
}
