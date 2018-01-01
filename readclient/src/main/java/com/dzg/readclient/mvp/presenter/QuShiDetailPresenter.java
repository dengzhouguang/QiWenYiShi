package com.dzg.readclient.mvp.presenter;

import android.content.Context;
import android.util.Log;

import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.PageResult;
import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.dao.DingCaiDAO;
import com.dzg.readclient.mvp.contract.QuShiDetailContract;
import com.dzg.readclient.mvp.model.DingOrCai;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.mvp.usecase.GetQuShiDetail;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
import com.dzg.readclient.utils.FastjsonUtil;
import com.dzg.readclient.utils.HttpUtil;
import com.dzg.readclient.utils.ToastUtils;
import com.dzg.readclient.utils.Util;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2017/8/23 0023.
 */

public class QuShiDetailPresenter implements QuShiDetailContract.Presenter {
    private static final String TAG = QuShiDetailPresenter.class.getName();
    QuShiDetailContract.View mView;
    CompositeSubscription mCompositeSubscription;
    GetQuShiDetail mUsecase;
    private DingCaiDAO mDingOrCaiDAO;
    private Context mContext;

    public QuShiDetailPresenter(GetQuShiDetail mUsecase, Context context) {
        this.mDingOrCaiDAO = new DingCaiDAO();
        this.mUsecase = mUsecase;
        this.mCompositeSubscription = new CompositeSubscription();
        this.mContext = context;
    }

    @Override
    public void attachView(QuShiDetailContract.View view) {
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
    public void getJokeById(Integer jokeId) {
        Subscription subscription = mUsecase.execute(new GetQuShiDetail.RequestValues(Constants.GETJOKEBYID, jokeId)).getResponseInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "getJokeById error", e);
                        if (!HttpUtil.isNetworkAvailable(mContext)) {
                            ToastUtils.showMessage(mContext, R.string.no_net);
                        } else {
                            RxBus.getInstance().post(Constants.COMMENT, new RxBusMessage(Constants.FAILURE_2, null));
                        }
                    }

                    @Override
                    public void onNext(ResponseInfo res) {
                        int code = Integer.parseInt(res.getCode());
                        if (code == Constants.SUCCESS) {
                            Log.e(TAG, "getJokeById success");
                            Joke joke = FastjsonUtil.parseObject(res.getData(), Joke.class);
                            RxBus.getInstance().post(Constants.COMMENT, new RxBusMessage(Constants.SUCCESS_2, joke));
                        } else {
                            Log.e(TAG, "getJokeById fail, fail msg is " + res.getDesc());
                            RxBus.getInstance().post(Constants.COMMENT, new RxBusMessage(Constants.FAILURE_2, null));
                        }
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void getCommentByJokeId(int jokeId, int offset, int count) {
        Subscription subscription = mUsecase.execute(new GetQuShiDetail.RequestValues(Constants.GETCOMMENTBYJOKEID, jokeId, offset, count)).getResponseInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(ResponseInfo res) {
                        try {
                            int code = Integer.valueOf(res.getCode());
                            if (code != 200) {
                                //请求失败
                                RxBusMessage message = new RxBusMessage();
                                message.setCode(Constants.FAILURE);
                                RxBus.getInstance().post(Constants.COMMENT, message);
                                return;
                            }
                            PageResult page = FastjsonUtil.parseObject(res.getData(), PageResult.class);
                            RxBusMessage message = new RxBusMessage();
                            message.setCode(Constants.SUCCESS);
                            message.setObject(page);
                            RxBus.getInstance().post(Constants.COMMENT, message);
                        } catch (Exception e) {
                            RxBusMessage message = new RxBusMessage();
                            message.setCode(Constants.FAILURE);
                            RxBus.getInstance().post(Constants.COMMENT, message);
                        }
                    }
                });
        mCompositeSubscription.add(subscription);

    }

    @Override
    public void addComment(Integer jokeId, String content) {
        if (ReadClientApp.getInstance().currentUser == null || Util.isEmpty(content)) {
            ToastUtils.showMessage(mContext, "请先进行登录操作！！！");
            return;
        }
        Subscription subscription = mUsecase.execute(new GetQuShiDetail.RequestValues(Constants.ADDCOMMENT, jokeId, content)).getResponseInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "comment failure", e);
                        RxBusMessage message = new RxBusMessage();
                        message.setCode(Constants.FAILURE_1);
                        RxBus.getInstance().post(Constants.COMMENT, message);
                        if (!HttpUtil.isNetworkAvailable(mContext)) {
                            ToastUtils.showMessage(mContext, R.string.no_net);
                        } else {
                            ToastUtils.showMessage(mContext, R.string.comment_fail);
                        }
                    }

                    @Override
                    public void onNext(ResponseInfo res) {
                        int code = Integer.parseInt(res.getCode());
                        if (code != 200) {
                            //请求失败
                            Log.e(TAG, "comment failure");
                            ToastUtils.showMessage(mContext, R.string.comment_fail);
                            RxBusMessage message = new RxBusMessage();
                            message.setCode(Constants.FAILURE_1);
                            RxBus.getInstance().post(Constants.COMMENT, message);
                            return;
                        }
                        Log.e(TAG, "comment success");
                        RxBusMessage message = new RxBusMessage();
                        message.setCode(Constants.SUCCESS_1);
                        RxBus.getInstance().post(Constants.COMMENT, message);
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public DingOrCai getDingOrCai(int userId, int jokeId) {
        return mDingOrCaiDAO.getDingOrCai(userId, jokeId);
    }

    @Override
    public void dingOrCai(int userId, int jokeId, int dingOrCai, int num) {
        mDingOrCaiDAO.dingOrCai(userId, jokeId, dingOrCai, num);
    }

    @Override
    public void reSet() {
        mView.showAndReSet();
    }

    @Override
    public void initQuShiContent() {
        mView.showQuShiContent();
    }

    @Override
    public void setQuShiContent() {
        mView.showSetQushiContent();
    }

    @Override
    public void loadComments() {
        mView.showComments();
    }

    @Override
    public void loadSuccess(PageResult pageResult) {
        mView.showLoadSuccess(pageResult);
    }

    @Override
    public void commentSuccess() {
        mView.showCommentSuccess();
    }
}
