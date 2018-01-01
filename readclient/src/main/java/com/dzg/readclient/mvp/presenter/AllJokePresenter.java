package com.dzg.readclient.mvp.presenter;

import com.dzg.readclient.mvp.contract.AllJokeContract;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2017/8/18 0018.
 */

public class AllJokePresenter implements AllJokeContract.Presenter {
    AllJokeContract.View mView;
    CompositeSubscription mCompositeSubscription;

    public AllJokePresenter() {
        this.mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void attachView(AllJokeContract.View view) {
        mView = view;
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
