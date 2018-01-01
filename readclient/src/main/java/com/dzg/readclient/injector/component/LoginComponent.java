package com.dzg.readclient.injector.component;

import com.dzg.readclient.injector.module.ActivityModule;
import com.dzg.readclient.injector.module.LoginModule;
import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.ui.activity.LoginActivity;

import dagger.Component;

/**
 * Created by Administrator on 2017/8/17 0017.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, LoginModule.class})
public interface LoginComponent {
    void inject(LoginActivity activity);
}