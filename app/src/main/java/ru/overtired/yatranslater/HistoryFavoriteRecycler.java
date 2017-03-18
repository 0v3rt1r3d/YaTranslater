package ru.overtired.yatranslater;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by overtired on 18.03.17.
 */

public abstract class HistoryFavoriteRecycler extends Fragment
{
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_history_favorite,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.history_favorite_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new HistoryFavoriteAdapter(getTranslations()));

        return view;
    }

    private class HistoryFavoriteHolder extends RecyclerView.ViewHolder 
            implements View.OnClickListener
    {
        private Translation mTranslation;
        
        private ImageButton mBookmark;
        private TextView mTextFromView;
        private TextView mTextToView;
        private TextView mTextDirection;
        
        public HistoryFavoriteHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
//            mBookmark.setOnClickListener();
            
            mBookmark = (ImageButton) itemView.findViewById(R.id.list_translation_button_save);
            mTextDirection = (TextView) itemView.findViewById(R.id.list_translation_text_direction);
            mTextFromView = (TextView) itemView.findViewById(R.id.list_translation_text_from);
            mTextToView = (TextView) itemView.findViewById(R.id.list_translation_text_to);
        }
        
        public void bindTranslation(Translation translation)
        {
            mTranslation = translation;
            if(mTranslation.isFavorite())
            {
                mBookmark.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
            }else 
            {
                mBookmark.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_favorite));
            }
            
            mTextDirection.setText(mTranslation.getShortFrom()+"-"+mTranslation.getShortTo());
            mTextFromView.setText(mTranslation.getWordFrom());
            mTextToView.setText(mTranslation.getWordTo());
        }

        @Override
        public void onClick(View v)
        {
//            Этот метод переводит пользователя на фрагмент перевода, он одинаков и для избранного
//            и для истории
            // TODO: 18.03.17  
        }
    }

    private class HistoryFavoriteAdapter extends RecyclerView.Adapter<HistoryFavoriteHolder>
    {
        private List<Translation> mTranslations;

        public HistoryFavoriteAdapter(List<Translation> translations)
        {
            mTranslations = translations;
        }

        @Override
        public HistoryFavoriteHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.list_translation,parent,false);
            return new HistoryFavoriteHolder(v);
        }

        @Override
        public void onBindViewHolder(HistoryFavoriteHolder holder, int position)
        {
            holder.bindTranslation(mTranslations.get(position));
        }

        @Override
        public int getItemCount()
        {
            return mTranslations.size();
        }
    }

    abstract protected List<Translation> getTranslations();
}
