package ru.overtired.yatranslater.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.activities.LanguageChooseActivity;
import ru.overtired.yatranslater.activities.SplashActivity;
import ru.overtired.yatranslater.database.Singleton;
import ru.overtired.yatranslater.database.PreferencesScheme;
import ru.overtired.yatranslater.database.Downloader;
import ru.overtired.yatranslater.structure.Translation;
import ru.overtired.yatranslater.structure.dictionary.Dictionary;

/**
 * Created by overtired on 14.03.17.
 */

public class LeftFragment extends Fragment implements DictionaryFragment.Callback
{
    public final static int REQUEST_LANG_FROM = 0;
    public final static int REQUEST_LANG_TO = 1;

    private static final String ARG_DICTIONARY = "arg_dictionary";
    private static final String ARG_TRANSLATION = "arg_translation";

    private Unbinder mUnbinder;

    private Translation mTranslation;
    private Dictionary mDictionary;

    @BindView(R.id.fragment_left_text_view_lang_from)
    TextView mFromLanguageTextView;
    @BindView(R.id.fragment_left_text_view_lang_to)
    TextView mToLanguageTextView;
    @BindView(R.id.fragment_left_edit_text_input)
    EditText mFieldToTranslate;
    @BindView(R.id.fragment_left_text_view_output)
    TextView mResultTextView;
    @BindView(R.id.fragment_left_button_swap_langs)
    ImageButton mSwapLanguagesButton;
    @BindView(R.id.fragment_left_button_save)
    ImageButton mSaveToFavoritesButton;
    @BindView(R.id.fragment_left_scroll_view_output_text)
    ScrollView mScrollView;
    @BindView(R.id.fragment_left_button_clear)
    ImageButton mButtonClear;
    @BindView(R.id.fragment_left_progressbar)
    MaterialProgressBar mProgressBar;

    private AsyncTranslater mAsyncTranslater;
    private AsyncDictionary mAsyncDictionary;

    private FrameLayout mFrameForDictionary;

