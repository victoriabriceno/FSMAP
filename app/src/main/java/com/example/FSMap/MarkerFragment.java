package com.example.FSMap;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapFragment;

public class MarkerFragment extends MapFragment {
    public View originalContentView;
    public MarkerTouch MTouch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        originalContentView = super.onCreateView(inflater, parent, savedInstanceState);
        MTouch = new MarkerTouch(getActivity());
        MTouch.addView(originalContentView);
        return MTouch;
    }

    @Override
    public View getView() {
        return originalContentView;
    }
}
