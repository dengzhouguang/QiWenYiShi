package com.dzg.readclient.injector.component;

import com.dzg.readclient.injector.module.QuTuActivityModule;
import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.ui.activity.QuTuActivity;

import dagger.Component;

/**
 * Created by Administrator on 2017/8/17 0017.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {QuTuActivityModule.class})
public interface QuTuActivityComponent {
    void inject(QuTuActivity activity);
}