package ru.overtired.yatranslater.activities;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.overtired.yatranslater.structure.Language;
import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.database.Singleton;

//Активность используется для выбора языка перевода из верхнего бара

public class LanguageChooseActivity extends AppCompatActivity
{
    @BindView(R.id.activity_language_chooser_recycler_view) RecyclerView mRecyclerView;

    private List<Language> mLanguages;

//    EXTRA_FROM_OR_TO_LANG - это направление перевода, to/from
//    EXTRA_LANG - собственно аргумент перевода, "ru"
    public static final String EXTRA_LANG = "ru.overtired.yatranslater.lang";
    private static final String EXTRA_FROM_OR_TO_LANG = "ru.overtired.yatranslater.direction";

    public static Intent newIntent(Context context,boolean isLanguageTo)
    {
        Intent intent = new Intent(context,LanguageChooseActivity.class);
        intent.putExtra(EXTRA_FROM_OR_TO_LANG,isLanguageTo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_chooser);
        ButterKnife.bind(this);

//        Заполняется список языков из синглетона
        mRecyclerView.setLayoutManager(new LinearLayoutManager(LanguageChooseActivity.this));
        mLanguages = Singleton.get(LanguageChooseActivity.this).getLanguages();
        LanguageAdapter adapter = new LanguageAdapter(mLanguages);
        mRecyclerView.setAdapter(adapter);

//        В зависимости от направления перевода меняется заголовок
        AppCompatActivity activity = (AppCompatActivity)this;
        if(getIntent().getBooleanExtra(EXTRA_FROM_OR_TO_LANG,false))
        {
            activity.getSupportActionBar().setTitle(R.string.language_translate);
        }else
        {
            activity.getSupportActionBar().setTitle(R.string.language_text);
        }
    }

    public class LanguageHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private Language mLanguage;
        @BindView(R.id.list_language_text_view) TextView mTextView;

        public LanguageHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        public void bindLanguage(Language language)
        {
            mLanguage = language;
            mTextView.setText(language.getFullLang());
        }

        @Override
        public void onClick(View v)
        {
//            После выбора языка данные передаются родительской активности
            Intent intent = new Intent();
            intent.putExtra(EXTRA_LANG,mLanguage.getShortLang());
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
            LayoutInflater layoutInflater = LayoutInflater.from(LanguageChooseActivity.this);
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