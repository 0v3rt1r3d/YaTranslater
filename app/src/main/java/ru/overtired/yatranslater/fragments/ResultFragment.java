package ru.overtired.yatranslater.fragments;

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
import ru.overtired.yatranslater.structure.dictionary.Dictionary;

public class ResultFragment extends Fragment
{
    private Unbinder mUnbinder;

    private static final String ARG_DICTIONARY = "arg_dictionary";

    @BindView(R.id.frament_result_main) TextView mMainResult;
    @BindView(R.id.fragment_result_transcription) TextView mTranscription;
    @BindView(R.id.frament_result_recycler_view) RecyclerView mRecyclerView;

    private Dictionary mDictionary;

    private List<ru.overtired.yatranslater.structure.dictionary.Translation> mTranslations;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        mUnbinder = ButterKnife.bind(this,view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(savedInstanceState!=null)
        {
            mDictionary = savedInstanceState.getParcelable(ARG_DICTIONARY);
        } else
        {
            mDictionary = getArguments().getParcelable(ARG_DICTIONARY);
        }

        mRecyclerView.setAdapter(new DicAdapter(mDictionary.getTranslations()));
        mMainResult.setText(mDictionary.getText());
        mTranscription.setText(mDictionary.getTranscription());

        return view;
    }

    public static ResultFragment newInstance(Dictionary dictionary)
    {
        Bundle args = new Bundle();
        args.putParcelable(ARG_DICTIONARY,dictionary);
        ResultFragment fragment = new ResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

//    Метод setDictionary меняет содержимое этого фрагмента, обновляет информацию
//    public void setDictionary(Dictionary dictionary)
//    {
//        mDictionary = dictionary;
//        if(mMainResult!=null)
//        {
//            updateView();
//        }
//    }

    private class DicHolder extends RecyclerView.ViewHolder
    {
        private ru.overtired.yatranslater.structure.dictionary.Translation mTranslation;

        private FlexboxLayout mFlexSynonyms;
        private FlexboxLayout mFlexMeans;
        private FlexboxLayout mFlexExamples;

        private TextView mNumberText;

        public DicHolder(View itemView)
        {
            super(itemView);

            mFlexSynonyms = (FlexboxLayout) itemView.findViewById(R.id.list_dic_flex_synonyms);
            mFlexMeans = (FlexboxLayout) itemView.findViewById(R.id.list_dic_flex_means);
            mFlexExamples = (FlexboxLayout) itemView.findViewById(R.id.list_dic_flex_examples);

            mNumberText = (TextView) itemView.findViewById(R.id.list_dic_tv_number);

            mFlexMeans.setVisibility(View.GONE);
            mFlexExamples.setVisibility(View.GONE);
        }

        public void bindDic(ru.overtired.yatranslater.structure.dictionary.Translation translation, int number)
        {
            mFlexSynonyms.removeAllViews();
            mFlexMeans.removeAllViews();
            mFlexExamples.removeAllViews();

            mTranslation = translation;

            mNumberText.setText((number+1)+". ");
            setTextViewSynonymStyle(mNumberText);

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView textView = new TextView(getActivity());
            textView.setLayoutParams(params);
            textView.setText(mTranslation.getText());
            setTextViewSynonymStyle(textView);

            mFlexSynonyms.addView(textView);

            if(translation.getSynonyms().size()!=0)
            {
                textView = new TextView(getActivity());
                textView.setLayoutParams(params);
                textView.setText(", ");
                textView.setLines(1);
                setTextViewSynonymStyle(textView);

                mFlexSynonyms.addView(textView);

                for(int i=0;i<translation.getSynonyms().size();i++)
                {
                    textView = new TextView(getActivity());
                    textView.setLayoutParams(params);
                    textView.setText(translation.getSynonyms().get(i));
                    setTextViewSynonymStyle(textView);

                    mFlexSynonyms.addView(textView);
                    if(i+1!=translation.getSynonyms().size())
                    {
                        textView = new TextView(getActivity());
                        textView.setLayoutParams(params);
                        textView.setText(", ");
                        setTextViewSynonymStyle(textView);
                        mFlexSynonyms.addView(textView);
                    }
                }
            }
            if(translation.getMeans().size()!=0)
            {
                mFlexMeans.setVisibility(View.VISIBLE);
                for(int i=0;i<translation.getMeans().size();i++)
                {
                    textView = new TextView(getActivity());
                    textView.setLayoutParams(params);
                    textView.setText(translation.getMeans().get(i));
                    setTextViewMeanStyle(textView);

                    mFlexMeans.addView(textView);

                    if(i+1!=translation.getMeans().size())
                    {
                        textView = new TextView(getActivity());
                        textView.setLayoutParams(params);
                        setTextViewMeanStyle(textView);
                        textView.setText(", ");
                        mFlexMeans.addView(textView);
                    }
                }
            }
            if(translation.getExamples().size()!=0)
            {
                mFlexExamples.setVisibility(View.VISIBLE);
                for(int i=0;i<translation.getExamples().size();i++)
                {
                    textView = new TextView(getActivity());
                    textView.setLayoutParams(params);
                    textView.setText(translation.getExamples().get(i).getText()+" - "+
                            translation.getExamples().get(i).getTranslation());
                    setTextViewExampleStyle(textView);

                    mFlexExamples.addView(textView);
                }
            }
        }
    }

    private class DicAdapter extends RecyclerView.Adapter<DicHolder>
    {
        private List<ru.overtired.yatranslater.structure.dictionary.Translation> mTranslations;

        public DicAdapter(List<ru.overtired.yatranslater.structure.dictionary.Translation> translations)
        {
            mTranslations = translations;
        }

        @Override
        public DicHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_dictionary,parent,false);
            return new DicHolder(view);
        }

        @Override
        public void onBindViewHolder(DicHolder holder, int position)
        {
            holder.bindDic(mTranslations.get(position),position);
        }

        @Override
        public int getItemCount()
        {
            return mTranslations.size();
        }
    }

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

//    public void updateView()
//    {
//        if(isVisible())
//        {
//            mMainResult.setText(mDictionary.getText());
//            mTranscription.setText(mDictionary.getTranscription());
//            mTranslations = mDictionary.getTranslations();
//            mRecyclerView.setAdapter(new DicAdapter(mTranslations));
//        }
//    }

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
}
