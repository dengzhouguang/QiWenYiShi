package com.dzg.readclient.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.adapters.CommentAdapter;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.PageResult;
import com.dzg.readclient.injector.component.ApplicationComponent;
import com.dzg.readclient.injector.component.DaggerQuShiDetailActivityComponent;
import com.dzg.readclient.injector.component.QuShiDetailActivityComponent;
import com.dzg.readclient.injector.module.ActivityModule;
import com.dzg.readclient.injector.module.QuShiDetailActivityModule;
import com.dzg.readclient.mvp.contract.QuShiDetailContract;
import com.dzg.readclient.mvp.model.Comment;
import com.dzg.readclient.mvp.model.DingOrCai;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.mvp.model.User;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
import com.dzg.readclient.ui.view.LoadListView;
import com.dzg.readclient.ui.view.Scroll2BottomListenerScrollView;
import com.dzg.readclient.ui.view.SharePopWindow;
import com.dzg.readclient.utils.FastjsonUtil;
import com.dzg.readclient.utils.ProgressDialogUtils;
import com.dzg.readclient.utils.TaskExecutor;
import com.dzg.readclient.utils.ToastUtils;
import com.dzg.readclient.utils.ToolsUtils;
import com.dzg.readclient.utils.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * @ClassName: QuShiDetailActivity
 * @Description: 趣事详情界面
 * @author lling
 * @date 2015-6-22
 */
public class QuShiDetailActivity extends BaseActivity implements QuShiDetailContract.View{

	@Inject
	QuShiDetailContract.Presenter mPreshenter;
	private final String TAG = "QuShiDetailActivity";
	
	@BindView(R.id.user_nick)
	TextView mUserNickTV;
	@BindView(R.id.create_date)
	TextView mCreateTimeTV;
	@BindView(R.id.qushi_content)
	TextView mContentTV;
	@BindView(R.id.ding_tv)
	TextView mDingTV;
	@BindView(R.id.cai_tv)
	TextView mCaiTV;
	@BindView(R.id.comment_tv)
	TextView mCommentTV;
	@BindView(R.id.share_tv)
	TextView mShareTV;
	@BindView(R.id.user_img)
	ImageView mUserPortraitIV;
	@BindView(R.id.listview)
	LoadListView mListView;
	@BindView(R.id.content_et)
	EditText mCommentContentET;
	@BindView(R.id.scrollview)
	Scroll2BottomListenerScrollView mScrollView;
	@BindView(R.id.qushi_list)
	LinearLayout mQushiLayout;
	@BindView(R.id.progress)
	ProgressBar mLoadProgressBar;
	
	private Joke mJoke;
	private CommentAdapter mCommentAdapter;
	private List<Comment> mCommentLists = new ArrayList<Comment>();
	private int mPageId = 1;
	private int mCount = 5;
	private boolean mHasNext = true;
	private int mUserId = -1;
	private boolean isCommnet = false;  //是否点击评论进来的
	private SharePopWindow mSharePopWindow;
	private int mJokeId = 1;
	private boolean mIsOpenFromPush = false;
	
	@Override
	public  void onCreateView(Bundle savedInstanceState) {
		if(ReadClientApp.getInstance().currentUser != null) {
			
    		mUserId = ReadClientApp.getInstance().currentUser.getId();
    	}
    	mPreshenter.attachView(this);
		mSharePopWindow = new SharePopWindow(this);
		mListView.setHaveScrollbar(false);
		mCommentAdapter = new CommentAdapter(this);
		mListView.setAdapter(mCommentAdapter);
		mListView.setOnFootLoadingListener(new LoadListView.OnFootLoadingListener() {
			@Override
			public void onFootLoading() {
				mPreshenter.loadComments();
			}
		});
		mPreshenter.initQuShiContent();
		subscriptionEvent();
	}

