package com.drevnitskaya.myplace.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
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
import com.drevnitskaya.myplace.services.GeofencesMonitoringService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by air on 01.07.17.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, MapContract.View {

    public static final String EXTRA_SELECTED_LOCATION = "com.drevnitskaya.myplace.selected_location";
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
        presenter.setupGeocoder(new Geocoder(this, Locale.getDefault()));

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.common_selectLocation);

        loadMap();

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
    protected void onStop() {
        super.onStop();
        presenter.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void loadMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        allowShowCurrentLocation();
        presenter.manageSelectedLocation(map.getCameraPosition().target);

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                presenter.manageSelectedLocation(map.getCameraPosition().target);
            }
        });
    }

    @Override
    public void setSelectedAddress(String selectedAddress) {
        textSelectedLoc.setText(selectedAddress);
    }

    @Override
    public void showDecodingError() {
        setSelectedAddress(getString(R.string.map_addressDecodingError));
    }

    private void allowShowCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_CURRENT_DISPLAYING_LOCATION_ACCESS);
            return;
        }
        startGeofenceMonitoring();
        map.setMyLocationEnabled(true);
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
            default:
                break;
        }
    }

    @OnClick(R.id.layoutAddressCont)
    protected void onLocationSelected() {
        if (presenter.getSelectedLocation() != null) {
            Intent data = new Intent();
            Bundle extras = new Bundle();
            extras.putParcelable(EXTRA_SELECTED_LOCATION, presenter.getSelectedLocation());
            data.putExtras(extras);
            setResult(Activity.RESULT_OK, data);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }

    private void startGeofenceMonitoring() {
        Intent intent = new Intent(this, GeofencesMonitoringService.class);
        startService(intent);
    }

}
