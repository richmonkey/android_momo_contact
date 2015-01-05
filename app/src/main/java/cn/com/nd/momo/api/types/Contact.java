
package cn.com.nd.momo.api.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.nd.momo.api.sync.MoMoContactsManager;
import cn.com.nd.momo.api.util.Log;
import cn.com.nd.momo.api.util.Utils;

public class Contact implements Comparable<Contact>, MomoType {
    public final static long EPOCH_DIFF = 11644473600000L;

    public final static String TAG = "ContactTAG";

    private final static String[][] EMPATY_ARRAY = new String[0][];

    public static enum UserStatus {
        VISITOR, UNACTIVATED, HASHOMEPAGE, MOMO, ACTIVATED
    }

    public static enum UserLink {
        STRANGER, HAS_MY_PHONENUMBER, AUTHORIZE_TO_ME, MYSELF
    }

    public static enum Reviewed {
        NOT_REVIEWED, UNDER_REVIEWED, REVIEWED
    }

    public static enum ListField {
        PhoneNumber, EMAIL, WEBSITE, RELATIONS, ADDRESS, EVENTS, IM
    };

    public static final int ROLE_APP = 2;

    private long rowId;

    private long contactId;

    private long phoneCid;

    private long uid;

    private String firstName = "";

    private String lastName = "";

    private String organization = "";

    private String department = "";

    private String note = "";

    private String birthday = "";

    private String jobTitle = "";

    private String nickName = "";

    private long modifyDate;

    private String contactCRC = "";

    private String formatName = "";

    private String[][] namePinyin = EMPATY_ARRAY;

    private boolean isFavoried = false;

    private boolean isFriend = false;

    private GroupInfo groupInfo = null;

    private List<Long> categoryIdList = new ArrayList<Long>();

    private List<Address> addressList = new ArrayList<Address>();

    private Avatar avatar;

    private int weight;// 权重，用于搜索結果

    private boolean needDownloadAvatar = false;

    private boolean isSavedToLocal;

    private int userStatus = 0;

    private int userLink = 0;

    private String name = "";

    private int gender;

    private String residence = "";

    private String lunarBirthDay = "";

    private boolean isNeedLunarBirthDay;

    private String animalSign = "";

    private String zodiac = "";

    private int healthStatus = 1;

    private int role = 0;

    private List<String> phoneList = new ArrayList<String>();

    private List<String> phoneLabelList = new ArrayList<String>();

    private List<Boolean> prefPhoneList = new ArrayList<Boolean>();

    private List<String> emailList = new ArrayList<String>();

    private List<String> emailLabelList = new ArrayList<String>();

    private List<String> websiteList = new ArrayList<String>();

    private List<String> websiteLabelList = new ArrayList<String>();

    private List<String> relationList = new ArrayList<String>();

    private List<String> relationLabelList = new ArrayList<String>();

    private List<String> eventList = new ArrayList<String>();

    private List<String> eventLabelList = new ArrayList<String>();

    private List<String> imList = new ArrayList<String>();

    private List<String> imProtocolList = new ArrayList<String>();

    private List<String> imLabelList = new ArrayList<String>();

    public final static String ADDR_SPLIT_STRING = "⊙";

    private boolean isToDelete = false;

    /**
     * 用户信息填写的完善程度
     */
    private int completed = -1;

    private int sendCardCount = 0;

    private int reviewed = -1;

    private boolean inMyContacts = true;

    private boolean isHideYear;

    private String primePhoneNumber = "";

    private String customRingtone = "";

    public static long DEFAULT_USER_ID_INVALID = -1;

    public static long DEFAULT_USER_ID_NOT_EXIST = 0;

    private String mainPhone = "";

    public Contact() {

    }

    public Contact(long contactId, long phoneCid, long modifyDate) {
        this.contactId = contactId;
        this.phoneCid = phoneCid;
        this.modifyDate = modifyDate;
    }

    public Contact(long rowId, long contactId, long phoneCid, long uid,
            String firstName, String lastName, String organization,
            String department, String note, String birthday, String jobTitle,
            String nickName, long modifyDate, String formatName,
            boolean isFavoried) {
        super();
        this.rowId = rowId;
        this.contactId = contactId;
        this.phoneCid = phoneCid;
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.organization = organization;
        this.department = department;
        this.note = note;
        this.birthday = birthday;
        this.jobTitle = jobTitle;
        this.nickName = nickName;
        this.modifyDate = modifyDate;
        this.formatName = formatName;
        this.isFavoried = isFavoried;
    }

