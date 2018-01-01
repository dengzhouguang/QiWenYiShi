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
import com.dzg.readclient.adapters.QuShiAdapter;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.PageResult;
import com.dzg.readclient.injector.component.ApplicationComponent;
import com.dzg.readclient.injector.component.DaggerQuShiComponent;
import com.dzg.readclient.injector.component.QuShiComponent;
import com.dzg.readclient.injector.module.ActivityModule;
import com.dzg.readclient.injector.module.QuShiModule;
import com.dzg.readclient.mvp.contract.QuShiContract;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
import com.dzg.readclient.service.IJokeService;
import com.dzg.readclient.service.impl.JokeServiceImpl;
import com.dzg.readclient.ui.activity.QuShiActivity;
import com.dzg.readclient.ui.activity.QuShiDetailActivity;
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
 * @ClassName: QuShiFragment
 * @Description: 趣事Fragment
 */
public class QuShiFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,QuShiContract.View {
	@Inject
	public QuShiContract.Presenter mPresenter;
	private final static String TAG = "QuShiFragment";
	private SwipeRefreshLayout mSwipeLayout;
	private LoadListView mListView;
	private QuShiActivity mQuShiActivity;
	private ArrayList<Joke> mJokeLists = new ArrayList<Joke>();
	private IJokeService mQuShiService;
	private QuShiAdapter mQuShiAdapter;
	private int mPageId;
	private int mCount;
	private boolean mHasNext;
	private int mNewOrHotFlag = Joke.SORT_NEW;
	private String mKey = Constants.CACHE_QUSHI_NEW;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		injectDependences();
		Bundle args = getArguments();
		mNewOrHotFlag = args != null ? args.getInt("newOrHotFlag") : Joke.SORT_NEW;
		if(mNewOrHotFlag == Joke.SORT_HOT) {
			mKey = Constants.CACHE_QUSHI_HOT;
		}
		mPageId = 1;
		mCount = 10;
		mHasNext = true;
		mQuShiService = new JokeServiceImpl(mQuShiActivity);
		mQuShiAdapter = new QuShiAdapter(mQuShiActivity);
		mPresenter.attachView(this);
	}

	@Override
	public void onResume() {
		Log.e(TAG, "onResume");
		if(mQuShiAdapter != null) {
			mQuShiAdapter.notifyDataSetChanged();
		}
		super.onResume();
	}
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onAttach(Activity activity) {
		this.mQuShiActivity = (QuShiActivity) activity;
		super.onAttach(activity);
	}
	@Override
	public void onDestroy() {
		mQuShiService.removeSubscription();
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
		
		mListView.setAdapter(mQuShiAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(mQuShiActivity, QuShiDetailActivity.class);
				intent.putExtra("qushi", mJokeLists.get(position));
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				ReadClientApp.getInstance().isStartOtherActivity = true;
				
			}
		});
		mListView.setOnFootLoadingListener(new LoadListView.OnFootLoadingListener() {
			@Override
			public void onFootLoading() {
				mPresenter.loadQuShi();
			}
		});
		clearListsAndInitPageInfo();
		mPresenter.loadData();
		subscriptionEvent();
		return view;
	}


	@Override
	public void showFromCache() {
		HashMap<String, Object> map = (HashMap<String, Object>) ReadClientApp.getInstance().getSpUtil().getObject(mKey, null);
		if (map == null || map.get("data") == null) {
			//无网络无缓存 ,显示无数据布局
			ToastUtils.showMessageInCenter(mQuShiActivity, "暂无数据");
			return;
		}
		mJokeLists = (ArrayList<Joke>) map.get("data");
		mHasNext = (Boolean) map.get("hasNext");
		mPageId = (Integer) map.get("pageId");
		mQuShiAdapter.setList(mJokeLists);
		mQuShiAdapter.notifyDataSetChanged();
		mListView.setHasMoreData(mHasNext);
	}
	private void injectDependences() {
		ApplicationComponent applicationComponent = ((ReadClientApp) getActivity().getApplication()).getApplicationComponent();
		QuShiComponent quShiComponent = DaggerQuShiComponent.builder()
				.applicationComponent(applicationComponent)
				.activityModule(new ActivityModule(getActivity()))
				.quShiModule(new QuShiModule())
				.build();
		quShiComponent.inject(this);
	}
	@Override
	public void showQuShi() {
		if (!HttpUtil.isNetworkAvailable(mQuShiActivity)) {
			ToastUtils.showMessage(mQuShiActivity, R.string.no_net);
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
			mQuShiAdapter.onDataChange(mJokeLists);
			mListView.setVisibility(View.VISIBLE);
		}
		mPresenter.saveCache(mJokeLists,mHasNext,mPageId,mKey);
	}

	@Override
	public void showComplete() {
		mListView.onFootLoadingComplete(true);
		mSwipeLayout.setRefreshing(false);
		mQuShiActivity.mPresenter.refreshCompete();
	}

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
	 * 刷新趣事
	 */
	@Override
	public void onRefresh() {
		//没有网络
		if(!HttpUtil.isNetworkAvailable(mQuShiActivity)) {
			ToastUtils.showMessage(mQuShiActivity, R.string.no_net);
			mSwipeLayout.setRefreshing(false);
			return;
		}
		clearListsAndInitPageInfo();
		mQuShiService.reflush( Joke.TYPE_QUSHI, mNewOrHotFlag, mCount);
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
		if(!HttpUtil.isNetworkAvailable(mQuShiActivity)) {
			ToastUtils.showMessage(mQuShiActivity, R.string.no_net);
			mQuShiActivity.mPresenter.refreshCompete();
			return;
		}
		if(mSwipeLayout.isRefreshing()) {
			return;
		}
		mSwipeLayout.setRefreshing(true);
		clearListsAndInitPageInfo();
		mListView.setSelection(0);
		mQuShiService.reflush( Joke.TYPE_QUSHI, mNewOrHotFlag, mCount);
	}

}
