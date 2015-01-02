
package cn.com.nd.momo.api.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.com.nd.momo.api.types.Address;
import cn.com.nd.momo.api.types.Avatar;
import cn.com.nd.momo.api.types.Contact;
import cn.com.nd.momo.api.types.Data;
import cn.com.nd.momo.api.util.Log;
import cn.com.nd.momo.api.util.PinyinHelper;
import cn.com.nd.momo.api.util.Utils;

/**
 * momo联系人管理类
 * 
 * @author chenjp
 */
public class MoMoContactsManager {
    private final static String TAG = "MoMoContactsManager";

    private final static String CONTACT_TABLE_NAME = "contact";

    private final static String IMAGE_TABLE_NAME = "image";

    private final static String DATA_TABLE_NAME = "data";

    public static final String COLUMN_ID = "id";

    public static final String COLUMN_CONTACT_ID = "contact_id";

    public static final String COLUMN_CONTACT_ROW_ID = "contact_row_id";

    public static final String COLUMN_PHONE_CID = "phone_cid";

    public static final String COLUMN_UID = "uid";

    public static final String COLUMN_AVATAR_ID = "avatar_id";

    public static final String COLUMN_FIRST_NAME = "first_name";

    public static final String COLUMN_LAST_NAME = "last_name";

    public static final String COLUMN_FORMAT_NAME = "format_name";

    public static final String COLUMN_IS_FRIEND = "is_friend";

    public static final String COLUMN_IS_SAVED_TO_LOCAL = "is_saved_to_local";

    public static final String COLUMN_ORGANIZATION = "organization";

    public static final String COLUMN_DEPARTMENT = "department";

    public static final String COLUMN_NOTE = "note";

    public static final String COLUMN_BIRTHDAY = "birthday";

    public static final String COLUMN_TITLE = "job_title";

    public static final String COLUMN_NICK_NAME = "nick_name";

    public static final String COLUMN_MODIFY_DATE = "modify_date";

    public static final String COLUMN_PHONE_CRC = "phone_crc";

    public static final String COLUMN_CATEGORY_IDS = "category_ids";

    public static final String COLUMN_STARRED = "starred";

    public static final String COLUMN_IDENTIFIER = "identifier";

    public static final String COLUMN_PROPERTY = "property";

    public static final String COLUMN_LABEL = "label";

    public static final String COLUMN_VALUE = "value";

    public static final String COLUMN_NAME = "name";

    public static final String COLUMN_USER_STATUS = "user_status";

    public static final String COLUMN_USER_LINK = "user_link";

    public static final String COLUMN_GENDER = "gender";

    public static final String COLUMN_RESIDENCE = "residence";

    public static final String COLUMN_LUNAR_BIRTHDAY = "lunar_birthday";

    public static final String COLUMN_IS_LUNAR = "is_lunar";

    public static final String COLUMN_ANIMAL_SIGN = "animal_sign";

    public static final String COLUMN_ZODIAC = "zodiac";

    public static final String COLUMN_HEALTH_STATUS = "health_status";

    public static final String COLUMN_CUSTOM_RINGTONE = "custom_ringtone";

    private static SQLiteDatabase mDB;

    private static MoMoContactsManager instance = null;

    private MoMoContactsManager() {
    }

    public static MoMoContactsManager getInstance() {
        if (null == instance) {
            instance = new MoMoContactsManager();
        }
        return instance;
    }

