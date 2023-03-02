package com.example.logintesting;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapFragment;

public class MarkerFragment extends MapFragment {
    public View originalContentView;
    public MarkerTouch touchView;
    public Context C;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        originalContentView = super.onCreateView(inflater, parent, savedInstanceState);
        touchView = new MarkerTouch(getActivity());
        touchView.addView(originalContentView);
        return touchView;
    }

    @Override
    public View getView() {
        return originalContentView;
    }
}
