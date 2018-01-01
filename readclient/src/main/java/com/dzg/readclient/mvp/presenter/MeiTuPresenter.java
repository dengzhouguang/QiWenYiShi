package com.dzg.readclient.mvp.presenter;

import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.PageResult;
import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.mvp.contract.AllContract;
import com.dzg.readclient.mvp.contract.MeiTuContract;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.mvp.usecase.GetAll;
import com.dzg.readclient.mvp.usecase.GetMeiTu;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
import com.dzg.readclient.utils.FastjsonUtil;
import com.dzg.readclient.utils.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2017/8/17 0017.
 */

public class MeiTuPresenter implements MeiTuContract.Presenter {
    private GetMeiTu mUsecase;
    private MeiTuContract.View mView;
    private CompositeSubscription mCompositeSubscription;

    @Override
    public void attachView(MeiTuContract.View view) {
        mView = view;
    }

    @Override
    public void subscribe() {

    }

    public MeiTuPresenter(GetMeiTu mUsecase) {
        this.mUsecase = mUsecase;
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void unsubscribe() {
        mCompositeSubscription.clear();

    }

    @Override
    public void loadData() {
        if (HttpUtil.isNetworkAvailable(ReadClientApp.getInstance())) {
            loadMeiTu();
        } else {
            mView.showFromCache();
        }
    }

    @Override
    public void clearListsAndInitPageInfo() {
        mView.clearListsAndInitPageInfo();
    }

    @Override
    public void loadMeiTu() {
    mView.showMeiTu();
    }

    @Override
    public void loadSuccess(PageResult pageResult) {
    mView.showSuccess(pageResult);
    }
    @Override
    public void saveCache(ArrayList<Joke> jokeLists, boolean hasNext, int pageId, String key) {
        HashMap<String, Object> cache = new HashMap<String, Object>();
        cache.put("data", jokeLists);
        cache.put("hasNext", hasNext);
        cache.put("pageId", pageId);
        ReadClientApp.getInstance().getInstance().getSpUtil().putObject(key, cache);
    }

    @Override
    public void getAll(int newOrHotFlag, int offset, int count) {
        Subscription subscription=mUsecase.execute(new GetMeiTu.RequestValues(newOrHotFlag,Joke.TYPE_MEITU,offset,count))
                .getList()
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
                                RxBusMessage message=new RxBusMessage();
                                message.setCode(Constants.FAILURE);
                                RxBus.getInstance().post(Constants.AllFRAGMENT, message);
                                return;
                            }
                            PageResult page=FastjsonUtil.parseObject(res.getData(),PageResult.class);
                            RxBusMessage message = new RxBusMessage();
                            message.setCode( Constants.SUCCESS);
                            message.setObject(page);
                            RxBus.getInstance().post(Constants.AllFRAGMENT, message);
                        } catch (Exception e) {
                            RxBusMessage message=new RxBusMessage();
                            message.setCode(Constants.FAILURE);
                            RxBus.getInstance().post(Constants.AllFRAGMENT, message);
                        }
                    }
                });
        mCompositeSubscription.add(subscription);
    }
    @Override
    public void reflush(int newOrHotFlag, int count) {
        Subscription subscribe =mUsecase.execute(new GetMeiTu.RequestValues(newOrHotFlag,count)).getList()
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
                                RxBusMessage message=new RxBusMessage();
                                message.setCode(Constants.FAILURE);
                                RxBus.getInstance().post(Constants.AllFRAGMENT, message);
                                return;
                            }
                            PageResult page= FastjsonUtil.parseObject(res.getData(),PageResult.class);
                            RxBusMessage message=new RxBusMessage();
                            message.setCode(Constants.SUCCESS_1);
                            message.setObject(page);
                            RxBus.getInstance().post(Constants.AllFRAGMENT, message);
                        } catch (Exception e) {
                            RxBusMessage message=new RxBusMessage();
                            message.setCode(Constants.FAILURE);
                            RxBus.getInstance().post(Constants.AllFRAGMENT, message);
                        }
                    }
                });
        mCompositeSubscription.add(subscribe);
    }
    @Override
    public void loadComplete() {
        mView.showComplete();
    }
}
