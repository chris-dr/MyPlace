package com.drevnitskaya.myplace.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drevnitskaya.myplace.R;
import com.drevnitskaya.myplace.contracts.MapContract;
import com.drevnitskaya.myplace.presenters.MapPresenter;
import com.drevnitskaya.myplace.services.AddressDecoderService;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by air on 01.07.17.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, MapContract.View {

    public static final String EXTRA_SELECTED_LOCATION = "com.drevnitskaya.myplace.selected_location";
    private static final int REQUEST_CODE_LAST_KNOWN_LOCATION_ACCESS = 100;
    private static final int REQUEST_CODE_CURRENT_DISPLAYING_LOCATION_ACCESS = 200;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.textSelectedLoc)
    TextView textSelectedLoc;
    @BindView(R.id.layoutPinContainer)
    ViewGroup layoutPinContainer;

    private MapContract.Presenter presenter;
    private ActionBar actionBar;
    private GoogleMap map;
    private ResultReceiver receiver;
    private LatLng selectedLocation;

    public static Intent getStartIntent(Context ctx) {
        Intent i = new Intent(ctx, MapActivity.class);
        return i;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        presenter = new MapPresenter(this);

        setupLastKnownLocation();

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.common_selectLocation);

        receiver = new AddressResultReceiver(new Handler());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void setupLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LAST_KNOWN_LOCATION_ACCESS);
            return;
        }

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        presenter.connectGoogleApiClient(result, builder);

    }

    @OnClick(R.id.layoutAddressCont)
    protected void onLocationSelected() {
        if (selectedLocation != null) {
            Intent data = new Intent();
            Bundle extras = new Bundle();
            extras.putParcelable(EXTRA_SELECTED_LOCATION, selectedLocation);
            data.putExtras(extras);
            setResult(Activity.RESULT_OK, data);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        allowShowCurrentLocation();
        LatLngBounds b = map.getProjection().getVisibleRegion().latLngBounds;
        selectedLocation = b.getCenter();

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLngBounds b = map.getProjection().getVisibleRegion().latLngBounds;
                selectedLocation = b.getCenter();
                startAddressDecoderService(selectedLocation);
            }
        });

    }

    private void allowShowCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_CURRENT_DISPLAYING_LOCATION_ACCESS);
            return;
        }
        map.setMyLocationEnabled(true);
        Location location = presenter.getCurrentLocation();
        if (location != null) {
//            map.setLatLngBoundsForCameraTarget(new LatLngBounds(new LatLng(location.getLatitude(),
//                    location.getLongitude()), new LatLng(location.getLatitude(),
//                    location.getLongitude())));
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
//                    location.getLongitude()), 8f);
//            map.animateCamera(cameraUpdate);

            CameraPosition position = CameraPosition.builder()
                    .target(new LatLng(location.getLatitude(),
                            location.getLongitude()))
                    .zoom(8f)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(position));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_CURRENT_DISPLAYING_LOCATION_ACCESS:
                allowShowCurrentLocation();
                break;
            case REQUEST_CODE_LAST_KNOWN_LOCATION_ACCESS:
                setupLastKnownLocation();
            default:
                break;

        }
    }

    protected void startAddressDecoderService(LatLng location) {
        Intent intent = new Intent(this, AddressDecoderService.class);
        intent.putExtra(AddressDecoderService.EXTRA_RESULT_RECEIVER, receiver);
        intent.putExtra(AddressDecoderService.EXTRA_SELECTED_LOCATION, location);
        startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case AddressDecoderService.RESULT_CODE_OK:
                    Address selectedAddress = resultData.getParcelable(AddressDecoderService.EXTRA_SELECTED_ADDRESS);
                    StringBuilder strReturnedAddress = new StringBuilder("");

                    for (int i = 0; i < selectedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(selectedAddress.getAddressLine(i)).append("\n");
                    }
                    String strAdd = strReturnedAddress.toString();

                    textSelectedLoc.setText(strAdd);
                    break;
                case AddressDecoderService.RESULT_CODE_ERROR:
                    break;
                default:
                    break;
            }
        }
    }

    public boolean isGPSEnabled() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void loadMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);

    }

}
