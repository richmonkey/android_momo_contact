
package cn.com.nd.momo.api.sync;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentValues;
import android.content.Context;
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
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import cn.com.nd.momo.api.types.Address;
import cn.com.nd.momo.api.types.Contact;
import cn.com.nd.momo.api.util.DateFormater;

/**
 * @author Administrator
 */
public class ContactOperations {

    private final ContentValues mValues;

    private Builder mBuilder;

    private final BatchOperation mBatchOperation;

    private boolean mYield;

    private long mRawContactId;

    private int mBackReference;

    private boolean mIsNewContact;

    /*---------Constructors---------*/
    private ContactOperations(Context context, BatchOperation batchOperation) {
        mValues = new ContentValues();
        mYield = true;
        mBatchOperation = batchOperation;
    }

    private ContactOperations(Context context, Account account,
            BatchOperation batchOperation, boolean isYield) {
        this(context, batchOperation);
        mBackReference = mBatchOperation.size();
        mIsNewContact = true;
        mYield = isYield;
        if (account == null) {
            mValues.putNull(RawContacts.ACCOUNT_NAME);
            mValues.putNull(RawContacts.ACCOUNT_TYPE);
        } else {
            mValues.put(RawContacts.ACCOUNT_NAME, account.name);
            mValues.put(RawContacts.ACCOUNT_TYPE, account.type);
        }
        // mValues.put("contact_in_visible_group", 1);
        mBuilder = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValues(mValues);
        mBatchOperation.add(mBuilder.build());
    }

    private ContactOperations(Context context, long rawContactId,
            BatchOperation batchOperations) {
        this(context, batchOperations);

        mIsNewContact = false;
        mRawContactId = rawContactId;
        // delete all data first
        String selection = Data.RAW_CONTACT_ID + "=? ";
        String[] selectionArgs = new String[] {
                String.valueOf(mRawContactId)
        };
        mBuilder = ContentProviderOperation.newDelete(Data.CONTENT_URI)
                .withYieldAllowed(false)
                .withSelection(selection, selectionArgs);
        mBatchOperation.add(mBuilder.build());
    }

    private ContactOperations(Context context, long rawContactId,
            BatchOperation batchOperations, String mimetype) {
        this(context, batchOperations);

        mIsNewContact = false;
        mRawContactId = rawContactId;
        String selection = Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE
                + " =? ";
        String[] selectionArgs = new String[] {
                String.valueOf(mRawContactId),
                mimetype
        };
        mBuilder = ContentProviderOperation.newDelete(Data.CONTENT_URI)
                .withYieldAllowed(false)
                .withSelection(selection, selectionArgs);
        mBatchOperation.add(mBuilder.build());
    }

    private ContactOperations(Context context, long rawContactId,
            BatchOperation batchOperation, boolean isNewContact) {
        this(context, batchOperation);

        mRawContactId = rawContactId;
        mIsNewContact = isNewContact;
    }

    private ContactOperations(Context context, Uri uri, long rawContactId,
            BatchOperation batchOperation) {
        this(context, batchOperation);
        mIsNewContact = false;
        mRawContactId = rawContactId;
    }

    private ContactOperations(Context context, Uri uri, long rawContactId,
            long cateId, BatchOperation batchOperation) {
        this(context, batchOperation);
        mRawContactId = rawContactId;
    }

    /*---------factory functions---------*/
    public static ContactOperations createNewContact(Context context,
            Account account, BatchOperation batchOperation, boolean isYield) {
        return new ContactOperations(context, account, batchOperation, isYield);
    }

    public static ContactOperations updateContact(Context context,
            long rawContactId, BatchOperation batchOperation) {
        return new ContactOperations(context, rawContactId, batchOperation);
    }

    public static ContactOperations updateContactData(Context context,
            long rawContactId, BatchOperation batchOperation, String mimeType) {
        return new ContactOperations(context, rawContactId, batchOperation, mimeType);
    }

    /**
     * 添加一个数据行在系统data表
     * 
     * @param context
     * @param rawContactId
     * @param batchOperation
     * @return
     */
    public static ContactOperations insertData(Context context,
            long rawContactId, BatchOperation batchOperation) {
        return new ContactOperations(context, rawContactId, batchOperation,
                false);
    }

