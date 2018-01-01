package com.dzg.readclient.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.injector.component.ApplicationComponent;
import com.dzg.readclient.injector.component.DaggerUserCenterComponent;
import com.dzg.readclient.injector.component.UserCenterComponent;
import com.dzg.readclient.injector.module.UserCenterModule;
import com.dzg.readclient.mvp.contract.UserCenterContract;
import com.dzg.readclient.ui.view.CTDialog;
import com.dzg.readclient.ui.view.CircleImageView;
import com.dzg.readclient.utils.DataCleanManager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ClassName: UserCenterActivity
 * @Description: 个人中心activity
 */
public class UserCenterActivity extends BaseActivity implements UserCenterContract.View{
	@Inject
	UserCenterContract.Presenter mPresenter;
	private final String TAG = "UserCenterActivity";
	private final int LOGIN_CODE = 1;
	
	@BindView(R.id.user_img)
	CircleImageView mUserPortrait;
	@BindView(R.id.username)
	TextView mUserNick;
	@BindView(R.id.cache_size_tv)
	TextView mCacheSizeTV;
	@BindView(R.id.img_load_cb)
	CheckBox isLoadImgOnNotWifi;
	@BindView(R.id.content_notify_cb)
	CheckBox isReceivePush;

	@Override
	public  void onCreateView(Bundle savedInstanceState) {
		mPresenter.attachView(this);
		mPresenter.init();
		mPresenter.initCheckBoxStatusAndSetListenner();
	}

	@Override
	public void injectDependences() {
		ApplicationComponent applicationComponent=ReadClientApp.getInstance().getApplicationComponent();
		UserCenterComponent component= DaggerUserCenterComponent.builder()
				.applicationComponent(applicationComponent)
				.userCenterModule(new UserCenterModule())
				.build();
		component.inject(this);
	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_user_center;
	}

	public void showContent() {
		if(ReadClientApp.getInstance().currentUser != null) {
            if (ReadClientApp.getInstance().currentUser.getPortraitUrl()!=null&&!ReadClientApp.getInstance().currentUser.getPortraitUrl().equals(""))
			     Glide.with(UserCenterActivity.this).load( ReadClientApp.getInstance().currentUser.getPortraitUrl()).into(mUserPortrait);
            else
                mUserPortrait.setImageResource(R.drawable.default_portrait);
			mUserNick.setText(ReadClientApp.getInstance().currentUser.getUserNike());
		} else {
			mUserPortrait.setImageResource(R.drawable.default_portrait);
			mUserNick.setText(R.string.click_login);
		}
		try {
			mCacheSizeTV.setText(DataCleanManager.getCacheSize(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onResume() {
		mPresenter.init();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	/**
	 * 个人信息
	 * @param view
	 */
	@OnClick(R.id.user_img)
	void toUserInfo(View view) {
		if(ReadClientApp.getInstance().currentUser == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivityWithAnimation(intent);
			ReadClientApp.getInstance().isStartOtherActivity = true;
			return;
		}
		//跳转到个人信息界面
		Intent intent = new Intent(this, UserInfoActivity.class);
		startActivityWithAnimation(intent);
		ReadClientApp.getInstance().isStartOtherActivity = true;
	}
    @OnClick(R.id.username)
    void nickClick(View view) {
        if(ReadClientApp.getInstance().currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityWithAnimation(intent);
            ReadClientApp.getInstance().isStartOtherActivity = true;
            return;
        }
        //跳转到个人信息界面
        Intent intent = new Intent(this, UserInfoActivity.class);
        startActivityWithAnimation(intent);
        ReadClientApp.getInstance().isStartOtherActivity = true;
    }
	/**
	 * 我的收藏
	 * @param view
	 */
	@OnClick(R.id.mycollect_layout)
	void toMyCollect(View view) {
		Intent intent = new Intent(this, MyCollectActivity.class);
		startActivityWithAnimation(intent);
		ReadClientApp.getInstance().isStartOtherActivity = true;
	}
	
	/**
	 * 反馈
	 * @param view
	 */
	@OnClick(R.id.feedback_layout)
	void toFeedback(View view) {
		Intent intent = new Intent(this, FeedbackActivity.class);
		startActivityWithAnimation(intent);
		ReadClientApp.getInstance().isStartOtherActivity = true;
	}
	
	/**
	 * 关于奇趣营
	 * @param view
	 */
	@OnClick(R.id.about_layout)
	void toAbout(View view) {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivityWithAnimation(intent);
		ReadClientApp.getInstance().isStartOtherActivity = true;
	}
	
	/**
	 * 检查版本更新
	 * @param view
	 */
	@OnClick(R.id.update_layout)
	void checkUpdate(View view) {
	}
	private CTDialog dialog;
	/**
	 * 清除缓存
	 * @param view
	 */
	@OnClick(R.id.clearcache_layout)
	void ache(View view) {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle("温馨提示")
				.setMessage("确定要清除缓存吗?")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DataCleanManager.CleanCache(UserCenterActivity.this);
						mCacheSizeTV.setText("0.0B");
					}
				});
		builder.create().show();
		/*if(dialog == null) {
			CTDialog.Builder customBuilder = new CTDialog.Builder(UserCenterActivity.this);
	        customBuilder.setTitle("温馨提示")
	            .setMessage("确定要清除缓存吗？")
	            .setNegativeButton("取消", 
	            		new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                	dialog.dismiss();
	                }
	            }).setPositiveButton("确定", 
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                	dialog.dismiss();
	                	DataCleanManager.CleanCache(UserCenterActivity.this);
	            		mCacheSizeTV.setText("0.0B");
	                }
	            });
	        dialog = customBuilder.create();
		}
		dialog.show();*/

	}

	@OnClick(R.id.apptuijian_layout)
	void gdtAppWallAd(View view) {
	}
	
	/**
	 * 初始化checkbox选中状态,并设置监听器
	 */
	public void showCheckBoxStatusAndSetListenner() {
		if(spUtil.getBoolean(Constants.IS_LOAD_IMG, true)) {
			isLoadImgOnNotWifi.setChecked(true);
		} else {
			isLoadImgOnNotWifi.setChecked(false);
		}
		if(spUtil.getBoolean(Constants.IS_RECEIVE_PUSH, true)) {
			isReceivePush.setChecked(true);
		} else {
			isReceivePush.setChecked(false);
		}
		setOnCheckedChangeListenner();
	}
	
	OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(buttonView.getId() == R.id.img_load_cb) {
				if(isChecked) {
					spUtil.putBoolean(Constants.IS_LOAD_IMG, true);
				} else {
					spUtil.putBoolean(Constants.IS_LOAD_IMG, false);
				}
			}
			if(buttonView.getId() == R.id.content_notify_cb) {
				if(isChecked) {
					spUtil.putBoolean(Constants.IS_RECEIVE_PUSH, true);
//					PushAgent.getInstance(UserCenterActivity.this).enable();
				} else {
					spUtil.putBoolean(Constants.IS_RECEIVE_PUSH, false);
//					PushAgent.getInstance(UserCenterActivity.this).disable();
				}
			}
		}
	};

	@Override
	protected void onDestroy() {
		mPresenter.unsubscribe();
		super.onDestroy();
	}

	/**
	 * 注册checkbox状态改变监听器
	 */
	private void setOnCheckedChangeListenner() {
		isLoadImgOnNotWifi.setOnCheckedChangeListener(checkedChangeListener);
		isReceivePush.setOnCheckedChangeListener(checkedChangeListener);
	}
	
}
