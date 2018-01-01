package com.dzg.readclient.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.utils.SharePreferenceUtil;

import butterknife.ButterKnife;
import qiu.niorgai.StatusBarCompat;

public abstract class BaseActivity extends AppCompatActivity {
    protected SharePreferenceUtil spUtil;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependences();
        setContentView(getLayoutId());
        ReadClientApp.addActivity(this); //在界面启动栈中加入该界面
        spUtil = ReadClientApp.getInstance().getSpUtil();
        ButterKnife.bind(this);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.bg_action_bar));
        onCreateView(savedInstanceState);
    }

    public abstract int getLayoutId();

    public abstract void onCreateView(Bundle savedInstanceState);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReadClientApp.removeActivity(this); //在界面启动栈中删除该界面
    }

    public void injectDependences() {
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    //带动画打开界面
    protected void startActivityForResultWithAnimation(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
    }

    //带动画打开界面
    protected void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
    }

    //带动画关闭界面
    protected void finishWithAnimation() {
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
    }

    @Override
    public void onBackPressed() {
        finishWithAnimation();
        super.onBackPressed();
    }

}