    public static void deleteAccountContact(Context context, Account account,
                                            BatchOperation batchOperation, boolean isYield) {
        String selection;
        String[] selectionArgs;
        if (account == null) {
            selection = RawContacts.ACCOUNT_TYPE + " is null AND " + RawContacts.ACCOUNT_NAME + " is null";
            selectionArgs = null;
        } else {
            selection = RawContacts.ACCOUNT_TYPE + " = ? AND " + RawContacts.ACCOUNT_NAME + " = ?";
            selectionArgs = new String[]{
                    account.type,
                    account.name
            };
        }
        Uri deleteUri = Uri.parse(RawContacts.CONTENT_URI.toString() + "?"
                + ContactsContract.CALLER_IS_SYNCADAPTER + "=true");
        ContentProviderOperation operation = ContentProviderOperation.newDelete(deleteUri)
                .withSelection(selection, selectionArgs)
                .withYieldAllowed(isYield)
                .build();
        batchOperation.add(operation);
    }

    public static void deleteContact(Context context, long rawContactId,
            BatchOperation batchOperation, boolean isYield) {
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = new String[] {
                String.valueOf(rawContactId)
        };
        Uri deleteUri = Uri.parse(RawContacts.CONTENT_URI.toString() + "?"
                + ContactsContract.CALLER_IS_SYNCADAPTER + "=true");
        batchOperation.add(ContentProviderOperation
                .newDelete(deleteUri)
                .withSelection(selection, selectionArgs)
                .withYieldAllowed(isYield).build());
    }

    public static boolean deleteCategoryMember(Context context,
            long rawContactId, long cateId, BatchOperation batchOperation) {
        String selection = GroupMembership.RAW_CONTACT_ID + " = ? AND "
                + GroupMembership.GROUP_ROW_ID + " = ?";
        String[] selectionArgs = new String[] {
                String.valueOf(rawContactId),
                String.valueOf(cateId)
        };
        batchOperation.add(ContentProviderOperation
                .newDelete(Groups.CONTENT_URI)
                .withSelection(selection, selectionArgs).withYieldAllowed(true)
                .build());
        return true;
    }

    public static boolean deleteAllCategoryByMember(Context context,
            String accountFilter, long rawContactId,
            BatchOperation batchOperation) {
        if (rawContactId == 0)
            return true; // it a new contact require quit, so just do nothing
        String selection = Data.RAW_CONTACT_ID + " =? AND " + Data.MIMETYPE
                + " = ?";
        if (accountFilter != null && accountFilter.length() > 0) {
            selection += " AND " + accountFilter;
        }
        String[] selectionArg = {
                String.valueOf(rawContactId),
                GroupMembership.CONTENT_ITEM_TYPE
        };
        batchOperation.add(ContentProviderOperation.newDelete(Data.CONTENT_URI)
                .withSelection(selection, selectionArg).build());
        return true;
    }

    public static boolean deleteCategory(Context context, long cateId,
            BatchOperation batchOperation) {
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = new String[] {
                String.valueOf(cateId)
        };
        batchOperation.add(ContentProviderOperation
                .newDelete(Groups.CONTENT_URI)
                .withSelection(selection, selectionArgs).withYieldAllowed(true)
                .build());
        return true;
    }

    /*------------------------------- add data-------------------------------*/
    public ContactOperations addName(String firstName, String middleName,
            String lastName) {
        mValues.clear();
        if (!TextUtils.isEmpty(firstName)) {
            mValues.put(StructuredName.GIVEN_NAME, firstName);
        }
        if (!TextUtils.isEmpty(middleName)) {
            mValues.put(StructuredName.MIDDLE_NAME, middleName);
        }
        if (!TextUtils.isEmpty(lastName)) {
            mValues.put(StructuredName.FAMILY_NAME, lastName);
        }

        if (mValues.size() > 0)
            addInsertOrUpdaeOp(StructuredName.CONTENT_ITEM_TYPE);

        return this;
    }

    public ContactOperations addPhone(String phone, int type, String label) {
        mValues.clear();
        if (!TextUtils.isEmpty(phone)) {
            mValues.put(Phone.DATA, phone);
            mValues.put(Phone.TYPE, type);
            if (type == BaseTypes.TYPE_CUSTOM)
                mValues.put(Phone.LABEL, label);

            addInsertOrUpdaeOp(Phone.CONTENT_ITEM_TYPE);
        }
        return this;
    }

