package com.dzg.readclient.mvp.presenter;

import android.content.Context;

import com.dzg.readclient.R;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.mvp.contract.UpdateNickContract;
import com.dzg.readclient.mvp.usecase.GetUpdateNick;
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
 * Created by Administrator on 2017/8/25 0025.
 */

public class UpdateNickPresenter implements UpdateNickContract.Presenter {
    private CompositeSubscription mCompositeSubscription;
    private UpdateNickContract.View mView;
    private GetUpdateNick mUsecase;
    private Context mContext;

    public UpdateNickPresenter(GetUpdateNick mUsecase, Context mContext) {
        this.mUsecase = mUsecase;
        this.mContext = mContext;
        this.mCompositeSubscription=new CompositeSubscription();
    }

    @Override
    public void attachView(UpdateNickContract.View view) {
        mView=view;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
     mCompositeSubscription.clear();
    }

    @Override
    public void setNickAndSex(String userId, String nickName, String sex) {
       Subscription subscription=mUsecase.execute(new GetUpdateNick.RequestValues(userId,nickName,sex)).getResponseInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        RxBusMessage message = new RxBusMessage();
                        message.setCode(Constants.FAILURE_2);
                        RxBus.getInstance().post(Constants.UPDATE, message);
                        if (!HttpUtil.isNetworkAvailable(mContext)) {
                            ToastUtils.showMessage(mContext, R.string.no_net);
                        } else {
                            ToastUtils.showMessage(mContext,
                                    R.string.set_nick_sex_fial);
                        }
                    }

                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        int code = Integer.valueOf(responseInfo.getCode());
                        if (code == Constants.SUCCESS) {
                            RxBusMessage message = new RxBusMessage();
                            message.setCode(Constants.SUCCESS_2);
                            RxBus.getInstance().post(Constants.UPDATE, message);
                        } else {
                            RxBusMessage message = new RxBusMessage();
                            message.setCode(Constants.FAILURE_2);
                            RxBus.getInstance().post(Constants.UPDATE, message);
                            ToastUtils.showMessage(mContext,
                                    R.string.set_nick_sex_fial);
                        }
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void setNickSuccess() {
        mView.showNickSuccess();
    }

    @Override
    public void init() {
        mView.showContent();
    }
}