	@Override
	public void injectDependences() {
		ApplicationComponent appComponent=ReadClientApp.getInstance().getApplicationComponent();
		QuShiDetailActivityComponent component= DaggerQuShiDetailActivityComponent.builder()
				.applicationComponent(appComponent)
				.activityModule(new ActivityModule(this))
				.quShiDetailActivityModule(new QuShiDetailActivityModule())
				.build();
		component.inject(this);
	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_qushi_detail;
	}

	/**
	 * 如果此界面已经打开，再点通知界面打开，则调用此方法
	 */
	protected void onNewIntent(Intent intent) {
		if(Constants.DEBUG) Log.d(TAG, "onNewIntent");
		setIntent(intent);
		mPreshenter.reSet();   //重置
		mPreshenter.initQuShiContent();  //加载
	}
	@Override
	public  void showAndReSet() {
		mPageId = 1;
		mHasNext = true;
		mCommentLists.clear();
		if(mCommentAdapter != null) {
			mCommentAdapter.notifyDataSetChanged();
		}}
	//初始化笑话内容
	@Override
	public void showQuShiContent() {
		mJoke = (Joke)getIntent().getSerializableExtra("qushi");
		if(mJoke == null) {  //从push进来的
			try {
				mJokeId = Integer.valueOf(getIntent().getStringExtra("id"));
				mIsOpenFromPush = true;
				ReadClientApp.getInstance().currentUser = (User)spUtil.getObject("user", null);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			mPreshenter.getJokeById(mJokeId);
		} else {   //从界面进来的
			isCommnet = getIntent().getBooleanExtra("isComment", false);
			mJokeId = mJoke.getId();
			mPreshenter.setQuShiContent();
		}
	}
	@Override
	public  void showSetQushiContent() {
		mPreshenter.loadComments();
		mLoadProgressBar.setVisibility(View.GONE);
		mScrollView.setVisibility(View.VISIBLE);
		Glide.with(QuShiDetailActivity.this).load(mJoke.getPortraitUrl()).into(mUserPortraitIV);
		mUserNickTV.setText(mJoke.getUserNike());
		mCreateTimeTV.setText(Util.getFormatDate(mJoke.getCreateDate()));
		mContentTV.setText(mJoke.getContent());
        mDingTV.setText(String.valueOf(mJoke.getSupportsNum()));
        mCaiTV.setText(String.valueOf(mJoke.getOpposesNum()));
        mCommentTV.setText(String.valueOf(mJoke.getCommentNum()));
        
        final DingOrCai dingOrCai = mPreshenter.getDingOrCai(mUserId, mJokeId);
        if(dingOrCai != null) {
        	if(dingOrCai.isDing()) {
        		mDingTV.setSelected(true);
        		mCaiTV.setSelected(false);
        		if(mJoke.getSupportsNum() < dingOrCai.getNum()) {
        			mJoke.setSupportsNum(dingOrCai.getNum());
        		}
        	} else {
        		mDingTV.setSelected(false);
        		mCaiTV.setSelected(true);
        		if(mJoke.getOpposesNum() < dingOrCai.getNum()) {
        			mJoke.setOpposesNum(dingOrCai.getNum());
        		}
        	}
        }
        
        mDingTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(dingOrCai != null) {
					if(dingOrCai.isDing()) {
						ToastUtils.showMessage(QuShiDetailActivity.this, R.string.has_ding);
					} else {
						ToastUtils.showMessage(QuShiDetailActivity.this, R.string.has_cai);
					}
					return;
				}
				if(v.isSelected()) {
					ToastUtils.showMessage(QuShiDetailActivity.this, R.string.has_ding);
					return;
				}
				View caiView = ((LinearLayout)v.getParent().getParent()).findViewById(R.id.cai_tv);
				if(caiView.isSelected()) {
					ToastUtils.showMessage(QuShiDetailActivity.this, R.string.has_cai);
					return;
				}
				final View view = ((RelativeLayout)v.getParent()).getChildAt(1);
				Animation addOneAnimation = AnimationUtils.loadAnimation(
						QuShiDetailActivity.this, R.anim.add_one);
				addOneAnimation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						view.setVisibility(View.VISIBLE);
					}
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					@Override
					public void onAnimationEnd(Animation animation) {
						view.setVisibility(View.GONE);
					}
				});
				view.startAnimation(addOneAnimation);
				v.setSelected(true);
				mJoke.setSupportsNum(mJoke.getSupportsNum()+1);
				((TextView)v).setText(String.valueOf(mJoke.getSupportsNum()));
				mPreshenter.dingOrCai(mUserId, mJokeId, DingOrCai.DING, mJoke.getSupportsNum());
			}
		});
        
        mCaiTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(dingOrCai != null) {
					if(dingOrCai.isDing()) {
						ToastUtils.showMessage(QuShiDetailActivity.this, R.string.has_ding);
					} else {
						ToastUtils.showMessage(QuShiDetailActivity.this, R.string.has_cai);
					}
					return;
				}
				if(v.isSelected()) {
					ToastUtils.showMessage(QuShiDetailActivity.this, R.string.has_cai);
					return;
				}
				View dingView = ((LinearLayout)v.getParent().getParent()).findViewById(R.id.ding_tv);
				if(dingView.isSelected()) {
					ToastUtils.showMessage(QuShiDetailActivity.this, R.string.has_ding);
					return;
				}
				final View view = ((RelativeLayout)v.getParent()).getChildAt(1);
				Animation addOneAnimation = AnimationUtils.loadAnimation(
						QuShiDetailActivity.this, R.anim.add_one);
				addOneAnimation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						view.setVisibility(View.VISIBLE);
					}
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					@Override
					public void onAnimationEnd(Animation animation) {
						view.setVisibility(View.GONE);
					}
				});
				view.startAnimation(addOneAnimation);
				v.setSelected(true);
				mJoke.setOpposesNum(mJoke.getOpposesNum()+1);
				((TextView)v).setText(String.valueOf(mJoke.getOpposesNum()));
				mPreshenter.dingOrCai(mUserId, mJokeId, DingOrCai.CAI, mJoke.getOpposesNum());
			}
		});
        mShareTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSharePopWindow.setmJoke(mJoke);
				mSharePopWindow.showAtLocation(v.getRootView(), Gravity.LEFT | Gravity.BOTTOM, 0, 0);
			}
		});
        mCommentTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCommentContentET.requestFocus();
				ToolsUtils.openKeybord(mCommentContentET, QuShiDetailActivity.this);
			}
		});
	}
	
	/**
	 * 加载趣事评论
	 */
	public  void showComments() {
		if(!mHasNext){
			mListView.onFootLoadingComplete(true);
			return;
		}
		mPreshenter.getCommentByJokeId( mJokeId, mPageId, mCount);
	}
	
	/**
	 * 加载数据成功，更新界面
	 * @param pageResult
	 */
	@Override
	public  void showLoadSuccess(PageResult pageResult) {
		mScrollView.setmListView(mListView);
		if(pageResult == null || pageResult.isEmpty()) {
			if(Constants.DEBUG) Log.w(TAG, "load qushi success, but no data!");
			mHasNext = false;
			return;
		}
		
		String tmp = FastjsonUtil.serialize(pageResult.getList());
		List<Comment> list = FastjsonUtil.deserializeList(tmp,
				Comment.class);
		mCommentLists.addAll(list);
		
		if(mCommentLists.size() <= 0) {   //无数据，显示无数据布局
		} else {
			mCommentAdapter.onDataChange(mCommentLists);
			mListView.setVisibility(View.VISIBLE);
		}
		if(isCommnet && mPageId == 1) {   //如果点击评论进来的，则滚动到评论处
			TaskExecutor.scheduleTaskOnUiThread(150, new Runnable() {
				@Override
				public void run() {
					int height = mQushiLayout.getHeight();
					mScrollView.scrollTo(0, height);
				}
			});
		}
		mHasNext = pageResult.getHasNext();
		mListView.setHasMoreData(mHasNext);  //设置是否还有更多数据
		if (pageResult.getHasNext()) {
			mPageId = pageResult.getNext();
		}
	}
	
	@OnClick(R.id.add_comment_btn)
	void sendComment(View view) {
		ToolsUtils.hideKeyboard(mCommentTV);
		if(ReadClientApp.getInstance().currentUser == null) {  //用户未登录
			Intent intent = new Intent(this, LoginActivity.class);
			startActivityWithAnimation(intent);
			return;
		}
		String content = mCommentContentET.getText().toString().trim();
		if(Util.isEmpty(content)) {
			ToastUtils.showMessage(getApplicationContext(), 
					R.string.comment_content_null);
			return;
		}
		content = ToolsUtils.Html2Text(content);
		if(Util.isEmpty(content)) {
			ToastUtils.showMessage(getApplicationContext(), 
					R.string.input_invalide);
			return;
		}
		if(mJoke == null) {
			return;
		}
		ProgressDialogUtils.showProgressDialog(this, getString(R.string.commenting));
		mPreshenter.addComment( mJokeId, content);
	}
	
	@OnClick(R.id.back)
	void back(View view) {
		if(mIsOpenFromPush && !ReadClientApp.getInstance().isStart) {
			Intent intent = new Intent(this, SplashActivity.class);
			startActivityWithAnimation(intent);
		}
		finishWithAnimation();
	}
	
	@Override
	public void onBackPressed() {
		if(mIsOpenFromPush && !ReadClientApp.getInstance().isStart) {
			Intent intent = new Intent(this, SplashActivity.class);
			startActivityWithAnimation(intent);
		}
		super.onBackPressed();
	}

	private void subscriptionEvent() {
		Subscription subscription = RxBus.getInstance().toObservable(Constants.COMMENT, RxBusMessage.class)
				.subscribe(new Action1<RxBusMessage>() {
					@Override
					public void call(RxBusMessage message) {
						switch (message.getCode()) {
							case Constants.SUCCESS: // 获取评论数据成功
								PageResult pageResult = (PageResult)message.getObject();
								mPreshenter.loadSuccess(pageResult);
								mListView.onFootLoadingComplete(true);
								break;
							case Constants.FAILURE:
								mListView.onFootLoadingComplete(true);
								break;
							case Constants.SUCCESS_1: // 发表评论成功
								ProgressDialogUtils.dismiss();
								mPreshenter.commentSuccess();
								break;
							case Constants.FAILURE_1:
								ProgressDialogUtils.dismiss();
								break;
							case Constants.SUCCESS_2:
								mJoke = (Joke) message.getObject();
								if(mJoke != null) {
									mPreshenter.setQuShiContent();
								}
								break;
							case Constants.FAILURE_2:
								mLoadProgressBar.setVisibility(View.GONE);
								break;
						}
					}
				});
		RxBus.getInstance().addSubscription(this,subscription);
	}
	/**
	 * 评论成功
	 */
	public void showCommentSuccess() {
		Comment comment = new Comment();
		comment.setContent(mCommentContentET.getText().toString().trim());
		comment.setUserId(ReadClientApp.getInstance().currentUser.getId());
		comment.setPortraitUrl(ReadClientApp.getInstance().currentUser.getPortraitUrl());
		comment.setUserNike(ReadClientApp.getInstance().currentUser.getUserNike());
		comment.setCreateDate(new Date());
		mCommentLists.add(0, comment);
		mCommentAdapter.onDataChange(mCommentLists);
		mCommentContentET.setText("");
		
		mJoke.setCommentNum(mJoke.getCommentNum()+1);
		mCommentTV.setText(String.valueOf(mJoke.getCommentNum()));
		
		mListView.setSelectionAfterHeaderView();//选中第一条
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		RxBus.getInstance().unSubscribe(this);
		mPreshenter.unsubscribe();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if(ToolsUtils.isKeybordShow(this)) {
			ToolsUtils.hideKeyboard(mCommentContentET);
		}
		super.onPause();
	}
}
