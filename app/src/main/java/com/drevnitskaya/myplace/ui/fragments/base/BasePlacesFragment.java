package com.drevnitskaya.myplace.ui.fragments.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.drevnitskaya.myplace.R;
import com.drevnitskaya.myplace.contracts.BasePlacesContract;
import com.drevnitskaya.myplace.model.entities.PlaceDetails;
import com.drevnitskaya.myplace.ui.adapters.PlacesAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.drevnitskaya.myplace.services.GeofencesMonitoringService.ACTION_REMOVE_MONITORING_GEOFENCE;

/**
 * Created by air on 01.07.17.
 */

public abstract class BasePlacesFragment extends Fragment implements BasePlacesContract.View, PlacesAdapter.ActionListener {

    @BindView(R.id.recyclerView)
    protected RecyclerView recyclerView;
    @BindView(R.id.textInfoMsg)
    TextView textInfoMsg;

    protected PlacesAdapter adapterPlaces;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createPresenter();
        getPresenter().initRealm();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().setupInfoMsg();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPresenter().closeRealm();

    }

    public abstract BasePlacesContract.Presenter getPresenter();

    @Override
    public void setInfoMsgText(@StringRes int msgResId) {
        textInfoMsg.setVisibility(View.VISIBLE);
        textInfoMsg.setText(msgResId);
    }

    @Override
    public void hideInfoMsg() {
        textInfoMsg.setVisibility(View.GONE);
    }

    public void notifyPlacesChanged() {
        adapterPlaces.notifyDataSetChanged();
    }

    protected abstract void createPresenter();

    protected void bindView(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void setupPlacesRecycler() {
        adapterPlaces = new PlacesAdapter(getPresenter().getWrappedPlaces(), this);
        recyclerView.setAdapter(adapterPlaces);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onFavoriteClicked(int position) {
        manageOnFavoriteClick(position);
    }

    protected abstract void manageOnFavoriteClick(int position);

    @Override
    public void onUrlLinkClicked(int position) {
        PlaceDetails placeDetails = getPresenter().getWrappedPlaces().get(position).getPlaceDetails();
        Uri uri = Uri.parse(placeDetails.getUrlLink());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    public void sendRemoveGeofenceBroadcast() {
        Intent intent = new Intent();
        intent.setAction(ACTION_REMOVE_MONITORING_GEOFENCE);
        getContext().sendBroadcast(intent);
    }
}
