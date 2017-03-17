package ru.overtired.yatranslater;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import ru.overtired.yatranslater.database.Data;

/**
 * Created by overtired on 14.03.17.
 */

public class TranslateFragment extends Fragment
{
    private Data mData;

    //View, показывающие языки
    private TextView mFromLanguageTextView;
    private TextView mToLanguageTextView;

    //Остальные элементы управления
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

    private void updateLanguages()
    {

        for(int i=0;i<mLanguages.size();i++)
        {
            if(mLanguages.get(i).getShortName().equals("ru"))
            {
                mFromLanguageTextView.setText(mLanguages.get(i).getFullName());
            }
            if(mLanguages.get(i).getShortName().equals("en"))
            {
                mToLanguageTextView.setText(mLanguages.get(i).getFullName());
            }
        }
    }
}
