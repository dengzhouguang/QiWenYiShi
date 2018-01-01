package com.dzg.readclient.service.impl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;;

import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.api.JokeService;
import com.dzg.readclient.api.JokeServiceUtil;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.PageResult;
import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.service.IQuTuService;
import com.dzg.readclient.utils.FastjsonUtil;
import com.dzg.readclient.utils.FileUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class QuTuServiceImpl implements IQuTuService {

	private Context mContext;
	private CompositeSubscription mCompositeSubscription;
	public QuTuServiceImpl(Context context) {
		mContext = context;
		mCompositeSubscription=new CompositeSubscription();
	}
	private JokeService service= JokeServiceUtil.getJokeService();
	@Override
	public void getQuTu(final Handler handler, int newOrHotFlag, int offset,
			int count) {
		Observer<ResponseInfo> observer = new Observer<ResponseInfo>() {
			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable e) {
				handler.sendEmptyMessage(Constants.FAILURE);
			}

			@Override
			public void onNext(ResponseInfo res) {
				int code = Integer.parseInt(res.getCode());
				if (code != 200) {
					//请求失败
					handler.sendEmptyMessage(Constants.FAILURE);
					return;
				}
				PageResult page= FastjsonUtil.parseObject(res.getData(),PageResult.class);
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putSerializable("pageResult", page);
				message.what = Constants.SUCCESS;
				message.setData(bundle);
				handler.sendMessage(message);
			}
		};
		Subscription subscribe = service.getQutu(newOrHotFlag, offset, count)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
		mCompositeSubscription.add(subscribe);
	}

	@Override
	public void refush(final Handler handler, int newOrHotFlag, int count) {
		Observer<ResponseInfo> observer = new Observer<ResponseInfo>() {
			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable e) {
				handler.sendEmptyMessage(Constants.FAILURE);
			}

			@Override
			public void onNext(ResponseInfo res) {
				int code = Integer.parseInt(res.getCode());
				if (code != 200) {
					//请求失败
					handler.sendEmptyMessage(Constants.FAILURE);
					return;
				}
				PageResult page=FastjsonUtil.parseObject(res.getData(),PageResult.class);
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putSerializable("pageResult", page);
				message.what = Constants.SUCCESS;
				message.setData(bundle);
				handler.sendMessage(message);
			}
		};
		Subscription subscribe = service.getQutu(newOrHotFlag, 0, count)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
		mCompositeSubscription.add(subscribe);
	}
	public void removeSubscription() {
		if (this.mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
			this.mCompositeSubscription.unsubscribe();
		}
	}

}
