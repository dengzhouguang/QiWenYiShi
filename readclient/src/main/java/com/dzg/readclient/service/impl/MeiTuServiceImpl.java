package com.dzg.readclient.service.impl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.dzg.readclient.api.JokeService;
import com.dzg.readclient.api.JokeServiceUtil;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.PageResult;
import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.service.IMeiTuService;
import com.dzg.readclient.utils.FastjsonUtil;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MeiTuServiceImpl implements IMeiTuService {
    private String TAG="MeiTuServiceImpl.class";
	private Context mContext;
	private CompositeSubscription mCompositeSubscription;
	public MeiTuServiceImpl(Context context) {
		mContext = context;
		mCompositeSubscription=new CompositeSubscription();
	}
	private JokeService service= JokeServiceUtil.getJokeService();

	@Override
	public void getMeiTu(final Handler handler, int newOrHotFlag, int offset,
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
		Subscription subscribe = service.getMeitu(newOrHotFlag, offset, count)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
		mCompositeSubscription.add(subscribe);
	}

	@Override
	public void refresh(final Handler handler, int newOrHotFlag, int count) {
		Observer<ResponseInfo> observer = new Observer<ResponseInfo>() {
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
						handler.sendEmptyMessage(Constants.FAILURE);
						return;
					}
					PageResult page=FastjsonUtil.parseObject(res.getData(),PageResult.class);
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putSerializable("pageResult", page);
					message.what = Constants.SUCCESS_1;
					message.setData(bundle);
					handler.sendMessage(message);
				} catch (Exception e) {
					handler.sendEmptyMessage(Constants.FAILURE);
				}
			}
		};
		Subscription subscribe = service.getMeitu(newOrHotFlag, 0, count)
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
