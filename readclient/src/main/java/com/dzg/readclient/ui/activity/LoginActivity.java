package com.dzg.readclient.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.injector.component.ApplicationComponent;
import com.dzg.readclient.injector.component.DaggerLoginComponent;
import com.dzg.readclient.injector.component.LoginComponent;
import com.dzg.readclient.injector.module.ActivityModule;
import com.dzg.readclient.injector.module.LoginModule;
import com.dzg.readclient.mvp.contract.LoginContract;
import com.dzg.readclient.mvp.model.User;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
import com.dzg.readclient.utils.ProgressDialogUtils;
import com.dzg.readclient.utils.ToastUtils;
import com.dzg.readclient.utils.Util;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;


public class LoginActivity extends BaseActivity implements LoginContract.View{
	@Inject
    LoginContract.Presenter mPresenter;
	private final String TAG = "LoginActivity";
	
	@BindView(R.id.login_layout)
	LinearLayout mLoginLayout;
	@BindView(R.id.regist_layout)
    LinearLayout mRegistLayout;
	@BindView(R.id.to_login_btn)
	TextView mToLoginBtn;
	@BindView(R.id.to_regist_btn)
	TextView mToRegistBtn;
	@BindView(R.id.login_phone)
	EditText mLoginEmail;
	@BindView(R.id.login_password)
	EditText mLoginPassword;
	@BindView(R.id.regist_phone)
	EditText mRegistPhone;
	@BindView(R.id.regist_password)
	EditText mRegistPassword;
	String mPhone;
	String mPassword;
	@Override
	public  void onCreateView(Bundle savedInstanceState) {
        mPresenter.attachView(this);
		subscriptionEvent();
	}

	@Override
	public void injectDependences() {
		ApplicationComponent applicationComponent=ReadClientApp.getInstance().getApplicationComponent();
		LoginComponent loginComponent= DaggerLoginComponent.builder()
				.applicationComponent(applicationComponent)
				.activityModule(new ActivityModule(this))
				.loginModule(new LoginModule())
				.build();
		loginComponent.inject(this);
	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_login;
	}
	@OnClick(R.id.btn_login_qq)
	void loginByQQ(View view) {
	}
	
