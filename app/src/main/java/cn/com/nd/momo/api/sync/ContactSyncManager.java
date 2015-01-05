
package cn.com.nd.momo.api.sync;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.text.TextUtils;

import cn.com.nd.momo.api.SyncContactHttpApi;
import cn.com.nd.momo.api.exception.MoMoException;
import cn.com.nd.momo.api.types.Address;
import cn.com.nd.momo.api.types.Avatar;
import cn.com.nd.momo.api.types.Contact;
import cn.com.nd.momo.api.types.MyAccount;
import cn.com.nd.momo.api.util.ConfigHelper;
import cn.com.nd.momo.api.util.Log;
import cn.com.nd.momo.api.util.Utils;
import android.util.Base64;


/**
 * contact sync manager
 * 
 * @author chenjp
 */
public class ContactSyncManager {

    private static String TAG = "ContactSyncManager";

    private final static String CONTACT_DEFFULT_AVATAR_KEY_WORDS = "head_default";

    private static ContactSyncManager instance = null;

    public static String KEY_ID = "id";

    public static String KEY_NAME = "name";

    private static MoMoContactsManager momoContactsManager;

    private static LocalContactsManager localContactsManager;

    private static int COUNT_TO_DELETE_EACH_TIME = 8;

    private static int COUNT_TO_ADD_EACH_TIME = 100;

    private static int COUNT_TO_RETRIEVE_CONTACTS = 100;

    private static int COUNT_TO_INSERT_AVATAR = 50;

    private static ContactSyncManager syncManager;
    private  boolean needStopSync = false;

    private boolean needForceDownloadAvatar = false;

    private List<Long> needMergeLocalList = new ArrayList<Long>();

    private ContactSyncManager() {
    }

    public static ContactSyncManager getInstance() {
        if (null == instance) {
            instance = new ContactSyncManager();
            momoContactsManager = MoMoContactsManager.getInstance();
            localContactsManager = LocalContactsManager.getInstance();
            syncManager = instance;
        }
        return instance;
    }

    private Map<Long, Contact> needRestoreContactMap = new HashMap<Long, Contact>();

    /**
     * 同步联系人
     */
    public void syncContacts() {
        Account account = Utils.getCurrentAccount();
        if (!Utils.isBindedAccountExist(account)) {
            Log.w(TAG, "momo account is't exist, please add momo account first!!!");
            return;
        }
        needRestoreContactMap.clear();
        updateLocalContactsToServer();
        updateMoMoContactsFromServer();
        Log.d(TAG, "sync end");
    }

