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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.adapters.CommentAdapter;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.PageResult;
import com.dzg.readclient.injector.component.ApplicationComponent;
import com.dzg.readclient.injector.component.DaggerTuDetailActivityComponent;
import com.dzg.readclient.injector.component.TuDetailActivityComponent;
import com.dzg.readclient.injector.module.ActivityModule;
import com.dzg.readclient.injector.module.TuDetailActivityModule;
import com.dzg.readclient.mvp.contract.TuDetailContract;
import com.dzg.readclient.mvp.model.Comment;
import com.dzg.readclient.mvp.model.DingOrCai;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.mvp.model.User;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
import com.dzg.readclient.ui.view.LoadListView;
import com.dzg.readclient.ui.view.LoadPercentImageView;
import com.dzg.readclient.ui.view.Scroll2BottomListenerScrollView;
import com.dzg.readclient.ui.view.SharePopWindow;
import com.dzg.readclient.utils.DensityUtil;
import com.dzg.readclient.utils.FastjsonUtil;
import com.dzg.readclient.utils.HttpUtil;
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
 * @ClassName: TuDetailActivity
 * @Description: 趣图或者美图详情界面
 */
public class TuDetailActivity extends BaseActivity implements TuDetailContract.View{
	@Inject
	public TuDetailContract.Presenter mPresenter;
	private final String TAG = "TuDetailActivity";
	@BindView(R.id.user_nick)
	 TextView mUserNickTV;
	@BindView(R.id.create_date)
	TextView mCreateTimeTV;
	@BindView(R.id.qutu_title)
	TextView mTitleTV;
	@BindView(R.id.qutu_content)
	LoadPercentImageView mContentIV;
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
	@BindView(R.id.qutu_layout)
	LinearLayout mQuTuLayout;
	@BindView(R.id.progress)
	ProgressBar mLoadProgressBar;
	
