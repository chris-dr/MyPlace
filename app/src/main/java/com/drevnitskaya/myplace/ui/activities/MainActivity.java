package com.drevnitskaya.myplace.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.drevnitskaya.myplace.R;
import com.drevnitskaya.myplace.contract.MainContract;
import com.drevnitskaya.myplace.presenter.MainPresenter;
import com.drevnitskaya.myplace.ui.adapters.PagesAdapter;
import com.drevnitskaya.myplace.ui.fragments.FavoritePlacesFragment;
import com.drevnitskaya.myplace.ui.fragments.NearbyPlacesFragment;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private static final int REQUEST_CODE_SELECT_LOCATION = 100;
    private static final int REQUEST_CODE_ACCESS_LOCATION = 200;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private ActionBar actionBar;
    private MainContract.Presenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        presenter = new MainPresenter(this);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        setTitle(R.string.app_title);

        setupPager();
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        presenter.setLocationClient(LocationServices.getFusedLocationProviderClient(this));
        getLastKnownLocation();
    }

    private void setupPager() {
        PagesAdapter adapter = new PagesAdapter(getSupportFragmentManager());
        adapter.addItem(getString(R.string.nearby_title), NearbyPlacesFragment.newInstance());
        adapter.addItem(getString(R.string.favorite_title), FavoritePlacesFragment.newInstance());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @OnClick(R.id.buttonShowOnMap)
    protected void onShowOnMapClicked() {
        startActivityForResult(MapActivity.getStartIntent(this), REQUEST_CODE_SELECT_LOCATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_SELECT_LOCATION:
                Bundle extras = data.getExtras();
                LatLng selectedLoc = extras.getParcelable(MapActivity.EXTRA_SELECTED_LOCATION);
                presenter.nearbyPlacesRequest(String.valueOf(selectedLoc.latitude), String.valueOf(selectedLoc.longitude));
                break;
            default:
                break;
        }
    }

    public void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ACCESS_LOCATION);
            return;
        }

        presenter.getLastKnownLocation();

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
            case REQUEST_CODE_ACCESS_LOCATION:
                presenter.getLastKnownLocation();
                break;
            default:
                break;

        }
    }

    public boolean isGPSEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public String getApiKey() {
        return getString(R.string.apiKey_googleMaps);
    }
}
