package com.nghiatv.filemanager.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nghiatv.filemanager.R;
import com.nghiatv.filemanager.util.FileUtil;
import com.nghiatv.filemanager.util.PreferenceUtil;

import java.io.File;

public class ViewHolder0 extends ViewHolder {

    private TextView txtFileName;

    private TextView txtFileDate;

    private TextView txtFileSize;

    ViewHolder0(Context context, OnItemClickListener listener, View itemView) {
        super(context, listener, itemView);
    }

    @Override
    protected void loadIcon() {
        imgFileIcon = (ImageView) itemView.findViewById(R.id.imgFileIcon);
    }

    @Override
    protected void loadName() {
        txtFileName = (TextView) itemView.findViewById(R.id.txtFileName);
    }

    @Override
    protected void loadInfo() {
        txtFileDate = (TextView) itemView.findViewById(R.id.txtFileDate);
        txtFileSize = (TextView) itemView.findViewById(R.id.txtFileSize);
    }

    @Override
    protected void bindIcon(File file, Boolean selected) {

        if (PreferenceUtil.getBoolean(context, "pref_icon", true)) {

            imgFileIcon.setOnClickListener(onActionClickListener);

            imgFileIcon.setOnLongClickListener(onActionLongClickListener);

            if (selected) {

                int color = ContextCompat.getColor(context, R.color.misc_file);

                imgFileIcon.setBackground(getBackground(color));

                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_selected);

                DrawableCompat.setTint(drawable, Color.rgb(255, 255, 255));

                imgFileIcon.setImageDrawable(drawable);
            }
            else {

                int color = ContextCompat.getColor(context, FileUtil.getColorResource(file));

                imgFileIcon.setBackground(getBackground(color));

                Drawable drawable = ContextCompat.getDrawable(context, FileUtil.getImageResource(file));

                DrawableCompat.setTint(drawable, Color.rgb(255, 255, 255));

                imgFileIcon.setImageDrawable(drawable);
            }
        }
        else {

            int color = ContextCompat.getColor(context, FileUtil.getColorResource(file));

            imgFileIcon.setBackground(null);

            Drawable drawable = ContextCompat.getDrawable(context, FileUtil.getImageResource(file));

            DrawableCompat.setTint(drawable, color);

            imgFileIcon.setImageDrawable(drawable);
        }
    }

    @Override
    protected void bindName(File file) {

        boolean extension = PreferenceUtil.getBoolean(context, "pref_extension", true);

        txtFileName.setText(extension ? FileUtil.getName(file) : file.getName());
    }

    @Override
    protected void bindInfo(File file) {

        txtFileDate.setText(FileUtil.getLastModified(file));

        txtFileSize.setText(FileUtil.getSize(context, file));

        setVisibility(txtFileDate, PreferenceUtil.getBoolean(context, "pref_date", true));

        setVisibility(txtFileSize, PreferenceUtil.getBoolean(context, "pref_size", false));
    }

    private ShapeDrawable getBackground(int color) {

        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());

        int size = (int) context.getResources().getDimension(R.dimen.avatar_size);

        shapeDrawable.setIntrinsicWidth(size);

        shapeDrawable.setIntrinsicHeight(size);

        shapeDrawable.getPaint().setColor(color);

        return shapeDrawable;
    }
}
