package com.drevnitskaya.myplace.contract;

import com.drevnitskaya.myplace.presenter.base.BasePresenter;
import com.drevnitskaya.myplace.view.base.BaseView;

/**
 * Created by air on 30.06.17.
 */

public interface MainContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {
    }

}
