package com.drevnitskaya.myplace.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by air on 02.07.17.
 */

public class AddressDecoderService extends IntentService {

    public static final String EXTRA_RESULT_RECEIVER = "com.drevnitskaya.myplace.extra_result_receiver";
    public static final String EXTRA_SELECTED_LOCATION = "com.drevnitskaya.myplace.extra_selected_location";
    public static final String EXTRA_SELECTED_ADDRESS = "com.drevnitskaya.myplace.extra_selected_address";
    public static final int RESULT_CODE_OK = 100;
    public static final int RESULT_CODE_ERROR = 200;

    private ResultReceiver receiver;

    public AddressDecoderService() {
        super("thread");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        receiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);
        LatLng location = intent.getParcelableExtra(EXTRA_SELECTED_LOCATION);
        List<Address> addresses = null;
        Address selectedAddress = null;

        try {
            addresses = geocoder.getFromLocation(location.latitude,
                    location.longitude,
                    1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && !addresses.isEmpty()) {
            selectedAddress = addresses.get(0);
            sendSelectedAddress(selectedAddress, RESULT_CODE_OK);
        } else {
            sendSelectedAddress(selectedAddress, RESULT_CODE_ERROR);
        }
    }

    private void sendSelectedAddress(Address address, int resultCode) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_SELECTED_ADDRESS, address);
        receiver.send(resultCode, bundle);
    }

}
