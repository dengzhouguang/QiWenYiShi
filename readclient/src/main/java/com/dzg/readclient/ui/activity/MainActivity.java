package com.dzg.readclient.ui.activity;

import android.Manifest;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TabHost;

import com.dzg.readclient.R;
import com.dzg.readclient.utils.StatusBarUtils;

import butterknife.BindView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.tabhost)
    TabHost mTabHost;
    @BindView(android.R.id.tabcontent)
    FrameLayout mFrameLayout;
    private TabHost.TabSpec mTabSpec;
    @BindView(R.id.radiogroup)
    RadioGroup mRadioGroup;
    protected LocalActivityManager mLocalActivityManager;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreateView(Bundle savedInstanceState) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mLocalActivityManager = new LocalActivityManager(MainActivity.this, true);
        mLocalActivityManager.dispatchCreate(savedInstanceState);
        initTab();
    }


    private void initTab() {
        mTabHost.setup(mLocalActivityManager);
        mTabSpec = mTabHost.newTabSpec("all").setIndicator("all")
                .setContent(new Intent(this, AllJokeActivity.class)); // 进入会首先显示这个activity
        mTabHost.addTab(mTabSpec);
        mTabSpec = mTabHost.newTabSpec("qushi").setIndicator("qushi")
                .setContent(new Intent(this, QuShiActivity.class));
        mTabHost.addTab(mTabSpec);
        mTabSpec = mTabHost.newTabSpec("qutu").setIndicator("qutu")
                .setContent(new Intent(this, QuTuActivity.class));
        mTabHost.addTab(mTabSpec);
        mTabSpec = mTabHost.newTabSpec("meitu").setIndicator("meitu")
                .setContent(new Intent(this, MeiTuActivity.class));
        mTabHost.addTab(mTabSpec);
        mTabSpec = mTabHost.newTabSpec("user").setIndicator("user")
                .setContent(new Intent(this, UserCenterActivity.class));
        mTabHost.addTab(mTabSpec);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (mTabHost != null) {
                    switch (checkedId) {
                        case R.id.all_tab:
                            mTabHost.setCurrentTabByTag("all");
                            break;
                        case R.id.qushi_tab:
                            mTabHost.setCurrentTab(1);
                            break;
                        case R.id.qutu_tab:
                            mTabHost.setCurrentTab(2);
                            break;
                        case R.id.meitu_tab:
                            mTabHost.setCurrentTab(3);
                            break;
                        case R.id.usercenter_tab:
                            mTabHost.setCurrentTab(4);
                            break;
                    }
                }
            }
        });
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
            mFrameLayout.setPadding(0, StatusBarUtils.getStatusBarHeight(this), 0, 0);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            requestPermissions();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        mLocalActivityManager.dispatchResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mLocalActivityManager.dispatchPause(isFinishing());
        super.onPause();
    }

    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == 1)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

            } else
            {
                finish();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
