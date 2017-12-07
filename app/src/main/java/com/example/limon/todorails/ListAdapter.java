package com.example.limon.todorails;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.TreeSet;


public class ListAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<String> mData = new ArrayList<>();
    private TreeSet<Integer> sectionHeader = new TreeSet<>();

    private LayoutInflater mInflater;

    public ListAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final String item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSectionDataItem(final String item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.cell, null);
                    holder.checkBox = convertView.findViewById(R.id.checkbox);
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.category_header, null);
                    holder.textView = convertView.findViewById(R.id.textSeparator);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
//            Log.d("Convert View", convertView.toString());
//            Log.d("Convert View's Tag", convertView.getTag().toString());
        }

        if (holder.textView != null) {
            holder.textView.setText(mData.get(position));

        } else if (holder.checkBox != null) {
            holder.checkBox.setText(mData.get(position));
            Todo todo = new Todo();
            todo.isCompleted = holder.checkBox.isChecked();
            holder.checkBox.setTag(todo);

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView != null) {
                        Todo todoInFocus = (Todo) buttonView.getTag();
                        if (todoInFocus.isCompleted == isChecked) return;
                    }

                }
            });
        }


        return convertView;
    }



    public static class ViewHolder {
        public TextView textView;
        public CheckBox checkBox;
    }
}
