package com.dzg.readclient.injector.module;

import android.app.Application;

import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.injector.scope.PerApplication;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/6/22.
 */
@Module
public class ApplicationModule {
    private final ReadClientApp mReadClient;

    public ApplicationModule(ReadClientApp application) {
        mReadClient = application;
    }

    @Provides
    @PerApplication
    public ReadClientApp provideReadClient() {
        return mReadClient;
    }

    @Provides
    @PerApplication
    public Application provideApplication() {
        return mReadClient;
    }
}