    public ContactOperations addPhoto(byte[] photo) {
        mValues.clear();
        if (photo != null) {
            mValues.put(Photo.PHOTO, photo);
            addInsertOrUpdaeOp(Photo.CONTENT_ITEM_TYPE);
        }
        return this;
    }

    public ContactOperations addEmail(String email, int type, String label) {
        mValues.clear();
        if (!TextUtils.isEmpty(email)) {
            mValues.put(Email.DATA, email);
            mValues.put(Email.TYPE, type);
            if (type == BaseTypes.TYPE_CUSTOM)
                mValues.put(Email.LABEL, label);

            addInsertOrUpdaeOp(Email.CONTENT_ITEM_TYPE);
        }
        return this;
    }

    public ContactOperations addAddress(Address addr, int type) {
        mValues.clear();
        if (!TextUtils.isEmpty(addr.getCountry()))
            mValues.put(StructuredPostal.COUNTRY, addr.getCountry());
        if (!TextUtils.isEmpty(addr.getState()))
            mValues.put(StructuredPostal.REGION, addr.getState());
        if (!TextUtils.isEmpty(addr.getCity()))
            mValues.put(StructuredPostal.CITY, addr.getCity());
        if (!TextUtils.isEmpty(addr.getStreet()))
            mValues.put(StructuredPostal.STREET, addr.getStreet());
        if (!TextUtils.isEmpty(addr.getPostalCode()))
            mValues.put(StructuredPostal.POSTCODE, addr.getPostalCode());

        if (mValues.size() > 0) {
            mValues.put(StructuredPostal.TYPE, type);
            if (type == BaseTypes.TYPE_CUSTOM) {
                mValues.put(StructuredPostal.LABEL, addr.getLabel());
            }

            addInsertOrUpdaeOp(StructuredPostal.CONTENT_ITEM_TYPE);
        }

        return this;

    }

    public ContactOperations addIm(String im, int protocol, int type) {
        mValues.clear();
        if (!TextUtils.isEmpty(im)) {
            mValues.put(Im.DATA, im);
            mValues.put(Im.PROTOCOL, protocol);
            if (protocol == Im.PROTOCOL_CUSTOM) {
                mValues.put(Im.CUSTOM_PROTOCOL, "91u");
            }
            mValues.put(Im.TYPE, BaseTypes.TYPE_CUSTOM); // TODO why insert im
                                                         // type =
            // Im.TYPE_CUSTOM?
            mValues.put(Im.TYPE, type);
            addInsertOrUpdaeOp(Im.CONTENT_ITEM_TYPE);
        }

        return this;

    }

    public ContactOperations addNiciName(String nickname) {
        mValues.clear();
        if (!TextUtils.isEmpty(nickname)) {
            mValues.put(Nickname.NAME, nickname);
            mValues.put(Nickname.TYPE, Nickname.TYPE_DEFAULT);

            addInsertOrUpdaeOp(Nickname.CONTENT_ITEM_TYPE);
        }

        return this;
    }

    public ContactOperations addAnniversay(String anniversary, int type) {
        long lAnniversay = DateFormater.getTimeStamp(anniversary);
        if (lAnniversay == Contact.EPOCH_DIFF)
            return this;
        // 获取生日字符串
        String strAnniversary = DateFormater.GetDate(lAnniversay);
        return addEvent(strAnniversary, type);
    }

    public ContactOperations addBirthDay(long birthday) {
        if (birthday == Contact.EPOCH_DIFF)
            return this;
        // 获取生日字符串
        String strBirthday = DateFormater.GetDate(birthday);
        return addEvent(strBirthday, Event.TYPE_BIRTHDAY);
    }

    public ContactOperations addRelation(String relation, int type) {
        mValues.clear();
        if (!TextUtils.isEmpty(relation)) {
            mValues.put(Relation.NAME, relation);
            mValues.put(Relation.TYPE, type);

            addInsertOrUpdaeOp(Relation.CONTENT_ITEM_TYPE);
        }
        return this;
    }

    public ContactOperations addWebsite(String website, int type) {
        mValues.clear();
        if (!TextUtils.isEmpty(website)) {
            mValues.put(Website.URL, website);
            mValues.put(Website.TYPE, type);

            addInsertOrUpdaeOp(Website.CONTENT_ITEM_TYPE);
        }

        return this;
    }

    public ContactOperations addNote(String note) {
        mValues.clear();
        if (!TextUtils.isEmpty(note)) {
            mValues.put(Note.NOTE, note);
            addInsertOrUpdaeOp(Note.CONTENT_ITEM_TYPE);
        }

        return this;
    }

