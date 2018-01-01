package com.dzg.readclient.mvp.presenter;

import android.content.Context;
import android.util.Log;

import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.PageResult;
import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.dao.DingCaiDAO;
import com.dzg.readclient.mvp.contract.TuDetailContract;
import com.dzg.readclient.mvp.model.DingOrCai;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.mvp.usecase.GetTuDetail;
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
 * Created by Administrator on 2017/8/25 0025.
 */

public class TuDetailPresenter implements TuDetailContract.Presenter {
    private static final String TAG =TuDetailPresenter.class.getName() ;
    private CompositeSubscription mCompositeSubscription;
    private TuDetailContract.View mView;
    private GetTuDetail mUsecase;
    private Context mContext;
    private DingCaiDAO mDingCaiDao;
    public TuDetailPresenter(GetTuDetail mUsecase,Context context) {
        this.mUsecase = mUsecase;
        this.mContext=context;
        this.mDingCaiDao=new DingCaiDAO();
        this.mCompositeSubscription=new CompositeSubscription();
    }

    @Override
    public void attachView(TuDetailContract.View view) {
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
    public void getJokeById(Integer jokeId) {
        Subscription subscription = mUsecase.execute(new GetTuDetail.RequestValues(Constants.GETJOKEBYID, jokeId)).getResponseInfo()
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
        Subscription subscription = mUsecase.execute(new GetTuDetail.RequestValues(Constants.GETCOMMENTBYJOKEID, jokeId, offset, count)).getResponseInfo()
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
        Subscription subscription = mUsecase.execute(new GetTuDetail.RequestValues(Constants.ADDCOMMENT, jokeId, content)).getResponseInfo()
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
        return mDingCaiDao.getDingOrCai(userId, jokeId);
    }

    @Override
    public void dingOrCai(int userId, int jokeId, int dingOrCai, int num) {
        mDingCaiDao.dingOrCai(userId, jokeId, dingOrCai, num);
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
    public void scroll2Target() {
        mView.show2Target();
    }

    @Override
    public void reSet() {
        mView.showAndReSet();
    }

    @Override
    public void commentSuccess() {
        mView.showCommentSuccess();
    }

    @Override
    public void setTuContent() {
        mView.showTuContent();
    }

    @Override
    public void initTuContent() {
    mView.showContent();
    }
}
