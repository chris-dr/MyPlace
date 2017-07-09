package com.drevnitskaya.myplace.presenters.base;

import com.drevnitskaya.myplace.contracts.BasePlacesContract;
import com.drevnitskaya.myplace.view.base.BaseView;

/**
 * Created by air on 08.07.17.
 */

public interface BaseStatefulPresenter<V extends BaseView, S> extends BasePlacesContract.Presenter {

    S getState();

}