    public ContactOperations addOrganization(String company, String department,
            String jobTitle) {
        mValues.clear();
        if (!TextUtils.isEmpty(company)) {
            mValues.put(Organization.COMPANY, company);
        }
        if (!TextUtils.isEmpty(department)) {
            mValues.put(Organization.DEPARTMENT, department);
        }
        if (!TextUtils.isEmpty(jobTitle)) {
            mValues.put(Organization.TITLE, jobTitle);
        }

        if (mValues.size() > 0) {
            mValues.put(Organization.TYPE, Organization.TYPE_WORK);
            addInsertOrUpdaeOp(Organization.CONTENT_ITEM_TYPE);
        }

        return this;
    }

    public ContactOperations addGroup(String groupName) {
        mValues.clear();
        if (!TextUtils.isEmpty(groupName.trim())) {
            mValues.put(Groups.TITLE, groupName);
        }

        return this;
    }

    public ContactOperations addGroupMember(long categoryId) {
        mValues.clear();
        if (categoryId > 0) {
            mValues.put(GroupMembership.GROUP_ROW_ID, categoryId);
            addInsertOrUpdaeOp(GroupMembership.CONTENT_ITEM_TYPE);
        }

        return this;
    }

    // 添加starred标识，由于这个标识不在data表，所以跟其他字段信息分开，做特殊处理
    public ContactOperations addStarred(boolean isStarred) {
        int starred = isStarred ? 1 : 0;
        if (mIsNewContact) {
            mBuilder.withValue(RawContacts.STARRED, starred);
            mBatchOperation.remove(mBackReference);
            mBatchOperation.add(mBackReference, mBuilder.build());
        } else {
            Builder builder = ContentProviderOperation
                    .newUpdate(RawContacts.CONTENT_URI)
                    .withSelection(BaseColumns._ID + "=?",
                            new String[] {
                                String.valueOf(mRawContactId)
                            })
                    .withValue(RawContacts.STARRED, starred)
                    .withYieldAllowed(mYield);
            mBatchOperation.add(builder.build());
        }
        return this;
    }

    public ContactOperations addCustomeRingtone(String ringtone) {
        if (null != ringtone && ringtone.length() > 0) {

            if (mIsNewContact) {
                mBuilder.withValue(RawContacts.CUSTOM_RINGTONE, ringtone);
                mBatchOperation.remove(mBackReference);
                mBatchOperation.add(mBackReference, mBuilder.build());
            } else {
                Builder builder = ContentProviderOperation
                        .newUpdate(RawContacts.CONTENT_URI)
                        .withSelection(BaseColumns._ID + "=?",
                                new String[] {
                                    String.valueOf(mRawContactId)
                                })
                        .withValue(RawContacts.CUSTOM_RINGTONE, ringtone)
                        .withYieldAllowed(mYield);
                mBatchOperation.add(builder.build());
            }
        }
        return this;
    }

    private ContactOperations addEvent(String event, int type) {
        mValues.clear();
        if (!TextUtils.isEmpty(event)) {

            mValues.put(Event.DATA, event);
            mValues.put(Event.TYPE, type);

            addInsertOrUpdaeOp(Event.CONTENT_ITEM_TYPE);
        }
        return this;
    }

    private void addInsertOrUpdaeOp(String contentItemType) {
        if (mIsNewContact) {
            addInsertOp(contentItemType);
        } else {
            addUpdateOp(contentItemType);
        }

    }

    /**
     * Adds an insert operation into the batch
     */
    private void addInsertOp(String contentItemType) {
        Builder builder = ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withYieldAllowed(mYield);

        mValues.put(Data.MIMETYPE, contentItemType);
        builder.withValues(mValues);
        builder.withValueBackReference(Data.RAW_CONTACT_ID, mBackReference);
        mYield = false;
        mBatchOperation.add(builder.build());
    }

    /**
     * Adds an update operation into the batch
     */
    private void addUpdateOp(String contentItemType) {
        if (mRawContactId <= 0)
            return;
        Builder builder = ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withYieldAllowed(mYield);
        mValues.put(Data.MIMETYPE, contentItemType);
        mValues.put(Data.RAW_CONTACT_ID, mRawContactId);
        builder.withValues(mValues);
        mYield = false;
        mBatchOperation.add(builder.build());
    }
}
