package com.skbuf.datagenerator;


import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class SettingsAdapter extends BaseAdapter {
    private final String TAG = "SettingsAdapter";

    private List<Pair<String, String>> pairwiseComparisons;
    private Context context;
    private HashMap<Integer, String> seekbarValueString = new HashMap<Integer, String>(){{
        put(0,"Not at all important");
        put(1,"Not important");
        put(2,"Slightly less important");
        put(3,"Equally important");
        put(4,"Slightly more important");
        put(5,"More important");
        put(6,"Extremely important");
    }};


    public SettingsAdapter(Context context, List<Pair<String, String>> pairwiseComparisons) {
        this.context = context;
        this.pairwiseComparisons = pairwiseComparisons;
    }

    @Override
    public int getCount() {
        return this.pairwiseComparisons.size();
    }

    @Override
    public Pair<String, String> getItem(int i) {
        return pairwiseComparisons.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void setImage(View view, ImageView imageView, String criteria) {
        if ("safety".equals(criteria)) {
            imageView.setImageResource(R.drawable.green_circle);
        } else  if ("proximity".equals(criteria)) {
            imageView.setImageResource(R.drawable.blue_circle);
        } else  if ("close to friends".equals(criteria)) {
            imageView.setImageResource(R.drawable.yellow_circle);
        } else  if ("not crowded".equals(criteria)) {
            imageView.setImageResource(R.drawable.red_circle);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newView = inflater.inflate(R.layout.preferences_item, null);

        Pair<String, String> criteriaPair = getItem(position);
        ImageView criteria1Image = (ImageView) newView.findViewById(R.id.criteria1_image);
        ImageView criteria2Image = (ImageView) newView.findViewById(R.id.criteria2_image);
        setImage(newView, criteria1Image, criteriaPair.first);
        setImage(newView, criteria2Image, criteriaPair.second);

        final TextView currentOption = (TextView) newView.findViewById(R.id.currentOption);
        SeekBar seekBar = (SeekBar) newView.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentOption.setText("  : " + seekbarValueString.get(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return newView;
    }
}
