package com.dzg.readclient.injector.module;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.QuShiContract;
import com.dzg.readclient.mvp.presenter.QuShiPresenter;
import com.dzg.readclient.mvp.usecase.GetQuShi;
import com.dzg.readclient.repository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class QuShiModule {
    @Provides
    @PerActivity
    QuShiContract.Presenter provideQuShiPresenter(GetQuShi getQuShi) {
        return new QuShiPresenter(getQuShi);
    }

    @Provides
    @PerActivity
    GetQuShi provideQuShi(Repository repository) {
        return new GetQuShi(repository);
    }
}
