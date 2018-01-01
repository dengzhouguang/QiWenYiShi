package com.dzg.readclient.mvp.contract;

import com.dzg.readclient.mvp.presenter.base.BasePresenter;
import com.dzg.readclient.mvp.view.BaseView;

/**
 * Created by Administrator on 2017/7/10.
 */
public interface FeedbackContract {

    interface View extends BaseView {
        void showFeedbackSuccess();

        void showBack();
    }

    interface Presenter extends BasePresenter<View> {
        void feedback(String content, String contact, String s);

        void feedbackSuccess();

        void toBack();
    }
}
