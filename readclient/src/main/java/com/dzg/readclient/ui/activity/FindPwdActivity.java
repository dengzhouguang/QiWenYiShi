package com.dzg.readclient.ui.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dzg.readclient.R;
import com.dzg.readclient.api.ApiCallBack;
import com.dzg.readclient.service.IUserService;
import com.dzg.readclient.service.impl.UserServiceImpl;
import com.dzg.readclient.ui.view.CTDialog;
import com.dzg.readclient.utils.ProgressDialogUtils;
import com.dzg.readclient.utils.TaskExecutor;
import com.dzg.readclient.utils.ToastUtils;
import com.dzg.readclient.utils.Util;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * @author lling
 * @ClassName: FindPwdActivity1
 * @Description: 找回密码，发送验证码
 * @date 2015-7-22
 */
public class FindPwdActivity extends BaseActivity {
    private final String TAG = "FindPwdActivity1";
    private final int RETRY_INTERVAL = 60;
    @BindView(R.id.phone)
    EditText mPhoneET;
    @BindView(R.id.sms_code)
    EditText mCodeET;
    @BindView(R.id.send_btn)
    Button mSendBtn;
    String mHint;
    SmsContent mSmsContent;
    //取消
    DialogInterface.OnClickListener sendCancelListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };
    //确定发送
    DialogInterface.OnClickListener sendOkListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            ProgressDialogUtils.showProgressDialog(FindPwdActivity.this, "正在发送验证短信...");
