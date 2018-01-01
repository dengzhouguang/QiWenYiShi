package com.dzg.readclient.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.injector.component.ApplicationComponent;
import com.dzg.readclient.injector.component.CreateUserComponent;
import com.dzg.readclient.injector.component.DaggerCreateUserComponent;
import com.dzg.readclient.injector.module.ActivityModule;
import com.dzg.readclient.injector.module.CreateUserModule;
import com.dzg.readclient.mvp.contract.CreateUserContract;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
import com.dzg.readclient.ui.view.CircleImageView;
import com.dzg.readclient.utils.ProgressDialogUtils;
import com.dzg.readclient.utils.ToastUtils;
import com.dzg.readclient.utils.ToolsUtils;
import com.dzg.readclient.utils.Util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

import static com.dzg.readclient.commons.Constants.IMAGE_FILE_NAME;

/**
 * @ClassName: CreateUserInfoActivity
 * @Description: 注册后创建个人资料
 */
public class CreateUserInfoActivity extends BaseActivity implements CreateUserContract.View {
    private final int CHOSE_PIC_CODE = 1;
    private final int TAKE_PHOTO_CODE = 2;
    private final int CROP_CODE = 3;
    @Inject
    public CreateUserContract.Presenter mPresenter;
    /* 头像名称 */
    @BindView(R.id.sex_tv)
    TextView mSexTV;
    @BindView(R.id.nick_et)
    EditText mNickET;
    @BindView(R.id.set_portrait_tv)
    TextView mSetPortraitTV;
    @BindView(R.id.user_img)
    CircleImageView mUserPortraitIV;
    /**
     * 保存个人资料
     *
     * @param view
     */
    String nickName;
    private Dialog mSexDialog;
    private Dialog mPortraitDialog;
    private int mSexFlag = 1;
    private String mPortraitURL = null;

    @Override
    public void onCreateView(Bundle savedInstanceState) {
        mPresenter.attachView(this);
        subscriptionEvent();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_user_info;
    }

    @Override
    public void injectDependences() {
        ApplicationComponent applicationComponent = ((ReadClientApp) getApplication()).getApplicationComponent();
        CreateUserComponent createUserComponent = DaggerCreateUserComponent.builder()
                .applicationComponent(applicationComponent)
                .activityModule(new ActivityModule(this))
                .createUserModule(new CreateUserModule())
                .build();
        createUserComponent.inject(this);
    }

    /**
     * 选择性别
     *
     * @param view
     */
    @OnClick(R.id.set_sex_layout)
    void setSex(View view) {
        if (mSexDialog == null) {
            mPresenter.createSexDialog();
        }
        mSexDialog.show();
    }

