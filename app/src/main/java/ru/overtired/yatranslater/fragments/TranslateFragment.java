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

import java.util.List;

import ru.overtired.yatranslater.activities.SplashActivity;
import ru.overtired.yatranslater.database.PreferencesScheme;
import ru.overtired.yatranslater.structure.dictionary.Dictionary;
import ru.overtired.yatranslater.structure.Language;
import ru.overtired.yatranslater.activities.LanguageChooseActivity;
import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.database.Translater;
import ru.overtired.yatranslater.structure.Translation;
import ru.overtired.yatranslater.database.Data;

/**
 * Created by overtired on 14.03.17.
 */

public class TranslateFragment extends Fragment
{
    public final static int REQUEST_LANG_FROM = 0;
    public final static int REQUEST_LANG_TO = 1;

    private static final String ARG_ID = "arg_id";
    private static final String ARG_DICTIONARY = "arg_dictionary";

    private Translation mTranslation;

    //View, показывающие языки
    private TextView mFromLanguageTextView;
    private TextView mToLanguageTextView;
    private EditText mFieldToTranslate;
    private TextView mResultTextView;
    private ImageButton mSwapLanguagesButton;
    private ImageButton mSaveToHistoryButton;

    private AsyncTranslater mAsyncTranslater;
    private AsyncDictionary mAsyncDictionary;

    private FrameLayout mFrameForDictionary;
    private ResultFragment mResultFragment;
    private Dictionary mDictionary;

    private ScrollView mScrollView;


    public static TranslateFragment newInstance(String id)
    {
        TranslateFragment fragment = new TranslateFragment();
        Bundle args = new Bundle();

        args.putString(ARG_ID, id);

        fragment.setArguments(args);
        return fragment;
    }

    public static TranslateFragment newInstance()
    {
        return new TranslateFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_translate, container, false);

        mScrollView = (ScrollView) v.findViewById(R.id.translate_fragment_scroll_view);
        mFrameForDictionary = (FrameLayout) v.findViewById(R.id.translation_container);

        mResultTextView = (TextView) v.findViewById(R.id.translated_text_view);

        mSaveToHistoryButton = (ImageButton) v.findViewById(R.id.button_bookmark);
        mSaveToHistoryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mTranslation.isFavorite())
                {
                    mTranslation.setFavorite(false);
                    mSaveToHistoryButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_favorite));
                }
                else
                {
                    mTranslation.setFavorite(true);
                    mSaveToHistoryButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                }
                Data.get(getActivity()).updateTranslation(mTranslation);
            }
        });
        mFieldToTranslate = (EditText) v.findViewById(R.id.field_for_translate);
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
        mFieldToTranslate.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                mSaveToHistoryButton.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_not_favorite));
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        mFromLanguageTextView = (TextView) v.findViewById(R.id.from_language_text_view);
        mFromLanguageTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = LanguageChooseActivity.newIntent(getActivity(), false);
                startActivityForResult(intent, REQUEST_LANG_FROM);
            }
        });

        mToLanguageTextView = (TextView) v.findViewById(R.id.to_language_text_view);
        mToLanguageTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = LanguageChooseActivity.newIntent(getActivity(), true);
                startActivityForResult(intent, REQUEST_LANG_TO);
            }
        });

        mSwapLanguagesButton = (ImageButton) v.findViewById(R.id.swap_languages_button);
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

