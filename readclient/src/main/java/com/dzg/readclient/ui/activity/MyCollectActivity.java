package com.dzg.readclient.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.adapters.JokeAdapter;
import com.dzg.readclient.injector.component.ApplicationComponent;
import com.dzg.readclient.injector.component.DaggerMyCollectComponent;
import com.dzg.readclient.injector.component.MyCollectComponent;
import com.dzg.readclient.injector.module.ActivityModule;
import com.dzg.readclient.injector.module.MyCollectModule;
import com.dzg.readclient.mvp.contract.MyCollectContract;
import com.dzg.readclient.mvp.model.Collect;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.utils.FastjsonUtil;
import com.dzg.readclient.utils.Util;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ClassName: MyCollectActivity
 * @Description: 我的收藏
 */
public class MyCollectActivity extends BaseActivity implements MyCollectContract.View{
@Inject
	MyCollectContract.Presenter mPresenter;
	@BindView(R.id.listview)
	ListView mListView;
	@BindView(R.id.no_data_layout)
	View mNoDataLayout;
	
	private JokeAdapter mJokeAdapter;
	private List<Collect> mCollects;
	private int mUserId = -1;
	private List<Joke> mJokes = new ArrayList<Joke>();
	
	@Override
	public  void onCreateView(Bundle savedInstanceState) {
		if(ReadClientApp.getInstance().currentUser != null) {
    		mUserId = ReadClientApp.getInstance().currentUser.getId();
    	}
		mPresenter.attachView(this);
		mPresenter.init();
	}
	@Override
	public void injectDependences(){
		ApplicationComponent appComponent=ReadClientApp.getInstance().getApplicationComponent();
		MyCollectComponent collectComponent= DaggerMyCollectComponent.builder()
				.applicationComponent(appComponent)
				.activityModule(new ActivityModule(this))
				.myCollectModule(new MyCollectModule())
				.build();
		collectComponent.inject(this);
	}
	@Override
	public int getLayoutId() {
		return R.layout.activity_mycollect;
	}

	public void showCollects() {
		mCollects = mPresenter.getCollects(mUserId);
		if(Util.isEmpty(mCollects)) { //暂无收藏，显示无数据布局
			mListView.setVisibility(View.GONE);
			mNoDataLayout.setVisibility(View.VISIBLE);
			return;
		} 
		for (Collect collect : mCollects) {
			Joke joke = FastjsonUtil.deserialize(collect.getJokeContent(), Joke.class);
			mJokes.add(joke);
		}
		mJokeAdapter = new JokeAdapter(this);
		mJokeAdapter.setList(mJokes);
		mListView.setAdapter(mJokeAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Joke joke = mJokes.get(position);
				if(joke.getType() == Joke.TYPE_QUSHI) {
					Intent intent = new Intent(MyCollectActivity.this, QuShiDetailActivity.class);
					intent.putExtra("qushi", joke);
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				} else if(joke.getType() == Joke.TYPE_QUTU || joke.getType() == Joke.TYPE_MEITU) {
					Intent intent = new Intent(MyCollectActivity.this, TuDetailActivity.class);
					intent.putExtra("content", joke);
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				}
			}
		});
	}
	
	/**
	 * 取消搜藏
	 * @param joke
	 */
	@Override
	public void cancelCollect(Joke joke) {
		mJokes.remove(joke);
		mJokeAdapter.notifyDataSetChanged();
		if(Util.isEmpty(mJokes)) { //显示无数据布局
			mListView.setVisibility(View.GONE);
			mNoDataLayout.setVisibility(View.VISIBLE);
		} 
	}
	
	@OnClick(R.id.back)
	void back(View view) {
		finishWithAnimation();
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
	protected void onDestroy() {
		mPresenter.unsubscribe();
		super.onDestroy();
	}
}
