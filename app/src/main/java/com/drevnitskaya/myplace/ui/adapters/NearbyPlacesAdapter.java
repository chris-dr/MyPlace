package com.drevnitskaya.myplace.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drevnitskaya.myplace.R;
import com.drevnitskaya.myplace.model.entities.Place;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by air on 01.07.17.
 */

public class NearbyPlacesAdapter extends RecyclerView.Adapter<NearbyPlacesAdapter.NearbyPlaceHolder> {

    private List<Place> places = new ArrayList<>();

    public NearbyPlacesAdapter(List<Place> places) {
        this.places = places;
    }

    @Override
    public NearbyPlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nearby_place, parent, false);
        NearbyPlaceHolder vh = new NearbyPlaceHolder(rootView);
        return vh;
    }

    @Override
    public void onBindViewHolder(NearbyPlaceHolder holder, int position) {
        Place place = places.get(position);

        holder.textPlaceTitle.setText(place.getName());
        holder.textPlaceUrlLink.setText(place.getReference());

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    class NearbyPlaceHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textPlaceTitle)
        TextView textPlaceTitle;
        @BindView(R.id.textPlaceUrlLink)
        TextView textPlaceUrlLink;

        public NearbyPlaceHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
