package com.drevnitskaya.myplace.ui.activities;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainContract.View {

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

    }
}
