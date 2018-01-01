package com.dzg.readclient.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.api.ApiCallBack;
import com.dzg.readclient.mvp.model.User;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.service.IUserService;
import com.dzg.readclient.service.impl.UserServiceImpl;
import com.dzg.readclient.ui.view.CTDialog;
import com.dzg.readclient.utils.ProgressDialogUtils;
import com.dzg.readclient.utils.ToastUtils;
import com.dzg.readclient.utils.Util;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ClassName: ReSetPwdActivity
 * @Description: 重置密码界面
 * @author lling
 * @date 2015-7-22
 */
public class ReSetPwdActivity extends BaseActivity {

	@BindView( R.id.password1)
	EditText mPassword1;
	@BindView(R.id.password2)
	EditText mPassword2;
	private IUserService mUserService = new UserServiceImpl(this);
	private String mPhone;
	
	@Override
	public  void onCreateView(Bundle savedInstanceState) {
		mPhone = getIntent().getStringExtra("phone");
	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_reset_pwd;
	}

	/**
	 * 修改密码
	 * @param view
	 */
	@OnClick(R.id.save_btn)
	void savePassword(View view) {
		String password1 = mPassword1.getText().toString().trim();
		String password2 = mPassword2.getText().toString().trim();
		if(Util.isEmpty(password1)) {
			ToastUtils.showMessage(this, R.string.input_pwd_hint);
			return;
		}
		if(password1.length() < 6) {
			ToastUtils.showMessage(this, R.string.password_short);
			return;
		}
		if(Util.isEmpty(password2)) {
			ToastUtils.showMessage(this, R.string.input_pwd_hint1);
			return;
		}
		if(!password1.equals(password2)) {
			ToastUtils.showMessage(this, R.string.input_not_same);
			return;
		}
		ProgressDialogUtils.showProgressDialog(ReSetPwdActivity.this, getString(R.string.doing));
		mUserService.reSetPassword(mPhone, password1, apiCallBack);
	}
	
	private ApiCallBack apiCallBack = new ApiCallBack() {
		@Override
		public void onSuccess(Object data) {
			ProgressDialogUtils.dismiss();
			ReadClientApp.getInstance().currentUser = (User)data;
			spUtil.putObject("user", ReadClientApp.getInstance().currentUser);
			Intent intent = new Intent(ReSetPwdActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivityWithAnimation(intent);
			finishWithAnimation();
		}
		
		@Override
		public void onFailure(String msg) {
			ProgressDialogUtils.dismiss();
			if(Constants.PARAM_INVALUD.equals(msg)) {
				ToastUtils.showMessage(ReSetPwdActivity.this, R.string.phone_invalid);
			} else if(Constants.SOURCE_NOT_EXISTS.equals(msg)) {
				ToastUtils.showMessage(ReSetPwdActivity.this, R.string.phone_not_exists);
			} else {
				ToastUtils.showMessage(ReSetPwdActivity.this, R.string.reset_pwd_fail);
			}
		}
		@Override
		public void onError(Exception e, String msg) {
			ProgressDialogUtils.dismiss();
			ToastUtils.showMessage(ReSetPwdActivity.this, R.string.reset_pwd_fail);
		}
	};
	
	@OnClick(R.id.back)
	void back(View view) {
		showBackDialog();
	}
	
	@Override
	public void onBackPressed() {
		showBackDialog();
	}
	
	// 取消
	DialogInterface.OnClickListener backCancelListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	};
	// 确定发送
	DialogInterface.OnClickListener backOkListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			finishWithAnimation();
		}
	};
	CTDialog.Builder customBuilder = new CTDialog.Builder(this);
	CTDialog sendSmsDialog;
	private void showBackDialog() {
		if(sendSmsDialog == null) {
			customBuilder.setTitle("温馨提示")
			.setMessage("确定要放弃此次密码重置操作吗？")
			.setNegativeButton("继续", backCancelListener)
			.setPositiveButton("放弃", backOkListener);
			sendSmsDialog = customBuilder.create();
		}
		sendSmsDialog.show();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
}
