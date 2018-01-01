package com.dzg.readclient.mvp.contract;

import com.dzg.readclient.mvp.presenter.base.BasePresenter;
import com.dzg.readclient.mvp.view.BaseView;

/**
 * Created by Administrator on 2017/7/10.
 */
public interface AllJokeContract {

    interface View extends BaseView {
        void showFragment();

        void showRefreshCompete();
    }

    interface Presenter extends BasePresenter<View> {
        void attachView(View view);

        void initFragment();

        void refreshCompete();
    }
}