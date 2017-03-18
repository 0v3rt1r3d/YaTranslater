package ru.overtired.yatranslater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import ru.overtired.yatranslater.database.Data;

/**
 * Created by overtired on 14.03.17.
 */

public class TranslateFragment extends Fragment
{
    public final static int REQUEST_LANG_FROM = 0;
    public final static int REQUEST_LANG_TO = 1;

    private String mFromLanguage = "ru";
    private String mToLanguage = "en";

    //View, показывающие языки
    private TextView mFromLanguageTextView;
    private TextView mToLanguageTextView;

    //Остальные элементы управления
    private EditText mFieldToTranslate;
    private ImageButton mSwapLanguagesButton;
    private TextView mResultTextView;

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
                            mFromLanguage+"-"+mToLanguage);
                    hideKeyboard();
                }
                return false;
            }
        });

        mFromLanguageTextView = (TextView) v.findViewById(R.id.from_language_text_view);
        mFromLanguageTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = LanguageChooserActivity.newIntent(getActivity(),false);
                startActivityForResult(intent,REQUEST_LANG_FROM);
            }
        });

        mToLanguageTextView = (TextView) v.findViewById(R.id.to_language_text_view);
        mToLanguageTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = LanguageChooserActivity.newIntent(getActivity(),true);
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
                String temp = mFromLanguage;
                mFromLanguage = mToLanguage;
                mToLanguage = temp;
                updateLanguagesView();
            }
        });


        return v;
    }

    private class AsyncTranslater extends AsyncTask<String,Void,String[]>
    {
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
        }
    }

    private String getFullNameByShortName(String shortName)
    {
        List<Language> mLanguages = Data.get(getActivity()).getLanguages();
        for (Language language:mLanguages)
        {
            if(language.getShortName().equals(shortName))
            {
                return language.getFullName();
            }
        }
        return null;
    }

    private void updateLanguagesView()
    {
        mToLanguageTextView.setText(getFullNameByShortName(mToLanguage));
        mFromLanguageTextView.setText(getFullNameByShortName(mFromLanguage));
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
            mFromLanguage = data.getStringExtra(LanguageChooserActivity.EXTRA_LANG);
            updateLanguagesView();
        }
        else if (requestCode == REQUEST_LANG_TO)
        {
            mToLanguage = data.getStringExtra(LanguageChooserActivity.EXTRA_LANG);
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
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}
