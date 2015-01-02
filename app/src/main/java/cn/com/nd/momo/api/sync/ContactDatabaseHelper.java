
package cn.com.nd.momo.api.sync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.com.nd.momo.api.util.Log;

public class ContactDatabaseHelper {

    private static final String DATABASE_NAME = "momo.db3";

    private static final String TAG = "ContactDatabaseHelper";

    private static final int DATABASE_VERSION = 13;

    private static final int DATABASE_VERSION_ADD_RINGTONE_VERSION = 9;

    private static SQLiteDatabase db;

    private static Context context;

    private static SQLiteDatabase readDb;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createDatabase(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqlitedatabase, int oldVersion, int newVersion) {
            Log.d(TAG, "update database");
            if (oldVersion < DATABASE_VERSION_ADD_RINGTONE_VERSION) {
                context.deleteDatabase(DATABASE_NAME);
            } else if (oldVersion == DATABASE_VERSION_ADD_RINGTONE_VERSION) {
                addColumnRingtong(sqlitedatabase);
            }
        }

        private void createDatabase(SQLiteDatabase db) {
            // 联系人
            db.execSQL(SQLCreator.CONTACT);
            db.execSQL(SQLCreator.CONTACT_ID_IDX);
            db.execSQL(SQLCreator.CONTACT_PHONE_CID_IDX);
            db.execSQL(SQLCreator.CONTACT_UID_IDX);

            // 联系人数据
            db.execSQL(SQLCreator.DATA);
            db.execSQL(SQLCreator.DATA_CONTACT_ID_IDX);
            db.execSQL(SQLCreator.DATA_CONTACT_ID_PROPERTY_IDX);
            db.execSQL(SQLCreator.DATA_PROPERTY_IDX);

            // 头像
            db.execSQL(SQLCreator.IMAGE);
            db.execSQL(SQLCreator.IMAGE_CONTACT_ID_IDX);

            // 设置
            db.execSQL(SQLCreator.PROFILE);
            db.execSQL(SQLCreator.PROFILE_KEY_IDX);
        }
    }

    /**
     * 判断某张表是否存在
     * 
     * @param tabName 表名
     * @return
     */
    @SuppressWarnings("unused")
    private static boolean tabbleIsExist(SQLiteDatabase sqlitedatabase, String tableName) {
        boolean result = false;
        if (tableName == null || tableName.trim().length() < 1 || sqlitedatabase == null) {
            return false;
        }
        Cursor cursor = null;
        try {
            String sql = "select * from " + tableName.trim();
            cursor = sqlitedatabase.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void addColumnRingtong(SQLiteDatabase sqlitedatabase) {
        final String CONTACT_TABLE = "contact";
        final String COLUMN_RINGTONE = "custom_ringtone";
        if (null != sqlitedatabase) {
            Cursor c = null;
            try {
                c = sqlitedatabase.rawQuery("select custom_ringtone from contact limit 1", null);
            } catch (Exception e) {
                sqlitedatabase.execSQL("ALTER TABLE " + CONTACT_TABLE + " ADD " + COLUMN_RINGTONE
                        + " String");
            } finally {
                if (null != c)
                    c.close();
            }
        }
    }

    private ContactDatabaseHelper() {

    }

    /**
     * 获取一个数据库连接
     * 
     * @return
     */
    public static SQLiteDatabase getInstance() {
        Data data = MyThreadLocal.get();
        if (data == null) {
            data = new Data();
        }
        if (!data.isUseNewConnected) { // 如果无需创建一个新连接，就使用现有的全局数据库连接
            if (ContactDatabaseHelper.db == null || !ContactDatabaseHelper.db.isOpen()) {
                DatabaseHelper mOpenHelper = new DatabaseHelper(context);
                ContactDatabaseHelper.db = mOpenHelper.getWritableDatabase();
            }
            return ContactDatabaseHelper.db;
        } else {
            if (data.db == null || !data.db.isOpen()) {
                DatabaseHelper openHelper = new DatabaseHelper(context);
                data.db = openHelper.getWritableDatabase();
                MyThreadLocal.set(data);
            }
            return data.db;
        }
    }

    public static SQLiteDatabase getReadableDatabase() {
        if (readDb == null || !readDb.isOpen()) {
            DatabaseHelper openHelper = new DatabaseHelper(context);
            readDb = openHelper.getReadableDatabase();
        }
        return readDb;
    }

    public static void initDatabase(Context context) {
        ContactDatabaseHelper.context = context;
        if (ContactDatabaseHelper.db == null || !ContactDatabaseHelper.db.isOpen()) {
            DatabaseHelper OpenHelper = new DatabaseHelper(context);
            ContactDatabaseHelper.db = OpenHelper.getWritableDatabase();
        }
    }

    public static void set(boolean isUseNewConnected) {
        if (MyThreadLocal.get() == null) {
            Data data = new Data();
            data.isUseNewConnected = isUseNewConnected;
            MyThreadLocal.set(data);
        }

        Data data = MyThreadLocal.get();
        data.isUseNewConnected = isUseNewConnected;
        MyThreadLocal.set(data);
    }

    public static void closeThreadDb() {
        Data data = MyThreadLocal.get();
        if (data != null && data.db != null && data.db.isOpen())
            data.db.close();
    }

    public static void close() {
        if (ContactDatabaseHelper.db != null && ContactDatabaseHelper.db.isOpen()) {
            ContactDatabaseHelper.db.close();
        }
    }

    private static class MyThreadLocal {

        private static ThreadLocal<Data> tLocal = new ThreadLocal<Data>();

        public static void set(Data i) {
            tLocal.set(i);
        }

        public static Data get() {
            return tLocal.get();
        }
    }

    private static class Data {
        public boolean isUseNewConnected = false;

        public SQLiteDatabase db = null;
    }

}
