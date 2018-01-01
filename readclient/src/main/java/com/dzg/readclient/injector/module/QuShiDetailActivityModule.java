package com.dzg.readclient.injector.module;

import android.content.Context;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.QuShiDetailContract;
import com.dzg.readclient.mvp.presenter.QuShiDetailPresenter;
import com.dzg.readclient.mvp.usecase.GetQuShiDetail;
import com.dzg.readclient.repository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class QuShiDetailActivityModule {
    @Provides
    @PerActivity
    QuShiDetailContract.Presenter provideQuShiDetailActivityPresenter(GetQuShiDetail getQuShiDetail, Context context) {
        return new QuShiDetailPresenter(getQuShiDetail,context);
    }
    @Provides
    @PerActivity
    GetQuShiDetail provideGetQuShiDetail(Repository repository){
        return new GetQuShiDetail(repository);
    }

}
