package com.dzg.readclient.service.impl;

import android.content.Context;
import android.util.Log;


import com.dzg.readclient.R;
import com.dzg.readclient.api.ApiCallBack;
import com.dzg.readclient.api.JokeService;
import com.dzg.readclient.api.JokeServiceUtil;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.mvp.model.User;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
import com.dzg.readclient.service.IUserService;
import com.dzg.readclient.utils.FastjsonUtil;
import com.dzg.readclient.utils.HttpUtil;
import com.dzg.readclient.utils.ToastUtils;
import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class UserServiceImpl implements IUserService {

    private static final String TAG = "UserServiceImpl";
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;

    public UserServiceImpl(Context context) {
        this.mContext = context;
        mCompositeSubscription = new CompositeSubscription();
    }

    private JokeService service = JokeServiceUtil.getJokeService();
    /*@Override
	public void addTencentUser( String openId, String portraitUrl, String nickName,
			int sex) {
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(1000 * 60 * 10); //缓存超期时间10分钟
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("openId", openId);
		params.addQueryStringParameter("portraitUrl", portraitUrl);
		try {
			params.addQueryStringParameter("nickName", URLEncoder.encode(nickName, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		params.addQueryStringParameter("sex", String.valueOf(sex));
		http.send(HttpRequest.HttpMethod.POST,
			RcpUri.INTERFACE_URI_ADD_TENCENT_USER, params,
			new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if (code != 200) {
						//请求失败
						handler.sendEmptyMessage(Constants.FAILURE);
						Log.e(TAG, "save user failure");
						return;
					}
					Log.e(TAG, "save user success");
					User user = FastjsonUtil.deserialize(
							map.get("data").toString(), User.class);
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putSerializable("user", user);
					message.what = Constants.SUCCESS;
					message.setData(bundle);
					handler.sendMessage(message);
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "save user failure", error);
					handler.sendEmptyMessage(Constants.FAILURE);
				}
			});
	}
	
	@Override
	public void addSinaUser( String uid, String portraitUrl,
			String nickName) {
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(1000 * 60 * 10); //缓存超期时间10分钟
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("uid", uid);
		params.addQueryStringParameter("portraitUrl", portraitUrl);
		try {
			params.addQueryStringParameter("nickName", URLEncoder.encode(nickName, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		http.send(HttpRequest.HttpMethod.POST,
			RcpUri.INTERFACE_URI_ADD_SINA_USER, params,
			new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if (code != 200) {
						//请求失败
						if(handler != null) {
							handler.sendEmptyMessage(Constants.FAILURE_2);
						}
						Log.e(TAG, "save user failure");
						return;
					}
					Log.e(TAG, "save user success");
					User user = FastjsonUtil.deserialize(
							map.get("data").toString(), User.class);
					if(handler != null) {
						Message message = new Message();
						Bundle bundle = new Bundle();
						bundle.putSerializable("user", user);
						message.what = Constants.SUCCESS_2;
						message.setData(bundle);
						handler.sendMessage(message);
					} else {
						App.currentUser = user;
						App.getInstance().getSpUtil().putObject("user", user);
					}
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "save user failure", error);
					if(handler != null) {
						handler.sendEmptyMessage(Constants.FAILURE_2);
					}
				}
			});
	}*/

    @Override
    public void regist(String phone, String password) {
        service.regist(phone, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        RxBusMessage message=new RxBusMessage();
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
                                RxBusMessage message=new RxBusMessage();
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
                            RxBusMessage message=new RxBusMessage();
                            message.setCode(Constants.SUCCESS_1);
                            message.setObject(user);
                            RxBus.getInstance().post(Constants.USER, message);
                        } catch (Exception e) {
                            RxBusMessage message=new RxBusMessage();
                            message.setCode(Constants.FAILURE);
                            RxBus.getInstance().post(Constants.USER, message);
                        }
                    }
                });
    }

    @Override
    public void regist(final ApiCallBack apiCallBack, String email, String password) {
        service.regist(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "regist failure", e);
                        if (!HttpUtil.isNetworkAvailable(mContext)) {
                            ToastUtils.showMessage(mContext, R.string.no_net);
                        } else {
                            apiCallBack.onError(new Exception(e), "regist failure");
                        }
                    }

                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        try {
                            int code = Integer.valueOf(responseInfo.getCode());
                            if (code != 200) {
                                //请求失败
                                apiCallBack.onFailure(responseInfo.getCode());
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
                            apiCallBack.onSuccess(user);
                        } catch (Exception e) {
                            apiCallBack.onFailure(e.getMessage());
                        }
                    }
                });
    }

    @Override
    public void login(String phone, String password) {
        service.login(phone, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "login failure", e);
                        RxBusMessage message=new RxBusMessage();
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
                            RxBusMessage message=new RxBusMessage();
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
                        RxBusMessage message=new RxBusMessage();
                        message.setCode(Constants.SUCCESS);
                        message.setObject(user);
                        RxBus.getInstance().post(Constants.USER, message);
                    }
                });
    }

    /*@Override
    public void getQiNiuToken(final Handler handler) {
        service.getQiNiuToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "get keyToken error", e);
                        handler.sendEmptyMessage(Constants.FAILURE);
                        if(!HttpUtil.isNetworkAvailable(mContext)) {
                            ToastUtils.showMessage(mContext, R.string.no_net);
                        } else {
                            ToastUtils.showMessage(mContext,
                                    R.string.set_portrait_fail);
                        }
                    }

                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        int code = Integer.valueOf(responseInfo.getCode());
                        if(code == Constants.SUCCESS) {
                            String data= FastjsonUtil.parseObject(responseInfo.getData(),String.class);
                            Message msg = new Message();
                            msg.what = Constants.SUCCESS;
                            msg.obj = data;
                            handler.sendMessage(msg);
                        } else {
                            Log.e(TAG, "get keyToken fail, fail msg is " + responseInfo.getDesc());
                            handler.sendEmptyMessage(Constants.FAILURE);
                            ToastUtils.showMessage(mContext,
                                    R.string.set_portrait_fail);
                        }
                    }
                });
    }
*/
    @Override
    public void setPortrait(String userId, String portraitURL) {
        service.setPortrait(userId, portraitURL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "set portrait error", e);
                        RxBusMessage message=new RxBusMessage();
                        message.setCode(Constants.FAILURE_1);
                        RxBus.getInstance().post(Constants.UPDATE, message);
                        if (!HttpUtil.isNetworkAvailable(mContext)) {
                            ToastUtils.showMessage(mContext, R.string.no_net);
                        } else {
                            ToastUtils.showMessage(mContext,
                                    R.string.set_portrait_fail);
                        }
                    }

                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        int code = Integer.valueOf(responseInfo.getCode());
                        if (code == Constants.SUCCESS) {
                            Log.e(TAG, "set portrait success");
                            RxBusMessage message=new RxBusMessage();
                            message.setCode(Constants.SUCCESS_1);
                            RxBus.getInstance().post(Constants.UPDATE, message);
                        } else {
                            Log.e(TAG, "set portrait fail, fail msg is " + responseInfo.getDesc());
                            RxBusMessage message=new RxBusMessage();
                            message.setCode(Constants.FAILURE_1);
                            RxBus.getInstance().post(Constants.UPDATE, message);
                            ToastUtils.showMessage(mContext,
                                    R.string.set_portrait_fail);
                        }
                    }
                });
    }

    @Override
    public void setNickAndSex(String userId, String nickName, String sex) {
        service.setNickAndSex(userId, nickName, sex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "set nickName sex error", e);
                        RxBusMessage message=new RxBusMessage();
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
                            Log.e(TAG, "set nickName sex success");
                            RxBusMessage message=new RxBusMessage();
                            message.setCode(Constants.SUCCESS_2);
                            RxBus.getInstance().post(Constants.UPDATE, message);
                        } else {
                            Log.e(TAG, "set nickName sex fail, fail msg is " + responseInfo.getDesc());
                            RxBusMessage message=new RxBusMessage();
                            message.setCode(Constants.FAILURE_2);
                            RxBus.getInstance().post(Constants.UPDATE, message);
                            ToastUtils.showMessage(mContext,
                                    R.string.set_nick_sex_fial);
                        }
                    }
                });
    }

    @Override
    public void feedback(String content, String contact, String imgUrl) {
        service.feedback(content, contact, imgUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "feedback error", e);
                        RxBusMessage message=new RxBusMessage();
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
                            RxBusMessage message=new RxBusMessage();
                            message.setCode(Constants.SUCCESS);
                            RxBus.getInstance().post(Constants.FEEDBACK, message);
                        } else {
                            Log.e(TAG, "feedback fail, fail msg is " + responseInfo.getDesc());
                            RxBusMessage message=new RxBusMessage();
                            message.setCode(Constants.FAILURE);
                            RxBus.getInstance().post(Constants.FEEDBACK, message);
                            ToastUtils.showMessage(mContext,
                                    R.string.feedback_fail);
                        }

                    }
                });
    }

    @Override
    public void checkUserExeist(String phone) {
        service.isExit(phone)
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
                            RxBusMessage message=new RxBusMessage(Constants.DEFAULT,null);
                            RxBus.getInstance().post(Constants.USER,message);
                        }
                    }

                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        int code = Integer.valueOf(responseInfo.getCode());
                        if (code == Constants.SUCCESS) {
                            Log.e(TAG, "checkUserExeist success");
                            RxBus.getInstance().post(Constants.USER,new RxBusMessage(code,null));
                        } else {
                            Log.e(TAG, "checkUserExeist fail, fail msg is " + responseInfo.getDesc());
                            RxBus.getInstance().post(Constants.USER,new RxBusMessage(code,null));
                        }

                    }
                });
    }

    @Override
    public void reSetPassword(String phone, String password,
                              final ApiCallBack apiCallBack) {
        service.resetPassword(phone, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "reSetPassword error", e);
                        if (!HttpUtil.isNetworkAvailable(mContext)) {
                            ToastUtils.showMessage(mContext, R.string.no_net);
                        } else {
                            apiCallBack.onError(new Exception(e), "reSetPassword error");
                        }
                    }

                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        int code = Integer.valueOf(responseInfo.getCode());
                        if (code == Constants.SUCCESS) {
                            Log.e(TAG, "reSetPassword success");
                            User user = FastjsonUtil.parseObject(responseInfo.getData(), User.class);
                            apiCallBack.onSuccess(user);
                        } else {
                            Log.e(TAG, "reSetPassword fail, fail msg is " + responseInfo.getDesc());
                            apiCallBack.onFailure(String.valueOf(code));
                        }

                    }
                });
    }

    @Override
    public void upLoadProtrait(File file, String userId) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), userId);
        MultipartBody.Part userIdPart = MultipartBody.Part.createFormData("userIdPart", userId, description);
        service.uploadPortrait(body, userIdPart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "reSetPassword error", e);
                        if (!HttpUtil.isNetworkAvailable(mContext)) {
                            ToastUtils.showMessage(mContext, R.string.no_net);
                        } else {
                            ToastUtils.showMessage(mContext, R.string.set_portrait_fail);
                        }
                    }

                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        int code = Integer.valueOf(responseInfo.getCode());
                        if (code == Constants.SUCCESS) {
                            Log.e(TAG, "set portrait success");
                            RxBusMessage message=new RxBusMessage();
                            message.setCode(Constants.SUCCESS_1);
                            message.setObject(responseInfo.getData());
                            RxBus.getInstance().post(Constants.UPDATE, message);
                        } else {
                            Log.e(TAG, "set portrait fail, fail msg is " + responseInfo.getDesc());
                            RxBusMessage message=new RxBusMessage();
                            message.setCode(Constants.FAILURE_1);
                            RxBus.getInstance().post(Constants.UPDATE, message);
                            ToastUtils.showMessage(mContext, R.string.set_portrait_fail);
                        }

                    }
                });
    }

    public void removeSubscription() {
        if (this.mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            this.mCompositeSubscription.unsubscribe();
        }
    }
}
