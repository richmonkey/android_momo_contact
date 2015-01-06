
package cn.com.nd.momo.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.telephony.TelephonyManager;
import cn.com.nd.momo.api.util.ConfigHelper;
import cn.com.nd.momo.api.util.Log;


/**
 * 用户信息全局配置管理类
 * 
 * @author jiaolei
 */
/** 
 * TODO(这里用一句话描述这个类的作用) 
 * @author 曾广贤 (muroqiu@qq.com)
 *
 */
public class GlobalUserInfo {
    private static final String TAG = "GlobalUserInfo";

    public static final String FLURRY_STRING = "L5MR8QTKQ49K5MHLA424";

    public static final String DEFAULT_ZONE_CODE = "86";

    // 程序是否未退出（拦截短信用）
    public static boolean appAlive = false;

    // 程序是在前台（拦截短信用）
    public static boolean inFront = false;

    // application context
    static public Context mAppContext = null;

    // contact id for myselfmAppContext
    static public long MY_CONTACT_ID = 0;

    // my avatar
    private static Bitmap mAvatarMap = null;

    static private String mPhoneNumber = ""; // login phone number

    static private String mUID = ""; // uid

    static private String mNAME = ""; // name

    static private String mAvatarName = ""; // avatar

    // ToDo Zgx 20120731
    static private String mSessionID = ""; // session id

    static private String mZoneCode = DEFAULT_ZONE_CODE;

    static private int mResetPassword = 0;

    static private String mQName = "";

    static public int STATUS_UNACTIVE = 1;

    static public int STATUS_NORMAL_USER = 2;

    static public int STATUS_VERIFY_USER = 3;

    static private String mStatus = ""; // value should be: 1 or 2 or 3

    // parameters for login
    static public final int LOGIN_STATUS_UNLOGIN = 0;

    static public final int LOGIN_STATUS_LOGINED = 1;

    static public volatile int mLoginStatus = 0; // read only, unlogin 0,

    // logined 1

    // parameters for net sync
    static public final int NET_SYNC_OK = 0;

    static public final int NET_SYNC_DOING = 1;

    static public int mNetSyncStatus = 0; // net sync OK 0, net sync is doing 1

    // 用户信息缓存时间，15天有效期
    static public final long USER_CACHE_TIME = 15 * 24 * 60 * 60 * 1000;

    public static final long MOMO_XIAOMI_USER_ID = 353;

    public static final int STATUSES_IMAGE_MODE_SMALL = 0;
    public static final int STATUSES_IMAGE_MODE_BIG = 1;
    public static final int STATUSES_IMAGE_MODE_NO = -1;
    /**
     * 分享图片预览模式 
     */
    public static int statuses_image_mode = STATUSES_IMAGE_MODE_SMALL;
    
    static public final String MOMO_ACCOUNT_TYPE = "cn.com.nd.momo";
    
    /**
     * 91U调用时传递过来的参数
     */
    // public static boolean isCalledFrom91U = false;
    //    
    // public static String param_91U_session_id = "";
    //    
    // public static String param_91U_func = "";
    
    public static String getPhoneNumber() {
        if (mPhoneNumber == null || mPhoneNumber.length() < 1) {
            mPhoneNumber = ConfigHelper.getInstance().loadKey(
                    ConfigHelper.CONFIG_KEY_PHONE_NUMBER);
        }
        return mPhoneNumber;
    }

    public static void setPhoneNumber(String mPhoneNumber) {
        GlobalUserInfo.mPhoneNumber = mPhoneNumber;
        ConfigHelper cHelper = ConfigHelper.getInstance();
        cHelper.saveKey(ConfigHelper.CONFIG_KEY_PHONE_NUMBER, GlobalUserInfo.mPhoneNumber);
        cHelper.commit();
    }

    public static String getUID() {
        // return "11890616";
        return mUID;
    }

    public static void setUID(String mUID) {
        GlobalUserInfo.mUID = mUID;
    }

    public static String getName() {
        if ("".equals(mNAME)) {
            mNAME = ConfigHelper.getInstance().loadKey(ConfigHelper.CONFIG_KEY_REALNAME);
        }
        return mNAME;
    }

    public static void setName(String mName) {
        GlobalUserInfo.mNAME = mName;
        ConfigHelper cHelper = ConfigHelper.getInstance();
        cHelper.saveKey(ConfigHelper.CONFIG_KEY_REALNAME, GlobalUserInfo.mNAME);
        cHelper.commit();
    }

    public static String getAvatar() {
        if (mAvatarName == null || mAvatarName.length() == 0) {
            mAvatarName = ConfigHelper.getInstance().loadKey(
                    ConfigHelper.CONFIG_KEY_AVATAR);
        }
        return mAvatarName;
    }

