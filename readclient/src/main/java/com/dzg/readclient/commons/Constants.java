package com.dzg.readclient.commons;

/**
 * 常量类
 */
public class Constants {
    //	public static final String BASE_API_URL ="http://120.77.35.181/QiQuYingServer/";
//   public static final String BASE_API_URL ="http://10.0.2.2/QiQuYing/";
//    public static final String BASE_API_URL ="http://10.0.2.2/QiQuYingServer/";
//	public static final String BASE_API_URL="http://dzg.tunnel.echomod.cn/QiQuYingServer/";
//    public static final String BASE_API_URL="http://dzg.tunnel.echomod.cn/QiQuYing/";
    public static final String BASE_API_URL = "http://39.108.152.183/QiQuYing/";
    public static final long HTTP_CONNECT_TIMEOUT = 15000;
    public static final long HTTP_READ_TIMEOUT = 15000;
    public static final long HTTP_CACHE_SIZE = 1024 * 1024 * 10 * 8;
    public static final boolean DEBUG = false;

    //SplashActivity
    public static final String FIRST_USE = "firstUse";
    public static final String IS_RECEIVE_PUSH = "isReceivePush";
    public static final String IS_LOAD_IMG = "isLoadImg";

    //http请求超时时间
    public static final int REQUEST_TIME_OUT = 15 * 1000;
    public static final int DEFAULT = 0;
    public static final int SUCCESS = 200;
    public static final int FAILURE = 500;
    public static final int SUCCESS_1 = 201;
    public static final int FAILURE_1 = 501;
    public static final int SUCCESS_2 = 202;
    public static final int FAILURE_2 = 502;
    public static final int SUCCESS_3 = 301;
    public static final int FAILURE_3 = 302;
    public static final String PARAM_INVALUD = "501";
    public static final String SOURCE_NOT_EXISTS = "503";

    public static final String CACHE_ALL_NEW = "cache_all_new";
    public static final String CACHE_ALL_HOT = "cache_all_hot";
    public static final String CACHE_QUSHI_NEW = "cache_qushi_new";
    public static final String CACHE_QUSHI_HOT = "cache_qushi_hot";
    public static final String CACHE_QUTU_NEW = "cache_qutu_new";
    public static final String CACHE_QUTU_HOT = "cache_qutu_hot";
    public static final String CACHE_MEITU_NEW = "cache_meitu_new";
    public static final String CACHE_MEITU_HOT = "cache_meitu_hot";


    public static final String IMAGE_FILE_NAME = "portrait.jpg";
    public static final int PIC_SIZE = 200;
    public static final int GETALL = 0;
    public static final int GETJOKEBYID = 1;
    public static final int GETCOMMENTS = 2;
    public static final int CROP_CODE = 3;
    public static final int AllFRAGMENT = 100;
    public static final int USER = 101;
    public static final int FEEDBACK = 102;
    public static final int COMMENT = 103;
    public static final int UPDATE = 104;
    public static final int SETNICKANDSEX = 105;
    public static final int UPLOADPORTRAIT = 106;
    public static final int REGIST = 107;
    public static final int LOGIN = 108;
    public static final int CHECKUSEREXEIST = 109;
    public static final int GETCOMMENTBYJOKEID = 110;
    public static final int ADDCOMMENT = 111;
}
