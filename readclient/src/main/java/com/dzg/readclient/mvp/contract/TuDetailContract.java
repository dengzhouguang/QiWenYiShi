package com.dzg.readclient.mvp.contract;

import com.dzg.readclient.commons.PageResult;
import com.dzg.readclient.mvp.model.DingOrCai;
import com.dzg.readclient.mvp.presenter.base.BasePresenter;
import com.dzg.readclient.mvp.view.BaseView;

/**
 * Created by Administrator on 2017/7/10.
 */
public interface TuDetailContract {

    interface View extends BaseView {
        void showAndReSet();
        void showTuContent();
        void showCommentSuccess();
        void showComments();
        void showLoadSuccess(PageResult pageResult);
        void show2Target();
        void showContent();
    }

    interface Presenter extends BasePresenter<View> {
       void getJokeById(Integer jokeId);
        void getCommentByJokeId(int jokeId, int offset, int count);
        void addComment(Integer jokeId, String content);
        DingOrCai getDingOrCai(int userId, int jokeId);
        void dingOrCai(int userId, int jokeId, int dingOrCai, int num);
        void loadComments();
        void loadSuccess(PageResult pageResult);
        void scroll2Target();
        void reSet();
        void commentSuccess();
        void setTuContent();
        void initTuContent();

    }
}