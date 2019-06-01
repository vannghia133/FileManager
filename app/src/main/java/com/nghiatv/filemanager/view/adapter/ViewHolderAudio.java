package com.nghiatv.filemanager.view.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nghiatv.filemanager.R;
import com.nghiatv.filemanager.util.FileUtil;
import com.nghiatv.filemanager.util.PreferenceUtil;

import java.io.File;

public class ViewHolderAudio extends ViewHolder {
    private TextView txtFileTitle;
    private TextView txtFileArtist;
    private TextView txtFileAlbum;

    public ViewHolderAudio(Context context, OnItemClickListener listener, View itemView) {
        super(context, listener, itemView);
    }

    @Override
    protected void loadIcon() {
        imgFileIcon = itemView.findViewById(R.id.imgFileIcon);
    }

    @Override
    protected void loadName() {
        txtFileTitle = itemView.findViewById(R.id.txtFileTitle);
    }

    @Override
    protected void loadInfo() {
        txtFileArtist = itemView.findViewById(R.id.txtFileArtist);
        txtFileAlbum = itemView.findViewById(R.id.txtFileAlbum);
    }

    @Override
    protected void bindIcon(File file, Boolean selected) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(file.getPath());
            Glide.with(context).load(retriever.getEmbeddedPicture()).into(imgFileIcon);

        } catch (Exception e) {
            imgFileIcon.setImageResource(R.drawable.ic_audio);
        }
    }

    @Override
    protected void bindName(File file) {
        boolean extension = PreferenceUtil.getBoolean(context, "pref_extension", true);
        String string = FileUtil.getTitle(file);
        txtFileTitle.setText(string != null && string.isEmpty() ? string : (extension ? FileUtil.getName(file) : file.getName()));
    }

    @Override
    protected void bindInfo(File file) {
        txtFileArtist.setText(FileUtil.getArtist(file));
        txtFileAlbum.setText(FileUtil.getAlbum(file));
    }
}
