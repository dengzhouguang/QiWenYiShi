package com.dzg.readclient.dao;

import android.content.Context;
import android.util.Log;

import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.mvp.model.Collect;
import com.dzg.readclient.mvp.model.DingOrCai;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: PraiseDAO
 * @Description: 顶踩功能DAO
 * @author lling
 * @date 2015-6-28
 */
public class CollectDAO {
	
	private static final String TAG = "CollectDAO";
	private Context context;
	com.dzg.readclient.greendao.CollectDao collectDAO=ReadClientApp.getInstance().getDaoSession().getCollectDao();
	public CollectDAO(Context context) {
		this.context = context;
	}
	
	/**
	 * 检查是否收藏过
	 * @param userId
	 * @param jokeId
	 * @return
	 */
	public Collect getCollect(int userId, int jokeId) {
		Collect collect = null;
		try {
//			collect = db.findFirst(Selector.from(Collect.class).where(WhereBuilder.b("user_id", "=", userId).and("joke_id", "=", jokeId)));
			List<Collect> collects = collectDAO.queryRaw("where USER_ID=? and JOKE_ID=?", String.valueOf(userId), String.valueOf(jokeId));
			if (collects.size()>0){
				collect=collects.get(0);
			}
			Log.d(TAG, "getDingOrCai success");
		} catch (Exception e) {
			Log.e(TAG, "getDingOrCai failure", e);
		}
		return collect;
	}
	
	/**
	 * 用户收藏
	 * @param userId
	 * @param jokeId
	 * @param jokeContent
	 */
	public void collect(int userId, int jokeId, String jokeContent) {
		Collect collect = new Collect();
		collect.setUserId(userId);
		collect.setJokeId(jokeId);
		collect.setJokeContent(jokeContent);
		collect.setIsUpload(DingOrCai.NOT_UPLOAD);
		collect.setCreateAt(new Date());
		try {
			collectDAO.insert(collect);
			Log.d(TAG, "collect success");
		} catch (Exception e) {
			Log.e(TAG, "collect failure", e);
		}
	}
	
	/**
	 * 取消收藏
	 * @param jokeId
	 */
	public void cancelCollect(int jokeId) {
		try {
			List<Collect> collects = collectDAO.queryRaw("where JOKE_ID=?", String.valueOf(jokeId));
			if (collects.size()>0){
				collectDAO.delete(collects.get(0));
			}
			Log.d(TAG, "cancelCollect success");
		} catch (Exception e) {
			Log.e(TAG, "cancelCollect failure", e);
		}
	}
	
	/**
	 * 获取我的收藏
	 * @param userId
	 * @return
	 */
	public List<Collect> getCollects(int userId) {
		List<Collect> dbModels = null;
		try {
			dbModels =collectDAO.loadAll();
			Log.d(TAG, "getCollects success");
		} catch (Exception e) {
			Log.e(TAG, "getCollects failure", e);
		}
		return dbModels;
	}
	
}
