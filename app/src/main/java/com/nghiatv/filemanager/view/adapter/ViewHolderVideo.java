package com.nghiatv.filemanager.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nghiatv.filemanager.R;
import com.nghiatv.filemanager.util.FileUtil;
import com.nghiatv.filemanager.util.PreferenceUtil;

import java.io.File;

public class ViewHolderVideo extends ViewHolder {

    private TextView txtFileName;

    private TextView txtFileDuration;

    public ViewHolderVideo(Context context, OnItemClickListener listener, View itemView) {
        super(context, listener, itemView);
    }

    @Override
    protected void loadIcon() {
        imgFileIcon = itemView.findViewById(R.id.imgFileIcon);
    }

    @Override
    protected void loadName() {
        txtFileName = itemView.findViewById(R.id.txtFileName);
    }

    @Override
    protected void loadInfo() {
        txtFileDuration = itemView.findViewById(R.id.txtFileDuration);
    }

    @Override
    protected void bindIcon(File file, Boolean selected) {
        Glide.with(context).load(file).into(imgFileIcon);
    }

    @Override
    protected void bindName(File file) {
        boolean extension = PreferenceUtil.getBoolean(context, "pref_extension", true);
        txtFileName.setText(extension ? FileUtil.getName(file) : file.getName());
    }

    @Override
    protected void bindInfo(File file) {
        txtFileDuration.setText(FileUtil.getDuration(file));
    }
}