package com.dzg.readclient.mvp.contract;

import android.net.Uri;

import com.dzg.readclient.mvp.presenter.base.BasePresenter;
import com.dzg.readclient.mvp.view.BaseView;

import java.io.File;

/**
 * Created by Administrator on 2017/7/10.
 */
public interface CreateUserContract {

    interface View extends BaseView {
        void showSexDialog();

        void showPortraitDialog();

        void showImageToView();

        void showPortraitSuccess();

        void showNickSexSuccess();
    }

    interface Presenter extends BasePresenter<View> {
        void createSexDialog();

        void setNickAndSex(String userId, String nickName, String sex);

        void upLoadProtrait(File file, String userId);

        void createPortraitDialog();

        void startPhotoZoom(Uri uri);

        void getImageToView();

        void setPortraitSuccess();

        void setNickSexSuccess();
    }
}