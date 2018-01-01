package com.dzg.readclient.injector.component;

import com.dzg.readclient.injector.module.AllJokeModule;
import com.dzg.readclient.injector.scope.PerActivity;
import com.dzg.readclient.ui.activity.AllJokeActivity;

import dagger.Component;

/**
 * Created by Administrator on 2017/8/17 0017.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {AllJokeModule.class})
public interface AllJokeComponent {
    void inject(AllJokeActivity allJokeActivity);
}