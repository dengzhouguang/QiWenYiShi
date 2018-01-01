package com.dzg.readclient.injector.module;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.AllContract;
import com.dzg.readclient.mvp.presenter.AllPresenter;
import com.dzg.readclient.mvp.usecase.GetAll;
import com.dzg.readclient.repository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class AllModule {
    @Provides
    @PerActivity
    AllContract.Presenter provideAllPresenter(GetAll getAll) {
        return new AllPresenter(getAll);
    }

    @Provides
    @PerActivity
    GetAll provideAllUsecase(Repository repository) {
        return new GetAll(repository);
    }
}
