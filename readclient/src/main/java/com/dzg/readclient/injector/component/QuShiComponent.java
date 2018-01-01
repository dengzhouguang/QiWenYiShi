package com.dzg.readclient.injector.component;

import com.dzg.readclient.injector.module.ActivityModule;
import com.dzg.readclient.injector.module.QuShiModule;
import com.dzg.readclient.injector.module.QuTuModule;
import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.ui.fragment.QuShiFragment;
import com.dzg.readclient.ui.fragment.QuTuFragment;

import dagger.Component;

/**
 * Created by Administrator on 2017/8/17 0017.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, QuShiModule.class})
public interface QuShiComponent {

    void inject(QuShiFragment quShiFragment);
}