package com.dzg.readclient;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dzg.readclient.dao.DingCaiDAO;
import com.dzg.readclient.greendao.DaoMaster;
import com.dzg.readclient.greendao.DaoSession;
import com.dzg.readclient.injector.component.ApplicationComponent;
import com.dzg.readclient.injector.component.DaggerApplicationComponent;
import com.dzg.readclient.injector.module.ApplicationModule;
import com.dzg.readclient.injector.module.NetworkModule;
import com.dzg.readclient.mvp.model.DingOrCai;
import com.dzg.readclient.mvp.model.User;
import com.dzg.readclient.service.IJokeService;
import com.dzg.readclient.service.impl.JokeServiceImpl;
import com.dzg.readclient.ui.activity.BaseActivity;
import com.dzg.readclient.utils.SharePreferenceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/22.
 */

public class ReadClientApp extends Application {
    private  static Context sContext;
    public static List<Activity> activities = new ArrayList<Activity>();
    private ApplicationComponent mApplicationComponent;
    private static SharePreferenceUtil mSpUtil;
    private static ReadClientApp sReadClientApp;
    public User currentUser;
    public boolean isStartOtherActivity=false;
    public static boolean isStart = false;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    @Override
    public void onCreate() {
        super.onCreate();
        sContext=getApplicationContext();
        sReadClientApp=this;
        setDatabase();
        setupInjector();
        uploadDingOrCai();
        currentUser= (User) getSpUtil().getObject("user",null);
    }

    private void uploadDingOrCai() {
        DingCaiDAO dingOrCaiDAO;
        IJokeService server;
        dingOrCaiDAO=new DingCaiDAO();
        server=new JokeServiceImpl(getContext());
        List<DingOrCai> unUploadDing = dingOrCaiDAO.getUnUploadDing();
        server.ding(unUploadDing);
        List<DingOrCai> unUploadCai = dingOrCaiDAO.getUnUploadCai();
        server.cai(unUploadCai);
    }

    private void setupInjector() {
        mApplicationComponent= DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule(this)).build();
    }
    public static Context getContext(){
        return  sContext;
    }
    public ApplicationComponent getApplicationComponent(){
        return mApplicationComponent;
    }
    public static void addActivity(BaseActivity baseActivity) {
        activities.add(baseActivity);
    }
    public synchronized SharePreferenceUtil getSpUtil() {
        if (mSpUtil == null)
            mSpUtil = new SharePreferenceUtil(this, "msg_sp");
        return mSpUtil;
    }
    public static ReadClientApp getInstance(){
        return sReadClientApp;
    }

    public static void removeActivity(BaseActivity baseActivity) {
        activities.remove(baseActivity);
    }
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
