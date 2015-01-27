
package cn.com.nd.momo.api.sync;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.BaseTypes;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.Relation;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContactsEntity;

import cn.com.nd.momo.api.exception.MoMoException;
import cn.com.nd.momo.api.types.Address;
import cn.com.nd.momo.api.types.Avatar;
import cn.com.nd.momo.api.types.Contact;
import cn.com.nd.momo.api.types.Data;
import cn.com.nd.momo.api.types.MyAccount;
import cn.com.nd.momo.api.util.DateFormater;
import cn.com.nd.momo.api.util.Log;
import cn.com.nd.momo.api.util.PinyinHelper;
import cn.com.nd.momo.api.util.Utils;
import cn.com.nd.momo.manager.GlobalUserInfo;


/**
 * 手机端联系人管理类
 * 
 * @author chenjp
 */
public class LocalContactsManager {
    private static final String TAG = "LocalContactsManager";

    private static LocalContactsManager instance = null;

    private static Context mContext;

    private boolean isTransaction = false;

    private BatchOperation mBatchOperation;

    public final static int FAILURE = -1;

    private LocalContactsManager() {

    }

    public static LocalContactsManager getInstance() {
        if (null == instance) {
            instance = new LocalContactsManager();
            mContext = Utils.getContext();
        }
        return instance;
    }


    public int getContactCountByAccount(Account account) {
        Cursor cursor = null;
        int count = 0;

        String filter = Utils.getAccountQueryFilterStr(account);
        String[] projection = {
                BaseColumns._COUNT
        };
        String andFilterStr = filter.length() > 0 ? "AND " + filter : "";
        String selection = RawContacts.DELETED + " = 0 " + andFilterStr;
        cursor = mContext.getContentResolver().query(RawContacts.CONTENT_URI,
                projection, selection, null, null);
        if (cursor.moveToNext())
            count = cursor.getInt(0);
        if (null != cursor) {
            cursor.close();
            cursor = null;
        }
        return count;
    }

    public List<Contact> getAllContactsList() {
        return getAllContactsListByAccount(true);
    }

    public List<Contact> getAllContactsListWithoutAccount() {
        return getAllContactsListByAccount(false);
    }

    private List<Contact> getAllContactsListByAccount(boolean withAccout) {
        List<Contact> contactsList = new ArrayList<Contact>();
        if (withAccout) {
            Account account = Utils.getCurrentAccount();
            if (account != null && !Utils.isBindedAccountExist(account)) {
                return contactsList;
            }
        }

        Cursor cursor = null;
        try {
            String queryFilterStr = "";
            if (withAccout) {
                Account account = Utils.getCurrentAccount();
                queryFilterStr = Utils.getAccountQueryFilterStr(account);
            }
            String andStr = queryFilterStr.length() > 0 ? " and " : "";
            String deletedSelection = RawContactsEntity.DELETED + " = 0 ";
            String selection = queryFilterStr + andStr + deletedSelection;
            String[] projection = {
                    RawContactsEntity.CONTACT_ID,
                    BaseColumns._ID,
                    RawContactsEntity.MIMETYPE,
                    RawContactsEntity.DELETED,
                    ContactsContract.RawContacts.STARRED,
                    ContactsContract.CommonDataKinds.Organization.TITLE,
                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                    ContactsContract.RawContacts.ACCOUNT_NAME,
                    ContactsContract.RawContacts.ACCOUNT_TYPE,
                    RawContactsEntity.DATA1, RawContactsEntity.DATA2,
                    RawContactsEntity.DATA3, RawContactsEntity.DATA4,
                    RawContactsEntity.DATA5, RawContactsEntity.DATA6,
                    RawContactsEntity.DATA7, RawContactsEntity.DATA8,
                    RawContactsEntity.DATA9, RawContactsEntity.DATA10,
                    RawContactsEntity.DATA11, RawContactsEntity.DATA12,
                    RawContactsEntity.DATA13, RawContactsEntity.DATA14,
                    RawContactsEntity.DATA15
            };
            cursor = mContext.getContentResolver().query(
                    RawContactsEntity.CONTENT_URI, projection, selection, null,
                    ContactsContract.RawContactsEntity.CONTACT_ID);
            contactsList = getContactListFromCursor(cursor);
        } catch (Exception e) {
            Log.e(TAG, "本询本地所有联系人出错:" + e.toString());
        } finally {
            if (null != cursor) {
                cursor.close();
            }

        }

        return contactsList;
    }

