package com.dzg.readclient.injector.component;

import com.dzg.readclient.injector.module.QuShiActivityModule;
import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.ui.activity.QuShiActivity;

import dagger.Component;

/**
 * Created by Administrator on 2017/8/17 0017.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {QuShiActivityModule.class})
public interface QuShiActivityComponent {
    void inject(QuShiActivity activity);
}