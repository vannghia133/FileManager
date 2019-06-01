package com.nghiatv.filemanager.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import com.nghiatv.filemanager.R;
import com.nghiatv.filemanager.util.FileUtil;
import com.nghiatv.filemanager.util.PreferenceUtil;

import java.io.File;

public class ViewHolderImage extends ViewHolder {

    private TextView txtFileName;

    private TextView txtFileDate;

    public ViewHolderImage(Context context, OnItemClickListener listener, View itemView) {
        super(context, listener, itemView);
    }

    @Override
    protected void loadIcon() {
        imgFileIcon = (ImageView) itemView.findViewById(R.id.imgFileIcon);
    }

    @Override
    protected void loadName() {
        txtFileName = itemView.findViewById(R.id.txtFileName);
    }

    @Override
    protected void loadInfo() {
        txtFileDate = itemView.findViewById(R.id.txtFileDate);
    }

    @Override
    protected void bindIcon(File file, Boolean selected) {

        final int color = ContextCompat.getColor(context, FileUtil.getColorResource(file));

        Glide.with(context).load(file).asBitmap().fitCenter().into(new BitmapImageViewTarget(imgFileIcon) {

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> animation) {

                this.view.setImageBitmap(resource);

                txtFileName.setBackgroundColor(Palette.from(resource).generate().getMutedColor(color));
            }
        });
    }

    @Override
    protected void bindName(File file) {
        boolean extension = PreferenceUtil.getBoolean(context, "pref_extension", true);
        txtFileName.setText(extension ? FileUtil.getName(file) : file.getName());
    }

    @Override
    protected void bindInfo(File file) {

        if (txtFileDate == null) {
            return;
        }

        txtFileDate.setText(FileUtil.getLastModified(file));
    }
}

