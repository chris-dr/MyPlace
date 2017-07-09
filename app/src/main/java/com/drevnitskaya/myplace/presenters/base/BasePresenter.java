package com.drevnitskaya.myplace.presenters.base;

import com.drevnitskaya.myplace.view.base.BaseView;

/**
 * Created by air on 30.06.17.
 */

public interface BasePresenter<V extends BaseView> {

    void initRealm();

    void closeRealm();

}