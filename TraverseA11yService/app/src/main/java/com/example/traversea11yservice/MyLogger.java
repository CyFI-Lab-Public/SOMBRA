package com.example.traversea11yservice;

import android.app.Application;
import java.util.ArrayList;
import java.util.List;


public class MyLogger extends Application {
    private final List<ViewMeta> protectedViews = new ArrayList<>();
    public List<String> viewIDs = new ArrayList<>();

    public void addViewMeta(ViewMeta meta) {
        protectedViews.add(meta);
    }

    public List<ViewMeta> getProtectedViews() {
        return protectedViews;
    }
}