    public void getAllDisplayContactsList(List<Contact> contactList, List<Contact> moList) {
        if (contactList == null) {
            contactList = new ArrayList<Contact>();
        } else {
            contactList.clear();
        }
        if (moList == null) {
            moList = new ArrayList<Contact>();
        } else {
            moList.clear();
        }
        HashMap<Long, Contact> contactMap = new HashMap<Long, Contact>();
        String sql = "select * from contact; ";
        Cursor cursor = null;
        try {
            mDB = ContactDatabaseHelper.getInstance();
            cursor = mDB.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                long contactId = cursor.getLong(cursor.getColumnIndex(COLUMN_CONTACT_ID));
                if (contactId == 1) {
                    continue;
                }
                String format_name = cursor.getString(cursor.getColumnIndex(COLUMN_FORMAT_NAME));
                Contact contact = new Contact();
                contact.setContactId(contactId);
                contact.setFormatName(format_name);
                contact.setNamePinyin(PinyinHelper.convertChineseToPinyinArray(format_name));
                contactMap.put(contactId, contact);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        for (Long contactId : contactMap.keySet()) {
            Contact contact = contactMap.get(contactId);
            contactList.add(contact);
        }
    }
    
    public List<Contact> getAllDisplayContactsList() {
        List<Contact> contactList = new ArrayList<Contact>();
        String sql = "select * from contact; ";
        Cursor cursor = null;
        try {
            mDB = ContactDatabaseHelper.getInstance();
            cursor = mDB.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                long contactId = cursor.getLong(cursor.getColumnIndex(COLUMN_CONTACT_ID));
                if (contactId == 1) {
                    continue;
                }
                String format_name = cursor.getString(cursor.getColumnIndex(COLUMN_FORMAT_NAME));
                Contact contact = new Contact();
                contact.setContactId(contactId);
                contact.setFormatName(format_name);
                contact.setNamePinyin(PinyinHelper.convertChineseToPinyinArray(format_name));
                contactList.add(contact);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return contactList;
    }

    /**
     * @return 所有带有手机号码的联系人
     */
    public List<Contact> getAllContactsWithMobile() {
        HashMap<Long, Contact> contactMap = new HashMap<Long, Contact>();
        String sql = "select contact.contact_id, contact.format_name, data.value from contact left outer join data on contact.contact_id = data.contact_row_id  where data.property = "
                + Data.PHONE + "; ";
        Cursor cursor = null;
        try {
            mDB = ContactDatabaseHelper.getInstance();
            cursor = mDB.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                long contactId = cursor.getLong(cursor.getColumnIndex(COLUMN_CONTACT_ID));
                String formatName = cursor.getString(cursor.getColumnIndex(COLUMN_FORMAT_NAME));
                String mobile = cursor.getString(cursor.getColumnIndex(COLUMN_VALUE));

                if (contactMap.containsKey(contactId)) {
                    contactMap.get(contactId).getPhoneList().add(mobile);
                } else {
                    Contact contact = new Contact();
                    contact.setContactId(contactId);
                    contact.setFormatName(formatName);
                    contact.setNamePinyin(PinyinHelper.convertChineseToPinyinArray(formatName));
                    contact.getPhoneList().add(mobile);
                    contactMap.put(contactId, contact);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        List<Contact> contactsList = new ArrayList<Contact>();
        if (contactMap.size() > 0) {
            contactsList.addAll(contactMap.values());
        }
        return contactsList;
    }

    /**
     * @return 所有联系人简要信息(姓名，联系人id)
     */
    public List<Contact> getAllContactsBriefInfo() {
        List<Contact> contactsList = new ArrayList<Contact>();
        String sql = "select contact_id, format_name from contact;";
        Cursor cursor = null;
        try {
            mDB = ContactDatabaseHelper.getInstance();
            cursor = mDB.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                long contactId = cursor.getLong(cursor.getColumnIndex(COLUMN_CONTACT_ID));
                String formatName = cursor.getString(cursor.getColumnIndex(COLUMN_FORMAT_NAME));
                Contact contact = new Contact();
                contact.setContactId(contactId);
                contact.setFormatName(formatName);
                contact.setNamePinyin(PinyinHelper.convertChineseToPinyinArray(formatName));
                contactsList.add(contact);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return contactsList;
    }

    /**
     * @return 限定条件之后的联系人集合
     */
    public List<Contact> getAllContactsIdList() {
        String sql = "select contact_id,phone_cid,modify_date,category_ids,phone_crc from contact ; ";
        Cursor cursor = null;
        List<Contact> contactList = new ArrayList<Contact>();
        try {
            mDB = ContactDatabaseHelper.getInstance();
            cursor = mDB.rawQuery(sql, null);
            if (null == cursor)
                return contactList;
            while (cursor.moveToNext()) {
                long contactId = cursor.getLong(cursor
                        .getColumnIndex(COLUMN_CONTACT_ID));
                long phoneCid = cursor.getLong(cursor
                        .getColumnIndex(COLUMN_PHONE_CID));
                long modifyDate = cursor.getLong(cursor
                        .getColumnIndex(COLUMN_MODIFY_DATE));
                String categoryIds = cursor.getString(cursor
                        .getColumnIndex(COLUMN_CATEGORY_IDS));
                String crc = cursor.getString(cursor
                        .getColumnIndex(COLUMN_PHONE_CRC));
                Contact contact = new Contact(contactId, phoneCid, modifyDate);
                contact.convertCategoryIdStringToList(categoryIds);
                contact.setContactCRC(crc);
                contactList.add(contact);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return contactList;
    }

    /**
     * @param contactId
     * @return 对应联系人的头像
     */
    public Avatar getAvatarByContactId(long contactId) {
        if (contactId < 1)
            return null;
        String sql = "select * from " + IMAGE_TABLE_NAME
                + " where image_contact_id = ? ; ";
        Avatar avatar = null;
        Cursor cursor = null;
        try {
            mDB = ContactDatabaseHelper.getInstance();
            cursor = mDB.rawQuery(sql,
                    new String[] {
                        String.valueOf(contactId)
                    });
            while (cursor.moveToNext()) {
                // long imageId =
                // cursor.getLong(cursor.getColumnIndex("image_id"));
                String serverAvatarURL = cursor.getString(cursor
                        .getColumnIndex("server_avatar_url"));
                byte[] momoImage = cursor.getBlob(cursor
                        .getColumnIndex("momo_avatar_image"));
                // byte[] localImage = cursor.getBlob(cursor
                // .getColumnIndex("local_avatar_image"));
                // avatar = new Avatar(contactId, serverAvatarURL, momoImage,
                // localImage);
                avatar = new Avatar(contactId, serverAvatarURL, momoImage);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return avatar;
    }

    /**
     * @param contactId
     * @retur 对应的联系人
     */
    public Contact getContactById(long contactId) {
        if (contactId < 0)
            return null;
        String sql = "select * from contact left outer join image on contact.contact_id = image.image_contact_id "
                + " where contact.contact_id = " + contactId + " ; ";
        return getContactBySql(sql);
    }


    /**
     * get specified contact with sql clause
     * 
     * @param sql
     * @return
     */
    private Contact getContactBySql(String sql) {
        Contact contact = null;
        Cursor cursor = null;
        try {
            mDB = ContactDatabaseHelper.getInstance();
            cursor = mDB.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                contact = retrieveContactFromCursor(cursor);
                long contactId = contact.getContactId();
                setContactData(contact);
                String serverAvatarURL = cursor.getString(cursor
                        .getColumnIndex("server_avatar_url"));
                byte[] momoImage = cursor.getBlob(cursor
                        .getColumnIndex("momo_avatar_image"));
                Avatar avatar = new Avatar(contactId, serverAvatarURL,
                        momoImage);
                contact.setAvatar(avatar);
                break;
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return contact;
    }
    
    public List<Contact> getAllContact() {
        List<Contact> contactList = new ArrayList<Contact>();
        Contact contact = null;
        Cursor cursor = null;
        try {
            mDB = ContactDatabaseHelper.getInstance();
            String sql = "select * from contact left outer join image on contact.contact_id = image.image_contact_id;";
            cursor = mDB.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                contact = retrieveContactFromCursor(cursor);
                long contactId = contact.getContactId();
                setContactData(contact);
                String serverAvatarURL = cursor.getString(cursor
                        .getColumnIndex("server_avatar_url"));
                byte[] momoImage = cursor.getBlob(cursor
                        .getColumnIndex("momo_avatar_image"));
                Avatar avatar = new Avatar(contactId, serverAvatarURL,
                        momoImage);
                contact.setAvatar(avatar);
                contactList.add(contact);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return contactList;
    }

    /**
     * @param cursor
     * @return 从游标中取出联系人
     */
    private Contact retrieveContactFromCursor(Cursor cursor) {
        if (null == cursor)
            return null;
        int starred = cursor.getInt(cursor.getColumnIndex(COLUMN_STARRED));
        boolean isFav = (0 == starred) ? false : true;
        long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
        long contactId = cursor.getLong(cursor
                .getColumnIndex(COLUMN_CONTACT_ID));
        long phoneCid = cursor.getLong(cursor.getColumnIndex(COLUMN_PHONE_CID));
        long uid = cursor.getLong(cursor.getColumnIndex(COLUMN_UID));
        int isFriend = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_FRIEND));
        String birthday = cursor.getString(cursor
                .getColumnIndex(COLUMN_BIRTHDAY));
        long modifyDateValue = cursor.getLong(cursor
                .getColumnIndex(COLUMN_MODIFY_DATE));
        String firstName = cursor.getString(cursor
                .getColumnIndex(COLUMN_FIRST_NAME));
        String lastName = cursor.getString(cursor
                .getColumnIndex(COLUMN_LAST_NAME));
        String format_name = cursor.getString(cursor
                .getColumnIndex(COLUMN_FORMAT_NAME));
        String organization = cursor.getString(cursor
                .getColumnIndex(COLUMN_ORGANIZATION));
        String department = cursor.getString(cursor
                .getColumnIndex(COLUMN_DEPARTMENT));
        String note = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE));
        String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
        String nickName = cursor.getString(cursor
                .getColumnIndex(COLUMN_NICK_NAME));
        String phonecrc = cursor.getString(cursor
                .getColumnIndex(COLUMN_PHONE_CRC));
        String categoryIds = cursor.getString(cursor
                .getColumnIndex(COLUMN_CATEGORY_IDS));

        String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        int userStatus = cursor.getInt(cursor
                .getColumnIndex(COLUMN_USER_STATUS));

        int userLink = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_LINK));

        int gender = cursor.getInt(cursor.getColumnIndex(COLUMN_GENDER));
        String residence = cursor.getString(cursor
                .getColumnIndex(COLUMN_RESIDENCE));

        String lunarBirthday = cursor.getString(cursor
                .getColumnIndex(COLUMN_LUNAR_BIRTHDAY));

        int isLunarInt = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_LUNAR));

        String animalSign = cursor.getString(cursor
                .getColumnIndex(COLUMN_ANIMAL_SIGN));

        String zodiac = cursor.getString(cursor.getColumnIndex(COLUMN_ZODIAC));

        int healthStatus = cursor.getInt(cursor
                .getColumnIndex(COLUMN_HEALTH_STATUS));
        String customRingtone = cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOM_RINGTONE));
        Contact contact = new Contact(id, contactId, phoneCid, uid, firstName,
                lastName, organization, department, note, birthday, title,
                nickName, 0, format_name, isFav, name, userStatus, userLink,
                gender, residence, lunarBirthday, isLunarInt == 1 ? true
                        : false, animalSign, zodiac, healthStatus);
        contact.convertCategoryIdStringToList(categoryIds);
        contact.setContactCRC(phonecrc);
        contact.setCustomRingtone(customRingtone);
        contact.setNamePinyin(PinyinHelper
                .convertChineseToPinyinArray(format_name));
        Avatar avatar = getAvatarByContactId(contactId);
        contact.setAvatar(avatar);
        contact.setModifyDate(modifyDateValue);
        contact.setFriend(isFriend == 0 ? false : true);
        return contact;
    }

    /**
     * 提取所有联系人的联系方式
     * 
     * @return
     */
    private List<Contact> loadAllContactPhoneList() {
        String sql = "select * from data where property = 1; ";
        Cursor cursor = null;
        List<Contact> contactsList = new ArrayList<Contact>();
        try {
            mDB = ContactDatabaseHelper.getInstance();
            cursor = mDB.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                Contact contact = new Contact();
                int property = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_PROPERTY));
                String label = cursor.getString(cursor
                        .getColumnIndex(COLUMN_LABEL));
                String value = cursor.getString(cursor
                        .getColumnIndex(COLUMN_VALUE));
                long contactId = cursor.getLong(cursor
                        .getColumnIndex(COLUMN_CONTACT_ROW_ID));
                int identifier = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_IDENTIFIER));
                contact.setContactId(contactId);
                setContactDataproperty(contact, property, value, label,
                        identifier);
                contactsList.add(contact);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return contactsList;
    }

    /**
     * set data properties to contact
     * 
     * @param contact
     */
    private void setContactData(Contact contact) {
        String sql = "select * from data where contact_row_id=?;";
        Cursor cursor = null;
        try {
            mDB = ContactDatabaseHelper.getInstance();
            cursor = mDB.rawQuery(sql,
                    new String[] {
                        String.valueOf(contact.getContactId())
                    });
            while (cursor.moveToNext()) {
                int property = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_PROPERTY));
                String label = cursor.getString(cursor
                        .getColumnIndex(COLUMN_LABEL));
                String value = cursor.getString(cursor
                        .getColumnIndex(COLUMN_VALUE));
                int identifier = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_IDENTIFIER));
                setContactDataproperty(contact, property, value, label,
                        identifier);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
    }

    /**
     * set data properties to contact
     * 
     * @param contact
     * @param property
     * @param value
     * @param label
     * @param identifier
     */
    private void setContactDataproperty(Contact contact, int property,
            String value, String label, int identifier) {
        switch (property) {
            case Data.PHONE:
                List<String> phoneList = contact.getPhoneList();
                phoneList.add(value);
                List<String> phoneLabelList = contact.getPhoneLabelList();
                phoneLabelList.add(label);
                List<Boolean> prefList = contact.getPrefPhoneList();
                prefList.add(identifier == 1 ? true : false);
                break;
            case Data.EMAIL:
                List<String> emailList = contact.getEmailList();
                emailList.add(value);
                List<String> emailLabelList = contact.getEmailLabelList();
                emailLabelList.add(label);
                break;
            case Data.INTERNET:
                List<String> websiteList = contact.getWebsiteList();
                websiteList.add(value);
                List<String> websiteLabelList = contact.getWebsiteLabelList();
                websiteLabelList.add(label);
                break;
            case Data.RELATE:
                List<String> relationList = contact.getRelationList();
                relationList.add(value);
                List<String> relationLabelList = contact.getRelationLabelList();
                relationLabelList.add(label);
                break;
            case Data.ADDR:
                List<Address> addressList = contact.getAddressList();
                Address address = new Address();
                Contact.addressFromString(value, address);
                address.setLabel(label);
                addressList.add(address);
                break;
            case Data.DAY:
                List<String> eventList = contact.getEventList();
                eventList.add(value);
                List<String> eventLabelList = contact.getEventLabelList();
                eventLabelList.add(label);
                break;
            case Data.IM:
                List<String> imList = contact.getImList();
                imList.add(value);
                List<String> imLabelList = contact.getImLabelList();
                imLabelList.add(label);
                String protocal = "";
                if (Data.IM_PROTOCOL_LABEL.containsKey(identifier)) {
                    protocal = Data.IM_PROTOCOL_LABEL.get(identifier);
                }
                List<String> protocalList = contact.getImProtocolList();
                protocalList.add(protocal);
                break;
            default:
                break;
        }
    }

    /**
     * 根据联系人id删除该联系人
     * 
     * @param momoContactId
     * @return
     */
    public boolean delContact(long momoContactId) {
        if (momoContactId < 1)
            return false;
        try {
            mDB = ContactDatabaseHelper.getInstance();
            mDB.beginTransaction();
            mDB.delete(CONTACT_TABLE_NAME, "contact_id=?",
                    new String[] {
                        String.valueOf(momoContactId)
                    });
            mDB.delete(DATA_TABLE_NAME, "contact_row_id=?",
                    new String[] {
                        String.valueOf(momoContactId)
                    });
            mDB.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            if (mDB.inTransaction()) {
                mDB.endTransaction();
            }
            return false;
        } finally {
            if (mDB.inTransaction()) {
                mDB.endTransaction();
            }
        }
        return true;
    }

    /**
     * 删除所有联系人
     * 
     * @return 删除結果
     */
    public boolean deleteAllContacts() {
        try {
            mDB = ContactDatabaseHelper.getInstance();
            mDB.beginTransaction();
            mDB.delete(DATA_TABLE_NAME, null, null);
            mDB.delete(IMAGE_TABLE_NAME, null, null);
            mDB.delete(CONTACT_TABLE_NAME, null, null);
            mDB.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            if (mDB.inTransaction()) {
                mDB.endTransaction();
            }
            return false;
        } finally {
            if (mDB.inTransaction()) {
                mDB.endTransaction();
            }
        }
        return true;

    }

    /**
     * 批量删除联系人
     * 
     * @param contactsList
     * @return 删除結果
     */
    public boolean batchDeleteContact(List<Contact> contactsList) {
        if (null == contactsList || contactsList.size() < 1)
            return false;
        try {
            mDB = ContactDatabaseHelper.getInstance();
            mDB.beginTransaction();
            for (Contact contact : contactsList) {
                long momoContactId = contact.getContactId();
                mDB.delete(CONTACT_TABLE_NAME, "contact_id=?",
                        new String[] {
                            String.valueOf(momoContactId)
                        });
                mDB.delete(DATA_TABLE_NAME, "contact_row_id=?",
                        new String[] {
                            String.valueOf(momoContactId)
                        });
            }
            mDB.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            if (mDB.inTransaction()) {
                mDB.endTransaction();
            }
            return false;
        } finally {
            if (mDB.inTransaction()) {
                mDB.endTransaction();
            }
        }
        return true;
    }

    /**
     * 批量删除联系人
     * 
     * @return 删除結果
     */
    public boolean batchDeleteContactByIdList(List<Long> contactIdList) {
        if (null == contactIdList || contactIdList.size() < 1)
            return false;
        try {
            mDB = ContactDatabaseHelper.getInstance();
            mDB.beginTransaction();
            for (Long momoContactId : contactIdList) {
                mDB.delete(CONTACT_TABLE_NAME, "contact_id=?",
                        new String[] {
                            String.valueOf(momoContactId)
                        });
                mDB.delete(DATA_TABLE_NAME, "contact_row_id=?",
                        new String[] {
                            String.valueOf(momoContactId)
                        });
            }
            mDB.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            if (mDB.inTransaction()) {
                mDB.endTransaction();
            }
            return false;
        } finally {
            if (mDB.inTransaction()) {
                mDB.endTransaction();
            }
        }
        return true;
    }

    /**
     * 批量增加联系人
     * 
     * @param contactList
     */
    public boolean batchAddContacts(List<Contact> contactList, boolean isSavedToLocal) {
        if (null == contactList || contactList.size() < 1)
            return false;
        boolean result = true;
        mDB = ContactDatabaseHelper.getInstance();
        try {
            mDB.beginTransaction();
            for (Contact contact : contactList) {
                String name = contact.getFormatName();
                if (contact.isToDelete() || null == name || name.length() < 1)
                    continue;
                contact.setSavedToLocal(isSavedToLocal);
                ContentValues values = convertContactToContentValues(contact);
                mDB.insert(CONTACT_TABLE_NAME, null, values);
                this.addContactData(contact);
                values.clear();
                values = null;
            }
            mDB.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            mDB.endTransaction();
        }
        return result;
    }
    
    public boolean batchReaddContacts(List<Contact> contactList, boolean isSavedToLocal) {
        if (null == contactList || contactList.size() < 1)
            return false;
        boolean result = true;
        mDB = ContactDatabaseHelper.getInstance();
        try {
            mDB.beginTransaction();
            
            mDB.delete(DATA_TABLE_NAME, null, null);
            mDB.delete(IMAGE_TABLE_NAME, null, null);
            mDB.delete(CONTACT_TABLE_NAME, null, null);
            
            for (Contact contact : contactList) {
                String name = contact.getFormatName();
                if (contact.isToDelete() || null == name || name.length() < 1)
                    continue;
                contact.setSavedToLocal(isSavedToLocal);
                ContentValues values = convertContactToContentValues(contact);
                mDB.insert(CONTACT_TABLE_NAME, null, values);
                this.addContactData(contact);
                values.clear();
                values = null;
            }
            mDB.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            mDB.endTransaction();
        }
        return result;
    }

    /**
     * 批量写入头像
     * 
     * @param contactList
     */
    public void batchInsertAvatar(List<Contact> contactList) {
        if (null == contactList || contactList.size() < 1)
            return;
        mDB = ContactDatabaseHelper.getInstance();
        try {
            mDB.beginTransaction();
            for (Contact contact : contactList) {
                if (contact.isToDelete())
                    continue;
                String name = contact.getFormatName();
                if (null != name && name.length() > 0) {
                    Avatar avatar = contact.getAvatar();
                    if (avatar == null)
                        continue;
                    long contactId = contact.getContactId();
                    String serverAvatarUrl = avatar.getServerAvatarURL();
                    byte[] momoAVatarImage = avatar.getMomoAvatarImage();
                    if (momoAVatarImage == null || momoAVatarImage.length < 1)
                        continue;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("image_contact_id", contactId);
                    if (serverAvatarUrl != null && serverAvatarUrl.length() > 0) {
                        contentValues.put("server_avatar_url", serverAvatarUrl);
                    }
                    contentValues.put("momo_avatar_image", momoAVatarImage);
                    if (contentValues.size() > 0) {
                        mDB.delete(IMAGE_TABLE_NAME, "image_contact_id=?", new String[] {
                                String.valueOf(contactId)
                        });
                        mDB.insert(IMAGE_TABLE_NAME, null, contentValues);
                    }
                }
            }
            mDB.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            if (mDB.inTransaction()) {
                mDB.endTransaction();
            }
        }
    }

    /**
     * 保存单个联系人的联系方式
     * 
     * @param contact
     * @return
     */
    private boolean addContactData(Contact contact) {
        try {
            long contactRawId = contact.getContactId();
            List<String> phoneList = contact.getPhoneList();
            List<String> phoneLabelList = contact.getPhoneLabelList();
            List<Boolean> prefList = contact.getPrefPhoneList();
            if (phoneList.size() != phoneLabelList.size()) {
                Log.e(TAG, "phoneList's size not equals phoneLabelList's size.");
                return false;
            }
            int prefSize = prefList.size();
            for (int i = 0; i < phoneList.size(); i++) {
                boolean pref = false;
                if (i < prefSize)
                    pref = prefList.get(i);
                ContentValues data = getDataContactValues(contactRawId,
                        phoneLabelList.get(i), phoneList.get(i), Data.PHONE,
                        pref ? 1 : 0);
                mDB.insert(DATA_TABLE_NAME, null, data);
            }

            List<String> emailList = contact.getEmailList();
            List<String> emailLabelList = contact.getEmailLabelList();
            if (emailList.size() != emailLabelList.size()) {
                Log.e(TAG, "emailList's size not equals emailLabelList's size.");
                return false;
            }
            for (int i = 0; i < emailList.size(); i++) {
                ContentValues data = getDataContactValues(contactRawId,
                        emailLabelList.get(i), emailList.get(i), Data.EMAIL, 0);
                mDB.insert(DATA_TABLE_NAME, null, data);
            }

            List<String> websiteList = contact.getWebsiteList();
            List<String> websiteLabelList = contact.getWebsiteLabelList();
            if (websiteList.size() != websiteLabelList.size()) {
                Log.e(TAG,
                        "websiteList's size not equals websiteLabelList's size.");
                return false;
            }
            for (int i = 0; i < websiteList.size(); i++) {
                ContentValues data = getDataContactValues(contactRawId,
                        websiteLabelList.get(i), websiteList.get(i),
                        Data.INTERNET, 0);
                mDB.insert(DATA_TABLE_NAME, null, data);
            }

            List<String> relationList = contact.getRelationList();
            List<String> relationLabelList = contact.getRelationLabelList();
            if (relationList.size() != relationLabelList.size()) {
                Log.e(TAG,
                        "relationList's size not equals relationLabelList's size.");
                return false;
            }
            for (int i = 0; i < relationList.size(); i++) {
                ContentValues data = getDataContactValues(contactRawId,
                        relationLabelList.get(i), relationList.get(i),
                        Data.RELATE, 0);
                mDB.insert(DATA_TABLE_NAME, null, data);
            }

            List<Address> addressList = contact.getAddressList();
            for (int i = 0; i < addressList.size(); i++) {
                String value = Contact.address2String(addressList.get(i));
                ContentValues data = getDataContactValues(contactRawId,
                        addressList.get(i).getLabel(), value, Data.ADDR, 0);
                mDB.insert(DATA_TABLE_NAME, null, data);
            }

            List<String> imList = contact.getImList();
            List<String> imProtocolList = contact.getImProtocolList();
            List<String> imLabelList = contact.getImLabelList();
            if (imList.size() != imProtocolList.size()
                    || imList.size() != imLabelList.size()) {
                Log.e(TAG, "imList's size(" + imList.size()
                        + ") unsuitably matched imProtocolList's size("
                        + imProtocolList.size() + ") or imLabelList's size("
                        + imLabelList.size() + ").");
            }

            for (int i = 0; i < imList.size(); i++) {
                String protocolLabel = imProtocolList.get(i).trim();
                int protocolId = -2;
                for (Integer id : Data.IM_PROTOCOL_LABEL.keySet()) {
                    if (Data.IM_PROTOCOL_LABEL.get(id).equalsIgnoreCase(
                            protocolLabel)) {
                        protocolId = id;
                        break;
                    }
                }
                if (protocolId < Data.PROTOCOL_91U
                        || protocolId > Data.PROTOCOL_JABBER) {
                    Log.e(TAG, "illegal im protocol label(" + protocolLabel
                            + ")");
                    return false;
                }
                ContentValues data = getDataContactValues(contactRawId,
                        imLabelList.get(i), imList.get(i), Data.IM, protocolId);
                mDB.insert(DATA_TABLE_NAME, null, data);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return true;
    }

    /**
     * 批量更新联系人
     * 
     * @param contactList
     * @return
     */
    public boolean updateContacts(List<Contact> contactList) {
        if (null == contactList)
            return false;
        mDB = ContactDatabaseHelper.getInstance();
        mDB.beginTransaction();
        boolean result = false;
        try {
            for (Contact contact : contactList) {
                long contactId = contact.getContactId();
                if (contactId < 1)
                    continue;
                ContentValues contentValues = this
                        .getImageContactValues(contact);
                if (contentValues.size() > 0) {
                    mDB.delete(IMAGE_TABLE_NAME, "image_contact_id=?", new String[] {
                            String.valueOf(contactId)
                    });
                    int avatarId = (int)mDB.insert(IMAGE_TABLE_NAME, null,
                            contentValues);
                    Avatar avatar = contact.getAvatar();
                    if (null != avatar)
                        avatar.setAvatarId(avatarId);
                }
                mDB.update(CONTACT_TABLE_NAME,
                        convertContactToContentValues(contact), "contact_id=?",
                        new String[] {
                            String.valueOf(contactId)
                        });
                mDB.delete(DATA_TABLE_NAME, "contact_row_id=?",
                        new String[] {
                            String.valueOf(contactId)
                        });
                this.addContactData(contact);
            }
            mDB.setTransactionSuccessful();
            result = true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            result = false;
        } finally {
            if (mDB.inTransaction()) {
                mDB.endTransaction();
            }
        }
        return result;
    }

    /**
     * 更新单个联系人的phoneID
     * 
     * @param contact
     * @return
     */
    public boolean updateContactPhoneCid(Contact contact) {
        if (null == contact || contact.getPhoneCid() < 1)
            return false;
        mDB = ContactDatabaseHelper.getInstance();
        boolean result = false;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_PHONE_CID, contact.getPhoneCid());
            long contactId = contact.getContactId();
            mDB.update(CONTACT_TABLE_NAME, contentValues, "contact_id=?",
                    new String[] {
                        String.valueOf(contactId)
                    });
            result = true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            result = false;
        }
        return result;
    }

    /**
     * 批量更新联系人crc
     * 
     * @param contactList
     * @return
     */
    public boolean batchUpdateContactsCRC(List<Contact> contactList) {
        if (null == contactList)
            return false;
        mDB = ContactDatabaseHelper.getInstance();
        mDB.beginTransaction();
        boolean result = false;
        try {
            for (Contact contact : contactList) {
                long contactId = contact.getContactId();
                String crc = contact.getContactCRC();
                if (contactId < 1 || crc.length() < 1)
                    continue;
                ContentValues values = new ContentValues();
                values.put(COLUMN_PHONE_CRC, crc);
                mDB.update(CONTACT_TABLE_NAME, values, "contact_id=?",
                        new String[] {
                            String.valueOf(contactId)
                        });
            }
            mDB.setTransactionSuccessful();
            result = true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            result = false;
        } finally {
            if (mDB.inTransaction()) {
                mDB.endTransaction();
            }
        }
        return result;
    }

    /**
     * 通过momoid列表取得所有对应的phone cid 列表
     * 
     * @param contactsIdList
     * @return
     */
    public List<Contact> getPhoneContactIdByContactId(List<Long> contactsIdList) {
        if (null == contactsIdList || contactsIdList.size() < 1)
            return new ArrayList<Contact>();

        StringBuilder sb = new StringBuilder();
        int size = contactsIdList.size();
        for (int i = 0; i < size; i++) {
            long contactId = contactsIdList.get(i);
            if (i == size - 1)
                sb.append(contactId);
            else
                sb.append(contactId).append(',');
        }
        String sql = "select contact_id,phone_cid,category_ids from contact where contact_id in ("
                + sb.toString() + "); ";
        Cursor cursor = null;
        List<Contact> contactList = new ArrayList<Contact>();
        try {
            mDB = ContactDatabaseHelper.getInstance();
            cursor = mDB.rawQuery(sql, null);
            if (null == cursor)
                return contactList;
            while (cursor.moveToNext()) {
                long contactId = cursor.getLong(cursor
                        .getColumnIndex(COLUMN_CONTACT_ID));
                long phoneCid = cursor.getLong(cursor
                        .getColumnIndex(COLUMN_PHONE_CID));
                String categoryIds = cursor.getString(cursor
                        .getColumnIndex(COLUMN_CATEGORY_IDS));
                Contact contact = new Contact(contactId, phoneCid, -1);
                contact.convertCategoryIdStringToList(categoryIds);
                contactList.add(contact);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return contactList;
    }

    /**
     * 批量保存手机联系人特定的属性（收藏、分组）
     * 
     * @param contactList
     * @return
     */
    public boolean batchSaveContactProperties(List<Contact> contactList) {
        if (null == contactList || contactList.size() < 1)
            return false;
        mDB = ContactDatabaseHelper.getInstance();
        mDB.beginTransaction();
        boolean result = false;
        try {
            for (Contact contact : contactList) {
                long contactId = contact.getContactId();
                List<Long> categoryIdList = contact.getCategoryIdList();
                String categoryIds = "";
                if (null != categoryIdList && categoryIdList.size() > 0) {
                    categoryIds = contact.convertCategoryIdListToString();
                }
                ContentValues values = new ContentValues();
                if (categoryIds.length() > 0)
                    values.put(COLUMN_CATEGORY_IDS, categoryIds);
                boolean isFav = contact.isFavoried();
                if (isFav)
                    values.put(COLUMN_STARRED, 1);
                if (values.size() > 0)
                    mDB.update(CONTACT_TABLE_NAME, values, "contact_id=?",
                            new String[] {
                                String.valueOf(contactId)
                            });
                Avatar avatar = contact.getAvatar();
                if (null != avatar) {
                    byte[] momoAVatarImage = avatar.getMomoAvatarImage();
                    if (null != momoAVatarImage && momoAVatarImage.length > 0) {
                        ContentValues imageValues = new ContentValues();
                        imageValues.put("momo_avatar_image", momoAVatarImage);
                        mDB.update(IMAGE_TABLE_NAME, imageValues,
                                "image_contact_id=?",
                                new String[] {
                                    String.valueOf(contactId)
                                });
                    }
                }
            }
            mDB.setTransactionSuccessful();
            result = true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            result = false;
        } finally {
            if (mDB.inTransaction()) {
                mDB.endTransaction();
            }
        }
        return result;
    }

    /**
     * 得到联系人的映射值
     * 
     * @param contact
     * @return
     */
    private ContentValues convertContactToContentValues(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACT_ID, contact.getContactId());
        values.put(COLUMN_PHONE_CID, contact.getPhoneCid());
        values.put(COLUMN_UID, contact.getUid());
        String ringtone = contact.getCustomRingtone();
        if (null != ringtone && ringtone.length() > 0)
            values.put(COLUMN_CUSTOM_RINGTONE, ringtone);

        StringBuilder sb = new StringBuilder();
        String firstName = contact.getFirstName();
        String lastName = contact.getLastName();
        if (!Utils.isEmpty(lastName)) {
            values.put(COLUMN_LAST_NAME, lastName);
            sb.append(lastName);
        }
        if (!Utils.isEmpty(firstName)) {
            values.put(COLUMN_FIRST_NAME, firstName);
            sb.append(firstName);
        }
        values.put(COLUMN_FORMAT_NAME, sb.toString());
        String organization = contact.getOrganization();
        if (!Utils.isEmpty(organization))
            values.put(COLUMN_ORGANIZATION, organization);
        String department = contact.getDepartment();
        if (!Utils.isEmpty(department))
            values.put(COLUMN_DEPARTMENT, department);
        String note = contact.getNote();
        if (!Utils.isEmpty(note))
            values.put(COLUMN_NOTE, note);
        values.put(COLUMN_IS_FRIEND, contact.isFriend() ? 1 : 0);
        String birthday = contact.getBirthday();
        if (!Utils.isEmpty(birthday))
            values.put(COLUMN_BIRTHDAY, birthday);

        String title = contact.getJobTitle();
        if (!Utils.isEmpty(title))
            values.put(COLUMN_TITLE, title);
        String nickname = contact.getNickName();
        if (!Utils.isEmpty(nickname))
            values.put(COLUMN_NICK_NAME, nickname);

        String categoryIds = contact.convertCategoryIdListToString();
        if (!Utils.isEmpty(categoryIds))
            values.put(COLUMN_CATEGORY_IDS, categoryIds);

        values.put(COLUMN_IS_SAVED_TO_LOCAL, contact.isSavedToLocal() ? 1 : 0);

        values.put(COLUMN_MODIFY_DATE, contact.getModifyDate());
        String crc = contact.getContactCRC();
        if (!Utils.isEmpty(crc))
            values.put(COLUMN_PHONE_CRC, null == crc ? "" : crc);
        boolean isFav = contact.isFavoried();
        values.put(COLUMN_STARRED, isFav ? 1 : 0);

        String name = contact.getName();
        if (!Utils.isEmpty(name))
            values.put(COLUMN_NAME, name);
        values.put(COLUMN_USER_STATUS, contact.getUserStatus());
        values.put(COLUMN_USER_LINK, contact.getUserLink());
        values.put(COLUMN_GENDER, contact.getGender());
        String residence = contact.getResidence();
        if (!Utils.isEmpty(residence))
            values.put(COLUMN_RESIDENCE, residence);

        String lunarBirthday = contact.getLunarBirthDay();
        if (!Utils.isEmpty(lunarBirthday))
            values.put(COLUMN_LUNAR_BIRTHDAY, lunarBirthday);

        String animalSign = contact.getAnimalSign();
        if (!Utils.isEmpty(animalSign))
            values.put(COLUMN_ANIMAL_SIGN, animalSign);

        String zodiac = contact.getZodiac();
        if (!Utils.isEmpty(zodiac))
            values.put(COLUMN_ZODIAC, zodiac);

        int healthStatus = contact.getHealthStatus();
        values.put(COLUMN_HEALTH_STATUS, healthStatus);
        values.put(COLUMN_IS_LUNAR, contact.isNeedLunarBirthDay() ? 1 : 0);
        return values;
    }

    /**
     * 得到头像的映射值
     * 
     * @param contact
     * @return
     */
    private ContentValues getImageContactValues(Contact contact) {
        ContentValues values = new ContentValues();
        Avatar avatar = contact.getAvatar();
        if (avatar == null) {
            return values;
        }
        long contactId = contact.getContactId();
        String serverAvatarUrl = avatar.getServerAvatarURL();
        byte[] momoAVatarImage = avatar.getMomoAvatarImage();
        values.put("image_contact_id", contactId);
        if (serverAvatarUrl != null && !"".equals(serverAvatarUrl)) {
            values.put("server_avatar_url", serverAvatarUrl);
        }
        if (momoAVatarImage != null && momoAVatarImage.length > 0) {
            values.put("momo_avatar_image", momoAVatarImage);
        }
        return values;
    }

    /**
     * 把联系人数据转为映射值
     * 
     * @param contactRowId
     * @param label
     * @param value
     * @param property
     * @param identifier
     * @return
     */
    private ContentValues getDataContactValues(long contactRowId, String label,
            String value, int property, int identifier) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACT_ROW_ID, contactRowId);
        values.put("label", label);
        values.put("value", value);
        values.put("property", property);
        values.put("identifier", identifier);
        return values;
    }

    public Map<Long, Contact> getContactListByMobile(String mobile) {
        Map<Long, Contact> contactMap = new HashMap<Long, Contact>();
        if (mobile == null || mobile.length() < 1) {
            return contactMap;
        }

        String sql = "select contact.contact_id, contact.format_name, data.value from contact left outer join data on contact.contact_id = data.contact_row_id  where data.property = "
                + Data.PHONE + " and data.value like '%" + mobile + "'; ";
        Cursor cursor = null;
        try {
            mDB = ContactDatabaseHelper.getInstance();
            cursor = mDB.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                long contactId = cursor.getLong(cursor.getColumnIndex(COLUMN_CONTACT_ID));
                String formatName = cursor.getString(cursor.getColumnIndex(COLUMN_FORMAT_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(COLUMN_VALUE));
                if (!contactMap.containsKey(contactId)) {
                    Contact contact = new Contact();
                    contact.setContactId(contactId);
                    contact.setFormatName(formatName);
                    contact.getPhoneList().add(phone);
                    contactMap.put(contactId, contact);
                } else {
                    contactMap.get(contactId).getPhoneList().add(phone);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return contactMap;
    }

}
