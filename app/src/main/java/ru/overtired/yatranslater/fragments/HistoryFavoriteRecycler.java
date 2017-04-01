package ru.overtired.yatranslater.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.database.Data;
import ru.overtired.yatranslater.structure.Translation;

/**
 * Created by overtired on 18.03.17.
 */

public abstract class HistoryFavoriteRecycler extends Fragment
{
    protected RecyclerView mRecyclerView;
    protected Callbacks mCallbacks;
    protected View mView;

    public interface Callbacks
    {
        void setTranslation(Translation translation);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        Log.e("activity",getActivity().toString());
        mCallbacks = (Callbacks)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_history_favorite,container,false);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.history_favorite_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new HistoryFavoriteAdapter(getTranslations()));

        return mView;
    }

    protected class HistoryFavoriteHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener,View.OnLongClickListener
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
            itemView.setOnLongClickListener(this);

            mBookmark = (ImageButton) itemView.findViewById(R.id.list_translation_button_save);
            mBookmark.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(mTranslation.isFavorite())
                    {
                        mTranslation.setFavorite(false);
                        mBookmark.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_favorite));
                    }else
                    {
                        mTranslation.setFavorite(true);
                        mBookmark.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                    }
                    Data.get(getActivity()).updateTranslation(mTranslation);
                    updateOtherRecycler();
                }
            });

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
            
            mTextDirection.setText(mTranslation.getLangFrom()+"-"+mTranslation.getLangTo());
            mTextFromView.setText(mTranslation.getTextFrom());
            mTextToView.setText(mTranslation.getTextTo());
        }

        @Override
        public void onClick(View v)
        {
            mCallbacks.setTranslation(mTranslation);
        }

        @Override
        public boolean onLongClick(View v)
        {
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.dialog_delete)
                    .setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            removeTranslation(mTranslation);
                            mRecyclerView.setAdapter(new HistoryFavoriteAdapter(getTranslations()));
                            updateOtherRecycler();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,null)
                    .show();

            return true;
        }
    }

    protected class HistoryFavoriteAdapter extends RecyclerView.Adapter<HistoryFavoriteHolder>
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

    public void updateRecycler()
    {
        mRecyclerView.setAdapter(new HistoryFavoriteAdapter(getTranslations()));
    }

    abstract protected void updateOtherRecycler();

    abstract protected void removeTranslation(Translation translation);

    @Override
    public void onDetach()
    {
        super.onDetach();
        mCallbacks = null;
    }
}
