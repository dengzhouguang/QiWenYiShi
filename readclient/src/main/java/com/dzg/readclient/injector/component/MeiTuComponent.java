package com.dzg.readclient.injector.component;

import com.dzg.readclient.injector.module.ActivityModule;
import com.dzg.readclient.injector.module.MeiTuModule;
import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.ui.fragment.MeiTuFragment;

import dagger.Component;

/**
 * Created by Administrator on 2017/8/17 0017.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, MeiTuModule.class})
public interface MeiTuComponent {

    void inject(MeiTuFragment meiTuFragment);
}