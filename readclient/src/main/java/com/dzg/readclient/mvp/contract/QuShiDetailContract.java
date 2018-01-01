package com.dzg.readclient.mvp.contract;

import com.dzg.readclient.commons.PageResult;
import com.dzg.readclient.mvp.model.DingOrCai;
import com.dzg.readclient.mvp.presenter.base.BasePresenter;
import com.dzg.readclient.mvp.view.BaseView;

/**
 * Created by Administrator on 2017/7/10.
 */
public interface QuShiDetailContract {

    interface View extends BaseView {
        void showAndReSet();
        void showQuShiContent();
        void showSetQushiContent();
        void showComments();
        void showLoadSuccess(PageResult pageResult);
        void showCommentSuccess();
    }

    interface Presenter extends BasePresenter<View> {
        void getJokeById(Integer jokeId);
        void getCommentByJokeId( int jokeId, int offset, int count);
        void addComment(Integer jokeId, String content);
        DingOrCai getDingOrCai(int userId, int jokeId);
        void dingOrCai(int userId, int jokeId, int dingOrCai, int num);
        void reSet();
        void initQuShiContent();
        void setQuShiContent();
        void loadComments();
        void loadSuccess(PageResult pageResult);
        void commentSuccess();
    }
}