package com.dzg.readclient.injector.module;

import android.content.Context;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.LoginContract;
import com.dzg.readclient.mvp.presenter.LoginPresenter;
import com.dzg.readclient.mvp.usecase.GetLogin;
import com.dzg.readclient.repository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class LoginModule {
    @Provides
    @PerActivity
    LoginContract.Presenter provideLoginPresenter(GetLogin getLogin, Context context) {
        return new LoginPresenter(getLogin,context) {
        };
    }

    @Provides
    @PerActivity
    GetLogin provideLoginUsecase(Repository repository) {
        return new GetLogin(repository);
    }
}
