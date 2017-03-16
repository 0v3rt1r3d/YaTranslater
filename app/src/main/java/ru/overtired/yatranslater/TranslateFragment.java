package ru.overtired.yatranslater;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
    private EditText mFieldToTranslate;
    private Button mTranslateButton;
    private TextView mResultTextView;

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
            super.onPostExecute(languages);
        }
    }

    private class AsyncTranslater extends AsyncTask<String,Void,String[]>
    {
        @Override
        protected String[] doInBackground(String... params)
        {
            Translater translater = new Translater();
            return translater.getTranslation(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(String[] strings)
        {
            mResultTextView.setText(strings[0]);
        }
    }
}
