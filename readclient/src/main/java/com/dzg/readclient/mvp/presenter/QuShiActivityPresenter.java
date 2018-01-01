package com.dzg.readclient.mvp.presenter;

import com.dzg.readclient.mvp.contract.QuShiActivityContract;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2017/8/18 0018.
 */

public class QuShiActivityPresenter implements QuShiActivityContract.Presenter {
    QuShiActivityContract.View mView;
    CompositeSubscription mCompositeSubscription;
    @Override
    public void attachView(QuShiActivityContract.View view) {
    mView=view;
    }

    public QuShiActivityPresenter() {
        this.mCompositeSubscription=new CompositeSubscription();
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
    mCompositeSubscription.clear();
    }

    @Override
    public void initFragment() {
        mView.showFragment();
    }

    @Override
    public void refreshCompete() {
        mView.showRefreshCompete();
    }
}