    public List<Contact> getAllContactsListByAccount(Account account) {
        List<Contact> contactsList = new ArrayList<Contact>();
        if (account == null || account.name.length() < 1 || account.type.length() < 1) {
            return contactsList;
        }
        Cursor cursor = null;
        try {
            String queryFilterStr = "";
            queryFilterStr = Utils.getAccountQueryFilterStr(account);
            String andStr = queryFilterStr.length() > 0 ? " and " : "";
            String deletedSelection = RawContactsEntity.DELETED + " = 0 ";
            String selection = queryFilterStr + andStr + deletedSelection;
            String[] projection = {
                    RawContactsEntity.CONTACT_ID,
                    BaseColumns._ID,
                    RawContactsEntity.MIMETYPE,
                    RawContactsEntity.DELETED,
                    ContactsContract.RawContacts.STARRED,
                    ContactsContract.CommonDataKinds.Organization.TITLE,
                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                    ContactsContract.RawContacts.ACCOUNT_NAME,
                    ContactsContract.RawContacts.ACCOUNT_TYPE,
                    RawContactsEntity.DATA1, RawContactsEntity.DATA2,
                    RawContactsEntity.DATA3, RawContactsEntity.DATA4,
                    RawContactsEntity.DATA5, RawContactsEntity.DATA6,
                    RawContactsEntity.DATA7, RawContactsEntity.DATA8,
                    RawContactsEntity.DATA9, RawContactsEntity.DATA10,
                    RawContactsEntity.DATA11, RawContactsEntity.DATA12,
                    RawContactsEntity.DATA13, RawContactsEntity.DATA14,
                    RawContactsEntity.DATA15
            };
            cursor = mContext.getContentResolver().query(
                    RawContactsEntity.CONTENT_URI, projection, selection, null,
                    ContactsContract.RawContactsEntity.CONTACT_ID);
            contactsList = getContactListFromCursor(cursor);
        } catch (Exception e) {
            Log.e(TAG, "本询本地所有联系人出错:" + e.toString());
        } finally {
            if (null != cursor) {
                cursor.close();
            }

        }

        return contactsList;
    }

    public boolean deleteAccountContact(Account account) {
        if (account == null || account.type == null || account.name == null) {
            return false;
        }
        if (account.type.equals("sim") && account.name.equals("sim卡")) {
            return false;
        }


        if (Utils.ACCOUNT_MOBILE_NAME.equals(account.name)
                && Utils.ACCOUNT_MOBILE_TYPE.equals(account.type)) {
            Account vendorAccount = Utils.getVendorAccount();
            if (null != vendorAccount) {
                account = vendorAccount;
            } else {
                account = null;
            }
        }
        if (isTransaction) {
            ContactOperations.deleteAccountContact(mContext, account, mBatchOperation, true);
        } else {
            if (!beginTransaction()) {
                Log.e(TAG, "nested is not allowed");
                return false;
            }
            ContactOperations.deleteAccountContact(mContext, account, mBatchOperation, true);
            execute();
        }
        return true;
    }

    public long addContact(Contact contact, Account account) {
        if (contact == null) {
            Log.e(TAG, "contact is null");
            return FAILURE;
        }
        if (isTransaction) {
            // int nRet = mBatchOperation.size();
            addOperation(contact, account, true);
            return mBatchOperation.size();
        } else {
            if (!beginTransaction()) {
                Log.e(TAG, "nested is not allowed");
                return FAILURE;
            }
            addOperation(contact, account, false);
            ContentProviderResult[] result = execute();
            long id = ContentUris.parseId(result[0].uri);
            return id > 0 ? id : FAILURE;
        }
    }

    public List<Contact> batchAddContacts(List<Contact> contacts) throws MoMoException {
        if (contacts == null) {
            Log.e(TAG, "contact is null");
            return null;
        }
        Log.d(TAG, "local add contact:" + contacts.size());
        Account account = Utils.getCurrentAccount();
        if(account != null && MyAccount.MOBILE_ACCOUNT.isEqual(account)) {
            account = Utils.getVendorAccount();
        }
        return batchAddContacts(contacts, account);
    }
    
    public List<Contact> batchAddContacts(List<Contact> contacts, Account account) throws MoMoException {
        if (contacts == null) {
            Log.e(TAG, "contact is null");
            return null;
        }
        beginTransaction();
        Log.d(TAG, "local add contact:" + contacts.size());
        for (Contact contact : contacts) {
            if (null != contact) {
                contact.setSavedToLocal(true);
                addContact(contact, account);
            }
        }
        ContentProviderResult[] result = execute();
        
        if (result == null) {
            Log.e(TAG, "batch add local contact failed");
            return null;
        }
        Log.d(TAG, "add contact result size:" + result.length);
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        int rawContactIdFlag = 1;
        
        matcher.addURI(ContactsContract.AUTHORITY, "raw_contacts/#",
                rawContactIdFlag);
        int index = 0;
        int length = result.length;
        for (int i = 0; i < length; i++) {
            Uri uri = result[i].uri;
            if (null != uri) {
                if (rawContactIdFlag == matcher.match(uri)) {
                    int phoneCid = (int)ContentUris.parseId(uri);
                    if (phoneCid == 0) {
                        throw new MoMoException("phone cid is 0");
                    }
                    contacts.get(index).setPhoneCid(phoneCid);
                    index++;
                }
            } else {
                Log.e(TAG, "batch add local index(" + i + ") failed");
            }
        }
        Log.d(TAG, "add contact success number:" + index);
        return contacts;
    }

