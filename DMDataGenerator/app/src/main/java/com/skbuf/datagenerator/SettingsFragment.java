package com.skbuf.datagenerator;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsFragment extends Fragment {

    List<String> criteria = new ArrayList<>(Arrays.asList("safety",
            "proximity",
            "close to friends",
            "not crowded"));
    List<String> pairwiseComparisons = createPairwiseComparisons(criteria);


    private ListView lv;
    private SettingsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new SettingsAdapter(getContext(), pairwiseComparisons);
        lv = (ListView) getView().findViewById(R.id.pref_list);
        lv.setAdapter(adapter);
    }

    private List<String> createPairwiseComparisons(List<String> criteria) {
        List<String> comparisons = new ArrayList<String>();

        for (String c1 : criteria) {
            for (String c2: criteria) {
                if (c1 != c2) {
                    String comparison = c1 + " over " + c2;
                    comparisons.add(comparison);
                }
            }
        }


        return  comparisons;
    }
}
