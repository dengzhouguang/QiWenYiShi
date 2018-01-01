package com.dzg.readclient.service;

import com.dzg.readclient.mvp.model.DingOrCai;

import java.util.List;

/**
 * @ClassName: IQuShiService
 * @Description: 趣事接口

 */
public interface IJokeService {

	/**
	 * 获取趣事列表
	 * @param newOrHotFlag
	 * @param offset
	 * @param count
	 */
	public void getList( int type, int newOrHotFlag, int offset, int count);
	/**
	 * 刷新趣事列表
	 * @param newOrHotFlag
	 * @param count
	 */
	public void reflush( int type, int newOrHotFlag, int count);
	
	/**
	 * 获取所有内容列表
	 * @param newOrHotFlag
	 * @param offset
	 * @param count
	 */
	public void getAll( int newOrHotFlag, int offset, int count);
	
	/**
	 * 刷新所有内容列表
	 * @param newOrHotFlag
	 * @param count
	 */
	public void refreshAll( int newOrHotFlag, int count);
	
	/**
	 * 根据jokeid获取评论列表
	 * @param jokeId
	 * @param offset
	 * @param count
	 */
	public void getCommentByJokeId( int jokeId, int offset, int count);
	
	/**
	 * 顶joke
	 * @param dingOrCais
	 */
	public void ding(List<DingOrCai> dingOrCais);
	
	/**
	 * 踩joke
	 * @param dingOrCais
	 */
	public void cai(List<DingOrCai> dingOrCais);
	
	/**
	 * 用户评论joke
	 * @param jokeId
	 * @param content
	 */
	public void addComment(Integer jokeId, String content);
	
	/**
	 * 根据jokeId获取joke对象
	 * @param jokeId
	 */
	public void getJokeById(Integer jokeId);
	public void removeSubscription();
}
