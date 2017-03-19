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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

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

    private String mShortLangFrom = "en";
    private String mShortLangTo = "ru";
    private String mTextFrom;
    private String mTextTo;

    private Translation mTranslation;

    //View, показывающие языки
    private TextView mFromLanguageTextView;
    private TextView mToLanguageTextView;
    private EditText mFieldToTranslate;
    private TextView mResultTextView;
    private ImageButton mSwapLanguagesButton;
    private ImageButton mSaveToHistoryButton;

    private AsyncTranslater mTranslater;

    public static TranslateFragment newInstance()
    {
//        Bundle args = new Bundle();

        TranslateFragment fragment = new TranslateFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_translate,container,false);

        mResultTextView = (TextView) v.findViewById(R.id.translated_text_view);

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
                            mShortLangFrom +"-"+mShortLangTo);
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

        updateLanguagesView();

        mSwapLanguagesButton = (ImageButton) v.findViewById(R.id.swap_languages_button);
        mSwapLanguagesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String temp = mShortLangFrom;
                mShortLangFrom = mShortLangTo;
                mShortLangTo = temp;
                updateLanguagesView();
            }
        });

        mSaveToHistoryButton = (ImageButton)v.findViewById(R.id.button_bookmark);
        mSaveToHistoryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //тут добавление в избранное
                mSaveToHistoryButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                mTranslation.setFavorite(true);
                Data.get(getActivity()).updateTranslation(mTranslation);
                // TODO: 19.03.17 Нужно сделать именно добавление в избранное
            }
        });

        return v;
    }

//    AsynkTask для для перевода текста
    private class AsyncTranslater extends AsyncTask<String,Void,String[]>
    {
        @Override
        protected void onPreExecute()
        {
            // TODO: 19.03.17 Временно блокирую кнопку сохранения в избранное, не знаю зачем
            mSaveToHistoryButton.setEnabled(false);
        }

        @Override
        protected String[] doInBackground(String... params)
        {
            Translater translater = new Translater();
            return translater.getTranslation(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String[] strings)
        {
            mResultTextView.setText(strings[0]);
            mTranslation = new Translation(mShortLangFrom,mShortLangTo,
                    mFieldToTranslate.getText().toString(),
                    mResultTextView.getText().toString(),false);
            Data.get(getActivity()).addTranslation(mTranslation);
            mSaveToHistoryButton.setEnabled(true);
            Toast.makeText(getActivity(),Integer.toString(Data.get(getActivity()).getHistory().size()),Toast.LENGTH_SHORT).show();
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

//    Метод обновляет название языков в верхнем баре
    private void updateLanguagesView()
    {
        mToLanguageTextView.setText(getFullNameByShortName(mShortLangTo));
        mFromLanguageTextView.setText(getFullNameByShortName(mShortLangFrom));
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
            mShortLangFrom = data.getStringExtra(LanguageChooseActivity.EXTRA_LANG);
            updateLanguagesView();
        }
        else if (requestCode == REQUEST_LANG_TO)
        {
            mShortLangTo = data.getStringExtra(LanguageChooseActivity.EXTRA_LANG);
            updateLanguagesView();
        }
    }

    public static boolean hasInternetConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
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
}