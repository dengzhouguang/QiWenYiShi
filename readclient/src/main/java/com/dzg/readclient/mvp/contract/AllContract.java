package com.dzg.readclient.mvp.contract;

import android.content.Context;

import com.dzg.readclient.commons.PageResult;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.mvp.presenter.base.BasePresenter;
import com.dzg.readclient.mvp.view.BaseView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/10.
 */
public interface AllContract {

    interface View extends BaseView {
        Context getContext();

        void showFromCache();

        void showQuShi();

        void showSuccess(PageResult pageResult);

        void showComplete();

        void clearListsAndInitPageInfo();
    }

    interface Presenter extends BasePresenter<View> {
        void loadData();

        void clearListsAndInitPageInfo();

        void loadQuShi();

        void loadSuccess(PageResult pageResult);

        void refreshAll(int newOrHotFlag, int count);

        void saveCache(ArrayList<Joke> jokeLists, boolean hasNext, int pageId, String key);

        void getAll(int newOrHotFlag, int offset, int count);

        void loadComplete();
    }
}