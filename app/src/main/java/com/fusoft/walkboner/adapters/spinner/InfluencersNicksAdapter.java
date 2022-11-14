/*
 * Copyright (c) 2022 - WalkBoner.
 * InfluencersNicksAdapter.java
 */

package com.fusoft.walkboner.adapters.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fusoft.walkboner.R;
import com.fusoft.walkboner.models.Influencer;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class InfluencersNicksAdapter extends ArrayAdapter<HashMap<String, String>> {
    public InfluencersNicksAdapter(Context context,
                                   ArrayList<HashMap<String, String>> influencers) {
        super(context, 0, influencers);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView,
                          ViewGroup parent)
    {
        // It is used to set our custom view.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_spinner_influencers, parent, false);
        }

        MaterialTextView textViewName = convertView.findViewById(R.id.influencer_nick_text);
        HashMap<String, String> currentItem = getItem(position);

        // It is used the name to the TextView when the
        // current item is not null.
        if (currentItem != null) {
            textViewName.setText(currentItem.get("nickname").toString());
        }
        return convertView;
    }
}
