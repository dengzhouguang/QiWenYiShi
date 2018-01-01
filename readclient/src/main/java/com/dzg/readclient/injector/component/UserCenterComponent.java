package com.dzg.readclient.injector.component;

import com.dzg.readclient.injector.module.UserCenterModule;
import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.ui.activity.UserCenterActivity;

import dagger.Component;

/**
 * Created by Administrator on 2017/8/17 0017.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = { UserCenterModule.class})
public interface UserCenterComponent {
    void inject(UserCenterActivity activity);
}