    public boolean addPhoto(long phoneCid, byte[] image) {
        if (phoneCid <= 0)
            return false;
        if (image == null)
            return true; // don't need add
        // add to builder

        if (isTransaction) {
            final ContactOperations contactOps = ContactOperations.insertData(
                    mContext, phoneCid, mBatchOperation);
            contactOps.addPhoto(image);
        } else {
            if (!beginTransaction()) {
                Log.e(TAG, "error beginTransaction");
                return false;
            }
            final ContactOperations contactOps = ContactOperations.insertData(
                    mContext, phoneCid, mBatchOperation);
            contactOps.addPhoto(image);
            execute();
        }
        return true;
    }

    public boolean batchAddPhoto(List<Contact> contacts) {
        if (contacts == null) {
            Log.e(TAG, "contact is null");
            return false;
        }
        beginTransaction();
        for (Contact contact : contacts) {
            Avatar avatar = contact.getAvatar();
            if (null != avatar && null != avatar.getMomoAvatarImage()
                    && avatar.getMomoAvatarImage().length > 0) {
                boolean res = addPhoto(contact.getPhoneCid(),
                        avatar.getMomoAvatarImage());
                if (!res) {
                    isTransaction = false;
                    return false;
                }
            }
        }
        ContentProviderResult[] result = execute();
        if (result == null) {
            return false;
        }
        return true;
    }

    public boolean deleteContact(long phoneCid) {
        if (phoneCid < 1) {
            Log.e(TAG, "illegal phoneCid(" + phoneCid + ")");
            return false;
        }
        if (isTransaction) {
            deleteOperation(phoneCid);
        } else {
            if (!beginTransaction()) {
                Log.e(TAG, "nested is not allowed");
                return false;
            }
            deleteOperation(phoneCid);
            execute();
        }
        return true;
    }

    public boolean batchDeleteContacts(List<Contact> contacts) {
        if (contacts == null) {
            Log.e(TAG, "contacts list is null");
            return false;
        }
        beginTransaction();
        for (Contact contact : contacts) {
            this.deleteContact(contact.getPhoneCid());
        }
        execute();
        return true;
    }

    public boolean batchDeleteContactsByIdList(List<Long> contactIdList) {
        if (contactIdList == null) {
            Log.e(TAG, "contacts list is null");
            return false;
        }
        beginTransaction();
        for (long phoneCid : contactIdList) {
            if (phoneCid < 1)
                continue;
            this.deleteContact(phoneCid);
        }
        execute();
        return true;
    }

    public boolean updateContact(Contact contact) {
        if (contact == null) {
            Log.e(TAG, "contact is null");
            return false;
        } else if (contact.getPhoneCid() < 1) {
            Log.e(TAG, "illegal contact's phone id");
            return false;
        }
        if (isTransaction) {
            updateOperation(contact.getPhoneCid(), contact);
            return true;
        } else {
            if (!beginTransaction()) {
                Log.e(TAG, "nested is not allowed");
                return false;
            }
            updateOperation(contact.getPhoneCid(), contact);
            execute();
            return true;
        }
    }

    public boolean batchUpdateContact(List<Contact> contacts) {
        if (contacts == null) {
            Log.e(TAG, "contacts list is null");
            return false;
        }
        beginTransaction();
        for (Contact contact : contacts) {
            this.updateContact(contact);
        }
        ContentProviderResult[] result = execute();
        if (result == null) {
            Log.e(TAG, "update contact list failed");
        }
        return true;
    }

