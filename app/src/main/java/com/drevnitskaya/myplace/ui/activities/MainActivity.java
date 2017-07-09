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
import com.drevnitskaya.myplace.contracts.MainContract;
import com.drevnitskaya.myplace.presenters.MainPresenter;
import com.drevnitskaya.myplace.ui.adapters.PagesAdapter;
import com.drevnitskaya.myplace.ui.fragments.FavoritePlacesFragment;
import com.drevnitskaya.myplace.ui.fragments.NearbyPlacesFragment;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.drevnitskaya.myplace.receivers.GeofencingEventsReceiver.EXTRA_GEOFENCE_NOTIFICATION;

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

        handleIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.closeRealm();
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra(EXTRA_GEOFENCE_NOTIFICATION)) {
            openFavorites();
        }
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
                openNearby();
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

    private void openNearby() {
        TabLayout.Tab tab = tabLayout.getTabAt(PagesAdapter.IDX_PAGE_NEARBY_PLACES);
        if (tab != null) {
            tab.select();
        }
    }

    private void openFavorites() {
        TabLayout.Tab tab = tabLayout.getTabAt(PagesAdapter.IDX_PAGE_FAVORITE_PLACES);
        if (tab != null) {
            tab.select();
        }
    }
}
