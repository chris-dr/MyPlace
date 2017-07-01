package com.drevnitskaya.myplace.presenter;

import com.drevnitskaya.myplace.contract.MainContract;

/**
 * Created by air on 30.06.17.
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;

    public MainPresenter(MainContract.View view) {
        this.view = view;
    }
}
