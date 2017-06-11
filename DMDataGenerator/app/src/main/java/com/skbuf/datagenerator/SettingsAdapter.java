package com.skbuf.datagenerator;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class SettingsAdapter extends BaseAdapter {
    private List<String> pairwiseComparisons;
    private Context context;

    public SettingsAdapter(Context context, List<String> pairwiseComparisons) {
        this.context = context;
        this.pairwiseComparisons = pairwiseComparisons;
    }

    @Override
    public int getCount() {
        return this.pairwiseComparisons.size();
    }

    @Override
    public String getItem(int position) {
        if (position < this.pairwiseComparisons.size())
            return this.pairwiseComparisons.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newView = inflater.inflate(R.layout.preferences_item, null);

        return newView;
    }


}
