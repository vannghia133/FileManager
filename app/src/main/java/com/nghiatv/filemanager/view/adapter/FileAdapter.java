package com.nghiatv.filemanager.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nghiatv.filemanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class FileAdapter extends RecyclerView.Adapter<ViewHolder> {
    private Context context;

    private SortedList<File> items;

    private SparseBooleanArray selectedItems;

    private Callback callback;

    private int itemLayout;

    private int spanCount;

    private OnItemClickListener onItemClickListener;

    private OnItemSelectedListener onItemSelectedListener;

    //----------------------------------------------------------------------------------------------

    public FileAdapter(Context context) {
        this.context = context;
        this.callback = new Callback(context, this);
        this.items = new SortedList<>(File.class, callback);
        this.selectedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(itemLayout, viewGroup, false);
        switch (itemLayout) {
            case R.layout.item_file_0:
                return new ViewHolder0(context, onItemClickListener, itemView);

            case R.layout.item_file_1:
                return new ViewHolderAudio(context, onItemClickListener, itemView);

            case R.layout.item_file_2:
                return new ViewHolderImage(context, onItemClickListener, itemView);

            case R.layout.item_file_3:
                return new ViewHolderVideo(context, onItemClickListener, itemView);

            default:
                return null;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        recyclerView.setLayoutManager(new GridLayoutManager(context, spanCount));

        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(get(position), getSelected(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItemLayout(int itemLayout) {
        this.itemLayout = itemLayout;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }

    public void add(File file) {
        items.add(file);
    }

    public void addAll(File... files) {
        items.addAll(files);
    }

    public void addAll(Collection<File> files) {
        items.addAll(files);
    }

    public void clear() {
        while (items.size() > 0) {
            items.removeItemAt(items.size() - 1);
        }
    }

    public void refresh() {
        for (int i = 0; i < getItemCount(); i++) {
            notifyItemChanged(i);
        }
    }

    public void removeAll(Collection<File> files) {
        for (File file : files) {
            files.remove(file);
        }
    }

    public void updateItemAt(int index, File file) {
        items.updateItemAt(index, file);
    }

    public void clearSelection() {
        ArrayList<Integer> selectedPositions = getSelectedPositions();
        selectedItems.clear();
        for (int i : selectedPositions) {
            notifyItemChanged(i);
        }
        onItemSelectedListener.onItemSelected();
    }

    public void update(int criteria) {
        if (callback.update(criteria)) {
            ArrayList<File> list = getItems();
            clear();
            addAll(list);
        }
    }

    public void select(ArrayList<Integer> positions) {
        selectedItems.clear();
        for (int i : positions) {
            selectedItems.append(i, true);
            notifyItemChanged(i);
        }
        onItemSelectedListener.onItemSelected();
    }

    public void toggle(int position) {
        if (getSelected(position)) {
            selectedItems.delete(position);
        } else {
            selectedItems.append(position, true);
        }
        notifyItemChanged(position);
        onItemSelectedListener.onItemSelected();
    }

    public boolean anySelected() {
        return selectedItems.size() > 0;
    }

    private boolean getSelected(int position) {
        return selectedItems.get(position);
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public int indexOf(File file) {
        return items.indexOf(file);
    }

    public ArrayList<File> getSelectedItems() {
        ArrayList<File> list = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            if (getSelected(i)) {
                list.add(get(i));
            }
        }
        return list;
    }

    private ArrayList<File> getItems() {
        ArrayList<File> list = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            list.add(get(i));
        }
        return list;
    }

    public ArrayList<Integer> getSelectedPositions() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            if (getSelected(i)) {
                list.add(i);
            }
        }
        return list;
    }

    public File get(int index) {
        return items.get(index);
    }
}
