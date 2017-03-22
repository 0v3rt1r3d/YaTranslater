package ru.overtired.yatranslater.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.structure.Dictionary;
import ru.overtired.yatranslater.structure.Translation;
import ru.overtired.yatranslater.structure.dictionary.Definition;

/**
 * Created by overtired on 22.03.17.
 */

public class ResultFragment extends Fragment
{
    private RecyclerView mRecyclerView;

    private List<ru.overtired.yatranslater.structure.dictionary.Translation> mTranslations;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_history_favorite, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.history_favorite_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    public static ResultFragment newInstance()
    {
        ResultFragment fragment = new ResultFragment();
        return fragment;
    }

    public void setDictionary(Dictionary dictionary)
    {
        mTranslations = new ArrayList<>();
        List<Definition> definitions = dictionary.getDefinitions();
        for(int i = 0;i<definitions.size();i++)
        {
            mTranslations.addAll(definitions.get(i).getTranslations());
        }
        mRecyclerView.setAdapter(new DicAdapter(mTranslations));
    }

    private class DicHolder extends RecyclerView.ViewHolder
    {
        private ru.overtired.yatranslater.structure.dictionary.Translation mTranslation;
        private TextView mSynonyms;
        private TextView mMean;

        public DicHolder(View itemView)
        {
            super(itemView);
            mSynonyms = (TextView) itemView.findViewById(R.id.list_dic_synonyms_tv);
            mMean = (TextView) itemView.findViewById(R.id.list_dic_mean_tv);
        }

        public void bindDic(ru.overtired.yatranslater.structure.dictionary.Translation translation)
        {
            mTranslation = translation;
            if(translation.getSynonyms().size()!=0)
            {
                String synonyms = "";
                for(int i=0;i<translation.getSynonyms().size();i++)
                {
                    synonyms = synonyms.concat(translation.getSynonyms().get(i));
                    if(i+1!=translation.getSynonyms().size())
                    {
                        synonyms = synonyms.concat(", ");
                    }
                }
                mSynonyms.setText(synonyms);
            }
            if(translation.getMeans().size()!=0)
            {
                String means = "";
                for(int i=0;i<translation.getMeans().size();i++)
                {
                    means = means.concat(translation.getMeans().get(i));
                    if(i+1!=translation.getSynonyms().size())
                    {
                        means = means.concat(", ");
                    }
                }
                mMean.setText(means);
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
            holder.bindDic(mTranslations.get(position));
        }

        @Override
        public int getItemCount()
        {
            return mTranslations.size();
        }
    }
}
