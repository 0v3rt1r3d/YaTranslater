package ru.overtired.yatranslater;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by overtired on 14.03.17.
 */

public class TranslateFragment extends Fragment
{
    private TextView mFromLanguageTextView;
    private TextView mToLanguageTextView;

    private EditText mFieldToTranslate;
    private Button mTranslateButton;
    private TextView mResultTextView;
    private Toolbar mToolbar;

    private List<Language> mLanguages;

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
        mTranslateButton = (Button) v.findViewById(R.id.button_translate);
        mTranslateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new AsyncTranslater().execute(mFieldToTranslate.getText().toString(),"en");
            }
        });

        mResultTextView = (TextView) v.findViewById(R.id.translated_text_view);
        mFieldToTranslate = (EditText) v.findViewById(R.id.field_for_translate);

        mToolbar =(Toolbar) v.findViewById(R.id.toolbar_translate);

        mToLanguageTextView = (TextView) v.findViewById(R.id.to_language_text_view);
        mToLanguageTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = LanguageChooserActivity.newIntent(getActivity());
                startActivity(intent);
            }
        });

        mFromLanguageTextView = (TextView) v.findViewById(R.id.from_language_text_view);
        mFromLanguageTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = LanguageChooserActivity.newIntent(getActivity());
                startActivity(intent);
            }
        });

        new LanguageTaker().execute("ru");

        return v;
    }

    private class LanguageTaker extends AsyncTask<String,Void,List<Language>>
    {
        @Override
        protected List<Language> doInBackground(String... params)
        {
            Translater translater = new Translater();
            return translater.getLanguages(getActivity(),params[0]);
        }

        @Override
        protected void onPostExecute(List<Language> languages)
        {
            //Нерационально беру элементы, нужен хешмап или что-то вроде того
            mLanguages = languages;
            for (Language language:mLanguages)
            {
                if(language.getShortName().equals("ru"))
                {
                    mFromLanguageTextView.setText(language.getFullName());
                }
            }
            for (Language language:mLanguages)
            {
                if(language.getShortName().equals("en"))
                {
                    mToLanguageTextView.setText(language.getFullName());
                }
            }
        }
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
}