package com.dzg.readclient.injector.module;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.QuTuContract;
import com.dzg.readclient.mvp.presenter.QuTuPresenter;
import com.dzg.readclient.mvp.usecase.GetQuTu;
import com.dzg.readclient.repository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class QuTuModule {
    @Provides
    @PerActivity
    QuTuContract.Presenter getQuTuPresenter(GetQuTu getQuTu) {
        return new QuTuPresenter(getQuTu);
    }

    @Provides
    @PerActivity
    GetQuTu getQuTu(Repository repository) {
        return new GetQuTu(repository);
    }
}
