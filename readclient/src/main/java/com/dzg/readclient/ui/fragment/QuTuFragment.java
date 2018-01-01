package com.dzg.readclient.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.adapters.QuTuAdapter;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.PageResult;
import com.dzg.readclient.injector.component.ApplicationComponent;
import com.dzg.readclient.injector.component.DaggerQuTuComponent;
import com.dzg.readclient.injector.component.QuTuComponent;
import com.dzg.readclient.injector.module.ActivityModule;
import com.dzg.readclient.injector.module.QuTuModule;
import com.dzg.readclient.mvp.contract.QuTuContract;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
import com.dzg.readclient.ui.activity.QuTuActivity;
import com.dzg.readclient.ui.activity.TuDetailActivity;
import com.dzg.readclient.ui.view.LoadListView;
import com.dzg.readclient.utils.FastjsonUtil;
import com.dzg.readclient.utils.HttpUtil;
import com.dzg.readclient.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

/**
 * @ClassName: QuTuFragment
 * @Description: 趣图Fragment
 */
public class QuTuFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,QuTuContract.View {
	@Inject
	QuTuContract.Presenter mPresenter;
	private final static String TAG = "QuTuFragment";
	
	private SwipeRefreshLayout mSwipeLayout;
	private LoadListView mListView;
	private QuTuActivity mQuTuActivity;
	private ArrayList<Joke> mJokeLists = new ArrayList<Joke>();
	private QuTuAdapter mQuTuAdapter;
	private int mPageId;
	private int mCount;
	private boolean mHasNext;
	
