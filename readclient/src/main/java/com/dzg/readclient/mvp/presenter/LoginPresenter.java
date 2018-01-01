package com.dzg.readclient.mvp.presenter;

import android.content.Context;
import android.util.Log;

import com.dzg.readclient.R;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.mvp.contract.LoginContract;
import com.dzg.readclient.mvp.model.User;
import com.dzg.readclient.mvp.usecase.GetLogin;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
import com.dzg.readclient.utils.FastjsonUtil;
import com.dzg.readclient.utils.HttpUtil;
import com.dzg.readclient.utils.ToastUtils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2017/8/22 0022.
 */

public class LoginPresenter implements LoginContract.Presenter {
    private static final String TAG = LoginPresenter.class.getName();
    LoginContract.View mView;
    CompositeSubscription mCompositeSubscription;
    GetLogin mUsercase;
    private Context mContext;

    public LoginPresenter(GetLogin mUsercase, Context mContext) {
        this.mUsercase = mUsercase;
        this.mContext = mContext;
        this.mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void attachView(LoginContract.View view) {
        mView = view;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mCompositeSubscription.clear();
    }

    @Override
    public void regist(String phone, String password) {
        Subscription subscription = mUsercase.execute(new GetLogin.RequestValues(Constants.REGIST, phone, password)).getResponseInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        RxBusMessage message = new RxBusMessage();
                        message.setCode(Constants.FAILURE_1);
                        RxBus.getInstance().post(Constants.USER, message);
                        if (!HttpUtil.isNetworkAvailable(mContext)) {
                            ToastUtils.showMessage(mContext, R.string.no_net);
                        } else {
                            ToastUtils.showMessage(mContext, R.string.regist_fail);
                        }
                    }

                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        try {
                            int code = Integer.valueOf(responseInfo.getCode());
                            if (code != 200) {
                                //请求失败
                                RxBusMessage message = new RxBusMessage();
                                message.setCode(Constants.FAILURE_1);
                                RxBus.getInstance().post(Constants.USER, message);
                                Log.e(TAG, "regist failure");
                                if (code == 502) {  //用户名存在
                                    ToastUtils.showMessage(mContext, R.string.phone_exists);
                                }
                                if (code == 504) {  //注册失败
                                    ToastUtils.showMessage(mContext, R.string.regist_fail);
                                }
                                return;
                            }
                            Log.e(TAG, "regist success");
                            User user = FastjsonUtil.parseObject(responseInfo.getData(), User.class);
                            RxBusMessage message = new RxBusMessage();
                            message.setCode(Constants.SUCCESS_1);
                            message.setObject(user);
                            RxBus.getInstance().post(Constants.USER, message);
                        } catch (Exception e) {
                            RxBusMessage message = new RxBusMessage();
                            message.setCode(Constants.FAILURE);
                            RxBus.getInstance().post(Constants.USER, message);
                        }
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void login(String phone, String password) {
        Subscription subscription = mUsercase.execute(new GetLogin.RequestValues(Constants.LOGIN, phone, password)).getResponseInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "login failure", e);
                        RxBusMessage message = new RxBusMessage();
                        message.setCode(Constants.FAILURE);
                        RxBus.getInstance().post(Constants.USER, message);
                        if (!HttpUtil.isNetworkAvailable(mContext)) {
                            ToastUtils.showMessage(mContext, R.string.no_net);
                        } else {
                            ToastUtils.showMessage(mContext, R.string.login_fail);
                        }
                    }

                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        int code = Integer.valueOf(responseInfo.getCode());
                        if (code != 200) {
                            //请求失败
                            RxBusMessage message = new RxBusMessage();
                            message.setCode(Constants.FAILURE);
                            RxBus.getInstance().post(Constants.USER, message);
                            Log.e(TAG, "regist failure");
                            if (code == 503) {  //用户名不存在
                                ToastUtils.showMessage(mContext, R.string.no_account);
                                return;
                            }
                            if (code == 504) {  //登录密码错误
                                ToastUtils.showMessage(mContext, R.string.password_error);
                                return;
                            }
                            ToastUtils.showMessage(mContext, R.string.login_fail);
                        }
                        Log.e(TAG, "login success");
                        User user = FastjsonUtil.parseObject(responseInfo.getData(), User.class);
                        RxBusMessage message = new RxBusMessage();
                        message.setCode(Constants.SUCCESS);
                        message.setObject(user);
                        RxBus.getInstance().post(Constants.USER, message);
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void checkUserExeist(String phone) {
        Subscription subscription = mUsercase.execute(new GetLogin.RequestValues(Constants.CHECKUSEREXEIST, phone))
                .getResponseInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "checkUserExeist error", e);
                        if (!HttpUtil.isNetworkAvailable(mContext)) {
                            ToastUtils.showMessage(mContext, R.string.no_net);
                        } else {
                            RxBusMessage message = new RxBusMessage(Constants.DEFAULT, null);
                            RxBus.getInstance().post(Constants.USER, message);
                        }
                    }

                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        int code = Integer.valueOf(responseInfo.getCode());
                        if (code == Constants.SUCCESS) {
                            Log.e(TAG, "checkUserExeist success");
                            RxBus.getInstance().post(Constants.USER, new RxBusMessage(code, null));
                        } else {
                            Log.e(TAG, "checkUserExeist fail, fail msg is " + responseInfo.getDesc());
                            RxBus.getInstance().post(Constants.USER, new RxBusMessage(code, null));
                        }

                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void registSuccess(User user) {
        mView.showRegistSuccess(user);
    }
}
