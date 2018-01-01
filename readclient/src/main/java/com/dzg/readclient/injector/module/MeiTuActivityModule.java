package com.dzg.readclient.injector.module;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.MeiTuActivityContract;
import com.dzg.readclient.mvp.presenter.MeiTuActivityPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class MeiTuActivityModule {
    @Provides
    @PerActivity
    MeiTuActivityContract.Presenter provideMTActivityPresenter() {
        return new MeiTuActivityPresenter();
    }

}
