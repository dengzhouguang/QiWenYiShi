package com.dzg.readclient.repository;

import android.content.Context;

import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.api.JokeService;
import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.repository.interfaces.Repository;

import okhttp3.MultipartBody;
import retrofit2.Retrofit;
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

public class RepositoryImpl implements Repository {
    private Context mContext;
    private JokeService mJokeService;
    public RepositoryImpl(ReadClientApp mReadClient, Retrofit retrofit) {
        mContext=mReadClient;
        mJokeService=retrofit.create(JokeService.class);
    }

    @Override
    public Observable<ResponseInfo> getList(int newOrHotFlag, int type, int offset, int count) {
        return mJokeService.getList(newOrHotFlag, type, offset, count);
    }

    @Override
    public Observable<ResponseInfo> getComments(int qushiId, int offset, int count) {
        return mJokeService.getComments(qushiId, offset, count);
    }

    @Override
    public Observable<ResponseInfo> addQushi(String content, int userId) {
        return mJokeService.addQushi(content, userId);
    }

    @Override
    public Observable<ResponseInfo> postDing(String ids) {
        return mJokeService.postDing(ids);
    }

    @Override
    public Observable<ResponseInfo> postCai(String ids) {
        return mJokeService.postCai(ids);
    }

    @Override
    public Observable<ResponseInfo> getJokeById(int jokeId) {
        return mJokeService.getJokeById(jokeId);
    }

    @Override
    public Observable<ResponseInfo> postComment(int jokeId, int userId, String userPortrait, String userNick, String content) {
        return mJokeService.postComment(jokeId, userId, userPortrait, userNick, content);
    }

    @Override
    public Observable<ResponseInfo> postQutu(String title, String imgUrl, int userId) {
        return mJokeService.postQutu(title, imgUrl, userId);
    }

    @Override
    public Observable<ResponseInfo> postMeitu(String title, String imgUrl, int userId) {
        return mJokeService.postMeitu(title, imgUrl, userId);
    }
    @Override
    public Observable<ResponseInfo> getMeitu(int  newOrHotFlag, int offset, int count) {
        return mJokeService.getMeitu(newOrHotFlag, offset, count);
    }

    @Override
    public Observable<ResponseInfo> getQutu(int newOrHotFlag, int offset, int count) {
        return mJokeService.getQutu(newOrHotFlag,offset,count);
    }

    @Override
    public Observable<ResponseInfo> regist(String userName, String password) {
        return mJokeService.regist(userName,password);
    }

    @Override
    public Observable<ResponseInfo> login(String userName, String password) {
        return mJokeService.login(userName,password);
    }

    @Override
    public Observable<ResponseInfo> getQiNiuToken() {
        return mJokeService.getQiNiuToken();
    }

    @Override
    public Observable<ResponseInfo> setPortrait(String userId, String portraitURL) {
        return mJokeService.setPortrait(userId,portraitURL);
    }

    @Override
    public Observable<ResponseInfo> setNickAndSex(String userId, String nickName, String sex) {
        return mJokeService.setNickAndSex(userId, nickName, sex);
    }

    @Override
    public Observable<ResponseInfo> feedback(String content, String contact, String imgUrl) {
        return mJokeService.feedback(content, contact, imgUrl);
    }

    @Override
    public Observable<ResponseInfo> isExit(String phone) {
        return mJokeService.isExit(phone);
    }

    @Override
    public Observable<ResponseInfo> resetPassword(String phone, String password) {
        return mJokeService.resetPassword(phone, password);
    }
    @Override
    public Observable<ResponseInfo> uploadPortrait(MultipartBody.Part fileToUpload, MultipartBody.Part userId) {
        return mJokeService.uploadPortrait(fileToUpload, userId);
    }



   /* @GET("meitu/list")
    Observable<ResponseInfo> getMeitu(@Query("newOrHotFlag") int newOrHotFlag, @Query("offset") int  offset, @Query("count")int count);
    @GET("qutu/list")
    Observable<ResponseInfo> getQutu(@Query("newOrHotFlag") int newOrHotFlag, @Query("offset") int  offset,@Query("count")int count);
    @FormUrlEncoded
    @POST("user/regist")
    Observable<ResponseInfo> regist(@Field("userName") String userName, @Field("password") String password);
    @FormUrlEncoded
    @POST("user/login")
    Observable<ResponseInfo> login(@Field("userName") String userName,@Field("password") String password);
    @GET("user/get_key_token")
    Observable<ResponseInfo> getQiNiuToken();
    @FormUrlEncoded
    @POST("user/set_portrait")
    Observable<ResponseInfo> setPortrait(@Field("userId") String userId,@Field("portraitURL") String portraitURL);
    @FormUrlEncoded
    @POST("user/set_nick_sex")
    Observable<ResponseInfo> setNickAndSex(@Field("userId") String userId,@Field("nickName") String nickName,@Field("sex") String sex);
    @FormUrlEncoded
    @POST("user/feedback")
    Observable<ResponseInfo> feedback(@Field("content") String content,@Field("contact") String contact,@Field("imgUrl") String imgUrl);
    @FormUrlEncoded
    @POST("user/exeist")
    Observable<ResponseInfo> isExit(@Field("phone") String phone);
    @FormUrlEncoded
    @POST("user/reset_password")
    Observable<ResponseInfo> resetPassword(@Field("phone") String phone,@Field("password") String password);
    @Multipart
    @POST("image/upLoadPortrait")
    Observable<ResponseInfo> uploadPortrait(@Part MultipartBody.Part fileToUpload, @Part MultipartBody.Part userId);*/
}
