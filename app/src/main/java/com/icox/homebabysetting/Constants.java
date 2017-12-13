package com.icox.homebabysetting;

import android.os.Environment;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-11-2 11:23
 * @des ${TODO}
 */
public class Constants {
    // 默认存储位置
    public final static String INTERNAL_MEMORY = Environment.getExternalStorageDirectory().getAbsolutePath();
    // 存储配置保存
    public final static String SHARE_NAME = "SHARE_NAME";
    public final static String SHARE_01 = "SAVE_PATH";

    /**
     * 配置保存目录
     */
    public static final String SHARE_DIR = "/.share_setting";
    // 存储配置保存文件名
    public final static String FILE_MNT = "saveMnt";

    public static int mScreenWidth = 0;
    public static int mScreenHeight = 0;
    public static int mScreenDip = 0;

    //    ********获得家长密码******
    static final String AUTHORITY = "cn.icoxedu.data";
    static final String INFO_TABLE = "info";
    static final String KEY = "_key";
    static final String PN = "_pn";
    public static final String VALUE = "_value";
    static final String KEY_PWD = "pwd";     // 家长管理密码
    public static final String KEY_IMEI = "imei";               // 设备识别码
    public static final String KEY_MAC = "mac";               // 设备MAC

    public static String PASSWORD = "";
    public static String PACKAGE_NAME = "";
    public static String IMEI = "";//识别码
    public static String MAC = "";//Mac

}
