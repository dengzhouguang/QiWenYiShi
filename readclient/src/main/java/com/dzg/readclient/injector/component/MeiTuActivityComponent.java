package com.dzg.readclient.injector.component;

import com.dzg.readclient.injector.module.MeiTuActivityModule;
import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.ui.activity.MeiTuActivity;

import dagger.Component;

/**
 * Created by Administrator on 2017/8/17 0017.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {MeiTuActivityModule.class})
public interface MeiTuActivityComponent {
    void inject(MeiTuActivity activity);
}