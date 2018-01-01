package com.dzg.readclient.injector.module;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.AllJokeContract;
import com.dzg.readclient.mvp.presenter.AllJokePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class AllJokeModule {
    @Provides
    @PerActivity
    AllJokeContract.Presenter provideAllPresenter() {
        return new AllJokePresenter();
    }

}
