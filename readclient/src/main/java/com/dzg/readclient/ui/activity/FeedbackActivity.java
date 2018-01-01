package com.dzg.readclient.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.dzg.readclient.R;
import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.injector.component.ApplicationComponent;
import com.dzg.readclient.injector.component.DaggerFeedbackComponent;
import com.dzg.readclient.injector.component.FeedbackComponent;
import com.dzg.readclient.injector.module.ActivityModule;
import com.dzg.readclient.injector.module.FeedbackModule;
import com.dzg.readclient.mvp.contract.FeedbackContract;
import com.dzg.readclient.rx.RxBus;
import com.dzg.readclient.rx.RxBusMessage;
import com.dzg.readclient.ui.view.CTDialog;
import com.dzg.readclient.utils.ProgressDialogUtils;
import com.dzg.readclient.utils.ToastUtils;
import com.dzg.readclient.utils.ToolsUtils;
import com.dzg.readclient.utils.Util;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * @ClassName: FeedbackActivity
 * @Description: 意见反馈activity
 */
public class FeedbackActivity extends BaseActivity implements FeedbackContract.View {
    @Inject
    FeedbackContract.Presenter mPresenter;
    @BindView(R.id.feedback_content)
    EditText mContentET;
    @BindView(R.id.feedback_contact)
    EditText mContactET;
    private CTDialog dialogEdited;
    private CTDialog dialogNotEdited;

    @Override
    public void onCreateView(Bundle savedInstanceState) {
        mPresenter.attachView(this);
        subscriptionEvent();
    }

    @Override
    protected void onDestroy() {
        RxBus.getInstance().unSubscribe(this);
        mPresenter.unsubscribe();
        super.onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    public void injectDependences() {
        ApplicationComponent applicationComponent = ((ReadClientApp) getApplication()).getApplicationComponent();
        FeedbackComponent feedbackComponent = DaggerFeedbackComponent.builder()
                .applicationComponent(applicationComponent)
                .activityModule(new ActivityModule(this))
                .feedbackModule(new FeedbackModule())
                .build();
        feedbackComponent.inject(this);
    }

    @OnClick(R.id.save_tv)
    void feedback(View view) {
        String content = mContentET.getText().toString().trim();
        if (Util.isEmpty(content)) {
            ToastUtils.showMessage(getApplicationContext(), R.string.content_null);
            return;
        }
        content = ToolsUtils.Html2Text(content);
        if (Util.isEmpty(content)) {
            ToastUtils.showMessage(getApplicationContext(),
                    R.string.input_invalide);
            return;
        }
        String contact = mContactET.getText().toString();
        if (Util.isNotEmpty(contact)) {  //如果填写了联系方式，则验证格式
            if (!Util.isEmail(contact) && !Util.isPhone(contact)) {
                ToastUtils.showMessage(getApplicationContext(), R.string.contact_invalid);
                return;
            }
        }
        contact = ToolsUtils.Html2Text(contact);
        ProgressDialogUtils.showProgressDialog(this, "提交反馈中...");
        mPresenter.feedback(content, contact, "");
    }

    public void showFeedbackSuccess() {
        finishWithAnimation();
    }

    private void subscriptionEvent() {
        Subscription subscription = RxBus.getInstance().toObservable(Constants.FEEDBACK, RxBusMessage.class)
                .subscribe(new Action1<RxBusMessage>() {
                    @Override
                    public void call(RxBusMessage message) {
                        switch (message.getCode()) {
                            case Constants.FAILURE: // 反馈失败
                                break;
                            case Constants.SUCCESS: // 反馈成功
                                mPresenter.feedbackSuccess();
                                break;
                        }
                        ProgressDialogUtils.dismiss();
                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    @Override
    public void onBackPressed() {
        mPresenter.toBack();
    }

    @OnClick(R.id.back)
    void back(View view) {
        mPresenter.toBack();
    }

    public void showBack() {
        String content = mContentET.getText().toString().trim();
        if (Util.isEmpty(content)) {
            if (dialogNotEdited == null) {
                CTDialog.Builder customBuilder = new CTDialog.Builder(this);
                customBuilder.setTitle("温馨提示")
                        .setMessage("既然来了，就给点意见呗！")
                        .setNegativeButton("我要反馈",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setPositiveButton("去意已决",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finishWithAnimation();
                            }
                        });
                dialogNotEdited = customBuilder.create();
            }
            dialogNotEdited.show();
        } else {
            if (dialogEdited == null) {
                CTDialog.Builder customBuilder = new CTDialog.Builder(this);
                customBuilder.setTitle("温馨提示")
                        .setMessage("辛辛苦苦写的意见真的不提交给我们吗？")
                        .setNegativeButton("我要反馈",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setPositiveButton("去意已决",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finishWithAnimation();
                            }
                        });
                dialogEdited = customBuilder.create();
            }
            dialogEdited.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
