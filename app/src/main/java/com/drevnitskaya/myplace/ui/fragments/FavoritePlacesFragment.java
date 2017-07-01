package com.drevnitskaya.myplace.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by air on 01.07.17.
 */

public class FavoritePlacesFragment extends Fragment {

    public static FavoritePlacesFragment newInstance() {

        Bundle args = new Bundle();

        FavoritePlacesFragment fragment = new FavoritePlacesFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
