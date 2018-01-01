package com.dzg.readclient.mvp.presenter;

import android.content.Context;
import android.util.Log;

import com.dzg.readclient.R;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.mvp.contract.FeedbackContract;
import com.dzg.readclient.mvp.usecase.GetFeedback;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
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

public class FeedbackPresenter implements FeedbackContract.Presenter {
    private static final String TAG = FeedbackPresenter.class.getName();
    FeedbackContract.View mView;
    CompositeSubscription mCompositeSubscription;
    GetFeedback mUsercase;
    Context mContext;

    public FeedbackPresenter(GetFeedback mUsercase, Context context) {
        this.mUsercase = mUsercase;
        this.mContext = context;
        this.mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void attachView(FeedbackContract.View view) {
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
    public void feedback(String content, String contact, String s) {
        Subscription subscription = mUsercase.execute(new GetFeedback.RequestValues(content, contact, s)).getResponseInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "feedback error", e);
                        RxBusMessage message = new RxBusMessage();
                        message.setCode(Constants.FAILURE);
                        RxBus.getInstance().post(Constants.FEEDBACK, message);
                        if (!HttpUtil.isNetworkAvailable(mContext)) {
                            ToastUtils.showMessage(mContext, R.string.no_net);
                        } else {
                            ToastUtils.showMessage(mContext,
                                    R.string.feedback_fail);
                        }
                    }

                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        int code = Integer.valueOf(responseInfo.getCode());
                        if (code == Constants.SUCCESS) {
                            Log.e(TAG, "feedback success");
                            ToastUtils.showMessage(mContext,
                                    R.string.feedback_success);
                            RxBusMessage message = new RxBusMessage();
                            message.setCode(Constants.SUCCESS);
                            RxBus.getInstance().post(Constants.FEEDBACK, message);
                        } else {
                            Log.e(TAG, "feedback fail, fail msg is " + responseInfo.getDesc());
                            RxBusMessage message = new RxBusMessage();
                            message.setCode(Constants.FAILURE);
                            RxBus.getInstance().post(Constants.FEEDBACK, message);
                            ToastUtils.showMessage(mContext,
                                    R.string.feedback_fail);
                        }

                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void feedbackSuccess() {
        mView.showFeedbackSuccess();
    }

    @Override
    public void toBack() {
        mView.showBack();
    }
}
