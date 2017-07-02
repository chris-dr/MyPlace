package com.drevnitskaya.myplace.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.drevnitskaya.myplace.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by air on 02.07.17.
 */

public class LocInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View view;

    public LocInfoWindowAdapter(Context ctx) {
        this.view = LayoutInflater.from(ctx).inflate(R.layout.window_info, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
