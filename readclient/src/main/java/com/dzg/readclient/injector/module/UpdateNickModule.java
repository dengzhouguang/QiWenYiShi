package com.dzg.readclient.injector.module;

import android.content.Context;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.UpdateNickContract;
import com.dzg.readclient.mvp.presenter.UpdateNickPresenter;
import com.dzg.readclient.mvp.usecase.GetUpdateNick;
import com.dzg.readclient.repository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class UpdateNickModule {
    @Provides
    @PerActivity
    UpdateNickContract.Presenter provideUpdateNickPresenter(GetUpdateNick getUpdateNick, Context context) {
        return new UpdateNickPresenter(getUpdateNick,context);
    }

    @Provides
    @PerActivity
    GetUpdateNick provideUpdateNickUsecase(Repository repository) {
        return new GetUpdateNick(repository);
    }
}
