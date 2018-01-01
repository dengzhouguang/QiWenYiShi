package com.dzg.readclient.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.injector.component.ApplicationComponent;
import com.dzg.readclient.injector.component.DaggerUserInfoComponent;
import com.dzg.readclient.injector.component.UserInfoComponent;
import com.dzg.readclient.injector.module.ActivityModule;
import com.dzg.readclient.injector.module.UserInfoModule;
import com.dzg.readclient.mvp.contract.UserInfoContract;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
import com.dzg.readclient.utils.ProgressDialogUtils;
import com.dzg.readclient.utils.ToastUtils;
import com.dzg.readclient.utils.ToolsUtils;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * @ClassName: UserInfoActivity
 * @Description: 用户基本信息界面
 */
public class UserInfoActivity extends BaseActivity implements UserInfoContract.View{
    @Inject
    UserInfoContract.Presenter mPresenter;
	private final String TAG = "UserInfoActivity";
	private final int CHOSE_PIC_CODE = 1;
	private final int TAKE_PHOTO_CODE = 2;
	private final int CROP_CODE = 3;
	/* 头像名称 */
	private final String IMAGE_FILE_NAME = "portrait.jpg";
	
	@BindView(R.id.userinfo_user_protrait)
	ImageView mPortraitIV;
	@BindView(R.id.userinfo_user_nickname)
	TextView mNickTV;
	@BindView(R.id.userinfo_user_sex)
	TextView mSexTV;
	
	private Dialog mSexDialog;
	private Dialog mPortraitDialog;

	private String mPortraitURL;
	private int mSexFlag = 1;
	
	@Override
	public  void onCreateView(Bundle savedInstanceState) {
        mPresenter.attachView(this);
		mPresenter.init();
		subscriptionEvent();
	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_user_info;
	}

	/**
	 * 初始化界面信息
	 */
	public  void showContent() {
		if(ReadClientApp.getInstance().currentUser == null) {
			return;
		}
		Glide.with(UserInfoActivity.this).load(ReadClientApp.getInstance().currentUser.getPortraitUrl()).into(mPortraitIV);
		mNickTV.setText(ReadClientApp.getInstance().currentUser.getUserNike());
		mSexTV.setText(ReadClientApp.getInstance().currentUser.getSex()==1?"男":"女");
	}
	
	@Override
	protected void onResume() {
		mPresenter.init();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		RxBus.getInstance().unSubscribe(this);
		mPresenter.unsubscribe();
		super.onDestroy();
	}

