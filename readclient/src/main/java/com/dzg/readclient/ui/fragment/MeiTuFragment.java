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
import com.dzg.readclient.injector.component.DaggerMeiTuComponent;
import com.dzg.readclient.injector.component.MeiTuComponent;
import com.dzg.readclient.injector.module.ActivityModule;
import com.dzg.readclient.injector.module.MeiTuModule;
import com.dzg.readclient.mvp.contract.MeiTuContract;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
import com.dzg.readclient.ui.activity.MeiTuActivity;
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
public class MeiTuFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener ,MeiTuContract.View{
	@Inject
	MeiTuContract.Presenter mPresenter;
	private final static String TAG = "QuTuFragment";
	private SwipeRefreshLayout mSwipeLayout;
	private LoadListView mListView;
	private MeiTuActivity mMeiTuActivity;
	private ArrayList<Joke> mJokeLists = new ArrayList<Joke>();
	private QuTuAdapter mMeiTuAdapter;
	private int mPageId;
	private int mCount;
	private boolean mHasNext;
	
	private int mNewOrHotFlag = Joke.SORT_NEW;
	private String mKey = Constants.CACHE_MEITU_NEW;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		injectDependences();
		Bundle args = getArguments();
		mNewOrHotFlag = args != null ? args.getInt("newOrHotFlag") : Joke.SORT_NEW;
		if(mNewOrHotFlag == Joke.SORT_HOT) {
			mKey = Constants.CACHE_MEITU_HOT;
		}
		mPageId = 1;
		mCount = 10;
		mHasNext = true;
		mMeiTuAdapter = new QuTuAdapter(mMeiTuActivity);
		mPresenter.attachView(this);
	}

	@Override
	public void onResume() {
		if(mMeiTuAdapter != null) {
			mMeiTuAdapter.notifyDataSetChanged();
		}
		super.onResume();
	}
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onAttach(Activity activity) {
		this.mMeiTuActivity = (MeiTuActivity) activity;
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
		
		mListView.setAdapter(mMeiTuAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(mMeiTuActivity, TuDetailActivity.class);
				intent.putExtra("content", mJokeLists.get(position));
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				ReadClientApp.getInstance().isStartOtherActivity = true;
				
			}
		});
		mListView.setOnFootLoadingListener(new LoadListView.OnFootLoadingListener() {
			@Override
			public void onFootLoading() {
				mPresenter.loadMeiTu();
			}
		});
		mPresenter.clearListsAndInitPageInfo();
		mPresenter.loadData();
		subscriptionEvent();
		return view;
	}
	private void injectDependences() {
		ApplicationComponent applicationComponent = ((ReadClientApp) getActivity().getApplication()).getApplicationComponent();
		MeiTuComponent meiTuComponent = DaggerMeiTuComponent.builder()
				.applicationComponent(applicationComponent)
				.activityModule(new ActivityModule(getActivity()))
				.meiTuModule(new MeiTuModule())
				.build();
		meiTuComponent.inject(this);
	}
	@Override
	public void showFromCache() {
		HashMap<String, Object> map = (HashMap<String, Object>) ReadClientApp.getInstance().getSpUtil().getObject(mKey, null);
		if (map == null || map.get("data") == null) {
			//无网络无缓存 ,显示无数据布局
			ToastUtils.showMessageInCenter(mMeiTuActivity, "暂无数据");
			return;
		}
		mJokeLists = (ArrayList<Joke>) map.get("data");
		mHasNext = (Boolean) map.get("hasNext");
		mPageId = (Integer) map.get("pageId");
		mMeiTuAdapter.setList(mJokeLists);
		mMeiTuAdapter.notifyDataSetChanged();
		mListView.setHasMoreData(mHasNext);
	}

	@Override
	public void showMeiTu() {
		if (!HttpUtil.isNetworkAvailable(mMeiTuActivity)) {
			ToastUtils.showMessage(mMeiTuActivity, R.string.no_net);
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
			mMeiTuAdapter.onDataChange(mJokeLists);
			mListView.setVisibility(View.VISIBLE);
		}
		mPresenter.saveCache(mJokeLists,mHasNext,mPageId,mKey);
	}

	@Override
	public void showComplete() {
		mListView.onFootLoadingComplete(true);
		mSwipeLayout.setRefreshing(false);
		mMeiTuActivity.mPresenter.refreshCompete();
	}
    @Override
	public  void clearListsAndInitPageInfo() {
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
								PageResult pageResult = (PageResult)message.getObject();
								mPresenter.loadComplete();
								mPresenter.loadSuccess(pageResult);
								break;
							case Constants.SUCCESS_1:  //刷新成功
								PageResult pageResult1 = (PageResult)message.getObject();
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
	 * 刷新美图
	 */
	@Override
	public void onRefresh() {
		//没有网络
		if(!HttpUtil.isNetworkAvailable(mMeiTuActivity)) {
			ToastUtils.showMessage(mMeiTuActivity, R.string.no_net);
			mSwipeLayout.setRefreshing(false);
			return;
		}
		mPresenter.clearListsAndInitPageInfo();
		mPresenter.reflush(mNewOrHotFlag, mCount);
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
		if(!HttpUtil.isNetworkAvailable(mMeiTuActivity)) {
			ToastUtils.showMessage(mMeiTuActivity, R.string.no_net);
			mMeiTuActivity.mPresenter.refreshCompete();
			return;
		}
		if(mSwipeLayout.isRefreshing()) {
			return;
		}
		mSwipeLayout.setRefreshing(true);
		mPresenter.clearListsAndInitPageInfo();
		mListView.setSelection(0);
		mPresenter.reflush(  mNewOrHotFlag, mCount);
	}


}
