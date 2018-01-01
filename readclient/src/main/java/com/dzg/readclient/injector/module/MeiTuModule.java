package com.dzg.readclient.injector.module;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.MeiTuContract;
import com.dzg.readclient.mvp.presenter.MeiTuPresenter;
import com.dzg.readclient.mvp.usecase.GetMeiTu;
import com.dzg.readclient.repository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class MeiTuModule {
    @Provides
    @PerActivity
    MeiTuContract.Presenter provideMeiTuPresenter(GetMeiTu getMeiTu) {
        return new MeiTuPresenter(getMeiTu);
    }

    @Provides
    @PerActivity
    GetMeiTu provideMeiTu(Repository repository) {
        return new GetMeiTu(repository);
    }
}
