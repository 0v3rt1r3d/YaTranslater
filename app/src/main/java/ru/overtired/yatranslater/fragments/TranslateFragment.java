package ru.overtired.yatranslater.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.activities.LanguageChooseActivity;
import ru.overtired.yatranslater.activities.SplashActivity;
import ru.overtired.yatranslater.database.Data;
import ru.overtired.yatranslater.database.PreferencesScheme;
import ru.overtired.yatranslater.database.Translater;
import ru.overtired.yatranslater.structure.Translation;
import ru.overtired.yatranslater.structure.dictionary.Dictionary;

/**
 * Created by overtired on 14.03.17.
 */

public class TranslateFragment extends Fragment
{
    public final static int REQUEST_LANG_FROM = 0;
    public final static int REQUEST_LANG_TO = 1;

    private static final String ARG_DICTIONARY = "arg_dictionary";
    private static final String ARG_TRANSLATION = "arg_translation";

    private Unbinder mUnbinder;

    private Translation mTranslation;
    private Dictionary mDictionary;

    @BindView(R.id.from_language_text_view)
    TextView mFromLanguageTextView;
    @BindView(R.id.to_language_text_view)
    TextView mToLanguageTextView;
    @BindView(R.id.field_for_translate)
    EditText mFieldToTranslate;
    @BindView(R.id.translated_text_view)
    TextView mResultTextView;
    @BindView(R.id.swap_languages_button)
    ImageButton mSwapLanguagesButton;
    @BindView(R.id.button_bookmark)
    ImageButton mSaveToFavoritesButton;
    @BindView(R.id.translate_fragment_scroll_view)
    ScrollView mScrollView;

    private AsyncTranslater mAsyncTranslater;
    private AsyncDictionary mAsyncDictionary;

    private FrameLayout mFrameForDictionary;

    public static TranslateFragment newInstance(Translation translation)
    {
//        Для инициализации уже готового перевода из истории
        Bundle args = new Bundle();
        args.putParcelable(ARG_TRANSLATION, translation);

        TranslateFragment fragment = new TranslateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TranslateFragment newInstance()
    {
        TranslateFragment fragment = new TranslateFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);

        mUnbinder = ButterKnife.bind(this, view);

//        Контейнер под фрагмент нахожу отдельно, потому что ссылка него нужна после уничтожения View
        mFrameForDictionary = (FrameLayout) view.findViewById(R.id.translation_container);

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
        else if (getArguments() != null && getArguments().getParcelable(ARG_TRANSLATION) != null)
        {
//            Инициализация при запуске из истории-избранного
            mTranslation = getArguments().getParcelable(ARG_TRANSLATION);
            updateView();
            translate();
//            В случае, если перевод был передан в аргументах
        }
        else if (mTranslation != null)
        {
//            Восстановление, если фрагмент не был на переднем плане
            setVisibleDictionaryFragment(mDictionary != null);
            updateView();
//            Т.е. не восстанавливалась после поворота
            // TODO: 01.04.17 Возможно этот метод не нужен, можно прямо сюда его вставить
        }
        else
        {
//            Создание с нуля, если запускается впервые
            initializeNewTranslation();
            updateView();
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
                Data.get(getActivity()).updateTranslation(mTranslation);
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
                String temp = mTranslation.getLangFrom();
                mTranslation.setLangFrom(mTranslation.getLangTo());
                mTranslation.setLangTo(temp);
                updateView();
            }
        });
    }

    //    Классы для перевода текста из переводчика и словаря
    private class AsyncTranslater extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            Translater translater = new Translater();
            return translater.getTranslation(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String string)
        {
            mTranslation.setTextTo(string);
            mTranslation.setInDictionary(false);
            mTranslation.setInHistory(true);

            setVisibleDictionaryFragment(false);
            Data.get(getActivity()).addTranslation(mTranslation);
            mResultTextView.setText(mTranslation.getTextTo());
        }
    }

    private class AsyncDictionary extends AsyncTask<String, Void, Dictionary>
    {
        @Override
        protected Dictionary doInBackground(String... params)
        {
            Translater translater = new Translater();
            return translater.getDictionary(getActivity(), params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Dictionary dictionary)
        {
            if (dictionary != null)
            {
                mDictionary = dictionary;

                mTranslation.setTextTo(dictionary.getTranslations().get(0).getText());
                mTranslation.setInDictionary(true);
                mTranslation.setInHistory(true);

                Data.get(getActivity()).addTranslation(mTranslation);
                updateView();

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
        mToLanguageTextView.setText(Data.get(getActivity())
                .getFullLanguageByShort(mTranslation.getLangTo()));
        mFromLanguageTextView.setText(Data.get(getActivity())
                .getFullLanguageByShort(mTranslation.getLangFrom()));
        mFieldToTranslate.setText(mTranslation.getTextFrom());
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
        if (Data.get(getActivity()).hasDirection(mTranslation.getLangFrom() + "-" + mTranslation.getLangTo()))
        {
            mTranslation.setFavorite(false);
            mTranslation.setTextFrom(mFieldToTranslate.getText().toString());
            updateView();

            Translation temp = Data.get(getActivity()).getTranslation(mTranslation);
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
                if (SplashActivity.hasInternetConnection(getActivity()))
                {
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
            // TODO: 04.04.17 анимация загрузки
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
                    .replace(R.id.translation_container, ResultFragment.newInstance(mDictionary))
                    .commit();
        }
        else
        {
            mScrollView.setVisibility(View.VISIBLE);
            mFrameForDictionary.setVisibility(View.GONE);
        }
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
}
