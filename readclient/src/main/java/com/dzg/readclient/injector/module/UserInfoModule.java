package com.dzg.readclient.injector.module;

import android.content.Context;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.UserInfoContract;
import com.dzg.readclient.mvp.presenter.UserInfoPresenter;
import com.dzg.readclient.mvp.usecase.GetUserInfo;
import com.dzg.readclient.repository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class UserInfoModule {
    @Provides
    @PerActivity
    UserInfoContract.Presenter provideCreateUserPresenter(GetUserInfo getUserInfo, Context context) {
        return new UserInfoPresenter(getUserInfo,context);
    }

    @Provides
    @PerActivity
    GetUserInfo provideUserInfoUsecase(Repository repository) {
        return new GetUserInfo(repository);
    }
}
