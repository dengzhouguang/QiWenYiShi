package com.dzg.readclient.dao;

import android.util.Log;

import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.greendao.DingOrCaiDao;
import com.dzg.readclient.mvp.model.DingOrCai;

import java.util.List;

/**
 * @ClassName: PraiseDAO
 * @Description: 顶踩功能DAO
 */
public class DingCaiDAO {
	
	private static final String TAG = "PraiseDAO";
	private DingOrCaiDao dingOrCaiDao= ReadClientApp.getInstance().getDaoSession().getDingOrCaiDao();
	/**
	 * 检查是否点过赞
	 * @param userId
	 * @param jokeId
	 * @return
	 */
	public DingOrCai getDingOrCai(int userId, int jokeId) {
		DingOrCai dingOrCai = null;
		try {
			List<DingOrCai> dingOrCais = dingOrCaiDao.queryRaw("where USER_ID=? and JOKE_ID=?", String.valueOf(userId), String.valueOf(jokeId));
			if (dingOrCais.size()>0){
				dingOrCai=dingOrCais.get(0);
			}
			Log.d(TAG, "getDingOrCai success");
		} catch (Exception e) {
			Log.d(TAG, "getDingOrCai failure", e);
		}
		return dingOrCai;
	}
	
	/**
	 * 用户顶或者踩
	 * @param userId
	 * @param jokeId
	 * @param dingOrCai  标记是顶是踩
	 */
	public void dingOrCai(int userId, int jokeId, int dingOrCai, int num) {
		DingOrCai praise = new DingOrCai();
		praise.setJokeId(jokeId);
		praise.setUserId(userId);
		praise.setDingOrCai(dingOrCai);
		praise.setNum(num);
		praise.setIsUpload(DingOrCai.NOT_UPLOAD);
		try {
			dingOrCaiDao.insert(praise);
			Log.d(TAG, "dingOrCai success");
		} catch (Exception e) {
			Log.d(TAG, "dingOrCai failure", e);
		}
	}
	
	/**
	 * 查找未同步到服务器的点赞数据
	 * @return
	 */
	public List<DingOrCai> getUnUploadCai() {
		List<DingOrCai> dbModels = null;
		try {
			dbModels=dingOrCaiDao.loadAll();
			for (DingOrCai cai:dbModels) {
				if (cai.isDing()||cai.getIsUpload()==1)
					dbModels.remove(cai);
			}
			Log.d(TAG, "getUnUpload success");
		} catch (Exception e) {
			Log.d(TAG, "getUnUpload failure", e);
		}
		return dbModels;
	}
	public List<DingOrCai> getUnUploadDing() {
		List<DingOrCai> dbModels = null;
		try {
			dbModels=dingOrCaiDao.loadAll();
			for (DingOrCai ding:dbModels) {
				if (!ding.isDing()||ding.getIsUpload()==1)
					dbModels.remove(ding);
			}
			Log.d(TAG, "getUnUpload success");
		} catch (Exception e) {
			Log.d(TAG, "getUnUpload failure", e);
		}
		return dbModels;
	}
	/**
	 * 修改同步后的数据
	 * @param praises
	 */
	public void upload(List<DingOrCai> praises) {
		for (DingOrCai praise : praises) {
			praise.setIsUpload(DingOrCai.UPLOAD);
			try {
				dingOrCaiDao.update(praise);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
