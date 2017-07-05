package com.drevnitskaya.myplace.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drevnitskaya.myplace.R;
import com.drevnitskaya.myplace.contracts.FavoritePlacesContract;
import com.drevnitskaya.myplace.presenters.FavoritePlacesPresenter;
import com.drevnitskaya.myplace.ui.fragments.base.BasePlacesFragment;


/**
 * Created by air on 02.07.17.
 */

public class FavoritePlacesFragment extends BasePlacesFragment implements FavoritePlacesContract.View {

    private FavoritePlacesContract.Presenter presenter;

    public static FavoritePlacesFragment newInstance() {
        FavoritePlacesFragment fragment = new FavoritePlacesFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_places_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPresenter().favoritePlacesQuery();
    }

    @Override
    public FavoritePlacesContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    protected void createPresenter() {
        presenter = new FavoritePlacesPresenter(this);
    }

    @Override
    public void notifyPlaceRemoved(int position) {
        adapterPlaces.notifyItemRemoved(position);
    }

    @Override
    protected void manageOnFavoriteClick(int position) {
        getPresenter().disableFavoritePlace(position);
    }
}