    public static void setAvatar(String avatar) {
        GlobalUserInfo.mAvatarName = avatar;
        ConfigHelper cHelper = ConfigHelper.getInstance();
        cHelper.saveKey(ConfigHelper.CONFIG_KEY_AVATAR, GlobalUserInfo.mAvatarName);
        cHelper.commit();
    }


    public static void cancelResetPassword() {
        mResetPassword = 0;
    }

    public static int getNeedResetPassword() {
        return mResetPassword;
    }

    public static String getQName() {
        if (mQName == null || "".equals(mQName)) {
            return "momoim_" + mUID;
        } else {
            return mQName;
        }
    }

    public static int getUserStatus() {
        if (mStatus == null || "".equals(mStatus)) {
            return -1;
        } else {
            return Integer.valueOf(mStatus);
        }

    }

    public static void setUserStatus(int status) {
        mStatus = String.valueOf(status);
    }

    public static String getZoneCode() {
        return mZoneCode;
    }

    public static void setZoneCode(String code) {
        mZoneCode = code;
    }

    public static void initDefaultZoneCode() {
        mZoneCode = DEFAULT_ZONE_CODE;
    }

    public static String getDeviceIMEI() {
        TelephonyManager tm = (TelephonyManager)mAppContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        return imei;
    }



    // TODO WAITIING FOR DELETE
    public static String getSessionID() {
        if (mSessionID == null) {
            mSessionID = "";
        }
        return mSessionID;
    }

    // check configuration to see if user has logined
    // do this when program started
    public synchronized static int checkLoginStatus(Context context) {
        ConfigHelper cHelper = ConfigHelper.getInstance();
        String strStatus = cHelper.loadKey(ConfigHelper.CONFIG_KEY_LOGIN_STATUS);
        Log.d(TAG, "login status: " + strStatus);

        // if user logined, we get all config out
        if (strStatus.equals(String.valueOf(LOGIN_STATUS_LOGINED))) {
            mLoginStatus = LOGIN_STATUS_LOGINED;

            mPhoneNumber = cHelper.loadKey(ConfigHelper.CONFIG_KEY_PHONE_NUMBER);
            mNAME = cHelper.loadKey(ConfigHelper.CONFIG_KEY_REALNAME);
            mUID = cHelper.loadKey(ConfigHelper.CONFIG_KEY_UID);

            mQName = cHelper.loadKey(ConfigHelper.CONFIG_QNAME);

            mStatus = cHelper.loadKey(ConfigHelper.CONFIG_USER_STATUS);
        } else {
            mLoginStatus = LOGIN_STATUS_UNLOGIN;
        }
        return mLoginStatus;
    }

    public synchronized static void setLoginStatus(int nStatus) {
        ConfigHelper.getInstance().saveKey(ConfigHelper.CONFIG_KEY_LOGIN_STATUS,
                String.valueOf(LOGIN_STATUS_LOGINED));
        ConfigHelper.getInstance().commit();
        mLoginStatus = nStatus;
    }

    /**
     * check login status
     * 
     * @return
     */
    public synchronized static boolean hasLogined() {
        Log.d(TAG, "haslogined called: " + mLoginStatus);
        if (mLoginStatus == LOGIN_STATUS_UNLOGIN) {
            return false;
        } else {
            return true;
        }
    }

    public static String getSyncMode(Context c) {
        ConfigHelper config = ConfigHelper.getInstance();
        String syncMode = config.loadKey(ConfigHelper.CONFIG_KEY_SYNC_MODE);
        return syncMode;
    }

    /**
     * 获取当前登录用户手机区号
     * 
     * @param context
     * @return 手机区号
     */
    public static String getCurrentZoneCode(Context context) {
        String zoneCode = GlobalUserInfo.DEFAULT_ZONE_CODE;
        ConfigHelper configHelper = ConfigHelper.getInstance();
        zoneCode = configHelper.loadKey(ConfigHelper.CONFIG_KEY_ZONE_CODE);
        if (zoneCode.equals("")) {
            zoneCode = GlobalUserInfo.DEFAULT_ZONE_CODE;
        }
        return zoneCode;
    }



    /**
     * check net sync status
     * 
     * @return true: net sync is now in processing
     */
    public static boolean isNetSyncDoing() {
        if (mNetSyncStatus == NET_SYNC_DOING) {
            return true;
        } else {
            return false;
        }
    }

    private static Thread mSyncThread = null;

    private static boolean mStopSync = false;



    public static void startSyncThread(Thread t) {
        mStopSync = false;
        mSyncThread = t;
        t.start();
    }

    public static void setAppContext(Context c) {
        mAppContext = c.getApplicationContext();
    }

    public static Bitmap getMyAvatarInMemory() {
        return mAvatarMap;
    }

    public static void setMyAvatar(Bitmap imgAvatar) {
        mAvatarMap = imgAvatar;
    }


    public final static String PARAM_STATUSES_ID = "STATUSES_ID";
}
