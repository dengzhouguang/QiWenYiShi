package com.dzg.readclient.mvp.contract;

import com.dzg.readclient.mvp.presenter.base.BasePresenter;
import com.dzg.readclient.mvp.view.BaseView;

/**
 * Created by Administrator on 2017/7/10.
 */
public interface UserCenterContract {

    interface View extends BaseView {
        void showContent();
        void showCheckBoxStatusAndSetListenner();
    }

    interface Presenter extends BasePresenter<View> {
        void init();
        void initCheckBoxStatusAndSetListenner();
    }
}