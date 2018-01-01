package com.dzg.readclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.dzg.readclient.commons.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by hefuyi on 2016/11/3.
 */

public class FileUtil {

    private static final String HTTP_CACHE_DIR = "http";

    public static File getHttpCacheDir(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return new File(context.getExternalCacheDir(), HTTP_CACHE_DIR);
        }
        return new File(context.getCacheDir(), HTTP_CACHE_DIR);
    }

    public static void saveBitmap( Bitmap bitmap) {
        File avaterFile = new File(Environment.getExternalStorageDirectory(), Constants.IMAGE_FILE_NAME);//设置文件名称
        if (avaterFile.exists()) {
            avaterFile.delete();
        }
        try {
            avaterFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(avaterFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}