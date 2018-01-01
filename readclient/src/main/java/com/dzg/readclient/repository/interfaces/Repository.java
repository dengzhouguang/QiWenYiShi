package com.dzg.readclient.repository.interfaces;

import com.dzg.readclient.commons.ResponseInfo;

import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2017/6/22.
 */

public interface Repository {
     Observable<ResponseInfo> getList(int newOrHotFlag, int type,int  offset,int count);
     Observable<ResponseInfo> getComments( int qushiId, int  offset,int count);
     Observable<ResponseInfo> addQushi( String content,  int  userId);
     Observable<ResponseInfo> postDing(String ids);
     Observable<ResponseInfo> postCai(String ids);
     Observable<ResponseInfo> getJokeById(int jokeId);
     Observable<ResponseInfo> postComment(int jokeId, int userId,String userPortrait,String userNick,String content);
     Observable<ResponseInfo> postQutu( String title, String imgUrl,int userId);
     Observable<ResponseInfo> postMeitu( String title, String imgUrl,int userId);
     Observable<ResponseInfo> getMeitu( int newOrHotFlag, int  offset,int count);
     Observable<ResponseInfo> getQutu( int newOrHotFlag, int  offset,int count);
     Observable<ResponseInfo> regist(String userName,String password);
     Observable<ResponseInfo> login(String userName,String password);
     Observable<ResponseInfo> getQiNiuToken();
     Observable<ResponseInfo> setPortrait( String userId, String portraitURL);
     Observable<ResponseInfo> setNickAndSex(String userId,String nickName,String sex);
     Observable<ResponseInfo> feedback(String content, String contact, String imgUrl);
     Observable<ResponseInfo> isExit( String phone);
     Observable<ResponseInfo> resetPassword(String phone,String password);
     Observable<ResponseInfo> uploadPortrait( MultipartBody.Part fileToUpload, MultipartBody.Part userId);
}
