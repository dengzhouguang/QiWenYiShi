package com.dzg.readclient.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dzg.readclient.R;
import com.dzg.readclient.utils.ToolsUtils;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * @ClassName: AboutActivity
 * @Description: 关于界面
 */
public class AboutActivity extends BaseActivity {

    @BindView(R.id.about_version)
    TextView mAboutVersion;

    @Override
    public void onCreateView(Bundle savedInstanceState) {

        mAboutVersion.setText(new StringBuilder("趣闻轶事").append(ToolsUtils.getVersionName(this)));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @OnClick(R.id.back)
    void back(View view) {
        finishWithAnimation();
    }

}
