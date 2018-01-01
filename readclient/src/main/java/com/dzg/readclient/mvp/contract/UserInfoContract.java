package com.dzg.readclient.mvp.contract;

import android.net.Uri;

import com.dzg.readclient.mvp.presenter.base.BasePresenter;
import com.dzg.readclient.mvp.view.BaseView;

import java.io.File;

/**
 * Created by Administrator on 2017/7/10.
 */
public interface UserInfoContract {

    interface View extends BaseView {
        void showSexDialog();
        void showSetSexSuccess();
        void showSetPortraitSuccess();
        void showPortraitDialog();
        void showContent();
    }

    interface Presenter extends BasePresenter<View> {
        void setNickAndSex(String userId, String nickName, String sex);
        void upLoadProtrait(File file, String userId);
        void startPhotoZoom(Uri uri);
        void createSexDialog();
        void getImageToView();
        void uploadPortrait();
        void setSexSuccess();
        void setPortraitSuccess();
        void createPortraitDialog();
        void init();
    }
}