package com.dzg.readclient.mvp.contract;

import com.dzg.readclient.mvp.presenter.base.BasePresenter;
import com.dzg.readclient.mvp.view.BaseView;

/**
 * Created by Administrator on 2017/7/10.
 */
public interface UpdateNickContract {

    interface View extends BaseView {
        void showContent();
        void showNickSuccess();
    }

    interface Presenter extends BasePresenter<View> {
        void setNickAndSex(String userId, String nickName, String sex);
        void init();
        void setNickSuccess();
    }
}