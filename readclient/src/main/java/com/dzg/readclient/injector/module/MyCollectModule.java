package com.dzg.readclient.injector.module;

import android.content.Context;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.MyCollectContract;
import com.dzg.readclient.mvp.presenter.MyCollectPresenter;
import com.dzg.readclient.mvp.usecase.GetMyCollect;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class MyCollectModule {
    @Provides
    @PerActivity
    MyCollectContract.Presenter provideMyCollectPresenter(GetMyCollect myCollect) {
        return new MyCollectPresenter(myCollect);
    }

    @Provides
    @PerActivity
    GetMyCollect provideGetMyCollectUsecase(Context context) {
        return new GetMyCollect(context);
    }
}
