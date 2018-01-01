package com.dzg.readclient.api;

/**
 * @author lling
 * @ClassName: ApiCallBack
 * @Description: 远程接口回调
 * @date 2015-7-20
 */
public interface ApiCallBack {

    void onSuccess(Object data);

    void onFailure(String msg);

    void onError(Exception e, String msg);
}
