package com.drevnitskaya.myplace.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drevnitskaya.myplace.R;
import com.drevnitskaya.myplace.model.entities.PlaceDetails;
import com.drevnitskaya.myplace.model.entities.WrapperPlace;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by air on 01.07.17.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.NearbyPlaceHolder> {

    private static final int NO_POSITION = -1;

    private List<WrapperPlace> places = new ArrayList<>();
    private WeakReference<ActionListener> listener;

    public PlacesAdapter(List<WrapperPlace> places, ActionListener listener) {
        this.places = places;
        this.listener = new WeakReference<>(listener);
    }

    @Override
    public NearbyPlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nearby_place, parent, false);
        final NearbyPlaceHolder vh = new NearbyPlaceHolder(rootView);
        vh.imageButtonFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = vh.getAdapterPosition();
                if (position != NO_POSITION) {
                    listener.get().onFavoriteClicked(position);
                }
            }
        });
        vh.textPlaceUrlLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = vh.getAdapterPosition();
                if (position != NO_POSITION) {
                    listener.get().onUrlLinkClicked(position);
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(NearbyPlaceHolder holder, int position) {
        PlaceDetails details = places.get(position).getPlaceDetails();

        holder.textPlaceTitle.setText(details.getName());
        String address = details.getAddress();
        if (!TextUtils.isEmpty(address)) {
            holder.textPlaceAddress.setVisibility(View.VISIBLE);
            holder.textPlaceAddress.setText(address);
        } else {
            holder.textPlaceAddress.setVisibility(View.GONE);
        }
        String urlLink = details.getUrlLink();
        if (!TextUtils.isEmpty(urlLink)) {
            holder.textPlaceUrlLink.setVisibility(View.VISIBLE);
            holder.textPlaceUrlLink.setMovementMethod(LinkMovementMethod.getInstance());
            holder.textPlaceUrlLink.setText(urlLink);
        } else {
            holder.textPlaceUrlLink.setVisibility(View.GONE);
        }
        holder.imageButtonFavorite.setSelected(places.get(position).isFavorite());

    }

    @Override
    public int getItemCount() {
        if (places != null) {
            return places.size();
        } else {
            return 0;
        }
    }

    class NearbyPlaceHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textPlaceTitle)
        TextView textPlaceTitle;
        @BindView(R.id.textPlaceAddress)
        TextView textPlaceAddress;
        @BindView(R.id.textPlaceUrlLink)
        TextView textPlaceUrlLink;
        @BindView(R.id.imageButtonFavorite)
        TextView imageButtonFavorite;

        NearbyPlaceHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ActionListener {
        void onFavoriteClicked(int position);

        void onUrlLinkClicked(int position);
    }
}
