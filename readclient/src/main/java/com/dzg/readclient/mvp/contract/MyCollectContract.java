package com.dzg.readclient.mvp.contract;

import com.dzg.readclient.mvp.model.Collect;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.mvp.presenter.base.BasePresenter;
import com.dzg.readclient.mvp.view.BaseView;

import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */
public interface MyCollectContract {

    interface View extends BaseView {
        void showCollects();
        void cancelCollect(Joke joke);
    }

    interface Presenter extends BasePresenter<View> {
        List<Collect> getCollects(int userId);
        void cancelCollect(Joke joke);
        void init();
    }
}