	@OnClick(R.id.to_regist_btn)
	void toRegist(View view) {
		Animation outLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.login_out_from_left);
		outLeftAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				mLoginLayout.setVisibility(View.GONE);
				Animation inRightAnimation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.login_in_from_right);
				inRightAnimation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					@Override
					public void onAnimationEnd(Animation animation) {
						mRegistLayout.setVisibility(View.VISIBLE);
					}
				});
				mRegistLayout.startAnimation(inRightAnimation);
			}
		});
		mLoginLayout.startAnimation(outLeftAnimation);
		mToRegistBtn.setVisibility(View.GONE);
		mToLoginBtn.setVisibility(View.VISIBLE);
	}
	
	@OnClick(R.id.to_login_btn)
	void toLogin(View view) {
		Animation outRightAnimation = AnimationUtils.loadAnimation(this, R.anim.login_out_from_right);
		outRightAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				mRegistLayout.setVisibility(View.GONE);
				Animation inLeftAnimation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.login_in_from_left);
				inLeftAnimation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					@Override
					public void onAnimationEnd(Animation animation) {
						mLoginLayout.setVisibility(View.VISIBLE);
					}
				});
				mLoginLayout.startAnimation(inLeftAnimation);
			}
		});
		mRegistLayout.startAnimation(outRightAnimation);
		mToRegistBtn.setVisibility(View.VISIBLE);
		mToLoginBtn.setVisibility(View.GONE);
	}
	
	@OnClick(R.id.login_btn)
	void login(View view) {
		String phone = mLoginEmail.getText().toString();
		String password = mLoginPassword.getText().toString();
		if(Util.isEmpty(phone)) {
			ToastUtils.showMessage(getApplicationContext(), R.string.phone_null);
			return;
		}
		if(Util.isEmpty(password)) {
			ToastUtils.showMessage(getApplicationContext(), R.string.password_null);
			return;
		}
		if(password.length() < 6) {
			ToastUtils.showMessage(getApplicationContext(), R.string.password_short);
			return;
		}
		if(!Util.isPhone(phone)) {
			ToastUtils.showMessage(getApplicationContext(), R.string.phone_invalid);
			return;
		}
		ProgressDialogUtils.showProgressDialog(this, getString(R.string.logining));
		mPresenter.login( phone, password);
	}
	
	@OnClick(R.id.regist_btn)
	void regist(View view) {
		mPhone = mRegistPhone.getText().toString();
		mPassword = mRegistPassword.getText().toString();
		if(Util.isEmpty(mPhone)) {
			ToastUtils.showMessage(getApplicationContext(), R.string.phone_null);
			return;
		}
		if(Util.isEmpty(mPassword)) {
			ToastUtils.showMessage(getApplicationContext(), R.string.password_null);
			return;
		}
		if(mPassword.length() < 6) {
			ToastUtils.showMessage(getApplicationContext(), R.string.password_short);
			return;
		}
		if(!Util.isPhone(mPhone)) {
			ToastUtils.showMessage(getApplicationContext(), R.string.phone_invalid);
			return;
		}
		mPresenter.regist(mPhone,mPassword);
		ProgressDialogUtils.showProgressDialog(this, getString(R.string.registing));
	}

	private void subscriptionEvent() {
		Subscription subscription = RxBus.getInstance().toObservable(Constants.USER, RxBusMessage.class)
				.subscribe(new Action1<RxBusMessage>() {
					@Override
					public void call(RxBusMessage message) {
						switch (message.getCode()) {
							case Constants.FAILURE: // 登录失败
								break;
							case Constants.SUCCESS: // 登录成功
								// 设置本地用户
								User user = (User)message.getObject();
								ReadClientApp.getInstance().currentUser = user;
								spUtil.putObject("user", user);
								finishWithAnimation();
								break;
							case Constants.FAILURE_2:
								break;
							case Constants.SUCCESS_2:
								// 设置本地用户
								User user1 = (User)message.getObject();
								ReadClientApp.getInstance().currentUser = user1;
								spUtil.putObject("user", user1);
								finishWithAnimation();
								break;
							case Constants.FAILURE_1: // 注册失败
								break;
							case Constants.SUCCESS_1: // 注册成功
								// 设置本地用户
								mPresenter.registSuccess((User)message.getObject());
								ProgressDialogUtils.dismiss();
								break;
                            case Constants.FAILURE_3: //已存在用户
                                ProgressDialogUtils.dismiss();
                                if("501".equals(message.getCode())) {
                                    ToastUtils.showMessageInCenter(LoginActivity.this, getString(R.string.phone_invalid));
                                } else if("502".equals(message.getCode())) {
                                    ToastUtils.showMessageInCenter(LoginActivity.this, getString(R.string.phone_exists));
                                } else {
                                    ToastUtils.showMessageInCenter(LoginActivity.this, "未知原因错误, 请重试");
                                }
                                break;
                            case Constants.SUCCESS_3: //未有用户使用该账号
                                ToastUtils.showMessageInCenter(LoginActivity.this,"响应成功");
                                mPresenter.regist( mPhone, mPassword);
                                ToastUtils.showMessageInCenter(LoginActivity.this,"注册成功");
                                break;
						}
						ProgressDialogUtils.dismiss();
					}
				});
		RxBus.getInstance().addSubscription(this,subscription);
	}
	public void showRegistSuccess(User user) {
		ReadClientApp.getInstance().currentUser = user;
		spUtil.putObject("user", user);
		Intent aintent = new Intent(this, CreateUserInfoActivity.class);
		startActivityWithAnimation(aintent);
		finishWithAnimation();
	}
	
	
	@OnClick(R.id.back)
	void back(View view) {
		finishWithAnimation();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	}
    
    @OnClick(R.id.btn_login_sina)
	void loginBySina(View view) {
	}
    @OnClick(R.id.forget_pwd)
    void findPwd(View view) {
    	Intent intent = new Intent(this, FindPwdActivity.class);
		startActivityWithAnimation(intent);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    }

	@Override
	protected void onDestroy() {
		RxBus.getInstance().unSubscribe(this);
        mPresenter.unsubscribe();
		super.onDestroy();
	}

	@Override
    protected void onResume() {
    	super.onResume();
    }
    
}
