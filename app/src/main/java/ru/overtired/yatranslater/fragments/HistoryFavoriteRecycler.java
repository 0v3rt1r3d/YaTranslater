package ru.overtired.yatranslater.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.database.Data;
import ru.overtired.yatranslater.structure.Translation;

/**
 * Created by overtired on 18.03.17.
 */

public abstract class HistoryFavoriteRecycler extends Fragment
{
    private Unbinder mUnbinder;
    @BindView(R.id.history_favorite_recycler_view) protected RecyclerView mRecyclerView;
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
        mUnbinder = ButterKnife.bind(this,mView);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new HistoryFavoriteAdapter(getTranslations()));

        return mView;
    }

    protected class HistoryFavoriteHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener,View.OnLongClickListener
    {
        private Translation mTranslation;
        
        @BindView(R.id.list_translation_button_save) ImageButton mBookmark;
        @BindView(R.id.list_translation_text_from) TextView mTextFromView;
        @BindView(R.id.list_translation_text_to) TextView mTextToView;
        @BindView(R.id.list_translation_text_direction) TextView mTextDirection;
        
        public HistoryFavoriteHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

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
                            updateRecycler();
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

        public void updateTranslationsList()
        {
            final TranslationsDiffCallback diffCallback =
                    new TranslationsDiffCallback(mTranslations,getTranslations());
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
            this.mTranslations.clear();
            this.mTranslations.addAll(getTranslations());
            diffResult.dispatchUpdatesTo(this);

        }
    }

    abstract protected List<Translation> getTranslations();

    public void updateRecycler()
    {
//        mRecyclerView.setAdapter(new HistoryFavoriteAdapter(getTranslations()));
        ((HistoryFavoriteAdapter)mRecyclerView.getAdapter()).updateTranslationsList();
    }

    abstract protected void updateOtherRecycler();

    abstract protected void removeTranslation(Translation translation);

    @Override
    public void onDetach()
    {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
