package com.dzg.readclient.mvp.presenter;

import com.dzg.readclient.mvp.contract.MyCollectContract;
import com.dzg.readclient.mvp.model.Collect;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.mvp.usecase.GetMyCollect;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2017/8/23 0023.
 */

public class MyCollectPresenter implements MyCollectContract.Presenter {
    MyCollectContract.View mView;
    CompositeSubscription mCompositeSubscription;
    GetMyCollect mUsercase;

    public MyCollectPresenter(GetMyCollect mUsercase) {
        this.mUsercase = mUsercase;
        mCompositeSubscription=new CompositeSubscription();
    }

    @Override
    public void attachView(MyCollectContract.View view) {
        this.mView=view;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
     mCompositeSubscription.clear();
    }

    @Override
    public List<Collect> getCollects(int userId) {
        return  mUsercase.execute(new GetMyCollect.RequestValues(userId)).getCollects();
    }

    @Override
    public void cancelCollect(Joke joke) {
        mView.cancelCollect(joke);
    }

    @Override
    public void init() {
        mView.showCollects();
    }
}
