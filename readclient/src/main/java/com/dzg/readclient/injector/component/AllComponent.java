package com.dzg.readclient.injector.component;

import com.dzg.readclient.injector.module.ActivityModule;
import com.dzg.readclient.injector.module.AllModule;
import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.ui.fragment.AllFragment;

import dagger.Component;

/**
 * Created by Administrator on 2017/8/17 0017.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, AllModule.class})
public interface AllComponent {
    void inject(AllFragment allFragment);
}