package com.dzg.readclient.mvp.contract;

import com.dzg.readclient.mvp.presenter.base.BasePresenter;
import com.dzg.readclient.mvp.view.BaseView;

/**
 * Created by Administrator on 2017/7/10.
 */
public interface QuShiActivityContract {

    interface View extends BaseView {
        void showFragment();
        void showRefreshCompete();
    }

    interface Presenter extends BasePresenter<View> {
        void initFragment();
        void refreshCompete();
    }
}