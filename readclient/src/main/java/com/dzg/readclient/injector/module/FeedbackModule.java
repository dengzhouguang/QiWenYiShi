package com.dzg.readclient.injector.module;

import android.content.Context;

import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.mvp.contract.FeedbackContract;
import com.dzg.readclient.mvp.presenter.FeedbackPresenter;
import com.dzg.readclient.mvp.usecase.GetFeedback;
import com.dzg.readclient.repository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
@Module
public class FeedbackModule {
    @Provides
    @PerActivity
    FeedbackContract.Presenter provideFeedbackPresenter(GetFeedback getFeedback, Context context) {
        return new FeedbackPresenter(getFeedback, context);
    }

    @Provides
    @PerActivity
    GetFeedback provideFeedbackUsecase(Repository repository) {
        return new GetFeedback(repository);
    }
}
