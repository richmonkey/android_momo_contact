
package cn.com.nd.momo.api.sync;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 配置项管理类
 * 
 * @author jiaolei
 */
public class ContactSyncConfigHelper {

    private static String PREF_NAME = "ContactSyncConfig";

    private static ContactSyncConfigHelper mInstance = null;

    private Context mContext;

    protected SharedPreferences mSettings;

    protected SharedPreferences.Editor mEditor;

    public static final String SYNC_MODE_TWO_WAY = "1";

    public static final String SYNC_MODE_LOCAL_ONLY = "2";

    public static final String CONFIG_KEY_SYNC_MODE = "sync_mode";

    public static ContactSyncConfigHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ContactSyncConfigHelper(context.getApplicationContext());
        }

        return mInstance;
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

    private ContactSyncConfigHelper(Context c) {
        mContext = c;
        mSettings = mContext.getSharedPreferences(PREF_NAME, 0);
        mEditor = mSettings.edit();
    }
}