    /**
     * 先传改变的，再传新增的，最后传删除的。 上传改变的可能会冲突，冲突就转为新增 删除好友可能会失败，失败之后反写到手机联系人。
     * 上传完成后，才会写入MOMO数据库。
     */
    private void updateLocalContactsToServer() {
        if (syncManager.needStopSync)
            return;
        List<Contact> momoContactsList = momoContactsManager.getAllContactsIdList();
        Collections.sort(momoContactsList, PhoneCIdComparator.getInstance());
        int momoSize = momoContactsList.size();
        Log.d(TAG, "all contacts on momo:" + momoSize);
        Log.d(TAG, "momo contact id:" + logPhoneCid(momoContactsList));
        List<Contact> localContactsList = localContactsManager.getAllContactsList();
        int localSize = localContactsList.size();
        Log.d(TAG, "all contacts on local:" + localSize);
        Collections.sort(localContactsList, PhoneCIdComparator.getInstance());
        Log.d(TAG, "local contact id:" + logPhoneCid(localContactsList));
        if (momoSize < 1 && localSize < 1)
            return;

        List<Long> toDeleteToServerList = new ArrayList<Long>();
        List<Contact> toUpdateToServerList = new ArrayList<Contact>();
        List<Contact> toAddToServerList = new ArrayList<Contact>();
        int localIndex = 0;
        int momoIndex = 0;
        while (true) {
            if (momoIndex == momoSize)
                break;
            if (localIndex == localSize)
                break;
            Contact localContact = localContactsList.get(localIndex);
            long localPhoneCid = localContact.getPhoneCid();

            Contact momoContact = momoContactsList.get(momoIndex);
            long momoPhoneCid = momoContact.getPhoneCid();
            if (momoPhoneCid < 1) {
                Log.e(TAG, "momoPhoneCid less than 1:" + momoPhoneCid);
                momoIndex++;
                continue;
            }
            if (localPhoneCid == momoPhoneCid) {
                if (!momoContact.equals(localContact)) {
                    localContact.setContactId(momoContact.getContactId());
                    localContact.setModifyDate(momoContact.getModifyDate());
                    toUpdateToServerList.add(localContact);
                    saveContactProperties(localContact);
                }
                localIndex++;
                momoIndex++;
            } else if (momoPhoneCid < localPhoneCid) {
                toDeleteToServerList.add(momoContact.getContactId());
                momoIndex++;
            } else {
                toAddToServerList.add(localContact);
                localIndex++;
            }
        }
        // 先传改变的
        int toUpdateSize = toUpdateToServerList.size();
        Log.d(TAG, "update to server: " + toUpdateSize);
        List<Contact> needUpdateToMoMoList = new ArrayList<Contact>();
        List<Contact> needMergeMoMoList = new ArrayList<Contact>();
        for (int i = 0; i < toUpdateSize; i++) {
            Contact contact = toUpdateToServerList.get(i);
            int resultCode = updateContactsToServer(contact);
            if (HttpStatus.SC_OK == resultCode) {
                contact.setSavedToLocal(true);
                long modifyDate = contact.getModifyDate();
                if (modifyDate < 1) {
                    // 需要重新到服务端取数据，先删除，然后再从服务端更新时处理
                    needMergeMoMoList.add(contact);
                } else {
                    needUpdateToMoMoList.add(contact);
                }
            } else if (resultCode == 409) {
                // 409(Conflict) 联系人冲突，改用新增接口
                Log.d(TAG, "上传联系人冲突，改为新增接口: " + contact.getFormatName());
                localContactsList.add(contact);
            }
        }
        if (needUpdateToMoMoList.size() > 0)
            momoContactsManager.updateContacts(needUpdateToMoMoList);
        if (needMergeMoMoList.size() > 0) {
            Log.d(TAG, "need merge momo count:" + needMergeMoMoList.size());
            boolean result = momoContactsManager.batchDeleteContact(needMergeMoMoList);
            if (result) {
                for (Contact contact : needMergeMoMoList) {
                    long phoneCid = contact.getPhoneCid();
                    saveContactProperties(contact);
                    needMergeLocalList.add(phoneCid);
                }
            }
        }
        toUpdateToServerList.clear();
        // 再传新增的
        localSize = localContactsList.size();
        if (localIndex < localSize)
            toAddToServerList.addAll(localContactsList.subList(localIndex, localSize));
        int toAddSize = toAddToServerList.size();
        try {
            Log.d(TAG, "add to server: " + toAddSize);
            addContactsToServer(toAddToServerList);
            toAddToServerList.clear();
        } catch (MoMoException e) {
            e.printStackTrace();
            return;
        }
        if (momoIndex < momoSize) {
            for (int i = momoIndex; i < momoSize; i++) {
                Contact contact = momoContactsList.get(i);
                long contactId = contact.getContactId();
                toDeleteToServerList.add(contactId);
            }
        }
        batchDeleteContacts(toDeleteToServerList);
        toDeleteToServerList.clear();
        toAddToServerList = null;
        toUpdateToServerList = null;
        toDeleteToServerList = null;
        System.gc();
    }

    /**
     * 上传本地手机所有联系人到服务器
     */
    public boolean uploadLocalContactsToServer() {
        if (syncManager.needStopSync) {
            return false;
        }

        List<Contact> localContactsList = localContactsManager.getAllContactsList();
        try {
            addContactsToServer(localContactsList);
            return true;
        } catch (MoMoException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (localContactsList != null) {
                localContactsList.clear();
                localContactsList = null;
            }
        }
    }