	private int mNewOrHotFlag = Joke.SORT_NEW;
	private String mKey = Constants.CACHE_QUTU_NEW;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		injectDependences();
		Bundle args = getArguments();
		mNewOrHotFlag = args != null ? args.getInt("newOrHotFlag") : Joke.SORT_NEW;
		if(mNewOrHotFlag == Joke.SORT_HOT) {
			mKey = Constants.CACHE_QUTU_HOT;
		}
		mPageId = 1;
		mCount = 10;
		mHasNext = true;
		mQuTuAdapter = new QuTuAdapter(mQuTuActivity);
		mPresenter.attachView(this);
	}

	@Override
	public void onResume() {
		Log.e(TAG, "onResume");
		if(mQuTuAdapter != null) {
			mQuTuAdapter.setLoadOnNotWifi(ReadClientApp.getInstance().getInstance().getSpUtil().getBoolean(Constants.IS_LOAD_IMG, true));
			mQuTuAdapter.notifyDataSetChanged();
		}
		super.onResume();
	}
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onAttach(Activity activity) {
		this.mQuTuActivity = (QuTuActivity) activity;
		super.onAttach(activity);
	}
	@Override
	public void onDestroy() {
		mPresenter.unsubscribe();
		RxBus.getInstance().unSubscribe(this);
		super.onDestroy();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_qushi, null);
		mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
		mListView = (LoadListView) view.findViewById(R.id.listview);
		// 顶部刷新的样式
		mSwipeLayout.setColorSchemeResources(R.color.holo_red_light,
			R.color.holo_green_light, R.color.holo_blue_bright,
			R.color.holo_orange_light);
		mSwipeLayout.setOnRefreshListener(this);
		
		mListView.setAdapter(mQuTuAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(mQuTuActivity, TuDetailActivity.class);
				intent.putExtra("content", mJokeLists.get(position));
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				ReadClientApp.getInstance().isStartOtherActivity = true;
			}
		});
		mListView.setOnFootLoadingListener(new LoadListView.OnFootLoadingListener() {
			@Override
			public void onFootLoading() {
				mPresenter.loadQuTu();
			}
		});
		mPresenter.clearListsAndInitPageInfo();
		mPresenter.loadData();
		subscriptionEvent();
		return view;
	}
	private void injectDependences() {
		ApplicationComponent applicationComponent = ((ReadClientApp) getActivity().getApplication()).getApplicationComponent();
		QuTuComponent quTuComponent = DaggerQuTuComponent.builder()
				.applicationComponent(applicationComponent)
				.activityModule(new ActivityModule(getActivity()))
				.quTuModule(new QuTuModule())
				.build();
		quTuComponent.inject(this);
	}
	@Override
	public void showFromCache() {
    mPresenter.saveCache(mJokeLists,mHasNext,mPageId,mKey);
	}

	@Override
	public void showQuTu() {
		if (!HttpUtil.isNetworkAvailable(mQuTuActivity)) {
			ToastUtils.showMessage(mQuTuActivity, R.string.no_net);
			return;
		}
		if (!mHasNext) {
			mListView.onFootLoadingComplete(true);
			return;
		}
		if (mPageId != 1) {
			mListView.setMoreDataMsg(getString(R.string.more_jingxuan));
		}
		mPresenter.getAll(mNewOrHotFlag,mPageId,mCount);
	}

	@Override
	public void showSuccess(PageResult pageResult) {
		if (pageResult == null || pageResult.isEmpty()) {
			Log.e(TAG, "load data success, but no data!");
			return;
		}

		mHasNext = pageResult.getHasNext();
		mListView.setHasMoreData(mHasNext);  //设置是否还有更多数据
		if (pageResult.getHasNext()) {
			mPageId = pageResult.getNext();
		}
		String tmp = FastjsonUtil.serialize(pageResult.getList());
		List<Joke> list = FastjsonUtil.deserializeList(tmp,
				Joke.class);
		mJokeLists.addAll(list);

		if (mJokeLists.size() <= 0) {   //无数据，显示无数据布局
		} else {
			mQuTuAdapter.onDataChange(mJokeLists);
			mListView.setVisibility(View.VISIBLE);
		}
		mPresenter.saveCache(mJokeLists,mHasNext,mPageId,mKey);
	}

	@Override
	public void showComplete() {
		mListView.onFootLoadingComplete(true);
		mSwipeLayout.setRefreshing(false);
		mQuTuActivity.mPresenter.refreshCompete();
	}
    @Override
	public void clearListsAndInitPageInfo() {
		mPageId = 1;
		mHasNext = true;   //重新加载保证从第一页加载
	}

	private void subscriptionEvent() {
		Subscription subscription = RxBus.getInstance().toObservable(Constants.AllFRAGMENT, RxBusMessage.class)
				.subscribe(new Action1<RxBusMessage>() {
					@Override
					public void call(RxBusMessage message) {
						switch (message.getCode()) {
							case Constants.SUCCESS: // 获取数据成功
								PageResult pageResult = (PageResult) message.getObject();
								mPresenter.loadComplete();
								mPresenter.loadSuccess(pageResult);
								break;
							case Constants.SUCCESS_1:  //刷新成功
								PageResult pageResult1 = (PageResult) message.getObject();
								mPresenter.loadComplete();
								mJokeLists.clear();
								mPresenter.loadSuccess(pageResult1);
								break;
							case Constants.FAILURE:
								mPresenter.loadComplete();
								break;
						}
					}
				});
		RxBus.getInstance().addSubscription(this,subscription);
	}

	/**
	 * 刷新趣图
	 */
	@Override
	public void onRefresh() {
		//没有网络
		if(!HttpUtil.isNetworkAvailable(mQuTuActivity)) {
			ToastUtils.showMessage(mQuTuActivity, R.string.no_net);
			mSwipeLayout.setRefreshing(false);
			return;
		}
		mPresenter.clearListsAndInitPageInfo();
		mPresenter.reflush(mNewOrHotFlag,mCount);
	}
	
	public boolean isRefresh() {
		if(mSwipeLayout == null) {
			mSwipeLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh_layout);
		}
		return mSwipeLayout.isRefreshing();
	}
	
	/**
	 * 刷新
	 */
	public void refresh() {
		//没有网络
		if(!HttpUtil.isNetworkAvailable(mQuTuActivity)) {
			ToastUtils.showMessage(mQuTuActivity, R.string.no_net);
			mQuTuActivity.mPresenter.refreshCompete();
			return;
		}
		if(mSwipeLayout.isRefreshing()) {
			return;
		}
		mSwipeLayout.setRefreshing(true);
		mPresenter.clearListsAndInitPageInfo();
		mListView.setSelection(0);
		mPresenter.reflush(mNewOrHotFlag,mCount);
	}
	
	/**
	 * 对内容进行缓存
	 */
	private void saveCache() {
		HashMap<String, Object> cache = new HashMap<String, Object>();
		cache.put("data", mJokeLists);
		cache.put("hasNext", mHasNext);
		cache.put("pageId", mPageId);
		ReadClientApp.getInstance().getInstance().getSpUtil().putObject(mKey, cache);
	}

}
