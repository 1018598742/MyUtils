package com.example.myblank;

import android.util.Log;

/**
 * 文件描述： 在代码中要打印log,就直接DebugUtil.debug(....).然后如果发布的时候,
 * 就直接把这个类的DEBUG 改成false,这样所有的log就不会再打印在控制台
 * 作者： Created by fta on 2017/4/18
 * 来源： CloudReader
 */

public class DebugUtil {
    public static final String TAG = "ContentValue";
    public static final boolean DEBUG = true;

    public static void i(String tag,String msg){
        if (DEBUG) {
            Log.i(tag,msg);
        }
    }


    public static void i(String msg){
        if (DEBUG) {
            Log.i(TAG,msg);
        }
    }

    public static void debug(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void debug(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void error(String tag, String error) {

        if (DEBUG) {

            Log.e(tag, error);
        }
    }

    public static void error(String error) {

        if (DEBUG) {

            Log.e(TAG, error);
        }
    }
}