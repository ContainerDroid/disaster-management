package com.skbuf.datagenerator;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsFragment extends Fragment {

    List<String> criteria = new ArrayList<>(Arrays.asList(
            "safety",
            "proximity",
            "close to friends",
            "not crowded"));
    List<Pair<String, String>> pairwiseComparisons = createPairwiseComparisons(criteria);

    private ListView lv;
    private SettingsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getText(R.string.title_settings));

        adapter = new SettingsAdapter(getContext(), pairwiseComparisons);
        lv = (ListView) getView().findViewById(R.id.pref_list);
        lv.setAdapter(adapter);
    }

    private List<Pair<String, String>> createPairwiseComparisons(List<String> criteria) {
        List<Pair<String, String>> comparisons = new ArrayList<Pair<String, String>>();
        Integer criteriaSize = criteria.size();

        for (Integer i = 0; i < criteriaSize; i++) {
            for (Integer j = i + 1; j < criteriaSize; j++) {
                String c1 = criteria.get(i);
                String c2 = criteria.get(j);
                comparisons.add(new Pair<String, String>(c1, c2));
            }
        }

        return  comparisons;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.settings, menu);
    }

}
