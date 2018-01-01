package com.dzg.readclient.injector.module;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.QuShiActivityContract;
import com.dzg.readclient.mvp.presenter.QuShiActivityPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class QuShiActivityModule {
    @Provides
    @PerActivity
    QuShiActivityContract.Presenter provideMTActivityPresenter() {
        return new QuShiActivityPresenter();
    }

}
