package com.skbuf.datagenerator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class FileListAdapter extends BaseAdapter {
    private List<String> items;
    private Context context;

    public FileListAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public String getItem(int position) {
        if (position < this.items.size())
            return this.items.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(String sampleFile){
        items.add(sampleFile);
    }

    public void removeItem(int position){
        items.remove(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newView = inflater.inflate(R.layout.list_item, null);

        TextView sampleName = (TextView) newView.findViewById(R.id.sample_name);
        sampleName.setText(getItem(position));

        Button sampleRemove = (Button) newView.findViewById(R.id.sample_remove);
        sampleRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(position);
                notifyDataSetChanged();
            }
        });

        return newView;
    }

}