	private Joke mJoke;
	private CommentAdapter mCommentAdapter;
	private List<Comment> mCommentLists = new ArrayList<Comment>();
	private int mPageId = 1;
	private int mCount = 10;
	private boolean mHasNext = true;
	private int mMaxWidth;
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
		mSharePopWindow = new SharePopWindow(this);
		mListView.setHaveScrollbar(false);
		mCommentAdapter = new CommentAdapter(this);
		mListView.setAdapter(mCommentAdapter);
		mListView.setOnFootLoadingListener(new LoadListView.OnFootLoadingListener() {
			@Override
			public void onFootLoading() {
				mPresenter.loadComments();
			}
		});
		mPresenter.attachView(this);
		mPresenter.initTuContent();
		subscriptionEvent();
	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_tu_detail;
	}

	@Override
	public void injectDependences() {
		ApplicationComponent appComponent=ReadClientApp.getInstance().getApplicationComponent();
		TuDetailActivityComponent component= DaggerTuDetailActivityComponent.builder()
				.applicationComponent(appComponent)
				.activityModule(new ActivityModule(this))
				.tuDetailActivityModule(new TuDetailActivityModule())
				.build();
		component.inject(this);
	}

	/**
	 * 如果此界面已经打开，再点通知界面打开，则调用此方法
	 */
	protected void onNewIntent(Intent intent) {
		if(Constants.DEBUG) Log.d(TAG, "onNewIntent");
		setIntent(intent);
		mPresenter.reSet();
		mPresenter.initTuContent();
	}
	
	public  void showAndReSet() {
		mPageId = 1;
		mHasNext = true;
		mCommentLists.clear();
		
	}
	
	//初始化笑话内容
	public  void showContent() {
		mJoke = (Joke)getIntent().getSerializableExtra("content");
		if(mJoke == null) {
			try {
				mJokeId = Integer.valueOf(getIntent().getStringExtra("id"));
				mIsOpenFromPush = true;
				ReadClientApp.getInstance().currentUser = (User) spUtil.getObject("user", null);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			mPresenter.getJokeById(mJokeId);
		} else {
			isCommnet = getIntent().getBooleanExtra("isComment", false);
			mJokeId = mJoke.getId();
			mPresenter.setTuContent();
		}
	}
	
	public void showTuContent() {
		mPresenter.loadComments();  //把加载评论放在加载内容之后（推送进来）
		mLoadProgressBar.setVisibility(View.GONE);
		mScrollView.setVisibility(View.VISIBLE);
		mMaxWidth = DensityUtil.getWidthInPx(this) - DensityUtil.dip2px(this, 32);
		mContentIV.setMaxWidth(mMaxWidth);
		int width = mJoke.getImgWidth();
        int height = mJoke.getImgHeight();
        if(width != 0 && width != 0) {
        	float scale = (float)width / (float)mMaxWidth;
            height = (int)(height / scale);
            mContentIV.setLayoutParams(new LinearLayout.LayoutParams(mMaxWidth, height));
        }
		mContentIV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TuDetailActivity.this, ImageShowActivity.class);
				intent.putExtra("url", mJoke.getImgUrl());
				startActivity(intent);
			}
		});
		Glide.with(TuDetailActivity.this).load(mJoke.getPortraitUrl()).into(mUserPortraitIV);
		mUserNickTV.setText(mJoke.getUserNike());
		mCreateTimeTV.setText(Util.getFormatDate(mJoke.getCreateDate()));
		if(Util.isEmpty(mJoke.getTitle())) {
			mTitleTV.setVisibility(View.GONE);
		} else {
			mTitleTV.setText(mJoke.getTitle());
		}
        mDingTV.setText(String.valueOf(mJoke.getSupportsNum()));
        mCaiTV.setText(String.valueOf(mJoke.getOpposesNum()));
        mCommentTV.setText(String.valueOf(mJoke.getCommentNum()));
        
        final DingOrCai dingOrCai = mPresenter.getDingOrCai(mUserId, mJokeId);
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
						ToastUtils.showMessage(TuDetailActivity.this, R.string.has_ding);
					} else {
						ToastUtils.showMessage(TuDetailActivity.this, R.string.has_cai);
					}
					return;
				}
				if(v.isSelected()) {
					ToastUtils.showMessage(TuDetailActivity.this, R.string.has_ding);
					return;
				}
				View caiView = ((LinearLayout)v.getParent().getParent()).findViewById(R.id.cai_tv);
				if(caiView.isSelected()) {
					ToastUtils.showMessage(TuDetailActivity.this, R.string.has_cai);
					return;
				}
				final View view = ((RelativeLayout)v.getParent()).getChildAt(1);
				Animation addOneAnimation = AnimationUtils.loadAnimation(
						TuDetailActivity.this, R.anim.add_one);
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
				mPresenter.dingOrCai(mUserId, mJokeId, DingOrCai.DING, mJoke.getSupportsNum());
			}
		});
        
        mCaiTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(dingOrCai != null) {
					if(dingOrCai.isDing()) {
						ToastUtils.showMessage(TuDetailActivity.this, R.string.has_ding);
					} else {
						ToastUtils.showMessage(TuDetailActivity.this, R.string.has_cai);
					}
					return;
				}
				if(v.isSelected()) {
					ToastUtils.showMessage(TuDetailActivity.this, R.string.has_cai);
					return;
				}
				View dingView = ((LinearLayout)v.getParent().getParent()).findViewById(R.id.ding_tv);
				if(dingView.isSelected()) {
					ToastUtils.showMessage(TuDetailActivity.this, R.string.has_ding);
					return;
				}
				final View view = ((RelativeLayout)v.getParent()).getChildAt(1);
				Animation addOneAnimation = AnimationUtils.loadAnimation(
						TuDetailActivity.this, R.anim.add_one);
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
				mPresenter.dingOrCai(mUserId, mJokeId, DingOrCai.CAI, mJoke.getOpposesNum());
			}
		});
        mShareTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				mSharePopWindow.setmJoke(mJoke);
				mSharePopWindow.showAtLocation(v.getRootView(), Gravity.LEFT | Gravity.BOTTOM, 0, 0);
			}
		});
        mCommentTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCommentContentET.requestFocus();
				ToolsUtils.openKeybord(mCommentContentET, TuDetailActivity.this);
			}
		});
        if(HttpUtil.isWifi(this) || spUtil.getBoolean(Constants.IS_LOAD_IMG, true)) {  //仅wifi状态下加载网络
        	mContentIV.setLoadImg(true);
			Glide.with(TuDetailActivity.this).load(mJoke.getImgUrl()).listener(new RequestListener<String, GlideDrawable>() {
				@Override
				public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
					LoadPercentImageView imageView = (LoadPercentImageView) mContentIV;
					imageView.setProgress(0);
					return false;
				}

				@Override
				public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
					LoadPercentImageView imageView = (LoadPercentImageView)mContentIV;
					imageView.setProgress(100);
					imageView.setComplete(true);
					return false;
				}
			}).into(mContentIV);
        } else {
        	mContentIV.setLoadImg(false);
        }
	}
	
	/**
	 * 加载评论
	 */
	public void showComments() {
		if(!mHasNext){
			mListView.onFootLoadingComplete(true);
			return;
		}
		mPresenter.getCommentByJokeId( mJokeId, mPageId, mCount);
	}
	
	/**
	 * 加载数据成功，更新界面
	 * @param pageResult
	 */
	public  void showLoadSuccess(PageResult pageResult) {
		mScrollView.setmListView(mListView);
		if(pageResult == null || pageResult.isEmpty()) {
			if(Constants.DEBUG) Log.w(TAG, "load data success, but no data!");
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
		if(mPageId == 1) {
			mPresenter.scroll2Target();
		}
		mHasNext = pageResult.getHasNext();
		mListView.setHasMoreData(mHasNext);  //设置是否还有更多数据
		if (pageResult.getHasNext()) {
			mPageId = pageResult.getNext();
		}
	}
	
	/**
	 * 第一次进来，如果点击评论则scrollview滚动到评论部分，否则滚到顶部
	 * scrollview嵌套listview时候，有时候会自动滚动到listview的第一个item
	 * 所以如果不是点击评论进来的要滚动到顶部
	 */
	public  void show2Target() {
		if(isCommnet) {   //如果点击评论进来的，则滚动到评论处
			TaskExecutor.scheduleTaskOnUiThread(150, new Runnable() {
				@Override
				public void run() {
					int height = mQuTuLayout.getHeight();
					mScrollView.scrollTo(0, height);
				}
			});
		} else {
			TaskExecutor.scheduleTaskOnUiThread(150, new Runnable() {
				@Override
				public void run() {
					mScrollView.scrollTo(0, 0);
				}
			});
		}
	}
	private void subscriptionEvent() {
		Subscription subscription = RxBus.getInstance().toObservable(Constants.COMMENT, RxBusMessage.class)
				.subscribe(new Action1<RxBusMessage>() {
					@Override
					public void call(RxBusMessage message) {
						switch (message.getCode()) {
							case Constants.SUCCESS: // 获取评论数据成功
								PageResult pageResult = (PageResult)message.getObject();
								mPresenter.loadSuccess(pageResult);
								mListView.onFootLoadingComplete(true);
								break;
							case Constants.FAILURE:
								mListView.onFootLoadingComplete(true);
								break;
							case Constants.SUCCESS_1: // 发表评论成功
								ProgressDialogUtils.dismiss();
								mPresenter.commentSuccess();
								break;
							case Constants.FAILURE_1:
								ProgressDialogUtils.dismiss();
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
		mPresenter.addComment( mJokeId, content);
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
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		if(ToolsUtils.isKeybordShow(this)) {
			ToolsUtils.hideKeyboard(mCommentContentET);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		RxBus.getInstance().unSubscribe(this);
		mPresenter.unsubscribe();
		super.onDestroy();
	}


}
