package com.dzg.readclient.injector.module;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.UserCenterContract;
import com.dzg.readclient.mvp.presenter.UserCenterPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class UserCenterModule {
    @Provides
    @PerActivity
    UserCenterContract.Presenter provideUserCenterPresenter() {
        return new UserCenterPresenter();
    }
}