    public static LeftFragment newInstance(Translation translation)
    {
//        Для инициализации уже готового перевода из истории
        Bundle args = new Bundle();
        args.putParcelable(ARG_TRANSLATION, translation);

        LeftFragment fragment = new LeftFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static LeftFragment newInstance()
    {
//        Создание фрагмента с нуля
        LeftFragment fragment = new LeftFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_left, container, false);

        mUnbinder = ButterKnife.bind(this, view);

//        Контейнер под фрагмент нахожу отдельно, потому что ссылка него нужна после уничтожения View
        mFrameForDictionary = (FrameLayout) view.findViewById(R.id.fragment_left_frame_for_dictionary_fragment);

//        Тут начало инициализации
        if (savedInstanceState != null)
        {
//            Восстановление после поворота, если фрагмент был на переднем плане
            mTranslation = savedInstanceState.getParcelable(ARG_TRANSLATION);
            updateView();
//            Если есть данные из словаря - загружаю их
            setVisibleDictionaryFragment((mDictionary =
                    savedInstanceState.getParcelable(ARG_DICTIONARY)) != null);
        }
        else if (getArguments() != null && getArguments().getParcelable(ARG_TRANSLATION) != null &&
                mTranslation == null)
        {
//            Инициализация при запуске из истории-избранного, перевод был передан в аргументах
            mTranslation = getArguments().getParcelable(ARG_TRANSLATION);
            updateView();
            translate();
        }
        else if (mTranslation != null)
        {
//            Восстановление, если фрагмент не был на переднем плане
            setVisibleDictionaryFragment(mDictionary != null);
            mTranslation.setFavorite(Singleton.get(getActivity()).isTranslationFavorite(mTranslation));
            updateView();
        }
        else
        {
//            Создание с нуля, если запускается впервые
            initializeNewTranslation();
            updateView();
            showKeyboard();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mSaveToFavoritesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mTranslation.isFavorite())
                {
                    mTranslation.setFavorite(false);
                    mSaveToFavoritesButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_favorite));
                }
                else
                {
                    mTranslation.setFavorite(true);
                    mSaveToFavoritesButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                }
                Singleton.get(getActivity()).updateTranslation(mTranslation);
            }
        });

        mFieldToTranslate.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mFieldToTranslate.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mFieldToTranslate.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                //Тут происходит перевод, при нажатии "done" на клавиатуре
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    translate();
                    hideKeyboard();
                }
                return false;
            }
        });

        mFromLanguageTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = LanguageChooseActivity.newIntent(getActivity(), false);
                startActivityForResult(intent, REQUEST_LANG_FROM);
            }
        });

        mToLanguageTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = LanguageChooseActivity.newIntent(getActivity(), true);
                startActivityForResult(intent, REQUEST_LANG_TO);
            }
        });

        mSwapLanguagesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mFieldToTranslate.getText().toString().equals(""))
                {
                    mTranslation = new Translation(
                            mTranslation.getLangFrom(),
                            mTranslation.getLangTo(),
                            "",
                            "",
                            false,
                            false,
                            false
                    );
                }
                mTranslation.swapDirection();
                updateView();
                if (!mTranslation.getTextTo().equals(""))
                {
                    translate();
                }

            }
        });

        mButtonClear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mFieldToTranslate.setText("");
                showKeyboard();
            }
        });
    }

    @Override
    public void translateNewWord(String text)
    {
        mTranslation.swapDirection();
        mTranslation.setTextFrom(text);
        updateView();
        translate();
    }

    //    Классы для перевода текста из переводчика и словаря
    private class AsyncTranslater extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            Downloader downloader = new Downloader();
            return downloader.getTranslation(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String string)
        {
            mTranslation.setTextTo(string);
            mTranslation.setInDictionary(false);
            mTranslation.setInHistory(true);

            setVisibleDictionaryFragment(false);
            Singleton.get(getActivity()).addTranslation(mTranslation);
            mResultTextView.setText(mTranslation.getTextTo());
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private class AsyncDictionary extends AsyncTask<String, Void, Dictionary>
    {
        @Override
        protected Dictionary doInBackground(String... params)
        {
            Downloader downloader = new Downloader();
            return downloader.getDictionary(getActivity(), params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Dictionary dictionary)
        {
            if (dictionary != null)
            {
                mDictionary = dictionary;

                mTranslation.setTextTo(dictionary.getVariants().get(0).getText());
                mTranslation.setInDictionary(true);
                mTranslation.setInHistory(true);

                Singleton.get(getActivity()).putDictionaryInCache(mTranslation, mDictionary);

                Singleton.get(getActivity()).addTranslation(mTranslation);
                updateView();

                mProgressBar.setVisibility(View.GONE);

                setVisibleDictionaryFragment(true);
            }
            else
            {
                setVisibleDictionaryFragment(false);
                if (mAsyncTranslater != null)
                {
                    mAsyncTranslater.cancel(true);
                }
                mAsyncTranslater = new AsyncTranslater();
                mAsyncTranslater.execute(mFieldToTranslate.getText().toString(),
                        mTranslation.getLangFrom() + "-" + mTranslation.getLangTo());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != Activity.RESULT_OK)
        {
            return;
        }
        if (requestCode == REQUEST_LANG_FROM)
        {
            mTranslation.setLangFrom(data.getStringExtra(LanguageChooseActivity.EXTRA_LANG));
            updateView();
        }
        else if (requestCode == REQUEST_LANG_TO)
        {
            mTranslation.setLangTo(data.getStringExtra(LanguageChooseActivity.EXTRA_LANG));
            updateView();
        }
    }

    private void hideKeyboard()
    {
        View view = getActivity().getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void updateView()
    {
//        Если во фрагменте есть mTranslation, то этот метод заполняет все поля
        mToLanguageTextView.setText(Singleton.get(getActivity())
                .getFullLanguageByShort(mTranslation.getLangTo()));
        mFromLanguageTextView.setText(Singleton.get(getActivity())
                .getFullLanguageByShort(mTranslation.getLangFrom()));
        mFieldToTranslate.setText(mTranslation.getTextFrom());
        mFieldToTranslate.setSelection(mFieldToTranslate.getText().length());
        mResultTextView.setText(mTranslation.getTextTo());

        if (mTranslation.isFavorite())
        {
            mSaveToFavoritesButton.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_favorite));
        }
        else
        {
            mSaveToFavoritesButton.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_not_favorite));
        }
    }

    public void setState(Bundle bundle)
    {
//        Наверняка можно было как-то по-человечески сделать, но сделал так
        mTranslation = bundle.getParcelable(ARG_TRANSLATION);
        if (bundle.getParcelable(ARG_DICTIONARY) != null)
        {
            mDictionary = bundle.getParcelable(ARG_DICTIONARY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TRANSLATION, mTranslation);

//        Если есть перевод из словаря - его тоже сохраняю
        if (mFrameForDictionary.getVisibility() == View.VISIBLE)
        {
            outState.putParcelable(ARG_DICTIONARY, mDictionary);
        }
    }

    private void translate()
    {
        if (mFieldToTranslate.getText().toString().equals(""))
        {
//            Ничего не делаю, если текст для перевода пустой
            return;
        }
        if(mFieldToTranslate.getText().length()>3000)
        {
            mFieldToTranslate.setText(mFieldToTranslate.getText().toString().substring(0,3000));
        }

        if (Singleton.get(getActivity()).hasDirection(mTranslation.getLangFrom() + "-" + mTranslation.getLangTo()))
        {
            mSaveToFavoritesButton.setEnabled(false);

            mTranslation.setFavorite(false);
            mTranslation.setTextFrom(mFieldToTranslate.getText().toString());

            updateView();

            Translation temp = Singleton.get(getActivity()).getTranslation(mTranslation);
            if (temp != null && !temp.isInDictionary())
            {
//                Если перевод не из словаря, просто восстанавливаю данные из базы
                mTranslation = temp;
                updateView();
            }
            else
            {
//                Нужно загружать данные из словаря
                if (temp != null)
                {
                    mTranslation = temp;
                    updateView();
                }
                Dictionary dic = Singleton.get(getActivity()).getDictionaryFromCache(mTranslation);
                if (dic != null)
                {
//                    Такое слово уже есть в кеше, восстанавливаю
                    mDictionary = dic;

                    mTranslation.setTextTo(dic.getVariants().get(0).getText());
                    mTranslation.setInDictionary(true);
                    mTranslation.setInHistory(true);

                    Singleton.get(getActivity()).addTranslation(mTranslation);
                    updateView();

                    setVisibleDictionaryFragment(true);
                }
                else if (Downloader.hasInternetConnection(getActivity()))
                {
                    mProgressBar.setVisibility(View.VISIBLE);
//                    Загрузка данных
                    if (mAsyncDictionary != null)
                    {
                        mAsyncDictionary.cancel(true);
                    }
                    mAsyncDictionary = new AsyncDictionary();
                    mAsyncDictionary.execute(mFieldToTranslate.getText().toString(),
                            mTranslation.getLangFrom() + "-" + mTranslation.getLangTo());
                }
                else
                {
                    Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
            hideKeyboard();
        }
        else
        {
            Toast.makeText(getActivity(), R.string.no_direction, Toast.LENGTH_SHORT).show();
        }
    }

    private void setVisibleDictionaryFragment(boolean visible)
    {
        if (visible)
        {
            mScrollView.setVisibility(View.GONE);
            mFrameForDictionary.setVisibility(View.VISIBLE);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_left_frame_for_dictionary_fragment, DictionaryFragment.newInstance(mDictionary))
                    .commit();
        }
        else
        {
            mScrollView.setVisibility(View.VISIBLE);
            mFrameForDictionary.setVisibility(View.GONE);
        }
        mSaveToFavoritesButton.setEnabled(true);
    }

    @Override
    public void onDestroy()
    {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                .putString(PreferencesScheme.PREF_LANG_FROM, mTranslation.getLangFrom())
                .putString(PreferencesScheme.PREF_LANG_TO, mTranslation.getLangTo())
                .apply();
        super.onDestroy();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private void initializeNewTranslation()
    {
        if (mTranslation == null)
        {
            mTranslation = new Translation(
                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .getString(PreferencesScheme.PREF_LANG_FROM, "ru"),
                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .getString(PreferencesScheme.PREF_LANG_TO, "en"),
                    "",
                    getString(R.string.result_of_translation),
                    false,
                    false,
                    false);
        }
    }

    public Translation getTranslation()
    {
        return mTranslation;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        if (mAsyncDictionary != null)
        {
            mAsyncDictionary.cancel(true);
        }
        if (mAsyncTranslater != null)
        {
            mAsyncTranslater.cancel(true);
        }
    }

    private void showKeyboard()
    {
        mFieldToTranslate.requestFocus();
        mFieldToTranslate.postDelayed(new Runnable()
        {

            @Override
            public void run()
            {
                InputMethodManager keyboard = (InputMethodManager) getActivity().
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(mFieldToTranslate, 0);
            }
        }, 200);
    }
}
