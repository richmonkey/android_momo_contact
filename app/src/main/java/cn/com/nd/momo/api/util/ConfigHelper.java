
package cn.com.nd.momo.api.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * 配置项管理类
 * 
 * @author jiaolei
 */
public class ConfigHelper {

    private static final String TAG = "ConfigHelper";

    private static String PREF_NAME = "MomoConfig";

    private static ConfigHelper mInstance = null;

    private Context mContext;

    protected SharedPreferences mSettings;

    protected SharedPreferences.Editor mEditor;

    // uid use CONFIG_KEY_UID
    static public final String CONFIG_OAUTH_KEY = "oauth_key";

    static public final String CONFIG_OAUTH_SECRET = "oauth_secret";

    static public final String CONFIG_QNAME = "q_name";

    static public final String CONFIG_USER_STATUS = "user_status";
    
    static public final String CONFIG_SID = "sid";

    // sync
    public static final String SYNC_MODE_TWO_WAY = "1";

    public static final String SYNC_MODE_LOCAL_ONLY = "2";

    public static final String CONFIG_KEY_SYNC_MODE = "sync_mode";

    public static final String LAST_TIME_UPDATE_USER_ID = "update_user_id_time";

    // im

    static public final String CONFIG_KEY_UID = "uid";

    static public final String CONFIG_KEY_REALNAME = "realname";

    static public final String CONFIG_KEY_AVATAR = "avatar";

    // user name and ticket
    static public final String CONFIG_KEY_ZONE_CODE = "zone_code";

    static public final String CONFIG_KEY_PHONE_NUMBER = "phone_num";

    // user login status
    static public final String CONFIG_KEY_LOGIN_STATUS = "login_status";

    // message ring
    public static final String CONFIG_KEY_MESSAGE_RING = "message_ring";

    public static final String CONFIG_KEY_MESSAGE_SYSTEM_SOUND = "message_system_sound";

    public static final String CONFIG_KEY_MESSAGE_VIBRATE = "message_vibrate";

    public static final String CONFIG_KEY_GPRS_IMAGE = "gprs_image";

    // 短信拦截
    public static final String CONFIG_KEY_INTERCEPT_ALL = "sms_intercept_all";

    public static final String CONFIG_KEY_INTERCEPT_MOMO = "sms_intercept_momo";

    //上传用源文件
    public static final String CONFIG_KEY_FULL_SIZE = "upload_full_size";

    public static final String CONFIG_KEY_MOMO_ACCOUNT_CREATED = "momo_account_created";
    // 导入帐号选择
    public static final String CONFIG_KEY_IMPORT_ACCOUNTS = "import_accounts";

    public static final String CONFIG_KEY_SELECTED_ACCOUNTS = "selected_accounts";

    public static final String CONFIG_KEY_IMPORT_PHONE_IDS = "import_phone_ids";
    // momosns
    static public final int PHOTO_UPLOAD_COMPRESS = 780;

    static public final int PHOTO_UPLOAD_THUMB = 80;

    public static String CONFIG_KEY_BINDED_ACCOUNT_NAME = "binded_account_name";

    public static String CONFIG_KEY_BINDED_ACCOUNT_TYPE = "binded_account_type";

    public static String PATTERN_USER_PRE = "<font color=\"#5e92b6\" style=\"text-decoration:none\"><A href=\"momouser://user/";

    public static String PATTERN_USER = PATTERN_USER_PRE
            + "([^\"]*?)/([^<]*?)\">([^<]*?)</A></font>";

    public static String PATTERN_URL = "http://[^\\s]*";

    public static String CONFIG_KEY_SYNC_SINA = "sync_sina";

    // geography location
    public static String CONFIG_KEY_GEOGRAPHY_LOCATION = "geo_location";

    public static String CONFIG_KEY_SKIP_VERSION = "skip_version";

    public static String CONFIG_KEY_IS_FIRST_RUN = "is_first_run";

    // guide
    public static final String TAB_CONTACTS = "contacts";

    public static final String TAB_IM = "im";

    public static final String GUIDE_ISFIRST = "guide_isfirst";

    // 私聊
    public static final String CONFIG_KEY_IM_LAST_SMS_TIME = "im_last_sms_time"; // 最后推送短信时间前缀

    public static final String CONFIG_KEY_IM_LAST_EDITOR = "im_last_editor"; // 最后使用的是录音还是文本

    public static final String CONFIG_KEY_IM_AUTO_PLAY = "im_auto_play"; // 最后使用的是录音还是文本

    // flurry
    public static final String CONFIG_KEY_FLURRY_MQ_CONNECT_LOG_TIMES = "flurry_mq_connect_log_times"; // 记录mq链接失败次数

    public static final int MAX_MQ_CONNECT_LOG_TIMES = 30;

    // 可用短信数量
    public static final String SMS_COUNT = "sms_count";

    public static int SZIE_AVATAR = 70;
    
    // last launch tab
    public static final String CONFIG_KEY_LAST_TAB = "last_tab"; // 记录mq链接失败次数
    
    /**
     * 分享图片预览模式： 存储值0：小图（默认）； 1：大图； -1：无图
     */
    public static final String CONFIG_KEY_IMAGE_VIEW_MODE = "statuses_image_mode"; 

    public static ConfigHelper getInstance() {
        return mInstance;
    }

    public static void initInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ConfigHelper(context.getApplicationContext());
        }
    }

    public boolean commit() {
        return mEditor.commit();
    }

    public String loadKey(String key) {
        return mSettings.getString(key, "");
    }

    public void saveKey(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public void removeKey(String key) {
        mEditor.remove(key);
        mEditor.commit();
    }

    public void clearkeys() {
        mEditor.clear();
        mEditor.commit();
    }

    public boolean loadBooleanKey(String key, boolean defValue) {
        return mSettings.getBoolean(key, defValue);
    }

    public void saveBooleanKey(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public Set<String> loadStringSetKey(String key, Set<String> defValue) {
        return mSettings.getStringSet(key, defValue);
    }

    public void saveStringSetKey(String key, Set<String> s) {
        mEditor.putStringSet(key, s);
        mEditor.commit();
    }

    public int loadIntKey(String key, int defValue) {
        return mSettings.getInt(key, defValue);
    }

    public void saveIntKey(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public long loadLongKey(String key, long defValue) {
        return mSettings.getLong(key, defValue);
    }

    public void saveLongKey(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    private ConfigHelper(Context c) {
        setContext(c);
    }

    public void setContext(Context c) {
        mContext = c;
        if (c == null)
            Log.e(TAG, "the context point is null");

        mSettings = mContext.getSharedPreferences(PREF_NAME, 0);
        mEditor = mSettings.edit();

    }
}