    /**
     * 由于是先上传再下载，下载只可能是新增或删除，不存在更新的可能。
     */
    private void updateMoMoContactsFromServer() {
        if (syncManager.needStopSync)
            return;
        Log.d(TAG, "update from server to momo db");
        List<Contact> serverContactsList = getAllRemoteContactsSimpleInfoList();
        if (null == serverContactsList)
            return;
        List<Contact> momoContactsList = momoContactsManager.getAllContactsIdList();
        Log.d(TAG, "all contacts on server:" + serverContactsList.size());
        Log.d(TAG, "all contacts on momo:" + momoContactsList.size());
        Collections.sort(serverContactsList, ContactIdComparator.getInstance());
        Log.d(TAG, "server contact id:" + logId(serverContactsList));
        Collections.sort(momoContactsList, ContactIdComparator.getInstance());
        Log.d(TAG, "momo contact id:" + logId(momoContactsList));

        List<Contact> needDeleteToMoMoList = new ArrayList<Contact>();
        List<Contact> needUpdateToMoMoList = new ArrayList<Contact>();
        List<Contact> needAddToMoMoList = new ArrayList<Contact>();
        int serverSize = serverContactsList.size();
        int momoSize = momoContactsList.size();
        int serverIndex = 0;
        int momoIndex = 0;
        while (true) {
            if (momoIndex == momoSize)
                break;
            if (serverIndex == serverSize)
                break;
            Contact serverContact = serverContactsList.get(serverIndex);
            long serverContactId = serverContact.getContactId();

            Contact momoContact = momoContactsList.get(momoIndex);
            long momoContactId = momoContact.getContactId();

            if (serverContactId == momoContactId) {
                long serverModifiDate = serverContact.getModifyDate();
                long momoModifiDate = momoContact.getModifyDate();
                if (serverModifiDate != momoModifiDate) {
                    Log.d(TAG, "server modifi Date:" + serverModifiDate);
                    Log.d(TAG, "momo modifi Date:" + momoModifiDate);
                    long phoneCid = momoContact.getPhoneCid();
                    serverContact.setPhoneCid(phoneCid);
                    needUpdateToMoMoList.add(serverContact);
                    saveContactProperties(momoContact);
                }
                serverIndex++;
                momoIndex++;
            } else if (serverContactId < momoContactId) {
                needAddToMoMoList.add(serverContact);
                serverIndex++;
            } else {
                needDeleteToMoMoList.add(momoContact);
                momoIndex++;
            }
        }
        if (serverIndex < serverSize) {
            needAddToMoMoList.addAll(serverContactsList.subList(serverIndex, serverSize));
        }

        if (momoIndex < momoSize) {
            needDeleteToMoMoList.addAll(momoContactsList.subList(momoIndex, momoSize));
        }
        int needUpdateCount = needUpdateToMoMoList.size();
        Log.d(TAG, "需要更新到MOMO的联系人个数：" + needUpdateCount);
        batchUpdateToLocal(needUpdateToMoMoList);

        int needAddCount = needAddToMoMoList.size();
        Log.d(TAG, "需要添加到MOMO的联系人个数：" + needAddCount);
        batchAddToLocal(needAddToMoMoList);

        int needDeleteCount = needDeleteToMoMoList.size();
        Log.d(TAG, "需要删除MOMO的联系人个数：" + needDeleteCount);
        batchDeleteToLocal(needDeleteToMoMoList);
        int mergeCount = needMergeLocalList.size();
        if (mergeCount > 0) {
            Log.d(TAG, "deleteing the contacts to merge:" + mergeCount);
            localContactsManager.batchDeleteContactsByIdList(needMergeLocalList);
            needMergeLocalList.clear();
        }
        batchRestoreContactProperties();
        needUpdateToMoMoList.clear();
        needAddToMoMoList.clear();
        needDeleteToMoMoList.clear();
        needUpdateToMoMoList = null;
        needAddToMoMoList = null;
        needDeleteToMoMoList = null;
        System.gc();
    }

    /**
     * 批量更新本地联系人
     * 
     * @param contactsList
     */
    private void batchUpdateToLocal(List<Contact> contactsList) {
        batchAddOrUpdateByStep(contactsList, false);
    }

