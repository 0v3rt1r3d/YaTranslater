package ru.overtired.yatranslater.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import ru.overtired.yatranslater.structure.Dictionary;
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

    private static final String ARG_LANG_TO = "arg_lang_to";
    private static final String ARG_LANG_FROM = "arg_lang_from";
    private static final String ARG_TEXT_TO = "arg_text_to";
    private static final String ARG_TEXT_FROM = "arg_text_from";
    private static final String ARG_IS_FAVORITE = "arg_is_favorite";
    private static final String ARG_ID = "arg_id";

    private Translation mTranslation;

    //View, показывающие языки
    private TextView mFromLanguageTextView;
    private TextView mToLanguageTextView;
    private EditText mFieldToTranslate;
    private TextView mResultTextView;
    private ImageButton mSwapLanguagesButton;
    private ImageButton mSaveToHistoryButton;
    private ImageButton mTranslateButton;

    private AsyncTranslater mTranslater;

    private ResultFragment mResultFragment;
    private Dictionary mDictionary;


    public static TranslateFragment newInstance(Translation translation)
    {
        TranslateFragment fragment = new TranslateFragment();
        Bundle args = new Bundle();

        args.putString(ARG_LANG_FROM,translation.getLangFrom());
        args.putString(ARG_LANG_TO,translation.getLangTo());
        args.putString(ARG_TEXT_FROM,translation.getTextFrom());
        args.putString(ARG_TEXT_TO,translation.getTextTo());
        args.putString(ARG_ID,translation.getId().toString());
        args.putBoolean(ARG_IS_FAVORITE,translation.isFavorite());

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
        View v = inflater.inflate(R.layout.fragment_translate,container,false);

        if(getArguments()!=null)
        {
            String langFrom = getArguments().getString(ARG_LANG_FROM);
            String langTo = getArguments().getString(ARG_LANG_TO);
            String textFrom = getArguments().getString(ARG_TEXT_FROM);
            String textTo = getArguments().getString(ARG_TEXT_TO);
            String id = getArguments().getString(ARG_ID);
            boolean isFavorite = getArguments().getBoolean(ARG_IS_FAVORITE);

            mTranslation = new Translation(langFrom,langTo,textFrom,textTo, id, isFavorite);
        }else
        {
            mTranslation = new Translation(
                    "ru",
                    "en",
                    "",
                    getString(R.string.result_of_translation),
                    false);
        }

        mTranslateButton = (ImageButton) v.findViewById(R.id.button_translate);
        mTranslateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new AsyncDictionary().execute(mFieldToTranslate.getText().toString(),
                        mTranslation.getLangFrom() +"-"+mTranslation.getLangTo());
                hideKeyboard();
            }
        });

        mResultTextView = (TextView) v.findViewById(R.id.translated_text_view);
        mResultTextView.setVisibility(View.INVISIBLE);

        mSaveToHistoryButton = (ImageButton)v.findViewById(R.id.button_bookmark);
        mSaveToHistoryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mTranslation.isFavorite())
                {
                    mTranslation.setFavorite(false);
                    mSaveToHistoryButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_favorite));
                }else
                {
                    mTranslation.setFavorite(true);
                    mSaveToHistoryButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                }
                Data.get(getActivity()).updateTranslation(mTranslation);
            }
        });
        mFieldToTranslate = (EditText) v.findViewById(R.id.field_for_translate);
        mFieldToTranslate.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                //Тут происходит перевод, при нажатии "done" на клавиатуре
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    if(mTranslater == null)
                    {
                        mTranslater = new AsyncTranslater();
                    }else
                    {
                        mTranslater.cancel(true);
                    }
                    mTranslater = new AsyncTranslater();
                    mTranslater.execute(mFieldToTranslate.getText().toString(),
                            mTranslation.getLangFrom() +"-"+mTranslation.getLangTo());
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
                Intent intent = LanguageChooseActivity.newIntent(getActivity(),false);
                startActivityForResult(intent,REQUEST_LANG_FROM);
            }
        });

        mToLanguageTextView = (TextView) v.findViewById(R.id.to_language_text_view);
        mToLanguageTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = LanguageChooseActivity.newIntent(getActivity(),true);
                startActivityForResult(intent,REQUEST_LANG_TO);
            }
        });

        updateView();

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

        mResultFragment = ResultFragment.newInstance();
        getFragmentManager().beginTransaction()
                .add(R.id.translation_container,mResultFragment)
                .commit();

        return v;
    }

//    AsynkTask для для перевода текста
    private class AsyncTranslater extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute()
        {
            // TODO: 19.03.17 Временно блокирую кнопку сохранения в избранное, не знаю зачем
            mSaveToHistoryButton.setEnabled(false);
        }

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
                    false);
            Data.get(getActivity()).addTranslation(mTranslation);
            mSaveToHistoryButton.setEnabled(true);
            Toast.makeText(getActivity(),Integer.toString(Data.get(getActivity()).getHistory().size()),Toast.LENGTH_SHORT).show();
        }
    }

    private class AsyncDictionary extends AsyncTask<String,Void,Dictionary>
    {
        @Override
        protected Dictionary doInBackground(String... params)
        {
            Translater translater = new Translater();

            return translater.getDictionary(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(Dictionary dictionary)
        {
            mDictionary = dictionary;

            mResultFragment.setDictionary(mDictionary);
//            getFragmentManager().beginTransaction()
//                    .replace(R.id.translation_container,mResultFragment)
//                    .commit();
        }
    }

    private String getFullNameByShortName(String shortName)
    {
        List<Language> mLanguages = Data.get(getActivity()).getLanguages();
        for (Language language:mLanguages)
        {
            if(language.getShortLang().equals(shortName))
            {
                return language.getFullLang();
            }
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode!= Activity.RESULT_OK)
        {
            return;
        }
        if(requestCode==REQUEST_LANG_FROM)
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
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void updateView()
    {
        mToLanguageTextView.setText(getFullNameByShortName(mTranslation.getLangTo()));
        mFromLanguageTextView.setText(getFullNameByShortName(mTranslation.getLangFrom()));
        mFieldToTranslate.setText(mTranslation.getTextFrom());
        mResultTextView.setText(mTranslation.getTextTo());
        if(mTranslation.isFavorite())
        {
            mSaveToHistoryButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
        }else
        {
            mSaveToHistoryButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_favorite));
        }

    }
}
