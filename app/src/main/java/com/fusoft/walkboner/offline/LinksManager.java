package com.fusoft.walkboner.offline;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.fusoft.walkboner.models.SavedLink;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.List;

public class LinksManager {

    private SharedPreferences sharedPreferences;

    private List<SavedLink> links;

    public LinksManager(Context context) {
        sharedPreferences = context.getSharedPreferences("savedLinks", Activity.MODE_PRIVATE);
    }

    public List<SavedLink> getLinks() {
        links = new Gson().fromJson(sharedPreferences.getString("links", ""), new TypeToken<List<SavedLink>>() {
        }.getType());
        return links;
    }

    public void addLink(SavedLink link) {
        links.add(link);
        sharedPreferences.edit().putString("links", new Gson().toJson(links)).apply();
    }

    public SavedLink getLinkAtPosition(int position) {
        return links.get(position);
    }
}