    public String getContactDisplayName(long contactId) {
        String displayName = "";
        if (contactId < 1) {
            Log.e(TAG, "illegal contact id(" + contactId + ")");
            return displayName;
        }
        Cursor cursor = null;
        try {
            String queryFilterStr = Utils.getAccountQueryFilterStr();
            String andStr = queryFilterStr.length() > 0 ? " and " : "";
            String deletedSelection = RawContactsEntity.DELETED
                    + " = 0 and "
                    + BaseColumns._ID
                    + " = ? ";
            String selection = queryFilterStr + andStr + deletedSelection;
            String[] projection = {
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            cursor = mContext.getContentResolver().query(
                    ContactsContract.RawContacts.CONTENT_URI,
                    projection, selection,
                    new String[] {
                        Long.toString(contactId)
                    },
                    null);
            while (cursor.moveToNext()) {
                displayName = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (displayName == null) {
                    displayName = "";
                }
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return displayName;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return displayName;
    }

    public long getContactId(long rawContactId) {
        long contactId = 0;
        if (rawContactId < 1) {
            Log.e(TAG, "illegal raw contact id(" + rawContactId + ")");
            return contactId;
        }
        Cursor cursor = null;
        try {
            String queryFilterStr = Utils.getAccountQueryFilterStr();
            String andStr = queryFilterStr.length() > 0 ? " and " : "";
            String deletedSelection = RawContactsEntity.DELETED
                    + " = 0 and "
                    + BaseColumns._ID
                    + " = ? ";
            String selection = queryFilterStr + andStr + deletedSelection;
            String[] projection = {
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            };

            cursor = mContext.getContentResolver().query(
                    ContactsContract.RawContacts.CONTENT_URI,
                    projection, selection,
                    new String[] {
                        Long.toString(rawContactId)
                    },
                    null);
            if (cursor.moveToNext()) {
                contactId = cursor.getLong(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return contactId;
    }

    private List<Contact> getContactListFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Contact> contactList = new ArrayList<Contact>();
        try {
            int defaultPhoneCid = -1111;
            if (count > 0) {
                int previousPhoneCid = defaultPhoneCid;
                Contact previousContact = new Contact();
                while (cursor.moveToNext()) {
                    String label = "";
                    int phoneCid = cursor
                            .getInt(cursor
                                    .getColumnIndex(BaseColumns._ID));

                    String mimeType = cursor
                            .getString(cursor
                                    .getColumnIndex(ContactsContract.RawContactsEntity.MIMETYPE));
                    Contact currentContact;
                    if (phoneCid != previousPhoneCid) {
                        currentContact = new Contact();
                        int starred = cursor
                                .getInt(cursor
                                        .getColumnIndex(ContactsContract.Contacts.STARRED));
                        currentContact.setFavoried(1 == starred ? true : false);
                        currentContact.setPhoneCid(phoneCid);
                        contactList.add(currentContact);
                    } else {
                        currentContact = previousContact;
                    }
                    if (null == mimeType) {
                        continue;
                    }
                    if (mimeType.endsWith("/name")) { // 姓名
                        String lastName = cursor.getString(cursor
                                .getColumnIndex(RawContactsEntity.DATA3));

                        StringBuilder sb = new StringBuilder(30);
                        if (null != lastName)
                            sb.append(lastName);

                        String middleName = cursor.getString(cursor
                                .getColumnIndex(RawContactsEntity.DATA5));
                        String firstName = cursor.getString(cursor
                                .getColumnIndex(RawContactsEntity.DATA2));
                        if (null != middleName) {
                            if (null == firstName)
                                firstName = "";
                            firstName = middleName + firstName;
                        }

                        sb.append(firstName);
                        String name = sb.toString();
                        if (name.length() > 0) {
                            currentContact.setLastName(lastName);
                            currentContact.setFirstName(firstName);
                            currentContact.setFormatName(name);
                            currentContact.setNamePinyin(PinyinHelper
                                    .convertChineseToPinyinArray(name));
                        } else {
                            Log.e(TAG, "name is null:");
                        }
                        previousContact = currentContact;
                        previousPhoneCid = phoneCid;
                    } else if (mimeType.endsWith("/nickname")) { // 昵称
                        currentContact.setNickName(cursor.getString(cursor
                                .getColumnIndex(Nickname.NAME)));
                        previousContact = currentContact;
                        previousPhoneCid = phoneCid;
                    } else if (mimeType.endsWith("/group_membership")) {
                        long categoryID = cursor.getLong(cursor
                                .getColumnIndex(GroupMembership.GROUP_ROW_ID));
                        currentContact.getCategoryIdList().add(categoryID);
                        previousContact = currentContact;
                        previousPhoneCid = phoneCid;
                    } else if (mimeType.endsWith("/contact_event")) { // 生日&纪念日
                        String startDate = cursor.getString(cursor
                                .getColumnIndex(Event.START_DATE));
                        if (startDate == null) {
                            continue;
                        }
                        if (cursor.getInt(cursor.getColumnIndex(Event.TYPE)) == Event.TYPE_BIRTHDAY) {
                            currentContact.setBirthday(startDate);
                        } else if (cursor.getInt(cursor
                                .getColumnIndex(Event.TYPE)) == Event.TYPE_ANNIVERSARY) {
                            currentContact.getEventLabelList().add(
                                    Data.LABEL_ANNIVERSARY);
                            currentContact.getEventList().add(startDate);
                        } else {
                            currentContact.getEventLabelList().add(
                                    Data.LABEL_ANNIVERSARY_OTHER);
                            currentContact.getEventList().add(startDate);
                        }
                        previousContact = currentContact;
                        previousPhoneCid = phoneCid;
                    } else if (mimeType.endsWith("/note")) { // 备注
                        currentContact.setNote(cursor.getString(cursor
                                .getColumnIndex(Note.NOTE)));
                        previousContact = currentContact;
                        previousPhoneCid = phoneCid;
                    } else if (mimeType.endsWith("/photo")) { // 头像
                        byte[] localAvatarImage = cursor.getBlob(cursor
                                .getColumnIndex(Photo.PHOTO));
                        Avatar avatar = new Avatar(0, "", localAvatarImage);
                        currentContact.setAvatar(avatar);
                        previousContact = currentContact;
                        previousPhoneCid = phoneCid;
                    } else if (mimeType.endsWith("/organization")) { // 组织机构
                        currentContact.setOrganization(cursor.getString(cursor
                                .getColumnIndex(Organization.COMPANY)));
                        currentContact.setDepartment(cursor.getString(cursor
                                .getColumnIndex(Organization.DEPARTMENT)));
                        currentContact.setJobTitle(cursor.getString(cursor
                                .getColumnIndex(Organization.TITLE)));
                        previousContact = currentContact;
                        previousPhoneCid = phoneCid;
                    } else if (mimeType.endsWith("/phone_v2")) { // 电话
                        String phoneNumber = cursor.getString(cursor
                                .getColumnIndex(Phone.NUMBER));
                        String phoneNumberAfterRetriped = Utils
                                .stripCharsInculdePlus(phoneNumber);
                        currentContact.getPhoneList().add(
                                phoneNumberAfterRetriped);
                        int type = cursor.getInt(cursor
                                .getColumnIndex(Phone.TYPE));
                        if (type == BaseTypes.TYPE_CUSTOM) {
                            label = cursor.getString(cursor
                                    .getColumnIndex(Phone.LABEL));
                        } else {
                            label = Data.getLabelByType(Data.PHONE, type);
                        }
                        if ("".equals(label) || null == label) {
                            label = Data.TYPE_OTHER;
                        }
                        currentContact.getPhoneLabelList().add(label);
                        previousContact = currentContact;
                        previousPhoneCid = phoneCid;
                    } else if (mimeType.endsWith("/email_v2")) { // 邮箱
                        currentContact.getEmailList().add(
                                cursor.getString(cursor
                                        .getColumnIndex(Email.DATA)));
                        int type = cursor.getInt(cursor
                                .getColumnIndex(Email.TYPE));
                        if (type == BaseTypes.TYPE_CUSTOM) {
                            label = cursor.getString(cursor
                                    .getColumnIndex(Email.LABEL));
                        } else {
                            label = Data.getLabelByType(Data.EMAIL, type);
                        }
                        if ("".equals(label) || null == label) {
                            label = Data.TYPE_OTHER_INTERNET;
                        }
                        currentContact.getEmailLabelList().add(label);
                        previousContact = currentContact;
                        previousPhoneCid = phoneCid;
                    } else if (mimeType.endsWith("/website")) { // 网址
                        currentContact.getWebsiteList().add(
                                cursor.getString(cursor
                                        .getColumnIndex(Website.URL)));
                        int type = cursor.getInt(cursor
                                .getColumnIndex(Website.TYPE));
                        if (type == BaseTypes.TYPE_CUSTOM) {
                            label = cursor.getString(cursor
                                    .getColumnIndex(Website.LABEL));
                        } else {
                            label = Data.getLabelByType(Data.INTERNET, type);
                        }
                        if ("".equals(label) || null == label) {
                            label = Data.LABEL_WEBSITE_OTHER;
                        }
                        currentContact.getWebsiteLabelList().add(label);
                        previousContact = currentContact;
                        previousPhoneCid = phoneCid;
                    } else if (mimeType.endsWith("/relation")) { // 关系
                        currentContact.getRelationList().add(
                                cursor.getString(cursor
                                        .getColumnIndex(Relation.NAME)));
                        int type = cursor.getInt(cursor
                                .getColumnIndex(Relation.TYPE));
                        if (type == BaseTypes.TYPE_CUSTOM) {
                            label = cursor.getString(cursor
                                    .getColumnIndex(Relation.LABEL));
                        } else {
                            label = Data.getLabelByType(Data.RELATE, type);
                        }
                        if ("".equals(label) || null == label) {
                            label = Data.LABEL_RELATED_OTHER;
                        }
                        currentContact.getRelationLabelList().add(label);
                        previousContact = currentContact;
                        previousPhoneCid = phoneCid;
                    } else if (mimeType.endsWith("/postal-address_v2")) {// 通讯地址
                        String country = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.COUNTRY));
                        String region = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.REGION));
                        String city = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.CITY));
                        String street = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.STREET));
                        String postCode = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.POSTCODE));
                        Address address = new Address();
                        address.setCountry(country);
                        address.setState(region);
                        address.setCity(city);
                        address.setStreet(street);
                        address.setPostalCode(postCode);
                        int type = cursor.getInt(cursor
                                .getColumnIndex(StructuredPostal.TYPE));
                        if (type == BaseTypes.TYPE_CUSTOM) {
                            label = cursor.getString(cursor
                                    .getColumnIndex(StructuredPostal.LABEL));
                        } else {
                            label = Data.getLabelByType(Data.ADDR, type);
                        }
                        if ("".equals(label) || null == label) {
                            label = Data.TYPE_OTHER;
                        }
                        address.setLabel(label);
                        currentContact.getAddressList().add(address);
                        previousContact = currentContact;
                        previousPhoneCid = phoneCid;
                    } else if (mimeType.endsWith("/im")) { // IM
                        int protocol = cursor.getInt(cursor
                                .getColumnIndex(Im.PROTOCOL));
                        if (protocol == Im.PROTOCOL_CUSTOM) {
                            String customProtocol = cursor.getString(cursor
                                    .getColumnIndex(Im.CUSTOM_PROTOCOL));
                            if ("91u".equalsIgnoreCase(customProtocol.trim()
                                    .toLowerCase())) {
                                protocol = Data.PROTOCOL_91U;
                            } else {
                                protocol = Data.PROTOCOL_QQ; // 不支持自定义的IM类型
                            }
                        }
                        int propertyId = Data.getProtocolPropertyId(protocol);
                        if (propertyId == 0) {
                            continue;
                        }
                        currentContact.getImProtocolList().add(
                                Data.getProtocolLabel(protocol));
                        int type = cursor
                                .getInt(cursor.getColumnIndex(Im.TYPE));
                        currentContact.getImLabelList().add(
                                Data.getLabelByType(propertyId, type));
                        currentContact.getImList()
                                .add(cursor.getString(cursor
                                        .getColumnIndex(Im.DATA)));
                        previousContact = currentContact;
                        previousPhoneCid = phoneCid;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "从cursor取联系人数据出错：" + e.toString());
        } finally {
            if (null != cursor)
                cursor.close();
        }
        Iterator<Contact> ite = contactList.iterator();
        while (ite.hasNext()) {
            Contact contact = ite.next();
            String name = contact.getFormatName();
            if (null == name || name.length() < 1) {
                String displayName = getContactDisplayName(contact.getPhoneCid());
                if (displayName.length() > 0) {
                    contact.setFirstName(displayName);
                    contact.setFormatName(displayName);
                    contact.setNamePinyin(PinyinHelper
                            .convertChineseToPinyinArray(displayName));
                } else {
                    ite.remove();
                    Log.e(TAG, "get display_name is null:" + contact.getPhoneCid());
                    continue;
                }
            }
            List<Long> categoryIdList = contact.getCategoryIdList();
            Collections.sort(categoryIdList);
        }
        return contactList;
    }

    private void addOperation(Contact contact, Account account, boolean isYeild) {
        final ContactOperations contactOp = ContactOperations.createNewContact(
                mContext, account, mBatchOperation, isYeild);
        Avatar avatar = contact.getAvatar();
        if (null != avatar)
            contactOp.addPhoto(avatar.getMomoAvatarImage());
        addOrUpdateOperation(contactOp, contact);
        return;
    }

    private void addOrUpdateOperation(final ContactOperations contactOp, Contact contact) {
        contactOp.addName(Utils.setStringToBlankIfNull(contact.getFirstName()),
                "", Utils.setStringToBlankIfNull(contact.getLastName()));
        String nickName = contact.getNickName();
        if (!Utils.isEmpty(nickName))
            contactOp.addNiciName(nickName);
        String note = contact.getNote();
        if (!Utils.isEmpty(note))
            contactOp.addNote(note);
        contactOp.addStarred(contact.isFavoried()).addOrganization(
                Utils.setStringToBlankIfNull(contact.getOrganization()),
                Utils.setStringToBlankIfNull(contact.getDepartment()),
                Utils.setStringToBlankIfNull(contact.getJobTitle()));
        String birthday = contact.getBirthday();
        if (null != birthday && birthday.length() > 0) {
            contactOp.addBirthDay(DateFormater.getTimeStamp(birthday));
        }
        String customRingtone = contact.getCustomRingtone();
        if (null != customRingtone && customRingtone.length() > 0) {
            contactOp.addCustomeRingtone(customRingtone);
        }
        List<Long> categoryIdList = contact.getCategoryIdList();
        if (categoryIdList != null && categoryIdList.size() > 0) {
            deleteContactAllCategory(contact.getPhoneCid());
            for (Long categoryId : categoryIdList) {
                if (categoryId < 1) {
                    Log.e(TAG, "local category id is illegal");
                    continue;
                }
                contactOp.addGroupMember(categoryId);
            }
        }

        List<String> phoneList = contact.getPhoneList();
        List<String> phoneLabelList = contact.getPhoneLabelList();
        if (phoneList.size() != phoneLabelList.size()) {
            Log.e(TAG, "phoneList's size(" + phoneList.size()
                    + ") unsuitably matched phoneLabelList's size("
                    + phoneLabelList.size() + ").");
        } else {
            for (int i = 0; i < phoneList.size(); i++) {
                int type = Data.getTypeByLabel(Data.PHONE,
                        phoneLabelList.get(i));
                if (type == -1) {
                    Log.e(TAG, "illegal phone label(" + phoneLabelList.get(i)
                            + ")");
                    continue;
                } else if (type == Data.TYPE_PHONE_CUSTOM) {
                    type = BaseTypes.TYPE_CUSTOM;
                }
                contactOp.addPhone(phoneList.get(i), type,
                        phoneLabelList.get(i));
            }
        }

        List<String> emailList = contact.getEmailList();
        List<String> emailLabelList = contact.getEmailLabelList();
        if (emailList.size() != emailLabelList.size()) {
            Log.e(TAG, "emailList's size(" + emailList.size()
                    + ") unsuitably matched emailLabelList's size("
                    + emailLabelList.size() + ").");
        } else {
            for (int i = 0; i < emailList.size(); i++) {
                int type = Data.getTypeByLabel(Data.EMAIL,
                        emailLabelList.get(i));
                if (type == -1) {
                    Log.e(TAG, "illegal email label(" + emailLabelList.get(i)
                            + ")");
                    continue;
                }
                contactOp.addEmail(emailList.get(i), type,
                        emailLabelList.get(i));
            }
        }

        List<String> websiteList = contact.getWebsiteList();
        List<String> websiteLabelList = contact.getWebsiteLabelList();
        if (websiteList.size() != websiteLabelList.size()) {
            Log.e(TAG, "websiteList's size(" + websiteList.size()
                    + ") unsuitably matched websiteLabelList's size("
                    + websiteLabelList.size() + ").");
        } else {
            for (int i = 0; i < websiteList.size(); i++) {
                int type = Data.getTypeByLabel(Data.INTERNET,
                        websiteLabelList.get(i));
                if (type == -1) {
                    Log.e(TAG,
                            "illegal website label(" + websiteLabelList.get(i)
                                    + ")");
                    continue;
                }
                contactOp.addWebsite(websiteList.get(i), type);
            }
        }

        List<String> relationList = contact.getRelationList();
        List<String> relationLabelList = contact.getRelationLabelList();
        if (relationList.size() != relationLabelList.size()) {
            Log.e(TAG, "relationList's size(" + relationList.size()
                    + ") unsuitably matched relationLabelList's size("
                    + relationLabelList.size() + ").");
        } else {
            for (int i = 0; i < relationList.size(); i++) {
                int type = Data.getTypeByLabel(Data.RELATE,
                        relationLabelList.get(i));
                if (type == -1) {
                    Log.e(TAG,
                            "illegal relation label("
                                    + relationLabelList.get(i) + ")");
                    continue;
                }
                contactOp.addRelation(relationList.get(i), type);
            }
        }

        List<Address> addressList = contact.getAddressList();
        for (int i = 0; i < addressList.size(); i++) {
            int type = Data.getTypeByLabel(Data.ADDR, addressList.get(i)
                    .getLabel());
            if (type == -1) {
                Log.e(TAG, "illegal address label("
                        + addressList.get(i).getLabel() + ")");
                continue;
            }
            contactOp.addAddress(addressList.get(i), type);
        }

        List<String> eventList = contact.getEventList();
        List<String> eventLabelList = contact.getEventLabelList();
        if (eventList.size() != eventLabelList.size()) {
            Log.e(TAG, "eventList's size(" + eventList.size()
                    + ") unsuitably matched eventLabelList's size("
                    + eventLabelList.size() + ").");
        } else {
            for (int i = 0; i < eventList.size(); i++) {
                int type = Data.getTypeByLabel(Data.DAY, eventLabelList.get(i));
                if (type == -1) {
                    Log.e(TAG, "illegal event label(" + eventLabelList.get(i)
                            + ")");
                    continue;
                }
                contactOp.addAnniversay(eventList.get(i), type);
            }
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
        } else {
            for (int i = 0; i < imList.size(); i++) {
                String protocolLabel = imProtocolList.get(i).trim();
                int property = 0;

                int protocolId = -2;
                for (Object object : Data.IM_PROTOCOL_LABEL.keySet()) {
                    if (Data.IM_PROTOCOL_LABEL.get(object).equalsIgnoreCase(
                            protocolLabel)) {
                        protocolId = Integer.valueOf(object.toString());
                    }
                }
                if (protocolId < Data.PROTOCOL_91U
                        || protocolId > Data.PROTOCOL_JABBER) {
                    Log.e(TAG, "illegal im protocol label(" + protocolLabel
                            + ")");
                    continue;
                }
                if (protocolId == Data.PROTOCOL_91U) {
                    property = Data.RTIME_91U;
                } else {
                    property = Data.IM_PROTOCOL.get(protocolId);
                }

                int type = Data.getTypeByLabel(property, imLabelList.get(i));
                if (type == -2) {
                    Log.e(TAG, "illegal im label(" + imLabelList.get(i) + ")");
                    continue;
                }
                contactOp.addIm(imList.get(i), protocolId, type);
            }
        }
        return;
    }

    private void updateOperation(long phoneCid, Contact contact) {
        final ContactOperations contactOp = ContactOperations.updateContact(
                mContext, phoneCid, mBatchOperation);
        addOrUpdateOperation(contactOp, contact);
        Avatar avatar = contact.getAvatar();
        if (null != avatar) {
            byte[] image = avatar.getMomoAvatarImage();
            if (null != image && image.length > 0)
                contactOp.addPhoto(image);
        }
    }

    private void deleteOperation(long phoneCid) {
        ContactOperations.deleteContact(mContext, phoneCid, mBatchOperation, true);
    }

    private boolean beginTransaction() {
        if (isTransaction) {
            Log.e(TAG, "transcation have begun");
            return false; // have begun
        }
        if (mBatchOperation != null) {
            mBatchOperation.clear();
        } else {
            mBatchOperation = new BatchOperation(mContext,
                    mContext.getContentResolver());
        }
        isTransaction = true;
        return true;
    }

    private ContentProviderResult[] execute() {
        isTransaction = false;
        return mBatchOperation.execute();
    }

    private boolean deleteContactAllCategory(long phoneCid) {
        if (phoneCid < 0) {
            Log.e(TAG, "illegal phoneCid(" + phoneCid + ")");
            return false;
        }
        String queryFilterStr = Utils.getAccountQueryFilterStr();
        return ContactOperations.deleteAllCategoryByMember(mContext,
                queryFilterStr, phoneCid, mBatchOperation);
    }

    /**
     * 通过phoneCid取铃声
     * 
     * @param phoneCid
     * @return 铃声的uri
     */
    public String getCustomRingtoneByPhoneCid(long phoneCid) {
        if (phoneCid < 1)
            return "";
        String ringtone = "";
        String[] projection = new String[] {
                RawContacts.CUSTOM_RINGTONE
        };
        String selection = RawContacts.CONTACT_ID + " = ?";
        Cursor c = mContext.getContentResolver().query(RawContacts.CONTENT_URI, projection,
                selection, new String[] {
                    String.valueOf(phoneCid)
                }, null);
        try {
            while (c.moveToNext()) {
                ringtone = c.getString(c.getColumnIndex(RawContacts.CUSTOM_RINGTONE));
                break;
            }
        } catch (Exception e) {
            Log.e(TAG, "查询铃声出错" + e.toString());
        } finally {
            c.close();
        }
        return ringtone;
    }

    /**
     * 读取sim联系人列表
     */
    public List<Contact> getSimContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        Cursor cursor = null;
        try {
            Uri uri = Uri.parse("content://icc/adn");
            cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String phone = cursor.getString(cursor.getColumnIndex("number"));
                    String email = cursor.getString(cursor.getColumnIndex("emails"));
                    long phoneCid = cursor.getLong(cursor.getColumnIndex("_id"));
                    name = Utils.setStringToBlankIfNull(name);
                    phone = Utils.setStringToBlankIfNull(phone);
                    email = Utils.setStringToBlankIfNull(email);
                    Log.d(TAG, "name:" + name + " number:" + phone + " emails:" + email + " _id"
                            + phoneCid);
                    if (name.length() < 1 && phone.length() < 1 && email.length() < 1) {
                        continue;
                    }
                    Contact contact = new Contact();
                    contact.setPhoneCid(phoneCid);
                    contact.setFirstName(name);
                    contact.setFormatName(name);
                    if (phone.length() > 0) {
                        contact.getPhoneList().add(phone);
                        contact.getPhoneLabelList().add(Data.TYPE_CELL);
                    }
                    if (email.length() > 0) {
                        contact.getEmailList().add(email);
                        contact.getEmailLabelList().add(Data.TYPE_OTHER_INTERNET);
                    }
                    contactList.add(contact);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return contactList;
    }

    public int getSimContactsCount() {
        int count = 0;
        Cursor cursor = null;
        try {
            Uri uri = Uri.parse("content://icc/adn");
            cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                count = cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return count;
    }


    public class PhoneContact {
        private long contactId;

        private String phone;

        public long getContactId() {
            return contactId;
        }

        public void setContactId(long contactId) {
            this.contactId = contactId;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    /**
     * 获取手机所有电话列表
     * 
     * @return
     */
    public List<PhoneContact> getAllPhoneList() {
        List<PhoneContact> pcList = new ArrayList<PhoneContact>();

        Cursor cursor = null;
        try {
            String[] projection = {
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            };
            cursor = mContext.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
            while (cursor.moveToNext()) {
                String phone = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                long contactId = cursor.getLong(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                PhoneContact pc = new PhoneContact();
                pc.setContactId(contactId);
                pc.setPhone(phone);
                pcList.add(pc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return pcList;
    }

    /**
     * 获取手机所有电话列表
     * 
     * @return
     */
    public HashSet<String> getAllPhones() {
        HashSet<String> hashSet = new HashSet<String>();
        Cursor cursor = null;
        try {
            String[] projection = {
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };
            cursor = mContext.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
            while (cursor.moveToNext()) {
                String phone = cursor.getString(0);
                hashSet.add(phone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return hashSet;
    }
}
