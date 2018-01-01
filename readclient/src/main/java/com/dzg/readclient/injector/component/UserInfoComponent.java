package com.dzg.readclient.injector.component;

import com.dzg.readclient.injector.module.ActivityModule;
import com.dzg.readclient.injector.module.UserInfoModule;
import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.ui.activity.UserInfoActivity;

import dagger.Component;

/**
 * Created by Administrator on 2017/8/17 0017.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, UserInfoModule.class})
public interface UserInfoComponent {
    void inject(UserInfoActivity activity);
}