    @Override
    public void showSexDialog() {
        mSexDialog = new Dialog(this, R.style.Translucent_NoTitle);
        mSexDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置对话框无title
        mSexDialog.setContentView(R.layout.dialog_userinfo);
        TextView title = (TextView) mSexDialog
                .findViewById(R.id.dialog_userinfo_title);
        title.setText(R.string.sex);
        ListView listview = (ListView) mSexDialog
                .findViewById(R.id.dialog_userinfo_listview);
        String[] strings = new String[]{"男", "女"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.listitem_userinfo_dialog,
                R.id.name, strings);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == 0) {
                    mSexTV.setText(R.string.sex_man);
                    mSexFlag = 1;
                } else {
                    mSexTV.setText(R.string.sex_women);
                    mSexFlag = 0;
                }
                mSexDialog.dismiss();
            }
        });
        ToolsUtils.setDialogBgAndAnim(mSexDialog);
    }

    @OnClick(R.id.set_portrait_tv)
    void setPortrait(View view) {
        if (mPortraitDialog == null) {
            mPresenter.createPortraitDialog();
        }
        mPortraitDialog.show();
    }

    @Override
    public void showPortraitDialog() {
        mPortraitDialog = new Dialog(this, R.style.Translucent_NoTitle);
        mPortraitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置对话框无title
        mPortraitDialog.setContentView(R.layout.dialog_userinfo);
        TextView title = (TextView) mPortraitDialog
                .findViewById(R.id.dialog_userinfo_title);
        title.setText(R.string.set_portrait);
        ListView listview = (ListView) mPortraitDialog
                .findViewById(R.id.dialog_userinfo_listview);
        String[] strings = new String[]{"相册", "相机"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.listitem_userinfo_dialog,
                R.id.name, strings);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0: // 选中相册
                        Intent intentFromGallery = new Intent();
                        intentFromGallery.setType("image/*"); // 设置文件类型
                        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResultWithAnimation(intentFromGallery,
                                CHOSE_PIC_CODE);
                        break;
                    case 1: // 选中照相机
                        Intent intentFromCapture = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        // 判断存储卡是否可以用，可用进行存储
                        if (ToolsUtils.hasSdcard()) {
                            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(new File(Environment
                                            .getExternalStorageDirectory(),
                                            IMAGE_FILE_NAME)));
                            startActivityForResultWithAnimation(intentFromCapture,
                                    TAKE_PHOTO_CODE);
                        } else {
                            ToastUtils.showMessageLong(getApplicationContext(),
                                    "未找到存储卡，无法存储照片！");
                        }
                        break;
                }
                mPortraitDialog.dismiss();
            }
        });
        ToolsUtils.setDialogBgAndAnim(mPortraitDialog);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CHOSE_PIC_CODE:   //从相册选择照片成功
                try {
                    mPresenter.startPhotoZoom(data.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case TAKE_PHOTO_CODE: //从相机拍照成功
                File tempFile = new File(Environment.getExternalStorageDirectory() + "/"
                        + Constants.IMAGE_FILE_NAME);
                try {
                    mPresenter.startPhotoZoom(Uri.fromFile(tempFile));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CROP_CODE:    //裁剪照片成功
                if (data == null) {
                    return;
                }
                mPresenter.getImageToView();
                break;
        }
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @throws IOException
     */
    public void showImageToView() {
        ProgressDialogUtils.showProgressDialog(this, "正在处理中...");
        RxBusMessage msg = new RxBusMessage(Constants.SUCCESS, Constants.IMAGE_FILE_NAME);
        RxBus.getInstance().post(Constants.USER, msg);
    }

    /**
     * 跳过
     *
     * @param view
     */
    @OnClick(R.id.skip_tv)
    void skip(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityWithAnimation(intent);
        finishWithAnimation();
    }

    @OnClick(R.id.save_tv)
    void save(View view) {
        nickName = mNickET.getText().toString().trim();
        if (Util.isEmpty(nickName)) {
            mNickET.requestFocus();
            ToastUtils.showMessage(getApplicationContext(), R.string.nick_null);
            return;
        }
        nickName = ToolsUtils.Html2Text(nickName);
        if (Util.isEmpty(nickName)) {
            ToastUtils.showMessage(getApplicationContext(),
                    R.string.input_invalide);
            return;
        }
        ToolsUtils.hideKeyboard(mNickET);
        if (ReadClientApp.getInstance().currentUser == null) {
            finishWithAnimation();
            return;
        }
        try {
            ProgressDialogUtils.showProgressDialog(this, "正在处理中...");
            mPresenter.setNickAndSex(String.valueOf(ReadClientApp.getInstance().currentUser.getId()),
                    URLEncoder.encode(nickName, "UTF-8"), String.valueOf(mSexFlag));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void subscriptionEvent() {
        Subscription subscription = RxBus.getInstance().toObservable(Constants.USER, RxBusMessage.class)
                .subscribe(new Action1<RxBusMessage>() {
                    @Override
                    public void call(RxBusMessage message) {
                        switch (message.getCode()) {
                            case Constants.FAILURE:
                                ProgressDialogUtils.dismiss();
                                break;
                            case Constants.SUCCESS:
                                Log.e("CREATE USER", "CREATE USER");
                                if (!Constants.IMAGE_FILE_NAME.equals(message.getObject())) {
                                    break;
                                }
                                File file = new File(Environment.getExternalStorageDirectory() + "/" + Constants.IMAGE_FILE_NAME);
                                mPresenter.upLoadProtrait(file, String.valueOf(ReadClientApp.getInstance().currentUser.getId()));
                                break;
                            case Constants.FAILURE_1: //设置头像失败
                                ProgressDialogUtils.dismiss();
                                break;
                            case Constants.SUCCESS_1: //设置头像成功
                                mPresenter.setPortraitSuccess();
                                ProgressDialogUtils.dismiss();
                                break;
                            case Constants.FAILURE_2: //设置昵称和性别失败
                                ProgressDialogUtils.dismiss();
                                break;
                            case Constants.SUCCESS_2: //设置昵称和性别成功
                                mPresenter.setNickSexSuccess();
                                ProgressDialogUtils.dismiss();
                                break;
                        }
                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    /**
     * 设置头像成功
     */
    public void showPortraitSuccess() {
        mUserPortraitIV.setVisibility(View.VISIBLE);
        mSetPortraitTV.setVisibility(View.GONE);
        Glide.with(this).load(mPortraitURL).into(mUserPortraitIV);
        if (ReadClientApp.getInstance().currentUser == null) {
            return;
        }
        ReadClientApp.getInstance().currentUser.setPortraitUrl(mPortraitURL);
        spUtil.putObject("user", ReadClientApp.getInstance().currentUser);
    }

    /**
     * 设置昵称和性别成功
     */
    public void showNickSexSuccess() {
        if (ReadClientApp.getInstance().currentUser == null) {
            return;
        }
        ReadClientApp.getInstance().currentUser.setSex(mSexFlag);
        ReadClientApp.getInstance().currentUser.setUserNike(nickName);
        spUtil.putObject("user", ReadClientApp.getInstance().currentUser);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityWithAnimation(intent);
        finishWithAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        RxBus.getInstance().unSubscribe(this);
        mPresenter.unsubscribe();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
