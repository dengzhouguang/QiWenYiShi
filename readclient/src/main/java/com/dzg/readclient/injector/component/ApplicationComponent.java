package com.dzg.readclient.injector.component;

import android.app.Application;

import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.injector.module.ApplicationModule;
import com.dzg.readclient.injector.module.NetworkModule;
import com.dzg.readclient.injector.scope.PerApplication;
import com.dzg.readclient.repository.interfaces.Repository;

import dagger.Component;

@PerApplication
@Component(modules = {ApplicationModule.class, NetworkModule.class})
public interface ApplicationComponent {

    Application application();

    ReadClientApp readClientApplication();

    Repository repository();
}
