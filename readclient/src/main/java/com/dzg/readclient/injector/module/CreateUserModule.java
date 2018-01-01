package com.dzg.readclient.injector.module;

import android.content.Context;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.CreateUserContract;
import com.dzg.readclient.mvp.presenter.CreateUserPresenter;
import com.dzg.readclient.mvp.usecase.GetCreateUser;
import com.dzg.readclient.repository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class CreateUserModule {
    @Provides
    @PerActivity
    CreateUserContract.Presenter provideCreateUserPresenter(GetCreateUser getCreateUser, Context context) {
        return new CreateUserPresenter(getCreateUser,context);
    }

    @Provides
    @PerActivity
    GetCreateUser provideCreateUserUsecase(Repository repository) {
        return new GetCreateUser(repository);
    }
}
