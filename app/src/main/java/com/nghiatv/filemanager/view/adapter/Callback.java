package com.nghiatv.filemanager.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;

import com.nghiatv.filemanager.util.FileUtil;
import com.nghiatv.filemanager.util.PreferenceUtil;

import java.io.File;

public class Callback extends SortedListAdapterCallback {
    private int criteria;

    public Callback(Context context, RecyclerView.Adapter adapter) {
        super(adapter);
        this.criteria = PreferenceUtil.getInteger(context, "pref_sort", 0);
    }

    @Override
    public int compare(Object obj1, Object obj2) {
        File file1 = (File) obj1;
        File file2 = (File) obj2;

        boolean isDirectory1 = file1.isDirectory();
        boolean isDirectory2 = file2.isDirectory();

        if (isDirectory1 != isDirectory2) {
            return isDirectory1 ? - 1 : +1;
        }

        switch (criteria) {
            case 0:
                return FileUtil.compareName(file1, file2);

            case 1:
                return FileUtil.compareDate(file1, file2);

            case 2:
                return FileUtil.compareSize(file1, file2);

                default:
                    return 0;
        }
    }

    @Override
    public boolean areContentsTheSame(Object obj1, Object obj2) {
        File oldFile = (File) obj1;
        File newFile = (File) obj2;

        return oldFile.equals(newFile);
    }

    @Override
    public boolean areItemsTheSame(Object obj1, Object obj2) {
        File file1 = (File) obj1;
        File file2 = (File) obj2;

        return file1.equals(file2);
    }


    public boolean update(int criteria) {

        if (criteria == this.criteria) {
            return false;
        }

        this.criteria = criteria;

        return true;
    }
}
