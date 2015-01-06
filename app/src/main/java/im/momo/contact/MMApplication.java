package im.momo.contact;

import android.app.Application;
import android.util.Log;

import com.google.code.p.leveldb.LevelDB;

import java.io.File;

import cn.com.nd.momo.api.sync.ContactDatabaseHelper;
import cn.com.nd.momo.api.util.ConfigHelper;
import cn.com.nd.momo.api.util.Utils;
import cn.com.nd.momo.manager.GlobalUserInfo;

/**
 * Created by houxh on 15-1-2.
 */
public class MMApplication extends Application {

    private static final String TAG = "contact";

    @Override
    public void onCreate() {
        super.onCreate();
        LevelDB ldb = LevelDB.getDefaultDB();
        String dir = getFilesDir().getAbsoluteFile() + File.separator + "db";
        Log.i(TAG, "dir:" + dir);
        ldb.open(dir);

        ContactDatabaseHelper.initDatabase(this.getApplicationContext());
        Utils.saveGlobleContext(this.getApplicationContext());
        GlobalUserInfo.setAppContext(this);
        ConfigHelper.initInstance(this);
    }
}