    public Contact(long rowId, long contactId, long phoneCid, long uid,
            String firstName, String lastName, String organization,
            String department, String note, String birthday, String jobTitle,
            String nickName, long modifyDate, String formatName,
            boolean isFavoried, String name, int userStatus, int userLink,
            int gender, String residence, String lunarBirthday,
            boolean isLunar, String animalSign, String zodiac, int healthStatus) {
        this(rowId, contactId, phoneCid, uid, firstName, lastName,
                organization, department, note, birthday, jobTitle, nickName,
                modifyDate, formatName, isFavoried);
        if (null != name)
            this.name = name;
        this.userStatus = userStatus;
        this.userLink = userLink;
        this.gender = gender;
        if (null != residence)
            this.residence = residence;
        if (null != lunarBirthday)
            this.lunarBirthDay = lunarBirthday;
        this.isNeedLunarBirthDay = isLunar;
        if (null != animalSign)
            this.animalSign = animalSign;
        if (null != zodiac)
            this.zodiac = zodiac;
        this.healthStatus = healthStatus;
    }

    public Contact(long rowId, long contactId, long phoneCid, long uid,
            String firstName, String lastName, String organization,
            String department, String note, String birthday, String jobTitle,
            String nickName, long modifyDate, String contactCRC,
            String formatName, boolean isFavoried, List<Long> categoryIdList,
            List<GroupInfo> groupList, List<Address> addressList,
            Avatar avator, List<String> phoneList, List<String> phoneLabelList,
            List<String> emailList, List<String> emailLabelList,
            List<String> websiteList, List<String> websiteLabelList,
            List<String> relationList, List<String> relationLabelList,
            List<String> eventList, List<String> eventLabelList,
            List<String> imList, List<String> imProtocolList,
            List<String> imLabelList) {
        this(rowId, contactId, phoneCid, uid, firstName, lastName,
                organization, department, note, birthday, jobTitle, nickName,
                modifyDate, formatName, isFavoried);
        this.contactCRC = contactCRC;
        this.addressList = addressList;
        this.avatar = avator;
        this.phoneList = phoneList;
        this.phoneLabelList = phoneLabelList;
        this.emailList = emailList;
        this.emailLabelList = emailLabelList;
        this.websiteList = websiteList;
        this.websiteLabelList = websiteLabelList;
        this.relationList = relationList;
        this.relationLabelList = relationLabelList;
        this.eventList = eventList;
        this.eventLabelList = eventLabelList;
        this.imList = imList;
        this.imProtocolList = imProtocolList;
        this.imLabelList = imLabelList;
        this.categoryIdList = categoryIdList;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public long getPhoneCid() {
        return phoneCid;
    }

    public void setPhoneCid(long phoneCid) {
        this.phoneCid = phoneCid;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(long modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getContactCRC() {
        return contactCRC;
    }

    public void setContactCRC(String contactCRC) {
        this.contactCRC = contactCRC;
    }

    public String getFormatName() {
        return formatName;
    }

    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }

    public boolean isFavoried() {
        return isFavoried;
    }

    public void setFavoried(boolean isFavoried) {
        this.isFavoried = isFavoried;
    }

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public List<String> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(List<String> phoneList) {
        this.phoneList = phoneList;
    }

    public List<String> getPhoneLabelList() {
        return phoneLabelList;
    }

    public void setPhoneLabelList(List<String> phoneLabelList) {
        this.phoneLabelList = phoneLabelList;
    }

    public List<String> getEmailList() {
        return emailList;
    }

    public void setEmailList(List<String> emailList) {
        this.emailList = emailList;
    }

    public List<String> getEmailLabelList() {
        return emailLabelList;
    }

    public void setEmailLabelList(List<String> emailLabelList) {
        this.emailLabelList = emailLabelList;
    }

    public List<String> getWebsiteList() {
        return websiteList;
    }

    public void setWebsiteList(List<String> websiteList) {
        this.websiteList = websiteList;
    }

    public List<String> getWebsiteLabelList() {
        return websiteLabelList;
    }

    public void setWebsiteLabelList(List<String> websiteLabelList) {
        this.websiteLabelList = websiteLabelList;
    }

    public List<String> getRelationList() {
        return relationList;
    }

    public void setRelationList(List<String> relationList) {
        this.relationList = relationList;
    }

    public List<String> getRelationLabelList() {
        return relationLabelList;
    }

    public void setRelationLabelList(List<String> relationLabelList) {
        this.relationLabelList = relationLabelList;
    }

    public List<String> getEventList() {
        return eventList;
    }

    public void setEventList(List<String> eventList) {
        this.eventList = eventList;
    }

    public List<String> getEventLabelList() {
        return eventLabelList;
    }

    public void setEventLabelList(List<String> eventLabelList) {
        this.eventLabelList = eventLabelList;
    }

    public List<String> getImList() {
        return imList;
    }

    public void setImList(List<String> imList) {
        this.imList = imList;
    }

    public List<String> getImProtocolList() {
        return imProtocolList;
    }

    public void setImProtocolList(List<String> imProtocolList) {
        this.imProtocolList = imProtocolList;
    }

    public List<String> getImLabelList() {
        return imLabelList;
    }

    public void setImLabelList(List<String> imLabelList) {
        this.imLabelList = imLabelList;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public String[][] getNamePinyin() {
        return namePinyin;
    }

    public void setNamePinyin(String[][] namePinyin) {
        this.namePinyin = namePinyin;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isNeedDownloadAvatar() {
        return needDownloadAvatar;
    }

    public void setNeedDownloadAvatar(boolean needDownloadAvatar) {
        this.needDownloadAvatar = needDownloadAvatar;
    }

    public boolean isSavedToLocal() {
        return isSavedToLocal;
    }

    public void setSavedToLocal(boolean isSavedToLocal) {
        this.isSavedToLocal = isSavedToLocal;
    }

    public List<Long> getCategoryIdList() {
        return categoryIdList;
    }

    public void setCategoryIdList(List<Long> categoryIdList) {
        this.categoryIdList = categoryIdList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getLunarBirthDay() {
        return lunarBirthDay;
    }

    public void setLunarBirthDay(String lunarBirthDay) {
        this.lunarBirthDay = lunarBirthDay;
    }

    public boolean isNeedLunarBirthDay() {
        return isNeedLunarBirthDay;
    }

    public void setNeedLunarBirthDay(boolean isNeedLunarBirthDay) {
        this.isNeedLunarBirthDay = isNeedLunarBirthDay;
    }

    public String getAnimalSign() {
        return animalSign;
    }

    public void setAnimalSign(String animalSign) {
        this.animalSign = animalSign;
    }

    public String getZodiac() {
        return zodiac;
    }

    public void setZodiac(String zodiac) {
        this.zodiac = zodiac;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public int getUserLink() {
        return userLink;
    }

    public void setUserLink(int userLink) {
        this.userLink = userLink;
    }

    public int getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(int healthStatus) {
        this.healthStatus = healthStatus;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public boolean isToDelete() {
        return isToDelete;
    }

    public void setToDelete(boolean isToDelete) {
        this.isToDelete = isToDelete;
    }

    public String getPrimePhoneNumber() {
        if (primePhoneNumber.length() > 0)
            return primePhoneNumber;
        for (int i = 0; i < prefPhoneList.size(); i++) {
            boolean pref = prefPhoneList.get(i);
            if (pref) {
                int size = phoneList.size();
                if (i < size) {
                    String phoneNumber = phoneList.get(i);
                    return phoneNumber;
                }
            }
        }
        return "";
    }

    public boolean hasSuchPrimePhoneNumber(String phoneNumber) {
        if (null == phoneNumber || phoneNumber.length() < 1)
            return false;
        for (String eachPhoneNumber : phoneList) {
            if (eachPhoneNumber.equals(phoneNumber))
                return true;
        }
        return false;
    }

    /**
     * 将多个属性值转换成一个JSON串
     * 
     * @param field
     * @return
     */
    public JSONArray convertFieldListToJsonArray(ListField field) {
        List<String> typeList = null;
        List<String> valueList = null;
        List<String> protocalList = null;
        List<Boolean> prefList = null;
        JSONArray jsonArray = new JSONArray();
        switch (field) {
            case PhoneNumber:
                typeList = this.getPhoneLabelList();
                valueList = this.getPhoneList();
                prefList = this.getPrefPhoneList();
                break;
            case EMAIL:
                typeList = this.getEmailLabelList();
                valueList = this.getEmailList();
                break;
            case EVENTS:
                typeList = this.getEventLabelList();
                valueList = this.getEventList();
                break;
            case IM:
                typeList = this.getImLabelList();
                valueList = this.getImList();
                protocalList = this.getImProtocolList();
                break;
            case WEBSITE:
                typeList = this.getWebsiteLabelList();
                valueList = this.getWebsiteList();
                break;
            case RELATIONS:
                typeList = this.getRelationLabelList();
                valueList = this.getRelationList();
                break;
            default:
                break;
        }

        if (field == ListField.ADDRESS) {
            List<Address> addressList = this.getAddressList();
            if (null != addressList) {
                int count = addressList.size();
                for (int i = 0; i < count; i++) {
                    Address address = addressList.get(i);
                    String country = address.getCountry();
                    String region = address.getState();
                    String city = address.getCity();
                    String street = address.getStreet();
                    String postal = address.getPostalCode();
                    String type = address.getLabel();
                    JSONObject object = new JSONObject();
                    try {
                        object.put("type", type);
                        object.put("country", country);
                        object.put("region", region);
                        object.put("city", city);
                        object.put("street", street);
                        object.put("postal", postal);
                        jsonArray.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if (null != typeList && null != valueList) {
                int count = valueList.size();
                for (int i = 0; i < count; i++) {
                    String type = typeList.get(i);
                    String value = valueList.get(i);
                    JSONObject obj = new JSONObject();
                    if (field == ListField.IM) {
                        if (null != protocalList && i < protocalList.size()) {
                            String protocal = protocalList.get(i);
                            try {
                                obj.put("protocol", protocal);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (field == ListField.PhoneNumber) {
                        if (null != prefList && i < prefList.size()) {
                            boolean pref = prefList.get(i);
                            try {
                                obj.put("pref", pref);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        obj.put("type", type);
                        obj.put("value", value);
                        jsonArray.put(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return jsonArray;
    }

    public List<List<String>> convertJsonArrayToFieldList(JSONArray jsonArray) {
        if (null == jsonArray)
            return null;
        List<String> labelList = new ArrayList<String>();
        List<String> valueList = new ArrayList<String>();
        List<String> protocalList = new ArrayList<String>();
        List<String> prefPhoneList = new ArrayList<String>();
        for (int j = 0; j < jsonArray.length(); j++) {
            try {
                JSONObject obj = jsonArray.getJSONObject(j);
                String type = obj.optString("type");
                String value = obj.optString("value");
                labelList.add(type);
                valueList.add(value);
                if (obj.has("protocol")) {
                    String protocal = obj.getString("protocol");
                    protocalList.add(protocal);
                } else if (obj.has("pref")) {
                    boolean pref = obj.getBoolean("pref");
                    prefPhoneList.add(pref ? "1" : "0");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        List<List<String>> result = new ArrayList<List<String>>();
        result.add(labelList);
        result.add(valueList);
        if (protocalList.size() > 0)
            result.add(protocalList);
        if (prefPhoneList.size() > 0)
            result.add(prefPhoneList);
        return result;
    }

    public List<Address> convertJsonArrayToAddressList(JSONArray jsonArray) {
        if (null == jsonArray)
            return null;
        List<Address> addressList = new ArrayList<Address>();
        for (int j = 0; j < jsonArray.length(); j++) {
            try {
                JSONObject obj = jsonArray.getJSONObject(j);
                String label = obj.getString("type");
                String country = obj.getString("country");
                String region = obj.getString("region");
                String city = obj.getString("city");
                String street = obj.getString("street");
                String postal = obj.getString("postal");
                Address address = new Address(0, label, street, city, region,
                        postal, country);
                addressList.add(address);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return addressList;
    }

    /**
     * @return
     */
    public long generateCRC() {
        // 获取要计算crc值的字段，组成String
        String srcCRC = getContactProperStrings(true);
        byte[] normalProperByte = srcCRC.getBytes();
        // 使用java的包计算crc值
        java.util.zip.CRC32 x = new java.util.zip.CRC32();
        int normalProperLen = normalProperByte.length;
        byte[] avatarByte = generateAvatarCRC();
        int avatarLen = avatarByte.length;
        byte[] together = new byte[normalProperLen + avatarLen];
        System.arraycopy(normalProperByte, 0, together, 0, normalProperLen);
        System.arraycopy(avatarByte, 0, together, normalProperLen, avatarLen);
        x.update(together);
        long crc = x.getValue();
        return crc;
    }

    /**
     * @return
     */
    public long generateProperCRC() {
        // 获取要计算crc值的字段，组成String
        String srcCRC = getContactProperStrings(false);
        byte[] normalProperByte = srcCRC.getBytes();
        // 使用java的包计算crc值
        java.util.zip.CRC32 x = new java.util.zip.CRC32();
        int normalProperLen = normalProperByte.length;
        byte[] together = new byte[normalProperLen];
        System.arraycopy(normalProperByte, 0, together, 0, normalProperLen);
        x.update(together);
        long crc = x.getValue();
        return crc;
    }

    public byte[] generateAvatarCRC() {
        byte[] avatarImage = null;
        if (avatar != null && avatar.getMomoAvatarImage() != null
                && avatar.getMomoAvatarImage().length > 128) {
            avatarImage = avatar.getMomoAvatarImage();
        }
        if (null == avatarImage || 128 > avatarImage.length)
            return new byte[0];

        byte[] crcImage = new byte[128];
        for (int i = 0; i < 128; i++) {
            crcImage[i] = avatarImage[i];
        }
        return crcImage;
    }



    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public int getSendCardCount() {
        return sendCardCount;
    }

    public void setSendCardCount(int sendCardCount) {
        this.sendCardCount = sendCardCount;
    }

    public int getReviewed() {
        return reviewed;
    }

    public void setReviewed(int reviewed) {
        this.reviewed = reviewed;
    }

    public boolean isInMyContacts() {
        return inMyContacts;
    }

    public void setInMyContacts(boolean inMyContacts) {
        this.inMyContacts = inMyContacts;
    }

    public boolean isHideYear() {
        return isHideYear;
    }

    public void setIsHideYear(boolean isHideYear) {
        this.isHideYear = isHideYear;
    }

    public List<Boolean> getPrefPhoneList() {
        return prefPhoneList;
    }

    public void setPrefPhoneList(List<Boolean> prefPhoneList) {
        this.prefPhoneList = prefPhoneList;
    }

    public String getCustomRingtone() {
        return customRingtone;
    }

    public void setCustomRingtone(String customRingtone) {
        this.customRingtone = customRingtone;
    }

    public void setPrimePhoneNumber(String primePhoneNumber) {
        this.primePhoneNumber = primePhoneNumber;
    }

    /**
     * @return
     */
    public String getContactProperStrings(boolean withSpecial) {
        StringBuilder sb = new StringBuilder(100);
        if (null != firstName)
            sb.append(firstName);
        if (null != lastName)
            sb.append(lastName);
        if (null != organization)
            sb.append(organization);
        if (null != jobTitle)
            sb.append(jobTitle);
        if (null != note)
            sb.append(note);
        if (null != birthday)
            sb.append(birthday);
        if (null != nickName)
            sb.append(nickName);
        if (null != department)
            sb.append(department);
        if (withSpecial) {
            sb.append(isFavoried ? 1 : 0);
            String categoryIds = convertCategoryIdListToString();
            if (categoryIds.length() > 0)
                sb.append(categoryIds);
        }
        sortAllList();
        for (Address address : addressList) {
            sb.append(address.toString());
        }
        sb.append(sortListAndGenerateStrings(emailList, emailLabelList));
        sb.append(sortListAndGenerateStrings(phoneList, phoneLabelList));
        sb.append(sortListAndGenerateStrings(websiteList, websiteLabelList));
        sb.append(sortListAndGenerateStrings(relationList, relationLabelList));
        sb.append(sortListAndGenerateStrings(eventList, eventLabelList));
        sb.append(sortListAndGenerateStrings(imList, imLabelList,
                imProtocolList));
        return sb.toString().toLowerCase();
    }

    /**
     * 结构化原始地址数据
     * 
     * @param addrVal 原始的地址字符串
     * @param addr 结构化后的数据
     */
    public static void addressFromString(final String addrVal, Address addr) {
        if (null == addrVal || addrVal.length() == 0)
            return;
        String[] strSplit = addrVal.split(ADDR_SPLIT_STRING, -1);

        assert strSplit.length >= 7;
        addr.setStreet(strSplit[2]);
        addr.setCity(strSplit[3]);
        addr.setState(strSplit[4]);
        addr.setPostalCode((strSplit[5]));
        addr.setCountry(strSplit[6]);
    }

    public String convertCategoryIdListToString() {
        StringBuilder sb = new StringBuilder();
        for (Long categoryId : categoryIdList) {
            sb.append(categoryId).append(',');
        }
        int len = sb.length();
        if (len > 0)
            sb.deleteCharAt(len - 1);
        return sb.toString();
    }

    public void convertCategoryIdStringToList(String categoryIds) {
        if (Utils.isEmpty(categoryIds))
            return;
        String[] ids = Utils.split(categoryIds, ',');
        for (String id : ids) {
            categoryIdList.add(Long.valueOf(id));
        }
    }

    /**
     * 用于地址的标注化输出
     * 
     * @param str 数据库里原始的带⊙的字符串
     * @return 用于输出现实的字符串
     */
    public static String getPrintAddrString(String str) {
        if (str == null || str.length() == 0)
            return "";
        Address addr = new Address();
        addressFromString(str, addr);

        return getPrintAddrString(addr);
    }

    /**
     * 将结构化的数据组装能存入数据库的字符串
     * 
     * @param addr
     * @return
     */
    public static String address2String(Address addr) {
        assert null != addr;
        String result = ADDR_SPLIT_STRING + ADDR_SPLIT_STRING
                + (addr.getStreet() == null ? "" : addr.getStreet())
                + ADDR_SPLIT_STRING
                + (addr.getCity() == null ? "" : addr.getCity())
                + ADDR_SPLIT_STRING
                + (addr.getState() == null ? "" : addr.getState())
                + ADDR_SPLIT_STRING
                + (addr.getPostalCode() == null ? "" : addr.getPostalCode())
                + ADDR_SPLIT_STRING
                + (addr.getCountry() == null ? "" : addr.getCountry());

        return result;
    }

    /**
     * 用于地址的标注化输出
     * 
     * @param str 数据库里原始的带⊙的字符串
     * @return 用于输出现实的字符串
     */
    public static String getPrintAddrString(Address addr) {
        if (addr == null)
            return null;

        return addr.getCountry() + " " + addr.getState() + addr.getCity()
                + addr.getStreet() + '\n' + addr.getPostalCode();
    }

    public void sortAllList() {
        Collections.sort(addressList, AddressComparator.getInstance());
    }

    private String sortListAndGenerateStrings(List<String> valueList,
            List<String> labelList) {
        if (null == valueList || null == labelList
                || valueList.size() != labelList.size())
            return "";
        SortedSet<String> sortedSet = new TreeSet<String>();
        for (int i = 0; i < valueList.size(); i++) {
            String value = valueList.get(i);
            if (value.length() < 1)
                continue;
            String label = labelList.get(i);
            sortedSet.add(value + label);
        }
        if (sortedSet.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String eachStr : sortedSet) {
                sb.append(eachStr);
            }
            return sb.toString();
        }
        return "";
    }

    private String sortListAndGenerateStrings(List<String> valueList,
            List<String> labelList, List<String> protocalList) {
        if (null == valueList || null == labelList || null == protocalList)
            return "";
        int valueSize = valueList.size();
        int labelSize = labelList.size();
        int protocalSize = protocalList.size();
        if (valueSize != labelSize || valueSize != protocalSize)
            return "";
        SortedSet<String> sortedSet = new TreeSet<String>();
        for (int i = 0; i < valueList.size(); i++) {
            String value = valueList.get(i);
            if (value.length() < 1)
                continue;
            String label = labelList.get(i);
            String protocal = protocalList.get(i);
            sortedSet.add(value + label + protocal);
        }
        if (sortedSet.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String eachStr : sortedSet) {
                sb.append(eachStr);
            }
            return sb.toString();
        }
        return "";
    }

    public static class AddressComparator implements Comparator<Address> {
        private static AddressComparator instance = null;

        private AddressComparator() {
        }

        public static AddressComparator getInstance() {
            if (null == instance) {
                instance = new AddressComparator();
            }
            return instance;
        }

        @Override
        public int compare(Address address1, Address address2) {
            String address1Str = address1.toString();
            String address2Str = address2.toString();
            return address1Str.compareTo(address2Str);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        if (null == contactCRC || contactCRC.length() < 1) {
            Log.i(TAG, "contact crc empty");
            MoMoContactsManager manager = MoMoContactsManager.getInstance();
            Contact fullContact = manager.getContactById(contactId);
            contactCRC = String.valueOf(fullContact.generateCRC());
            List<Contact> contactList = new ArrayList<Contact>();
            contactList.add(this);
            MoMoContactsManager.getInstance().batchUpdateContactsCRC(
                    contactList);
        }
        Contact other = (Contact)obj;
        if (null == contactCRC || contactCRC.length() < 1)
            return false;
        long crc = Long.valueOf(contactCRC);
        long otherCrc = other.generateCRC();
        if (crc != otherCrc) {
            Log.d(TAG, "momo crc:" + crc);
            Log.d(TAG, "loca crc:" + otherCrc);
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Contact another) {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getMainPhone() {
        return mainPhone;
    }

    public void setMainPhone(String mainPhone) {
        this.mainPhone = mainPhone;
    }
}
