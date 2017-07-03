package com.drevnitskaya.myplace.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
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
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private static final int REQUEST_CODE_SELECT_LOCATION = 100;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private ActionBar actionBar;
    private MainContract.Presenter presenter;
    private PagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        presenter = new MainPresenter(this);
        presenter.initRealm();

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        setTitle(R.string.app_title);

        setupPager();
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.closeRealm();
    }

    private void setupPager() {
        adapter = new PagesAdapter(getSupportFragmentManager());
        adapter.addItem(getString(R.string.nearby_title), NearbyPlacesFragment.newInstance());
        adapter.addItem(getString(R.string.favorite_title), FavoritePlacesFragment.newInstance());
        viewPager.setAdapter(adapter);
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
                updateNearbyPlaces(selectedLoc);
                break;
            default:
                break;
        }
    }

    public void updateNearbyPlaces(LatLng selectedLoc) {
        NearbyPlacesFragment fragment = adapter.getNearbyPlaceFragment();
        fragment.getNearbyPlaces(selectedLoc);
    }
}
