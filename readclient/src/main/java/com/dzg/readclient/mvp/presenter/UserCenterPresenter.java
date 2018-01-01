package com.dzg.readclient.mvp.presenter;

import com.dzg.readclient.mvp.contract.UserCenterContract;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2017/8/25 0025.
 */

public class UserCenterPresenter implements UserCenterContract.Presenter {
    private CompositeSubscription mCompiteSubscription;
    private UserCenterContract.View mView;

    @Override
    public void attachView(UserCenterContract.View view) {
        mView = view;
    }

    public UserCenterPresenter() {
        mCompiteSubscription = new CompositeSubscription();
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mCompiteSubscription.clear();
    }

    @Override
    public void init() {
        mView.showContent();
    }

    @Override
    public void initCheckBoxStatusAndSetListenner() {
        mView.showCheckBoxStatusAndSetListenner();
    }
}
