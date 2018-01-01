package com.dzg.readclient.injector.module;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.QuTuActivityContract;
import com.dzg.readclient.mvp.presenter.QuTuActivityPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class QuTuActivityModule {
    @Provides
    @PerActivity
    QuTuActivityContract.Presenter provideQTActivityPresenter() {
        return new QuTuActivityPresenter();
    }

}
