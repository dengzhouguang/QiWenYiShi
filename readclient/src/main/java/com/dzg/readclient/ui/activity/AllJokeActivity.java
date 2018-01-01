package com.dzg.readclient.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.adapters.QQYFragmentPagerAdapter;
import com.dzg.readclient.injector.component.AllJokeComponent;
import com.dzg.readclient.injector.component.ApplicationComponent;
import com.dzg.readclient.injector.component.DaggerAllJokeComponent;
import com.dzg.readclient.injector.module.AllJokeModule;
import com.dzg.readclient.mvp.contract.AllJokeContract;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.ui.fragment.AllFragment;
import com.dzg.readclient.ui.view.CategoryTabStrip;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ClassName: AllJokeActivity
 */
public class AllJokeActivity extends BaseActivity implements AllJokeContract.View {
    private final String TAG = "AllJokeActivity";
    @Inject
    public AllJokeContract.Presenter mPresenter;
    @BindView(R.id.mViewPager)
    ViewPager mViewPager;
    @BindView(R.id.refresh)
    ImageView mRefreshIV;
    @BindView(R.id.nav_view)
    ImageView mNavView;
    @BindView(R.id.categoryTabStrip)
    CategoryTabStrip mCategoryTabStrip;
    private QQYFragmentPagerAdapter mAdapetr;
    private ArrayList<Fragment> mFragments;
    /**
     * tab栏目（最新、最热）
     */
    private ArrayList<String> mCategoryList = new ArrayList<String>();

    private Animation mRefreshAnimation;  //刷新图标动画

    @Override
    public void onCreateView(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        mCategoryList.add("最新");
        mCategoryList.add("最热");
        mFragments = new ArrayList<Fragment>();
        mPresenter.attachView(this);
        mPresenter.initFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_all_joke;
    }

    @Override
    public void injectDependences() {
        ApplicationComponent applicationComponent = ((ReadClientApp) getApplication()).getApplicationComponent();
        AllJokeComponent allJokeComponent = DaggerAllJokeComponent.builder()
                .applicationComponent(applicationComponent)
                .allJokeModule(new AllJokeModule())
                .build();
        allJokeComponent.inject(this);
    }

    /**
     * 初始化fragment
     */
    @Override
    public void showFragment() {
        Bundle data = new Bundle();
        data.putInt("newOrHotFlag", Joke.SORT_NEW);
        AllFragment allNewFragment = new AllFragment();
        allNewFragment.setArguments(data);
        mFragments.add(allNewFragment);

        Bundle data1 = new Bundle();
        data1.putInt("newOrHotFlag", Joke.SORT_HOT);
        AllFragment allHotFragment = new AllFragment();
        allHotFragment.setArguments(data1);
        mFragments.add(allHotFragment);

        mAdapetr = new QQYFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mAdapetr);
        mCategoryTabStrip.setViewPager(mViewPager);
        mCategoryTabStrip.setCatalogs(mCategoryList);
    }

    @OnClick(R.id.refresh)
    void refresh(View view) {
        if (mRefreshAnimation == null) {
            mRefreshAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.progress_anim);
        }
        AllFragment allFragment = (AllFragment) mFragments.get(mViewPager.getCurrentItem());
        if (allFragment.isRefresh()) {
            return;
        }
        view.startAnimation(mRefreshAnimation);
        allFragment.refresh();
    }

    @OnClick(R.id.nav_view)
    void nav(View view) {
        DrawerLayout drawer = (DrawerLayout) AllJokeActivity.this.getParent().findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }

    /**
     * 刷新完毕，结束刷新动画
     */
    @Override
    public void showRefreshCompete() {
        Log.e("refreshCompete", "refreshCompete");
        mRefreshIV.clearAnimation();
        if (mRefreshAnimation != null) {
            mRefreshAnimation.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
    }

}
