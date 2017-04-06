package ru.overtired.yatranslater.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.activities.MainActivity;
import ru.overtired.yatranslater.structure.dictionary.Dictionary;
import ru.overtired.yatranslater.structure.dictionary.Variant;

public class DictionaryFragment extends Fragment
{
    interface Callback
    {
        void translateNewWord(String text);
    }

    private Unbinder mUnbinder;
    private Callback mCallback;

    private static final String ARG_DICTIONARY = "arg_dictionary";

    @BindView(R.id.frament_dictionary_text_view_main) TextView mMainResult;
    @BindView(R.id.fragment_dictionary_text_view_transcription) TextView mTranscription;
    @BindView(R.id.frament_dictionary_recycler_view) RecyclerView mRecyclerView;

    private Dictionary mDictionary;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);
        mUnbinder = ButterKnife.bind(this,view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(savedInstanceState!=null)
        {
            mDictionary = savedInstanceState.getParcelable(ARG_DICTIONARY);
        } else
        {
            mDictionary = getArguments().getParcelable(ARG_DICTIONARY);
        }

        mRecyclerView.setAdapter(new DictionaryAdapter(mDictionary.getVariants()));
        mMainResult.setText(mDictionary.getText());
        mTranscription.setText(mDictionary.getTranscription());

        return view;
    }

    public static DictionaryFragment newInstance(Dictionary dictionary)
    {
        Bundle args = new Bundle();
        args.putParcelable(ARG_DICTIONARY,dictionary);
        DictionaryFragment fragment = new DictionaryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public class DictionaryHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private Variant mVariant;

        @BindView(R.id.list_dictionary_flexbox_synonyms) FlexboxLayout mFlexSynonyms;
        @BindView(R.id.list_dictionary_flexbox_means) FlexboxLayout mFlexMeans;
        @BindView(R.id.list_dictionary_flexbox_examples) FlexboxLayout mFlexExamples;

        @BindView(R.id.list_dictionary_text_view_number) TextView mNumberText;

        public DictionaryHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);
            mFlexMeans.setVisibility(View.GONE);
            mFlexExamples.setVisibility(View.GONE);
        }

        public void bindDictionary(Variant variant, int number)
        {
            mFlexSynonyms.removeAllViews();
            mFlexMeans.removeAllViews();
            mFlexExamples.removeAllViews();

            mVariant = variant;

            mNumberText.setText((number+1)+". ");
            setTextViewSynonymStyle(mNumberText);

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView textView = new TextView(getActivity());
            textView.setLayoutParams(params);
            textView.setText(mVariant.getText());
            textView.setOnClickListener(this);
            setTextViewSynonymStyle(textView);

            mFlexSynonyms.addView(textView);

            if(variant.getSynonyms().size()!=0)
            {
                textView = new TextView(getActivity());
                textView.setLayoutParams(params);
                textView.setText(", ");
                textView.setLines(1);
                setTextViewSynonymStyle(textView);

                mFlexSynonyms.addView(textView);

                for(int i = 0; i< variant.getSynonyms().size(); i++)
                {
                    textView = new TextView(getActivity());
                    textView.setOnClickListener(this);
                    textView.setLayoutParams(params);
                    textView.setText(variant.getSynonyms().get(i));
                    setTextViewSynonymStyle(textView);

                    mFlexSynonyms.addView(textView);
                    if(i+1!= variant.getSynonyms().size())
                    {
                        textView = new TextView(getActivity());
                        textView.setLayoutParams(params);
                        textView.setText(", ");
                        setTextViewSynonymStyle(textView);
                        mFlexSynonyms.addView(textView);
                    }
                }
            }
            if(variant.getMeans().size()!=0)
            {
                mFlexMeans.setVisibility(View.VISIBLE);
                for(int i = 0; i< variant.getMeans().size(); i++)
                {
                    textView = new TextView(getActivity());
                    textView.setLayoutParams(params);
                    textView.setText(variant.getMeans().get(i));
                    setTextViewMeanStyle(textView);

                    mFlexMeans.addView(textView);

                    if(i+1!= variant.getMeans().size())
                    {
                        textView = new TextView(getActivity());
                        textView.setLayoutParams(params);
                        setTextViewMeanStyle(textView);
                        textView.setText(", ");
                        mFlexMeans.addView(textView);
                    }
                }
            }
            if(variant.getExamples().size()!=0)
            {
                mFlexExamples.setVisibility(View.VISIBLE);
                for(int i = 0; i< variant.getExamples().size(); i++)
                {
                    textView = new TextView(getActivity());
                    textView.setLayoutParams(params);
                    textView.setText(variant.getExamples().get(i).getText()+" - "+
                            variant.getExamples().get(i).getTranslation());
                    setTextViewExampleStyle(textView);

                    mFlexExamples.addView(textView);
                }
            }
        }

        @Override
        public void onClick(View v)
        {
            TextView textView = (TextView)v;
            mCallback.translateNewWord(textView.getText().toString());
        }
    }

    private class DictionaryAdapter extends RecyclerView.Adapter<DictionaryHolder>
    {
        private List<Variant> mVariants;

        public DictionaryAdapter(List<Variant> variants)
        {
            mVariants = variants;
        }

        @Override
        public DictionaryHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_dictionary,parent,false);
            return new DictionaryHolder(view);
        }

        @Override
        public void onBindViewHolder(DictionaryHolder holder, int position)
        {
            holder.bindDictionary(mVariants.get(position),position);
        }

        @Override
        public int getItemCount()
        {
            return mVariants.size();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_DICTIONARY,mDictionary);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mUnbinder.unbind();
    }

//    Методы для изменения стиля TextViews из RecyclerView
    private void setTextViewSynonymStyle(TextView textView)
    {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        textView.setTextColor(getResources().getColor(R.color.colorBlack));
    }

    private void setTextViewMeanStyle(TextView textView)
    {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void setTextViewExampleStyle(TextView textView)
    {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        textView.setTextColor(getResources().getColor(R.color.colorGrey));
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mCallback = (Callback)getFragmentManager().findFragmentByTag(MainActivity.TAG_LEFT_FRAGMENT);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mCallback = null;
    }
}
