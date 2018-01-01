package com.dzg.readclient.mvp.contract;

import com.dzg.readclient.mvp.model.User;
import com.dzg.readclient.mvp.presenter.base.BasePresenter;
import com.dzg.readclient.mvp.view.BaseView;

/**
 * Created by Administrator on 2017/7/10.
 */
public interface LoginContract {

    interface View extends BaseView {
        void showRegistSuccess(User user);
    }

    interface Presenter extends BasePresenter<View> {
        void regist( String phone, String password);
        void login( String phone, String password);
        void checkUserExeist(String phone);
        void registSuccess(User user);
    }

}