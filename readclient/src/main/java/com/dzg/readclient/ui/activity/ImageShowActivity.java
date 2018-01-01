package com.dzg.readclient.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.ViewUtils;
import android.view.View;


import com.bumptech.glide.Glide;
import com.dzg.readclient.R;
import com.dzg.readclient.utils.ImgUtil;
import com.dzg.readclient.utils.ToastUtils;
import com.dzg.readclient.utils.ToolsUtils;

import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;

/**
 * @ClassName: ImageShowActivity
 * @Description: 图片预览
 * @author lling
 * @date 2015-6-27
 */
public class ImageShowActivity extends BaseActivity {
	private final String SAVE_PATH = "/qiquying/images/" ;
	@BindView(R.id.photoView)
	PhotoView mPhotoView;
	@Override
	public  void onCreateView(Bundle savedInstanceState) {
		String url = getIntent().getStringExtra("url");
		Glide.with(this).load(url).into(mPhotoView);
	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_image_show;
	}

	
	@OnClick(R.id.save)
	void save(View view) {
		mPhotoView.setDrawingCacheEnabled(true);
		Bitmap bitmap = mPhotoView.getDrawingCache();
		if(bitmap == null) {
			mPhotoView.setDrawingCacheEnabled(false);
			return;
		}
		if (!ToolsUtils.hasSdcard()) {
			ToastUtils.showMessage(getApplicationContext(), "未找到存储卡");
		}
		view.setEnabled(false);
		ToastUtils.showMessage(getApplicationContext(), "图片已保存到sd卡" + SAVE_PATH);
		try {
			ImgUtil.saveBitmapToFile(bitmap, Environment.getExternalStorageDirectory()
					+ SAVE_PATH + UUID.randomUUID() + ".jpg");
		} catch (IOException e) {
			ToastUtils.showMessage(getApplicationContext(), "保存失败");
		}
		mPhotoView.setDrawingCacheEnabled(false);
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
	protected void onPause() {
		super.onPause();
	}

}
