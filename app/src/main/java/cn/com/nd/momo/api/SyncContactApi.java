
package cn.com.nd.momo.api;

import java.util.List;

import android.accounts.Account;
import android.content.Context;
import android.os.Handler;
import cn.com.nd.momo.api.sync.ContactDatabaseHelper;
import cn.com.nd.momo.api.sync.LocalContactsManager;
import cn.com.nd.momo.api.sync.MoMoContactsManager;
import cn.com.nd.momo.api.types.Contact;
import cn.com.nd.momo.api.util.ConfigHelper;

public final class SyncContactApi {
    private static Context mContext = null;

    private static volatile SyncContactApi mInstance = null;

    public static final int MSG_SYNC_PROCESS_START = 1;

    public static final int MSG_SYNC_PROCESS_PART_FINISHED = MSG_SYNC_PROCESS_START + 1;

    public static final int MSG_SYNC_PROCESS_FINISHED = MSG_SYNC_PROCESS_START + 2;

    public static final int MSG_MQ_PROCESS_FINISHED = MSG_SYNC_PROCESS_START + 3;

    public static final int MSG_SYNC_NO_NET_WORK = MSG_SYNC_PROCESS_START + 4;
    
    public static final int MSG_SYNC_IN_PROCESS = MSG_SYNC_PROCESS_START + 5;
    
    private SyncContactApi() {

    }

    public static synchronized SyncContactApi getInstance(Context context) {
        if (null == mInstance) {
            mContext = context.getApplicationContext();
            ContactDatabaseHelper.initDatabase(mContext);
            mInstance = new SyncContactApi();
        }
        return mInstance;
    }

    /**
     * 根据服务器联系人Id获取联系人信息
     * 
     * @param contactId MoMoContactsManager.getInstance().
     * @return Contact
     */
    public Contact getContactById(long contactId) {
        return MoMoContactsManager.getInstance().getContactById(contactId);
    }
    
    /**
     * 获取同步方式
     * 
     * @return
     */
    public String getSyncMode() {
        ConfigHelper config = ConfigHelper.getInstance(mContext);
        String syncMode = config.loadKey(ConfigHelper.CONFIG_KEY_SYNC_MODE);
        return syncMode;
    }

    /**
     * 获取手机sim卡上的联系人总数
     * 
     * @return 联系人数量
     */
    public int getSimContactsCount() {
        return LocalContactsManager.getInstance().getSimContactsCount();
    }
    
    /**
     * 获取手机sim卡上所有联系人
     * 
     * @return 联系人列表
     */
    public List<Contact> getSimContactList() {
        return LocalContactsManager.getInstance().getSimContacts();
    }
    
    /**
     * 根据手机联系人帐号获取对用帐号所有联系人
     * 
     * @param account 手机帐号
     * @return 联系人列表
     */
    public List<Contact> getLocalContactListByAccount(Account account) {
        return LocalContactsManager.getInstance().getAllContactsListByAccount(account);
    }
    
    /**
     * 批量添加联系人到手机指定的帐号(包括空帐号)
     * 
     * @param contactList 联系人列表
     */
    public void addContactListToAccount(List<Contact> contactList, Account account) {
        LocalContactsManager.getInstance().batchAddContacts(contactList, account);
    }
    
    /**
     * 获取要显示的联系人列表（只有contactId, formatName, namePinyin三个字段，只为显示列表和搜索使用）
     * 
     * @return 联系人列表
     */
    public List<Contact> getDisplayContactList() {
        return MoMoContactsManager.getInstance().getAllContactsBriefInfo();
    }
    
}
