package com.dzg.readclient.api;

import com.dzg.readclient.commons.ResponseInfo;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
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

public interface JokeService {
    @GET("api/list")
    Observable<ResponseInfo> getList(@Query("newOrHotFlag") int newOrHotFlag, @Query("type") int type, @Query("offset") int  offset, @Query("count") int count);
    @GET("api/comments")
    Observable<ResponseInfo> getComments(@Query("qushiId") int qushiId, @Query("offset") int  offset,@Query("count")int count);
    @FormUrlEncoded
    @POST("api/qushi/add")
    Observable<ResponseInfo> addQushi(@Field("content") String content, @Field("userId") int  userId);
    @FormUrlEncoded
    @POST("api/ding")
    Observable<ResponseInfo> postDing(@Field("ids") String ids);
    @FormUrlEncoded
    @POST("api/cai")
    Observable<ResponseInfo> postCai(@Field("ids") String ids);
    @GET("api/get_joke")
    Observable<ResponseInfo> getJokeById(@Query("jokeId") int jokeId);
    @FormUrlEncoded
    @POST("api/comment")
    Observable<ResponseInfo> postComment(@Field("jokeId") int jokeId,@Field("userId") int userId,@Field("userPortrait")String userPortrait,@Field("userNick")String userNick,@Field("content") String content);
    @FormUrlEncoded
    @POST("api/qutu/add")
    Observable<ResponseInfo> postQutu(@Field("title") String title,@Field("imgUrl") String imgUrl,@Field("userId")int userId);
    @FormUrlEncoded
    @POST("api/add")
    Observable<ResponseInfo> postMeitu(@Field("title") String title,@Field("imgUrl") String imgUrl,@Field("userId")int userId);
    @GET("meitu/list")
    Observable<ResponseInfo> getMeitu(@Query("newOrHotFlag") int newOrHotFlag, @Query("offset") int  offset,@Query("count")int count);
    @GET("qutu/list")
    Observable<ResponseInfo> getQutu(@Query("newOrHotFlag") int newOrHotFlag, @Query("offset") int  offset,@Query("count")int count);
    @FormUrlEncoded
    @POST("user/regist")
    Observable<ResponseInfo> regist(@Field("userName") String userName,@Field("password") String password);
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
    Observable<ResponseInfo> uploadPortrait(@Part MultipartBody.Part fileToUpload,@Part MultipartBody.Part userId);
  /*@Multipart
  @POST("image/upLoadPortrait")
  Observable<ResponseInfo> uploadPortrait(@Part MultipartBody.Part fileToUpload);*/
}
