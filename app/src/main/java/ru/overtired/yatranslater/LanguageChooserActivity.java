package ru.overtired.yatranslater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.overtired.yatranslater.database.Data;

public class LanguageChooserActivity extends AppCompatActivity
{
    private RecyclerView mRecyclerView;
    private List<Language> mLanguages;

    private static final String EXTRA_DIR = "ru.overtired.yatranslater.direction";

    public static final String EXTRA_LANG = "ru.overtired.yatranslater.lang";

    public static Intent newIntent(Context context,boolean isLanguageTo)
    {
        Intent intent = new Intent(context,LanguageChooserActivity.class);
        intent.putExtra(EXTRA_DIR,isLanguageTo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_chooser);

        mRecyclerView = (RecyclerView) findViewById(R.id.language_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(LanguageChooserActivity.this));

        mLanguages = Data.get(LanguageChooserActivity.this).getLanguages();

        LanguageAdapter adapter = new LanguageAdapter(mLanguages);

        mRecyclerView.setAdapter(adapter);

        AppCompatActivity activity = (AppCompatActivity)this;

        if(getIntent().getBooleanExtra(EXTRA_DIR,false))
        {
            activity.getSupportActionBar().setTitle(R.string.language_translate);
        }else
        {
            activity.getSupportActionBar().setTitle(R.string.language_text);
        }
    }

    private class LanguageHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private Language mLanguage;

        private TextView mTextView;

        public LanguageHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            mTextView = (TextView) itemView.findViewById(R.id.list_language_text_view);
        }

        public void bindLanguage(Language language)
        {
            mLanguage = language;
            mTextView.setText(language.getFullName());
        }

        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_LANG,mLanguage.getShortName());
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
    }

    private class LanguageAdapter extends RecyclerView.Adapter<LanguageHolder>
    {
        private List<Language> mLanguages;

        public LanguageAdapter(List<Language> languages)
        {
            mLanguages = languages;
        }

        @Override
        public LanguageHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(LanguageChooserActivity.this);
            View v = layoutInflater.inflate(R.layout.list_language,parent,false);
            return new LanguageHolder(v);
        }

        @Override
        public void onBindViewHolder(LanguageHolder holder, int position)
        {
            Language language = mLanguages.get(position);
            holder.bindLanguage(language);
        }

        @Override
        public int getItemCount()
        {
            return mLanguages.size();
        }
    }
}
