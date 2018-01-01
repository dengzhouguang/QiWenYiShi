package com.dzg.readclient.mvp.presenter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.mvp.contract.UserInfoContract;
import com.dzg.readclient.mvp.contract.UserInfoContract.Presenter;
import com.dzg.readclient.mvp.usecase.GetUserInfo;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
import com.dzg.readclient.utils.HttpUtil;
import com.dzg.readclient.utils.ProgressDialogUtils;
import com.dzg.readclient.utils.ToastUtils;
import com.dzg.readclient.utils.ToolsUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.dzg.readclient.commons.Constants.IMAGE_FILE_NAME;

/**
 * Created by Administrator on 2017/8/25 0025.
 */

public class UserInfoPresenter implements Presenter {
    private static final String TAG = UserInfoPresenter.class.getName();
    private CompositeSubscription mCompositeSubscription;
    private UserInfoContract.View mView;
    private GetUserInfo mUsercase;
    private Context mContext;

    public UserInfoPresenter(GetUserInfo mUsecase, Context mContext) {
        this.mUsercase = mUsecase;
        this.mContext = mContext;
        this.mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void attachView(UserInfoContract.View view) {
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
    public void setNickAndSex(String userId, String nickName, String sex) {
        Subscription subscription = mUsercase.execute(new GetUserInfo.RequestValues(Constants.SETNICKANDSEX, userId, nickName, sex)).getResponseInfo()
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
    public void upLoadProtrait(File file, String userId) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), userId);
        MultipartBody.Part userIdPart = MultipartBody.Part.createFormData("userIdPart", userId, description);
        Subscription subscription = mUsercase.execute(new GetUserInfo.RequestValues(Constants.UPLOADPORTRAIT, body, userIdPart)).getResponseInfo()
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
                            RxBusMessage message = new RxBusMessage();
                            message.setCode(Constants.SUCCESS_1);
                            message.setObject(responseInfo.getData());
                            RxBus.getInstance().post(Constants.UPDATE, message);
                        } else {
                            Log.e(TAG, "set portrait fail, fail msg is " + responseInfo.getDesc());
                            RxBusMessage message = new RxBusMessage();
                            message.setCode(Constants.FAILURE_1);
                            RxBus.getInstance().post(Constants.UPDATE, message);
                            ToastUtils.showMessage(mContext, R.string.set_portrait_fail);
                        }

                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));

        byte[] mContent = new byte[0];
        ContentResolver resolver = mContext.getContentResolver();

        // 将图片内容解析成字节数组
        try {
            mContent = ToolsUtils.readStream(resolver
                    .openInputStream(Uri.parse(uri.toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 2;

        // 将字节数组转换为ImageView可调用的Bitmap对象
        Bitmap myBitmap = ToolsUtils.getPicFromBytes(mContent, opts);
        if (myBitmap.getWidth() >= Constants.PIC_SIZE && myBitmap.getHeight() >= Constants.PIC_SIZE) {
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
        }
        intent.putExtra("return-data", true);
        ((Activity) mContext).startActivityForResult(intent, Constants.CROP_CODE);
    }

    @Override
    public void createSexDialog() {
        mView.showSexDialog();
    }

    @Override
    public void getImageToView() {
        ProgressDialogUtils.showProgressDialog(mContext, "正在处理中...");
        RxBusMessage msg=new RxBusMessage();
        msg.setCode(Constants.SUCCESS);
        RxBus.getInstance().post(Constants.UPDATE, msg);
    }

    @Override
    public void uploadPortrait() {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
        upLoadProtrait(file, String.valueOf(ReadClientApp.getInstance().currentUser.getId()));
    }

    @Override
    public void setSexSuccess() {
        mView.showSetSexSuccess();
    }

    @Override
    public void setPortraitSuccess() {
        mView.showSetPortraitSuccess();
    }

    @Override
    public void createPortraitDialog() {
        mView.showPortraitDialog();
    }

    @Override
    public void init() {
        mView.showContent();
    }
}
