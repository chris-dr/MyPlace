package com.drevnitskaya.myplace.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by air on 01.07.17.
 */

public class NearbyPlacesFragment extends Fragment {

    public static NearbyPlacesFragment newInstance() {

        Bundle args = new Bundle();

        NearbyPlacesFragment fragment = new NearbyPlacesFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