	@Override
	public void injectDependences() {
		ApplicationComponent appComponent=ReadClientApp.getInstance().getApplicationComponent();
		UserInfoComponent component= DaggerUserInfoComponent.builder()
				.applicationComponent(appComponent)
				.activityModule(new ActivityModule(this))
				.userInfoModule(new UserInfoModule())
				.build();
		component.inject(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	/* 修改昵称 */
	@OnClick(R.id.user_nickname_biglayout)
	void setNick(View view) {
		Intent intent = new Intent(this, UpdateNickActivity.class);
		startActivityWithAnimation(intent);
	}
	
	/* 修改性别 */
	@OnClick(R.id.user_sex_biglayout)
	void setSex(View view) {
		if (mSexDialog == null) {
			mPresenter.createSexDialog();
		}
		mSexDialog.show();
	}
	
	/**
	 * 创建性别选择对话框
	 */
	public  void showSexDialog() {
		mSexDialog = new Dialog(this, R.style.Translucent_NoTitle);
		mSexDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置对话框无title
		mSexDialog.setContentView(R.layout.dialog_userinfo);
		TextView title = (TextView) mSexDialog
				.findViewById(R.id.dialog_userinfo_title);
		title.setText(R.string.sex);
		ListView listview = (ListView) mSexDialog
				.findViewById(R.id.dialog_userinfo_listview);
		String[] strings = new String[] { "男", "女" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.listitem_userinfo_dialog,
				R.id.name, strings);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == 0) {
					mSexTV.setText(R.string.sex_man);
					mSexFlag = 1;
				} else {
					mSexTV.setText(R.string.sex_women);
					mSexFlag = 0;
				}
				mSexDialog.dismiss();
				if(mSexFlag == ReadClientApp.getInstance().currentUser.getSex()) {
					return;
				}
				ProgressDialogUtils.showProgressDialog(UserInfoActivity.this, "正在处理中...");
				mPresenter.setNickAndSex(String.valueOf(ReadClientApp.getInstance().currentUser.getId()),
						null, String.valueOf(mSexFlag));
			}
		});
		ToolsUtils.setDialogBgAndAnim(mSexDialog);
	}
	
	/* 修改头像 */
	@OnClick(R.id.user_portrait_biglayout)
	void setPortrait(View view) {
		if (mPortraitDialog == null) {
			mPresenter.createPortraitDialog();
		}
		mPortraitDialog.show();
	}
	
	public  void showPortraitDialog() {
		mPortraitDialog = new Dialog(this, R.style.Translucent_NoTitle);
		mPortraitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置对话框无title
		mPortraitDialog.setContentView(R.layout.dialog_userinfo);
		TextView title = (TextView) mPortraitDialog
				.findViewById(R.id.dialog_userinfo_title);
		title.setText(R.string.set_portrait);
		ListView listview = (ListView) mPortraitDialog
				.findViewById(R.id.dialog_userinfo_listview);
		String[] strings = new String[] { "相册", "相机" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.listitem_userinfo_dialog,
				R.id.name, strings);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0: // 选中相册
					Intent intentFromGallery = new Intent();
					intentFromGallery.setType("image/*"); // 设置文件类型
					intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResultWithAnimation(intentFromGallery,
							CHOSE_PIC_CODE);
					break;
				case 1: // 选中照相机
					Intent intentFromCapture = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					// 判断存储卡是否可以用，可用进行存储
					if(ToolsUtils.hasSdcard()) {
						intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(new File(Environment
										.getExternalStorageDirectory(),
										IMAGE_FILE_NAME)));
						startActivityForResultWithAnimation(intentFromCapture,
								TAKE_PHOTO_CODE);
					} else {
						ToastUtils.showMessageLong(getApplicationContext(),
								"未找到存储卡，无法存储照片！");
					}
					break;
				}
				mPortraitDialog.dismiss();
			}
		});
		ToolsUtils.setDialogBgAndAnim(mPortraitDialog);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case CHOSE_PIC_CODE:   //从相册选择照片成功
			try {
				mPresenter.startPhotoZoom(data.getData());
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case TAKE_PHOTO_CODE: //从相机拍照成功
			File tempFile = new File(Environment.getExternalStorageDirectory() + "/"
					+ IMAGE_FILE_NAME);
			try {
				mPresenter.startPhotoZoom(Uri.fromFile(tempFile));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case CROP_CODE:    //裁剪照片成功
			if (data == null) {
				return;	
			}
			mPresenter.getImageToView();
			break;
		}
	}


	private void subscriptionEvent() {
		Subscription subscription = RxBus.getInstance().toObservable(Constants.UPDATE, RxBusMessage.class)
				.subscribe(new Action1<RxBusMessage>() {
					@Override
					public void call(RxBusMessage message) {
						switch (message.getCode()) {
							case Constants.FAILURE:
                                ProgressDialogUtils.dismiss();
								break;
							case Constants.SUCCESS:
                                Log.e(TAG,"uploadPortrait");
								mPresenter.uploadPortrait();
								break;
							case Constants.FAILURE_1: //设置头像失败
                                Log.e(TAG,"uploadPortrait faild");
								ProgressDialogUtils.dismiss();
								break;
							case Constants.SUCCESS_1: //设置头像成功
                                Log.e(TAG,"uploadPortrait success");
								ProgressDialogUtils.dismiss();
								mPortraitURL= (String) message.getObject();
								mPresenter.setPortraitSuccess();
								break;
							case Constants.FAILURE_2: //设置性别失败
								ProgressDialogUtils.dismiss();
								break;
							case Constants.SUCCESS_2: //设置性别成功
								mPresenter.setSexSuccess();
								ProgressDialogUtils.dismiss();
								break;
						}
					}
				});
		RxBus.getInstance().addSubscription(this,subscription);
	}
	/**
	 * 设置昵称和性别成功
	 */
	public void showSetSexSuccess() {
		if(ReadClientApp.getInstance().currentUser == null) {
			return;
		}
		ReadClientApp.getInstance().currentUser.setSex(mSexFlag);
		spUtil.putObject("user", ReadClientApp.getInstance().currentUser);
	}
	/**
	 * 设置头像成功
	 */
	public  void showSetPortraitSuccess() {
		Glide.with(UserInfoActivity.this).load(mPortraitURL).into(mPortraitIV);
		if(ReadClientApp.getInstance().currentUser == null) {
			return;
		}
		ReadClientApp.getInstance().currentUser.setPortraitUrl(mPortraitURL);
		spUtil.putObject("user", ReadClientApp.getInstance().currentUser);
	}
	
	/**
	 * 注销当前账号
	 * @param view
	 */
	@OnClick(R.id.login_out_btn)
	void loginOut(View view) {
		ReadClientApp.getInstance().currentUser = null;
		spUtil.remove("user");
		finishWithAnimation();
	}
	
	
	@OnClick(R.id.back)
	void back(View view) {
		finishWithAnimation();
	}
	
}