//        	mUserService.checkUserExeist(mPhone, checkExeistCallBack);
        }
    };
    CTDialog.Builder customBuilder = new CTDialog.Builder(this);
    private int mTime = RETRY_INTERVAL;
    private String mPhone;
    private IUserService mUserService = new UserServiceImpl(this);
    private ApiCallBack checkExeistCallBack = new ApiCallBack() {
        @Override
        public void onSuccess(Object data) {
            ToastUtils.showMessageInCenter(FindPwdActivity.this, getString(R.string.phone_not_exists));
            ProgressDialogUtils.dismiss();
        }

        @Override
        public void onFailure(String msg) {
            if ("501".equals(msg)) {
                ToastUtils.showMessageInCenter(FindPwdActivity.this, getString(R.string.phone_invalid));
                ProgressDialogUtils.dismiss();
            } else if ("502".equals(msg)) {
//				SMSSDK.getVerificationCode("86", mPhone.trim());
                ToastUtils.showMessage(FindPwdActivity.this, "错误代码502");
            } else {
                ToastUtils.showMessageInCenter(FindPwdActivity.this, "发送失败, 请重试");
                ProgressDialogUtils.dismiss();
            }
        }

        @Override
        public void onError(Exception e, String msg) {
            ProgressDialogUtils.dismiss();
            ToastUtils.showMessageInCenter(FindPwdActivity.this, "发送失败, 请重试");
        }
    };

    @Override
    public void onCreateView(Bundle savedInstanceState) {
        mHint = getResources().getString(R.string.sms_timer);
        mSmsContent = new SmsContent(new Handler());
        //注册短信变化监听
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, mSmsContent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_find_pwd_activity1;
    }

    @OnClick(R.id.send_btn)
    void sendSmsCode(View view) {
        mPhone = mPhoneET.getText().toString();
        if (Util.isEmpty(mPhone)) {
            ToastUtils.showMessage(getApplicationContext(), R.string.phone_null);
            return;
        }
        if (!Util.isPhone(mPhone)) {
            ToastUtils.showMessage(getApplicationContext(), R.string.phone_invalid);
            return;
        }

		/*if(ReadClientApp.getInstance().smsCodeRecodeFindPwd != null && ReadClientApp.getInstance().smsCodeRecodeFindPwd.containsKey(mPhone)) {
            long time = ReadClientApp.getInstance().smsCodeRecodeFindPwd.get(mPhone);
			int interval = (int)((System.currentTimeMillis() - time) / 1000);
			int left = 60 - interval;
			if(left > 10) {
				mTime = left;
				String hintTest = String.format(mHint, mTime);
		        mSendBtn.setText(hintTest);
		        mSendBtn.setEnabled(false);
		        countDown();
				return;
			}
		}*/
        showSendSmsDialog();
    }
	
	/*EventHandler mEventHandler = new EventHandler(){
		@Override
		public void afterEvent(int event, int result, Object data) {
			Log.e(TAG, event + "," + result);
			if (result == SMSSDK.RESULT_COMPLETE) {
				// 回调完成
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//验证成功
					Intent intent = new Intent(FindPwdActivity.this, ReSetPwdActivity.class);
					intent.putExtra("phone", mPhone);
					finishWithAnimation();
					startActivityWithAnimation(intent);
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){  //发送成功
					ProgressDialogUtils.dismiss();
					if(ReadClientApp.getInstance().smsCodeRecodeFindPwd == null) {
						ReadClientApp.getInstance().smsCodeRecodeFindPwd = new HashMap<String, Long>();
					}
					//记录已发送记录
					ReadClientApp.getInstance().smsCodeRecodeFindPwd.put(mPhone, System.currentTimeMillis());
					ImgToastUtils.showMessage(FindPwdActivity.this, "发送成功", R.drawable.center_ok_tip);
					countDown();
				}
			} else {
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//验证错误
					ToastUtils.showMessageInCenter(FindPwdActivity.this, "验证码输入错误");
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					ProgressDialogUtils.dismiss();
					ToastUtils.showMessageInCenter(FindPwdActivity.this, "发送失败");
				}
			}
		}
	};*/

    private void showSendSmsDialog() {
        customBuilder.setTitle("温馨提示")
                .setMessage("我们将发送验证码到这个号码\n+86 " + mPhone)
                .setNegativeButton("取消", sendCancelListener).setPositiveButton("确定",
                sendOkListener);
        CTDialog sendSmsDialog = customBuilder.create();
        sendSmsDialog.show();
    }

    /**
     * 倒数计时
     */
    private void countDown() {
        TaskExecutor.scheduleTaskOnUiThread(1000, new Runnable() {
            public void run() {
                mTime--;
                if (mTime == 0) {
                    String hintTest = String.format(mHint, "");
                    mSendBtn.setText(hintTest);
                    mSendBtn.setEnabled(true);
                    mTime = RETRY_INTERVAL;
                } else {
                    String hintTest = String.format(mHint, mTime);
                    mSendBtn.setText(hintTest);
                    mSendBtn.setEnabled(false);
                    TaskExecutor.scheduleTaskOnUiThread(1000, this);
                }
            }
        });
    }

    @OnClick(R.id.next_btn)
    void next(View view) {
        if (Util.isEmpty(mPhone)) {
            ToastUtils.showMessage(getApplicationContext(), R.string.phone_null);
            return;
        }
        if (!Util.isPhone(mPhone)) {
            ToastUtils.showMessage(getApplicationContext(), R.string.phone_invalid);
            return;
        }
        String code = mCodeET.getText().toString().trim();
        if (Util.isEmpty(code)) {
            ToastUtils.showMessageInCenter(this, "请输入验证码");
            return;
        }
        ToastUtils.showMessageInCenter(this, "已将验证码发送到手机");

    }

    @OnClick(R.id.back)
    void back(View view) {
        finishWithAnimation();
    }

    @Override
    protected void onPause() {
//    	SMSSDK.unregisterEventHandler(mEventHandler);
//    	MobclickAgent.onPageEnd("FindPwdActivity1");
        super.onPause();
    }

    @Override
    protected void onResume() {
//    	SMSSDK.registerEventHandler(mEventHandler);
//    	MobclickAgent.onPageStart("FindPwdActivity1"); //统计页面
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        this.getContentResolver().unregisterContentObserver(mSmsContent);
        super.onDestroy();
    }

    /*
     * 监听短信数据库
     */
    class SmsContent extends ContentObserver {
        private Cursor cursor = null;

        public SmsContent(Handler handler) {
            super(handler);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            // 读取收件箱中指定号码的短信
            cursor = managedQuery(Uri.parse("content://sms/inbox"),
                    new String[]{"_id", "address", "read", "body"},
                    " read=?",
                    new String[]{"0"}, "_id desc");
            // 按id排序，如果按date排序的话，修改手机时间后，读取的短信就不准了
            if (cursor != null && cursor.getCount() > 0) {
                ContentValues values = new ContentValues();
                values.put("read", "1"); // 修改短信为已读模式
                cursor.moveToNext();
                int smsbodyColumn = cursor.getColumnIndex("body");
                String smsBody = cursor.getString(smsbodyColumn);
                mCodeET.setText(Util.getDynamicPassword(smsBody));
            }
            // 在用managedQuery的时候，不能主动调用close()方法， 否则在Android 4.0+的系统上， 会发生崩溃
            if (Build.VERSION.SDK_INT < 14) {
                cursor.close();
            }
        }
    }

}
