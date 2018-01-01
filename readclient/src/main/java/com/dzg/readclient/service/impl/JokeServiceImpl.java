package com.dzg.readclient.service.impl;

import android.content.Context;
import android.util.Log;

import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.api.JokeService;
import com.dzg.readclient.api.JokeServiceUtil;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.PageResult;
import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.dao.DingCaiDAO;
import com.dzg.readclient.mvp.model.DingOrCai;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
import com.dzg.readclient.service.IJokeService;
import com.dzg.readclient.utils.FastjsonUtil;
import com.dzg.readclient.utils.HttpUtil;
import com.dzg.readclient.utils.TaskExecutor;
import com.dzg.readclient.utils.ToastUtils;
import com.dzg.readclient.utils.Util;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class JokeServiceImpl implements IJokeService {
	private static final String TAG = "QuShiServiceImpl";
	private Context mContext;
	private DingCaiDAO mDingCaiDAO;
	private JokeService service= JokeServiceUtil.getJokeService();
	private CompositeSubscription mCompositeSubscription;
	public JokeServiceImpl(Context context) {
        mContext = context;
        mDingCaiDAO = new DingCaiDAO();
		mCompositeSubscription=new CompositeSubscription();
    }
	@Override
	public void getAll( int newOrHotFlag, int offset, int count) {
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
//						handler.ssage(Constants.FAILURE);
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
		};
		Subscription subscribe = service.getList(newOrHotFlag, 1, offset, count)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
		mCompositeSubscription.add(subscribe);

	}

	@Override
	public void refreshAll( int newOrHotFlag, int count) {
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
						RxBusMessage message=new RxBusMessage();
						message.setCode(Constants.FAILURE);
						RxBus.getInstance().post(Constants.AllFRAGMENT, message);
						return;
					}
					PageResult page=FastjsonUtil.parseObject(res.getData(),PageResult.class);
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
		};
		Subscription subscribe = service.getList(newOrHotFlag, 1, 0, count)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
		mCompositeSubscription.add(subscribe);
	}

	@Override
	public void getList( int type, int newOrHotFlag, int offset,
						int count) {
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
						RxBusMessage message=new RxBusMessage();
						message.setCode(Constants.FAILURE);
						RxBus.getInstance().post(Constants.AllFRAGMENT, message);
						return;
					}
					PageResult page=FastjsonUtil.parseObject(res.getData(),PageResult.class);
					RxBusMessage message=new RxBusMessage();
					message.setCode(Constants.SUCCESS);
					message.setObject(page);
					RxBus.getInstance().post(Constants.AllFRAGMENT, message);
				} catch (Exception e) {
					RxBusMessage message=new RxBusMessage();
					message.setCode(Constants.FAILURE);
					RxBus.getInstance().post(Constants.AllFRAGMENT, message);
				}
			}
		};
		Subscription subscribe = service.getList(newOrHotFlag, type, offset, count)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
		mCompositeSubscription.add(subscribe);
	}


	@Override
	public void reflush( int type, int newOrHotFlag, int count) {
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
						RxBusMessage message=new RxBusMessage();
						message.setCode(Constants.FAILURE);
						RxBus.getInstance().post(Constants.AllFRAGMENT, message);
						return;
					}
					PageResult page=FastjsonUtil.parseObject(res.getData(),PageResult.class);
					RxBusMessage message=new RxBusMessage();
					message.setCode(Constants.SUCCESS);
					message.setObject(page);
					RxBus.getInstance().post(Constants.AllFRAGMENT, message);
				} catch (Exception e) {
					RxBusMessage message=new RxBusMessage();
					message.setCode(Constants.FAILURE);
					RxBus.getInstance().post(Constants.AllFRAGMENT, message);
				}
			}
		};;
		Subscription subscribe = service.getList(newOrHotFlag, type, 0, count)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
		mCompositeSubscription.add(subscribe);
	}

	@Override
	public void getCommentByJokeId( int jokeId, int offset,
								   int count) {
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
						RxBusMessage message=new RxBusMessage();
						message.setCode(Constants.FAILURE);
						RxBus.getInstance().post(Constants.COMMENT, message);
						return;
					}
					PageResult page=FastjsonUtil.parseObject(res.getData(),PageResult.class);
					RxBusMessage message=new RxBusMessage();
					message.setCode(Constants.SUCCESS);
					message.setObject(page);
					RxBus.getInstance().post(Constants.COMMENT, message);
				} catch (Exception e) {
					RxBusMessage message=new RxBusMessage();
					message.setCode(Constants.FAILURE);
					RxBus.getInstance().post(Constants.COMMENT, message);
				}
			}
		};
		Subscription subscribe = service.getComments(jokeId, offset, count)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
		mCompositeSubscription.add(subscribe);
	}

	@Override
	public void ding(final List<DingOrCai> dingOrCais) {
		if(dingOrCais == null || dingOrCais.size() == 0) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (DingOrCai dingOrCai : dingOrCais) {
			sb.append(dingOrCai.getJokeId()).append(",");
		}
		String ids = sb.toString().substring(0, sb.toString().length() - 1);

		Observer<ResponseInfo> observer = new Observer<ResponseInfo>() {
			@Override
			public void onCompleted() {
;
			}

			@Override
			public void onError(Throwable e) {
				Log.e(TAG, "ding failure", e);
			}

			@Override
			public void onNext(ResponseInfo res) {
				int code = Integer.parseInt(res.getCode());
				if (code != 200) {
					//请求失败
					Log.e(TAG, "ding failure:" +res.getDesc());
					return;
				}
				//修改本地数据库
				TaskExecutor.executeTask(new Runnable() {
					@Override
					public void run() {
						mDingCaiDAO.upload(dingOrCais);
					}
				});
			}
		};
		Subscription subscribe = service.postDing(ids)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
		mCompositeSubscription.add(subscribe);
	}

	@Override
	public void cai(final List<DingOrCai> dingOrCais) {
		if(dingOrCais == null || dingOrCais.size() == 0) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (DingOrCai dingOrCai : dingOrCais) {
			sb.append(dingOrCai.getJokeId()).append(",");
		}
		String ids = sb.toString().substring(0, sb.toString().length() - 1);
		Observer<ResponseInfo> observer = new Observer<ResponseInfo>() {
			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable e) {
				Log.e(TAG, "cai failure", e);
			}

			@Override
			public void onNext(ResponseInfo res) {
				int code = Integer.parseInt(res.getCode());
				if (code != 200) {
					//请求失败
					Log.e(TAG, "cai failure:" +res.getDesc());
					return;
				}
				//修改本地数据库
				TaskExecutor.executeTask(new Runnable() {
					@Override
					public void run() {
						mDingCaiDAO.upload(dingOrCais);
					}
				});
			}
		};
		Subscription subscribe = service.postCai(ids)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
		mCompositeSubscription.add(subscribe);
	}

	@Override
	public void addComment(Integer jokeId, String content) {
		if(ReadClientApp.getInstance().currentUser == null || Util.isEmpty(content)) {
			ToastUtils.showMessage(mContext,"请先进行登录操作！！！");
			return;
		}
		Observer<ResponseInfo> observer = new Observer<ResponseInfo>() {
			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable e) {
				Log.e(TAG, "comment failure", e);
				RxBusMessage message=new RxBusMessage();
				message.setCode(Constants.FAILURE_1);
				RxBus.getInstance().post(Constants.COMMENT, message);
				if(!HttpUtil.isNetworkAvailable(mContext)) {
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
					RxBusMessage message=new RxBusMessage();
					message.setCode(Constants.FAILURE_1);
					RxBus.getInstance().post(Constants.COMMENT, message);
					return;
				}
				Log.e(TAG, "comment success");
				RxBusMessage message=new RxBusMessage();
				message.setCode(Constants.SUCCESS_1);
				RxBus.getInstance().post(Constants.COMMENT, message);
			}
		};
		Subscription subscribe = service.postComment(jokeId, ReadClientApp.getInstance().currentUser.getId(), ReadClientApp.getInstance().currentUser.getPortraitUrl(), ReadClientApp.getInstance().currentUser.getUserNike(), content)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
		mCompositeSubscription.add(subscribe);
	}

	@Override
	public void getJokeById(Integer jokeId) {
		Observer<ResponseInfo> observer = new Observer<ResponseInfo>() {
			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable e) {
				Log.e(TAG, "getJokeById error", e);
				if(!HttpUtil.isNetworkAvailable(mContext)) {
					ToastUtils.showMessage(mContext, R.string.no_net);
				} else {
					RxBus.getInstance().post(Constants.COMMENT,new RxBusMessage(Constants.FAILURE_2,null));
				}
			}

			@Override
			public void onNext(ResponseInfo res) {
				int code = Integer.parseInt(res.getCode());
				if(code == Constants.SUCCESS) {
					Log.e(TAG, "getJokeById success");
					Joke joke=FastjsonUtil.parseObject(res.getData(),Joke.class);
					RxBus.getInstance().post(Constants.COMMENT,new RxBusMessage(Constants.SUCCESS_2,joke));
				} else {
					Log.e(TAG, "getJokeById fail, fail msg is " +res.getDesc());
					RxBus.getInstance().post(Constants.COMMENT,new RxBusMessage(Constants.FAILURE_2,null));
				}
			}
		};
		Subscription subscribe = service.getJokeById(jokeId)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
		mCompositeSubscription.add(subscribe);
	}
	@Override
	public void removeSubscription() {
		if (this.mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
			this.mCompositeSubscription.unsubscribe();
		}
	}
}
