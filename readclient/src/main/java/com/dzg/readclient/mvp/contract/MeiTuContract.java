package com.dzg.readclient.mvp.contract;

import android.content.Context;

import com.dzg.readclient.commons.PageResult;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.mvp.presenter.base.BasePresenter;
import com.dzg.readclient.mvp.view.BaseView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/17 0017.
 */

public interface MeiTuContract {
    interface View extends BaseView {
        Context getContext();
        void showFromCache();
        void showMeiTu();
        void showSuccess(PageResult pageResult);
        void showComplete();
        void clearListsAndInitPageInfo();
    }

    interface Presenter extends BasePresenter<MeiTuContract.View> {
        void loadData();
        void clearListsAndInitPageInfo();
        void loadMeiTu();
        void loadSuccess(PageResult pageResult);
        void reflush( int newOrHotFlag, int count);
        void saveCache(ArrayList<Joke> jokeLists, boolean hasNext, int pageId, String key);
        void getAll(int newOrHotFlag,int offset,int count);
        void loadComplete();
    }
}