    /**
     * batch add/update contatct from server bystep
     * 
     * @param contactsList
     * @param isAdd
     */
    private void batchAddOrUpdateByStep(List<Contact> contactsList, boolean isAdd) {
        if (syncManager.needStopSync || null == contactsList)
            return;
        int serverContactsToAddCount = contactsList.size();
        if (serverContactsToAddCount < 1)
            return;
        int times = serverContactsToAddCount / COUNT_TO_RETRIEVE_CONTACTS + 1;
        for (int i = 0; i < times; i++) {
            List<Contact> list;
            int begin = i * COUNT_TO_RETRIEVE_CONTACTS;
            if (i == times - 1) {
                list = contactsList.subList(begin, serverContactsToAddCount);
            } else {
                int end = (i + 1) * COUNT_TO_RETRIEVE_CONTACTS;
                list = contactsList.subList(begin, end);
            }
            if (isAdd)
                batchAddContactDetails(list);
            else
                batchUpdateContactDetails(list);
        }
    }

    /**
     * batcha dd to * @param contactsList
     */
    private void batchAddToLocal(List<Contact> contactsList) {
        if (syncManager.needStopSync)
            return;
        if (null != contactsList) {
            int serverContactsToAddCount = contactsList.size();
            needForceDownloadAvatar = true;
            if (serverContactsToAddCount > 0) {
                batchAddOrUpdateByStep(contactsList, true);
            }
            needForceDownloadAvatar = false;
        }
    }

    /**
     * 批量添加联系人到ｍｏｍｏ与手机
     */
    private void batchAddContactDetails(List<Contact> list) {
        if (syncManager.needStopSync || null == list || list.size() < 1)
            return;
        List<Contact> contactDetailsList = getRemoteContactsDetailsList(list, true);
        if (null != contactDetailsList && contactDetailsList.size() > 0) {
                Log.d(TAG, "正要写入手机：" + contactDetailsList.size());
                List<Contact> resultList = localContactsManager
                        .batchAddContacts(contactDetailsList);
                if (null != resultList && resultList.size() > 0) {
                    Log.d(TAG, "正要写入momo：" + resultList.size());
                    boolean result = momoContactsManager.batchAddContacts(resultList, true);
                    Log.d(TAG, "写入momo結果：" + result);
                    if (result)
                        batchInsertAvatar(resultList);
                } else {
                }

        }
    }

    private void saveContactProperties(Contact contact) {
        if (null == contact)
            return;
        long serverContactId = contact.getContactId();
        long phoneCid = contact.getPhoneCid();
        if (serverContactId < 1 || phoneCid < 1)
            return;
        Contact storedContact = needRestoreContactMap.get(serverContactId);
        if (null == storedContact) {
            storedContact = new Contact();
            needRestoreContactMap.put(serverContactId, storedContact);
        }
        storedContact.setContactId(serverContactId);
        storedContact.setPhoneCid(phoneCid);
        List<Long> categoryIdList = contact.getCategoryIdList();
        if (contact.isFavoried())
            storedContact.setFavoried(true);
        if (null != categoryIdList && categoryIdList.size() > 0)
            storedContact.setCategoryIdList(categoryIdList);
        Avatar avatar = contact.getAvatar();
        if (null != avatar) {
            byte[] avatarImage = avatar.getMomoAvatarImage();
            if (null != avatarImage && avatarImage.length > 0)
                storedContact.setAvatar(avatar);
        }

        String ringtone = localContactsManager.getCustomRingtoneByPhoneCid(phoneCid);
        if (null != ringtone && ringtone.length() > 0)
            storedContact.setCustomRingtone(ringtone);
    }

    /**
     * 更新联系人的詳細信息
     */
    private void batchUpdateContactDetails(List<Contact> needUpdateList) {
        if (syncManager.needStopSync || null == needUpdateList)
            return;
        List<Contact> contactListFromServer = getRemoteContactsDetailsList(needUpdateList, false);
        List<Contact> resultList = updateContactAvatarFromServer(contactListFromServer);
        if (null != resultList && resultList.size() > 0) {
            Log.d(TAG, "正要更新momo：" + resultList.size());
            for (Contact contact : resultList) {
                contact.setSavedToLocal(true);
            }

            boolean result = momoContactsManager.updateContacts(resultList);
            Log.d(TAG, "update momo result:" + result);
            if (result) {
                Log.d(TAG, "正要更新手机：" + resultList.size());
                localContactsManager.batchUpdateContact(resultList);
            }
        }
    }

