package com.nghiatv.filemanager.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public abstract class ViewHolder extends RecyclerView.ViewHolder {
    Context context;

    ImageView imgFileIcon;

    View.OnClickListener onActionClickListener;

    View.OnLongClickListener onActionLongClickListener;

    private View.OnClickListener onClickListener;

    private View.OnLongClickListener onLongClickListener;

    ViewHolder(Context context, OnItemClickListener listener, View itemView) {
        super(itemView);
        this.context = context;
        setClickListener(listener);
        loadIcon();
        loadName();
        loadInfo();
    }

    protected abstract void loadIcon();

    protected abstract void loadName();

    protected abstract void loadInfo();

    protected abstract void bindIcon(File file, Boolean selected);

    protected abstract void bindName(File file);

    protected abstract void bindInfo(File file);

    private void setClickListener(final OnItemClickListener listener) {
        this.onActionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(getAdapterPosition());
            }
        };

        this.onActionLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return listener.onItemLongClick(getAdapterPosition());
            }
        };

        this.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(getAdapterPosition());
            }
        };

        this.onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return listener.onItemLongClick(getAdapterPosition());
            }
        };
    }

    void setData(File file, Boolean selected) {

        itemView.setOnClickListener(onClickListener);
        itemView.setOnLongClickListener(onLongClickListener);
        itemView.setSelected(selected);
        bindIcon(file, selected);
        bindName(file);
        bindInfo(file);
    }

    public void setVisibility(View view, boolean visibility) {
        view.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

}
