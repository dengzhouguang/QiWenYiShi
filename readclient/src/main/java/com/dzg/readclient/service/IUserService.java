package com.dzg.readclient.service;

import com.dzg.readclient.api.ApiCallBack;

import java.io.File;


/**
 * @ClassName: IUserService
 * @Description: 用户service
 * @author lling
 * @date 2015年6月30日
 */
public interface IUserService {

	/**
	 * 增加腾讯QQ用户
	 * @param handler
	 * @param portraitUrl
	 * @param nickName
	 * @param sex
	 *//*
	public void addTencentUser( String openId, String portraitUrl, String nickName, int sex);
	
	*//**
	 * 增加新浪微博用户
	 * @param handler
	 * @param uid
	 * @param portraitUrl
	 * @param nickName
	 *//*
	public void addSinaUser( String uid, String portraitUrl, String nickName);
	*/
	/**
	 * 用户注册
	 * @param phone
	 * @param password
	 */
	public void regist( String phone, String password);
	
	/**
	 * 回调方式注册
	 * @param apiCallBack
	 * @param email
	 * @param password
	 */
	public void regist(ApiCallBack apiCallBack, String email, String password);
	
	/**
	 * 用户登录
	 * @param phone
	 * @param password
	 */
	public void login( String phone, String password);
	
	/**
	 * 获取七牛上传token
	 */
//	public void getQiNiuToken(Handler handler);
	
	/**
	 * 设置用户头像
	 * @param userId
	 * @param portraitURL
	 */
	public void setPortrait( String userId, String portraitURL);
	
	/**
	 * 设置昵称和性别
	 * @param nickName
	 * @param sex
	 */
	public void setNickAndSex(String userId, String nickName, String sex);
	
	/**
	 * 用户反馈
	 * @param content
	 * @param contact
	 * @param imgUrl
	 */
	public void feedback( String content, String contact, String imgUrl);
	
	/**
	 * 验证用户是否注册
	 * @param phone
	 */
	public void checkUserExeist(String phone);
	
	/**
	 * 重置密码
	 * @param phone
	 * @param password
	 * @param apiCallBack
	 */
	public void reSetPassword(String phone, String password, ApiCallBack apiCallBack);

	/**
	 * 上传头像到服务器
	 * @param file
	 * @param userId
	 */
	public void upLoadProtrait(File file,String userId);
}
