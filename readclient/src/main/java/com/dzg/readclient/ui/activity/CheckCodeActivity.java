package com.dzg.readclient.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.api.ApiCallBack;
import com.dzg.readclient.mvp.model.User;
import com.dzg.readclient.service.IUserService;
import com.dzg.readclient.service.impl.UserServiceImpl;
import com.dzg.readclient.ui.view.CTDialog;
import com.dzg.readclient.utils.ProgressDialogUtils;
import com.dzg.readclient.utils.ToastUtils;
import com.dzg.readclient.utils.Util;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ClassName: CheckCodeActivity
 * @Description: 注册验证短信验证码
 */
public class CheckCodeActivity extends BaseActivity {
    private final String TAG = "CheckCodeActivity";
    private final int RETRY_INTERVAL = 60;
    @BindView(R.id.sms_hint_tv)
    TextView mSmsHintTV;
    @BindView(R.id.resend_btn)
    Button mResendBtn;
    @BindView(R.id.code)
    EditText mCodeEt;
    String mHint;
    //注册接口回调
    ApiCallBack mRegistCallBack = new ApiCallBack() {
        @Override
        public void onSuccess(Object data) {
            ProgressDialogUtils.dismiss();
            User user = (User) data;
            ReadClientApp.getInstance().currentUser = user;
            spUtil.putObject("user", user);
            Intent aintent = new Intent(CheckCodeActivity.this, CreateUserInfoActivity.class);
            startActivityWithAnimation(aintent);
            finishWithAnimation();
        }

        @Override
        public void onFailure(String msg) {
            ProgressDialogUtils.dismiss();
            ToastUtils.showMessage(CheckCodeActivity.this, R.string.regist_fail);
        }

        @Override
        public void onError(Exception e, String msg) {
            ProgressDialogUtils.dismiss();
            ToastUtils.showMessage(CheckCodeActivity.this, R.string.regist_fail);
        }
    };
    private int mTime = RETRY_INTERVAL;
    private String mPhone;
    private String mPassword;
    private IUserService mUserService = new UserServiceImpl(this);
    private CTDialog dialog;

    //	SmsContent mSmsContent;
    @Override
    public void onCreateView(Bundle savedInstanceState) {
//		mSmsContent = new SmsContent(new Handler());
        //注册短信变化监听
//		this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, mSmsContent);
        init();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_code;
    }

    private void init() {
        mHint = getResources().getString(R.string.sms_timer);
        mPhone = getIntent().getStringExtra("phone");
        mPassword = getIntent().getStringExtra("password");
        int left = getIntent().getIntExtra("left", RETRY_INTERVAL);
        mTime = left;
        String hintTest = String.format(mHint, mTime);
        mResendBtn.setText(hintTest);
        String phone = getResources().getString(R.string.sms_hint);
        String phoneTest = String.format(phone, mPhone);
        mSmsHintTV.setText(Html.fromHtml(phoneTest));
        countDown();
    }

    /**
     * 倒数计时
     */
    private void countDown() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mTime--;
                if (mTime == 0) {
                    String hintTest = String.format(mHint, "");
                    mResendBtn.setText(hintTest);
                    mResendBtn.setEnabled(true);
                    mTime = RETRY_INTERVAL;
                } else {
                    String hintTest = String.format(mHint, mTime);
                    mResendBtn.setText(hintTest);
                    mResendBtn.setEnabled(false);
                    countDown();
                }
            }
        }, 1000);
    }

    @OnClick(R.id.resend_btn)
    void reSend(View view) {
        ToastUtils.showMessage(getApplicationContext(), "已重新发送，请注意查收短信！！");
        mTime = RETRY_INTERVAL;
        countDown();
    }

    @OnClick(R.id.clear)
    void clean(View view) {
        mCodeEt.setText("");
    }

/*	@Override
	protected void onDestroy() {
		this.getContentResolver().unregisterContentObserver(mSmsContent);
		super.onDestroy();
	}*/

    @OnClick(R.id.check_code_btn)
    void check(View view) {
        String code = mCodeEt.getText().toString().trim();
        if (Util.isEmpty(code)) {
            ToastUtils.showMessageInCenter(this, "请输入验证码");
            return;
        }
        ProgressDialogUtils.showProgressDialog(CheckCodeActivity.this, getString(R.string.registing));

    }

    @OnClick(R.id.back)
    void back(View view) {
        showBackDialog();
    }

    @Override
    public void onBackPressed() {
        showBackDialog();
    }

    private void showBackDialog() {
        if (dialog == null) {
            CTDialog.Builder customBuilder = new CTDialog.Builder(this);
            customBuilder.setTitle("温馨提示")
                    .setMessage(getString(R.string.sms_back))
                    .setNegativeButton("继续等待",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton("取消验证",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            CheckCodeActivity.this.finishWithAnimation();
                        }
                    });
            dialog = customBuilder.create();
        }
        dialog.show();
    }
	
	/*
     * 监听短信数据库
     *//*
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
    				new String[] { "_id", "address", "read", "body" },
    				" address=? and read=?",
    				new String[] { Constants.SMS_SENDER, "0" }, "_id desc");
    		// 按id排序，如果按date排序的话，修改手机时间后，读取的短信就不准了
    		if (cursor != null && cursor.getCount() > 0) {
    			ContentValues values = new ContentValues();
    			values.put("read", "1"); // 修改短信为已读模式
    			cursor.moveToNext();
    			int smsbodyColumn = cursor.getColumnIndex("body");
    			String smsBody = cursor.getString(smsbodyColumn);
    			mCodeEt.setText(Util.getDynamicPassword(smsBody));
    		}
    		// 在用managedQuery的时候，不能主动调用close()方法， 否则在Android 4.0+的系统上， 会发生崩溃
    		if (Build.VERSION.SDK_INT < 14) {
    			cursor.close();
    		}
    	}
    }*/

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