    /**
     * 图像下载完之后批量写到库里
     */
    private void batchInsertAvatar(List<Contact> contactsList) {
        if (syncManager.needStopSync || null == contactsList)
            return;
        List<Contact> contactListWithAvatar = updateContactAvatarFromServer(contactsList);
        if (null == contactListWithAvatar)
            return;
        int nSize = contactListWithAvatar.size();
        if (nSize > 0) {
            int index = 0;
            while (index < nSize) {
                if (syncManager.needStopSync)
                    return;
                List<Contact> lstTemp;
                int currentEnd = index + COUNT_TO_INSERT_AVATAR;
                boolean needBreak = false;
                if (currentEnd < nSize) {
                    lstTemp = contactListWithAvatar.subList(index, currentEnd);
                } else {
                    lstTemp = contactListWithAvatar.subList(index, nSize);
                    needBreak = true;
                }
                momoContactsManager.batchInsertAvatar(lstTemp);
                momoContactsManager.batchUpdateContactsCRC(lstTemp);
                localContactsManager.batchAddPhoto(lstTemp);

                if (needBreak)
                    break;
                index = currentEnd;
            }
        }

    }

    /**
     * batch delete momo/local contacts
     * 
     * @param needDeleteList
     */
    private void batchDeleteToLocal(List<Contact> needDeleteList) {
        if (syncManager.needStopSync)
            return;
        if (null != needDeleteList && needDeleteList.size() > 0) {
            Log.d(TAG, "正要删除momo：" + needDeleteList.size());
            boolean result = momoContactsManager.batchDeleteContact(needDeleteList);
            if (result) {
                Log.d(TAG, "正要删除手机：" + needDeleteList.size());
                localContactsManager.batchDeleteContacts(needDeleteList);
            }
        }

    }

    /**
     * 还原联系人的分组与收藏
     */
    private void batchRestoreContactProperties() {
        Collection<Contact> contacts = needRestoreContactMap.values();
        List<Contact> list = new ArrayList<Contact>(contacts);
        if (list.size() > 0) {
            boolean result = momoContactsManager.batchSaveContactProperties(list);
            Log.d(TAG, "restore contact to momo result:" + result);
            if (result) {
                boolean localResult = localContactsManager.batchUpdateContact(list);
                Log.d(TAG, "restore contact to local result:" + localResult);
            }
        }
        needRestoreContactMap.clear();
    }

    /**
     * @Description: 从服务器下载更新联系人的头像
     */
    private List<Contact> updateContactAvatarFromServer(List<Contact> contactsList) {
        if (null == contactsList)
            return new ArrayList<Contact>();
        int size = contactsList.size();
        for (int i = 0; i < size; i++) {
            if (syncManager.needStopSync)
                return null;
            Contact contact = contactsList.get(i);
            Avatar avatar = contact.getAvatar();
            boolean needDownloadAvatar = false;
            if (needForceDownloadAvatar)
                needDownloadAvatar = true;
            else if (contact.isNeedDownloadAvatar())
                needDownloadAvatar = true;

            if (null != avatar && needDownloadAvatar) {
                String serverURL = avatar.getServerAvatarURL();
                if (null != serverURL && serverURL.length() > 0) {
                    byte[] image = downloadImageFromServer(serverURL);
                    if (null != image && image.length > 0)
                        avatar.setMomoAvatarImage(image);
                }
            }
            contact.setContactCRC(String.valueOf(contact.generateCRC()));
        }
        return contactsList;
    }

    /**
     * @param isNew 标志待更新联系人（isNew = false）或新增联系人（isNew = true）2种情况，判断是否下载联系人头像
     * @return
     */
    public List<Contact> getRemoteContactsDetailsList(List<Contact> list, boolean isNew) {
        if (syncManager.needStopSync || null == list || list.size() < 1)
            return null;
        List<Contact> contactsList = new ArrayList<Contact>();
        try {
            contactsList = SyncContactHttpApi.getContactDetailsList(list);
            if (contactsList != null) {
                int length = contactsList.size();
                for (int i = 0; i < length; ++i) {
                    Contact contact = contactsList.get(i);
                    Contact localContact = list.get(i);
                    long phoneCid = localContact.getPhoneCid();
                    contact.setPhoneCid(phoneCid);
                    long contactId = contact.getContactId();
                    localContact.setContactId(contactId);
                    saveContactProperties(localContact);
                    restoreContactProperties(contact);
                }
            }

        } catch (MoMoException e) {
            e.printStackTrace();
        }
        return contactsList;
    }

