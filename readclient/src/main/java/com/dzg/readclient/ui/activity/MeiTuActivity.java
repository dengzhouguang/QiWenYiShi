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
import com.dzg.readclient.injector.component.ApplicationComponent;
import com.dzg.readclient.injector.component.DaggerMeiTuActivityComponent;
import com.dzg.readclient.injector.component.MeiTuActivityComponent;
import com.dzg.readclient.injector.module.MeiTuActivityModule;
import com.dzg.readclient.mvp.contract.MeiTuActivityContract;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.ui.fragment.MeiTuFragment;
import com.dzg.readclient.ui.view.CategoryTabStrip;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ClassName: MeiTuActivity
 * @Description: 美图activity
 */
public class MeiTuActivity extends BaseActivity implements MeiTuActivityContract.View{
	@Inject
	public MeiTuActivityContract.Presenter mPresenter;
	@BindView(R.id.mViewPager)
    ViewPager mViewPager;
	@BindView(R.id.refresh)
	ImageView mRefreshIV;
	private QQYFragmentPagerAdapter mAdapetr;
	private ArrayList<Fragment> mFragments;
	
	@BindView(R.id.categoryTabStrip)
	CategoryTabStrip mCategoryTabStrip;
	/** tab栏目（最新、最热）*/
	private ArrayList<String> mCategoryList = new ArrayList<String>();
	private Animation mRefreshAnimation;  //刷新图标动画
	
	@Override
	public  void onCreateView(Bundle savedInstanceState) {
		mCategoryList.add("最新");
		mCategoryList.add("最热");
		mFragments = new ArrayList<Fragment>();
		mPresenter.attachView(this);
		mPresenter.initFragment();
	}

	@Override
	public void injectDependences() {
		ApplicationComponent applicationComponent= ReadClientApp.getInstance().getApplicationComponent();
		MeiTuActivityComponent component= DaggerMeiTuActivityComponent.builder()
				.applicationComponent(applicationComponent)
				.meiTuActivityModule(new MeiTuActivityModule())
				.build();
		component.inject(this);
	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_meitu;
	}

	/**
	 * 初始化fragment
	 */
	public void showFragment() {
		Bundle data = new Bundle();
		data.putInt("newOrHotFlag", Joke.SORT_NEW);
		MeiTuFragment meiTuNewFragment = new MeiTuFragment();
		meiTuNewFragment.setArguments(data);
		mFragments.add(meiTuNewFragment);
		
		Bundle data1 = new Bundle();
		data1.putInt("newOrHotFlag", Joke.SORT_HOT);
		MeiTuFragment meiTuHotFragment = new MeiTuFragment();
		meiTuHotFragment.setArguments(data1);
		mFragments.add(meiTuHotFragment);
		
		mAdapetr = new QQYFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
		mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mAdapetr);
        
        mCategoryTabStrip.setViewPager(mViewPager);
        mCategoryTabStrip.setCatalogs(mCategoryList);
	}
	
	@OnClick(R.id.refresh)
	void refresh(View view) {
		if(mRefreshAnimation == null) {
			mRefreshAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.progress_anim);
		}
		MeiTuFragment meiTuFragment = (MeiTuFragment)mFragments.get(mViewPager.getCurrentItem());
		if(meiTuFragment.isRefresh()) {
			return;
		}
		view.startAnimation(mRefreshAnimation);
		meiTuFragment.refresh();
	}
	@OnClick(R.id.nav_view)
	void nav(View view) {
		DrawerLayout drawer = (DrawerLayout)MeiTuActivity.this.getParent().findViewById(R.id.drawer_layout);
		drawer.openDrawer(GravityCompat.START);
	}
	/**
	 * 刷新完毕，结束刷新动画
	 */
	public void showRefreshCompete() {
		Log.e("refreshCompete", "refreshCompete");
		mRefreshIV.clearAnimation();
		if(mRefreshAnimation != null) {
			mRefreshAnimation.cancel();
		}
	}

	@Override
	protected void onDestroy() {
		mPresenter.unsubscribe();
		super.onDestroy();
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
//		super.onSaveInstanceState(outState);
	}
}
