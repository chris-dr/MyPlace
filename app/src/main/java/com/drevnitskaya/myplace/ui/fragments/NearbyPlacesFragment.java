package com.drevnitskaya.myplace.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drevnitskaya.myplace.R;
import com.drevnitskaya.myplace.contract.NearbyPlacesContract;
import com.drevnitskaya.myplace.model.entities.Place;
import com.drevnitskaya.myplace.presenter.NearbyPlacesPresenter;
import com.drevnitskaya.myplace.ui.adapters.NearbyPlacesAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by air on 01.07.17.
 */

public class NearbyPlacesFragment extends Fragment implements NearbyPlacesContract.View {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private NearbyPlacesContract.Presenter presenter;
    private NearbyPlacesAdapter adapterNearbyPlaces;

    public static NearbyPlacesFragment newInstance() {

        Bundle args = new Bundle();

        NearbyPlacesFragment fragment = new NearbyPlacesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new NearbyPlacesPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nearby_places, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void setPresenter(NearbyPlacesContract.Presenter presenter) {
        this.presenter = presenter;
    }


    public void setupPlacesList(List<Place> places) {
        adapterNearbyPlaces = new NearbyPlacesAdapter(places);
        recyclerView.setAdapter(adapterNearbyPlaces);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
    }
}