    /**
     * 从服务端获取联系人的简要信息
     */
    private List<Contact> getAllRemoteContactsSimpleInfoList() {
        List<Contact> contactsList = null;
        try {
            contactsList = SyncContactHttpApi.getAllContactSimpleInfoList();
        } catch (MoMoException e) {
            e.printStackTrace();
        }
        return contactsList;
    }

    /**
     * @Title: updateContactsToServer
     * @Description: 更新联系人到服务端
     */
    public int updateContactsToServer(Contact contact) {
        if (syncManager.needStopSync) {
            return -1;
        }
        try {
            String response = SyncContactHttpApi.updateContact(contact);
            Log.d(TAG, response);
            JSONObject result = new JSONObject();
            long modifiedAt = result.optLong("modified_at");
            Log.d(TAG, "update to server result modifiedDate:" + modifiedAt);
            contact.setModifyDate(modifiedAt);
            return HttpStatus.SC_OK;
        } catch (MoMoException e) {
            Log.e(TAG, "updateContactsToServer" + e.toString());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 上传联系人
     */
    private void addContactsToServer(List<Contact> contactsList) throws MoMoException {
        if (null == contactsList || contactsList.size() < 1)
            return;
        int nSize = contactsList.size();
        if (nSize > 0) {
            int index = 0;
            while (index < nSize) {
                if (syncManager.needStopSync)
                    return;
                List<Contact> list;
                int currentEnd = index + COUNT_TO_ADD_EACH_TIME;
                if (currentEnd < nSize) {
                    list = contactsList.subList(index, currentEnd);
                    addContactsToServerByStep(list);
                } else {
                    list = contactsList.subList(index, nSize);
                    addContactsToServerByStep(list);
                    break;
                }

                index = currentEnd;
            }
        }
    }

    /**
     * 还原联系人属性，比如收藏、分组
     * 
     * @param contact
     */
    private void restoreContactProperties(Contact contact) {
        if (null == contact)
            return;
        long contactId = contact.getContactId();
        if (contactId < 1)
            return;
        Contact savedContact = needRestoreContactMap.get(contactId);
        if (null != savedContact) {
            boolean isFav = savedContact.isFavoried();
            if (isFav)
                contact.setFavoried(true);
            String ringtone = savedContact.getCustomRingtone();
            if (null != ringtone && ringtone.length() > 0)
                contact.setCustomRingtone(ringtone);
            List<Long> categoryIdListSaved = savedContact.getCategoryIdList();
            if (null != categoryIdListSaved && categoryIdListSaved.size() > 0) {
                contact.getCategoryIdList().addAll(categoryIdListSaved);
                savedContact.getCategoryIdList().clear();
            }
            Avatar savedAvatar = savedContact.getAvatar();
            if (null != savedAvatar) {
                byte[] image = savedAvatar.getMomoAvatarImage();
                if (null != image && image.length > 0) {
                    contact.setAvatar(savedAvatar);
                }
            }
            savedContact = null;
            needRestoreContactMap.remove(contactId);
        }
    }

    /**
     * 分批次上传联系人
     */
    private List<Contact> addContactsToServerByStep(final List<Contact> contactsList)
            throws MoMoException {
        if (null == contactsList || contactsList.size() < 1) {
            return new ArrayList<Contact>();
        }
        try {
            String response = SyncContactHttpApi.addContactList(contactsList);
            handleResponseOfAddToServer(response, contactsList);
        } catch (MoMoException e) {
            Log.e(TAG, "addContactsToServerByStep" + e.toString());
            e.printStackTrace();

        }
        return contactsList;
    }

    /**
     * 上传联系人之后的处理
     */
    private void handleResponseOfAddToServer(String response, List<Contact> contactsList) {
        Log.d(TAG, response);
        JSONObject resultObj;
        try {
            resultObj = new JSONObject(response);
            Iterator<Contact> contactIter = contactsList.iterator();
            while (contactIter.hasNext()) {
                if (syncManager.needStopSync)
                    return;
                Contact contact = contactIter.next();
                long phoneCid = contact.getPhoneCid();
                JSONObject eachObj = (JSONObject)resultObj.get(String.valueOf(phoneCid));
                int status = eachObj.getInt("status");
                long contactId = eachObj.getLong("id");
                contact.setContactId(contactId);
                if (201 == status) {
                    long modifiedDate = eachObj.optLong("modified_at");
                    contact.setModifyDate(modifiedDate);
                } else if (303 == status) {
                    // 说明为合并数据，这时需要直接删除本地的数据，然后通过从服务端下载更新时添加到本地，以避免重复数据。
                    if (phoneCid > 0) {
                        saveContactProperties(contact);
                        needMergeLocalList.add(phoneCid);
                        contact.setToDelete(true);
                    }
                } else {
                    Log.e(TAG, "add contact to server result error, status: " + status);
                }
                if (!contact.isToDelete()) {
                    restoreContactProperties(contact);
                    contact.setContactCRC(String.valueOf(contact.generateCRC()));
                }
            }
            if (null != contactsList && contactsList.size() > 0) {
                Log.d(TAG, "add to momo: " + contactsList.size());
                boolean result = momoContactsManager.batchAddContacts(contactsList, true);
                Log.d(TAG, "add to momo result: " + result);
                if (result) {
                    momoContactsManager.batchInsertAvatar(contactsList);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "json exception" + e.toString());
        }
    }

    /**
     * 删除服务端的联系人
     */
    private List<Boolean> deleteContactsToServer(List<Long> toDeleteIdList) {
        if (null == toDeleteIdList)
            return null;
        List<Boolean> resultList = new ArrayList<Boolean>();
        try {
            resultList = SyncContactHttpApi.deleteContactList(toDeleteIdList);
        } catch (MoMoException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * 删除服务端联系人
     * 
     * @param toDeleteList
     */
    private void batchDeleteContacts(List<Long> toDeleteList) {
        if (null == toDeleteList)
            return;
        int count = toDeleteList.size();
        Log.d(TAG, "need server delete count: " + count);
        if (count > 0) {
            Account account = Utils.getCurrentAccount();
            if(account != null && MyAccount.ACCOUNT_MOBILE_NAME.equals(account)) {
                account = Utils.getVendorAccount();
            }
            int index = 0;
            while (index < count) {
                List<Long> list;
                int currentEnd = index + COUNT_TO_DELETE_EACH_TIME;
                if (currentEnd < count) {
                    list = toDeleteList.subList(index, currentEnd);
                } else {
                    list = toDeleteList.subList(index, count);
                }
                List<Boolean> result = deleteContactsToServer(list);
                int size = result.size();
                if (null != result && size > 0) {
                    for (int j = 0; j < size; j++) {
                        boolean deleted = result.get(j);
                        long serverContactId = list.get(j);
                        if (deleted) {
                            momoContactsManager.delContact(serverContactId);
                        } else {
                            // 反写到本地库
                            Contact contact = momoContactsManager.getContactById(serverContactId);
                            if (null != contact) {
                                long phoneCid = localContactsManager.addContact(contact, account);
                                if (phoneCid > 0) {
                                    contact.setPhoneCid(phoneCid);
                                    momoContactsManager.updateContactPhoneCid(contact);
                                }
                            }
                        }
                    }
                }
                index = currentEnd;
            }
        }
        toDeleteList.clear();
    }

    /**
     * ＊从服务端下载头像
     */
    private byte[] downloadImageFromServer(String avatarUrl) {
        byte[] avatarByte = null;
        if (null == avatarUrl || "".equals(avatarUrl)
                || avatarUrl.contains(CONTACT_DEFFULT_AVATAR_KEY_WORDS)) {
            return avatarByte;
        }
        try {
            SyncContactHttpApi.downloadContactAvatar(avatarUrl);
        } catch (MoMoException e) {
            e.printStackTrace();
        }
        return avatarByte;
    }

    /**
     * 将手机联系人写到momo联系人
     */
    private void addContactsToMoMo(List<Contact> localContactsList) {
        if (null == localContactsList || localContactsList.size() < 1) {
            return;
        }
        for (Contact localContact : localContactsList) {
            localContact.setContactId(localContact.getPhoneCid());
            localContact.setContactCRC(String.valueOf(localContact.generateCRC()));
        }
        boolean result = momoContactsManager.batchAddContacts(localContactsList, true);
        if (result) {
            momoContactsManager.batchInsertAvatar(localContactsList);
        }
    }

    /**
     * phoneId comparator
     * 
     * @author chenjp
     */
    private static class PhoneCIdComparator implements Comparator<Contact> {
        private static PhoneCIdComparator instance = null;

        private PhoneCIdComparator() {
        }

        public static PhoneCIdComparator getInstance() {
            if (null == instance) {
                instance = new PhoneCIdComparator();
            }
            return instance;
        }

        @Override
        public int compare(Contact contact1, Contact contact2) {
            long id1 = contact1.getPhoneCid();
            long id2 = contact2.getPhoneCid();
            return (id1 < id2 ? -1 : (id1 == id2 ? 0 : 1));
        }
    }

    private static class ContactIdComparator implements Comparator<Contact> {
        private static ContactIdComparator instance = null;

        private ContactIdComparator() {
        }

        public static ContactIdComparator getInstance() {
            if (null == instance) {
                instance = new ContactIdComparator();
            }
            return instance;
        }

        @Override
        public int compare(Contact contact1, Contact contact2) {
            long id1 = contact1.getContactId();
            long id2 = contact2.getContactId();
            return (id1 < id2 ? -1 : (id1 == id2 ? 0 : 1));
        }
    }

    private String logId(List<Contact> contactsList) {
        StringBuilder sb = new StringBuilder();
        for (Contact contact : contactsList) {
            sb.append(contact.getContactId()).append(',');
        }
        return sb.toString();
    }

    private String logPhoneCid(List<Contact> contactsList) {
        StringBuilder sb = new StringBuilder();
        for (Contact contact : contactsList) {
            sb.append(contact.getPhoneCid()).append(',');
        }
        return sb.toString();
    }

    /**
     * 同时删除一个服务端、momo、手机联系人
     * 
     * @param contact
     * @return 删除结果
     */
    public boolean deleteServerMoMoAndLocalContact(Contact contact) {
        if (null == contact)
            return false;
        long contactId = contact.getContactId();
        if (contactId < 1)
            return false;
        long phoneCid = contact.getPhoneCid();

        if (phoneCid < 1)
            return false;
        List<Long> toDeleteIdList = new ArrayList<Long>();
        toDeleteIdList.add(contactId);
        List<Boolean> deleteServerResult = deleteContactsToServer(toDeleteIdList);
        if (null == deleteServerResult || deleteServerResult.size() < 1)
            return false;
        boolean result = deleteServerResult.get(0);
        if (!result)
            return false;
        boolean deleteMoMoResult = momoContactsManager.delContact(contactId);
        if (!deleteMoMoResult)
            return false;
        boolean deleteLocalResult = localContactsManager.deleteContact(phoneCid);
        return deleteLocalResult;
    }

    /**
     * 同时删除一个momo、手机联系人
     * 
     * @param contact
     * @return 删除结果
     */
    public boolean deleteMoMoAndLocalContact(Contact contact) {
        if (null == contact)
            return false;
        long contactId = contact.getContactId();
        if (contactId < 1)
            return false;
        long phoneCid = contact.getPhoneCid();

        if (phoneCid < 1)
            return false;
        boolean deleteMoMoResult = momoContactsManager.delContact(contactId);
        if (!deleteMoMoResult)
            return false;
        boolean deleteLocalResult = localContactsManager.deleteContact(phoneCid);
        return deleteLocalResult;
    }

    /**
     * 新增联系人到服务器(用户手动添加联系人，接收联系人名片消息)
     */
    public List<Contact> saveContactsToServer(final List<Contact> contactsList) {
        if (null == contactsList || contactsList.size() < 1) {
            return new ArrayList<Contact>();
        }
        try {
            String response = SyncContactHttpApi.addContactList(contactsList);
            JSONObject resultObj = new JSONObject(response);
            Iterator<Contact> contactIter = contactsList.iterator();
            while (contactIter.hasNext()) {
                Contact contact = contactIter.next();
                long phoneCid = contact.getPhoneCid();
                JSONObject eachObj = resultObj.optJSONObject(String
                        .valueOf(phoneCid));
                long contactId = eachObj.optLong("id");
                contact.setContactId(contactId);
            }
        } catch (MoMoException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contactsList;
    }

}
