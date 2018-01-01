package com.dzg.readclient.injector.module;

import android.content.Context;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.TuDetailContract;
import com.dzg.readclient.mvp.presenter.TuDetailPresenter;
import com.dzg.readclient.mvp.usecase.GetTuDetail;
import com.dzg.readclient.repository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class TuDetailActivityModule {
    @Provides
    @PerActivity
    TuDetailContract.Presenter provideTuDetailActivityPresenter(GetTuDetail getTuDetail, Context context) {
        return new TuDetailPresenter(getTuDetail,context);
    }
    @Provides
    @PerActivity
    GetTuDetail provideGetTuDetail(Repository repository){
        return new GetTuDetail(repository);
    }

}
