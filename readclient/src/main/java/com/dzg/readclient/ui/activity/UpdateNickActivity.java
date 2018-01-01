package com.dzg.readclient.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.injector.component.ApplicationComponent;
import com.dzg.readclient.injector.component.DaggerUpdateNickComponent;
import com.dzg.readclient.injector.component.UpdateNickComponent;
import com.dzg.readclient.injector.module.ActivityModule;
import com.dzg.readclient.injector.module.UpdateNickModule;
import com.dzg.readclient.mvp.contract.UpdateNickContract;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
import com.dzg.readclient.utils.ProgressDialogUtils;
import com.dzg.readclient.utils.ToastUtils;
import com.dzg.readclient.utils.ToolsUtils;
import com.dzg.readclient.utils.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * @ClassName: UpdateNickActivity
 * @Description: 修改用户昵称
 */
public class UpdateNickActivity extends BaseActivity implements UpdateNickContract.View {
    @Inject
    public UpdateNickContract.Presenter mPresenter;
    @BindView(R.id.update_nickname)
    EditText mNickET;

    @Override
    public void onCreateView(Bundle savedInstanceState) {
        mPresenter.attachView(this);
        mPresenter.init();
        subscriptionEvent();
    }

    @Override
    public void injectDependences() {
        ApplicationComponent appComponent=ReadClientApp.getInstance().getApplicationComponent();
        UpdateNickComponent component= DaggerUpdateNickComponent.builder()
                .applicationComponent(appComponent)
                .activityModule(new ActivityModule(this))
                .updateNickModule(new UpdateNickModule())
                .build();
        component.inject(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_update_nick;
    }

    public void showContent() {
        if (ReadClientApp.getInstance().currentUser != null) {
            mNickET.setText(ReadClientApp.getInstance().currentUser.getUserNike());
            mNickET.setSelection(ReadClientApp.getInstance().currentUser.getUserNike().length());
        }
    }

    /* 提交修改昵称 */
    String nickName;

    @OnClick(R.id.save_tv)
    void updateNick(View view) {
        nickName = mNickET.getText().toString().trim();
        if (Util.isEmpty(nickName)) {
            ToastUtils.showMessage(this, R.string.user_update_nickname_null);
            return;
        }
        nickName = ToolsUtils.Html2Text(nickName);
        if (Util.isEmpty(nickName)) {
            ToastUtils.showMessage(getApplicationContext(),
                    R.string.input_invalide);
            return;
        }
        ToolsUtils.hideKeyboard(mNickET);
        if (ReadClientApp.getInstance().currentUser != null) {
            if (nickName.equals(ReadClientApp.getInstance().currentUser.getUserNike())) {
                finishWithAnimation();
                return;
            }
            try {
                ProgressDialogUtils.showProgressDialog(this, "正在处理中...");
                mPresenter.setNickAndSex(String.valueOf(ReadClientApp.getInstance().currentUser.getId()),
                        URLEncoder.encode(nickName, "UTF-8"), null);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void subscriptionEvent() {
        Subscription subscription = RxBus.getInstance().toObservable(Constants.UPDATE, RxBusMessage.class)
                .subscribe(new Action1<RxBusMessage>() {
                    @Override
                    public void call(RxBusMessage message) {
                        switch (message.getCode()) {
                            case Constants.FAILURE_2: //设置性别失败
                                ProgressDialogUtils.dismiss();
                                break;
                            case Constants.SUCCESS_2: //设置性别成功
                                mPresenter.setNickSuccess();
                                ProgressDialogUtils.dismiss();
                                break;
                        }
                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    /**
     * 设置昵称和性别成功
     */
    public void showNickSuccess() {
        if (ReadClientApp.getInstance().currentUser == null) {
            return;
        }
        ReadClientApp.getInstance().currentUser.setUserNike(nickName);
        spUtil.putObject("user", ReadClientApp.getInstance().currentUser);
        finishWithAnimation();
    }

    @OnClick(R.id.clear)
    void clear(View view) {
        mNickET.setText("");
    }

    @OnClick(R.id.back)
    void back(View view) {
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