//        Фрагмент для вывода информации, запрошенной из словаря
        mResultFragment = ResultFragment.newInstance();


        if (getArguments() != null)
        {
            String id = getArguments().getString(ARG_ID);
            if (Data.get(getActivity()).hasTranslation(id))
            {
                mTranslation = Data.get(getActivity()).getTranslation(id);
                updateView();
                if(SplashActivity.hasInternetConnection(getActivity()))
                {
                    translate();
                }
            }
            else
            {
                initializeNewTranslation();
                updateView();
            }
        }
        else if (savedInstanceState != null)
        {
            String id = savedInstanceState.getString(ARG_ID);
            if(Data.get(getActivity()).hasTranslation(id))
            {
                mTranslation = Data.get(getActivity()).getTranslation(id);
                mDictionary = savedInstanceState.getParcelable(ARG_DICTIONARY);
                if(mDictionary!=null)
                {
                    setVisibleDictionaryFragment(true);
                    mResultFragment.setDictionary(mDictionary);
                    // TODO: 29.03.17 Разобраться с восстановлением данных из словаря
                    
                }//elseif
                updateView();
                if(SplashActivity.hasInternetConnection(getActivity()))
                {
                    translate();
                }
            }
            else
            {
                initializeNewTranslation();
                updateView();
            }
        }
        else
        {
            initializeNewTranslation();
            updateView();
        }

        getFragmentManager().beginTransaction()
                .add(R.id.translation_container, mResultFragment)
                .commit();

        return v;
    }

    //    AsynkTask для для перевода текста
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
            mResultTextView.setText(string);
            mTranslation = new Translation(
                    mTranslation.getLangFrom(),
                    mTranslation.getLangTo(),
                    mFieldToTranslate.getText().toString(),
                    mResultTextView.getText().toString(),
                    false,
                    true);
            setVisibleDictionaryFragment(false);
            Data.get(getActivity()).addTranslation(mTranslation);
            mSaveToHistoryButton.setEnabled(true);
            mResultTextView.setText(mTranslation.getTextTo());
        }
    }

    private class AsyncDictionary extends AsyncTask<String, Void, Dictionary>
    {
        @Override
        protected Dictionary doInBackground(String... params)
        {
            Translater translater = new Translater();
            return translater.getDictionary(getActivity(),params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Dictionary dictionary)
        {
            if (dictionary != null)
            {
                mDictionary = dictionary;
                mResultFragment.setDictionary(mDictionary);

                mTranslation = new Translation(
                        mTranslation.getLangFrom(),
                        mTranslation.getLangTo(),
                        mFieldToTranslate.getText().toString(),
                        mDictionary.getTranslations().get(0).getText(),
                        false,
                        true);
                Data.get(getActivity()).addTranslation(mTranslation);

                setVisibleDictionaryFragment(true);
                mSaveToHistoryButton.setEnabled(true);
            }
            else
            {
                setVisibleDictionaryFragment(false);
                useTranslaterAPI();
            }
        }
    }

    private String getFullNameByShortName(String shortName)
    {
        List<Language> mLanguages = Data.get(getActivity()).getLanguages();
        for (Language language : mLanguages)
        {
            if (language.getShortLang().equals(shortName))
            {
                return language.getFullLang();
            }
        }
        return null;
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
        saveLangsToPrefs();
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

    public void updateView()
    {
        mToLanguageTextView.setText(getFullNameByShortName(mTranslation.getLangTo()));
        mFromLanguageTextView.setText(getFullNameByShortName(mTranslation.getLangFrom()));
        mFieldToTranslate.setText(mTranslation.getTextFrom());
        mResultTextView.setText(mTranslation.getTextTo());
        if (mTranslation.isFavorite())
        {
            mSaveToHistoryButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
        }
        else
        {
            mSaveToHistoryButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_favorite));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_ID, mTranslation.getId().toString());
        if (mFrameForDictionary.getVisibility() == View.VISIBLE)
        {
            outState.putParcelable(ARG_DICTIONARY,mDictionary);
        }
    }

    private void translate()
    {
        if(Data.get(getActivity()).hasDirection(mTranslation.getLangFrom()+
            "-"+mTranslation.getLangTo()))
        {
            if (SplashActivity.hasInternetConnection(getActivity()))
            {
                int countOfWords = mFieldToTranslate.getText().toString().split(" ").length;
                if (countOfWords > 2)
                {
                    useTranslaterAPI();
                }
                else
                {
                    useDictionaryAPI();
                }
                hideKeyboard();
            }
            else
            {
                Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
        } else
        {
            Toast.makeText(getActivity(),R.string.no_direction,Toast.LENGTH_SHORT).show();
        }
    }

    private void useTranslaterAPI()
    {
        if (mAsyncTranslater != null)
        {
            mAsyncTranslater.cancel(true);
        }
        mAsyncTranslater = new AsyncTranslater();
        mAsyncTranslater.execute(mFieldToTranslate.getText().toString(),
                mTranslation.getLangFrom() + "-" + mTranslation.getLangTo());
    }

    private void useDictionaryAPI()
    {
        if (mAsyncDictionary != null)
        {
            mAsyncDictionary.cancel(true);
        }
        mAsyncDictionary = new AsyncDictionary();
        mAsyncDictionary.execute(mFieldToTranslate.getText().toString(),
                mTranslation.getLangFrom() + "-" + mTranslation.getLangTo());
    }

    private void setVisibleDictionaryFragment(boolean visible)
    {
        if (visible)
        {
            mScrollView.setVisibility(View.GONE);
            mFrameForDictionary.setVisibility(View.VISIBLE);
        }
        else
        {
            mScrollView.setVisibility(View.VISIBLE);
            mFrameForDictionary.setVisibility(View.GONE);
        }
    }

    private void saveLangsToPrefs()
    {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                .putString(PreferencesScheme.PREF_LANG_FROM, mTranslation.getLangFrom())
                .putString(PreferencesScheme.PREF_LANG_TO, mTranslation.getLangTo())
                .apply();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        saveLangsToPrefs();
    }

    private void initializeNewTranslation()
    {
        mTranslation = new Translation(
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getString(PreferencesScheme.PREF_LANG_FROM, "ru"),
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getString(PreferencesScheme.PREF_LANG_TO, "en"),
                "",
                getString(R.string.result_of_translation),
                false,
                false);
    }
}
