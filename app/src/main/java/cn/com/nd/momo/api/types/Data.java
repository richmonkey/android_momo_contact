
package cn.com.nd.momo.api.types;

import java.util.HashMap;
import java.util.Map;

import android.provider.ContactsContract;

@SuppressWarnings("serial")
public class Data implements Cloneable {

    private long rowId;

    private long phoneCid; // 联系人id

    private long dataId; // 字段在andorid联系人表中的ID,
                         // 可能是phone的，也可能是其他的，会重复（此字段不同步，也不写入momo数据库 ）

    private int property; // 属性值：1电话、2邮箱、3网址、4人员、5地址、6纪念日、7即时通讯

    private long identifier; // 是否是第一个（目前所知主要用于主要显示，android的字段叫做isPrimary）

    private int poomRelate; // 是否写入手机本身通讯录（本字段不同步）

    private String label; // 联系方式的标签

    private String value; // 联系方式的值

    private int visitTimes; // 拨打次数（本字段不同步）

    public Data() {
        rowId = -1;
        phoneCid = -1;
        setDataId(-1);
        property = -1;
        identifier = -1;
        poomRelate = -1;
        label = null;
        value = null;
        visitTimes = -1;

    }

    public Data(int property, String label, String value) {
        this.property = property;
        this.label = label;
        this.value = value;
    }

    // 定义的数据值代表的意义
    /**
     * 电话
     */
    public final static int PHONE = 1;

    /**
     * email
     */
    public final static int EMAIL = 2;

    /**
     * 网址
     */
    public final static int INTERNET = 3;

    /**
     * 人员，android1.5手机通讯录无此字段
     */
    public final static int RELATE = 4;

    /**
     * 通讯地址
     */
    public final static int ADDR = 5;

    /**
     * 纪念日，android1.5手机通讯录无此字段
     */
    public final static int DAY = 6;

    /*
     * IM type
     */
    public final static int IM = 7;

    /**
     * IM
     */
    public final static int RTIME_91U = 71;

    public final static int RTIME_QQ = 72;

    public final static int RTIME_ICQ = 73;

    public final static int RTIME_GTALK = 74;

    public final static int RTIME_YAHOO = 75;

    public final static int RTIME_SKYPE = 76;

    public final static int RTIME_MSN = 77;

    public final static int RTIME_AIM = 78;

    public final static int RTIME_JABBER = 79;

    // /**
    // * 部门，职位，以下字段不放入data数据表里
    // */
    // public final static int ORG = -1;
    //
    // public final static int TITLE = -2;
    //
    // public final static int NOTE = -3;

    public final static String TYPE_HOME = "home"; // 家庭电话、家庭住址

    public final static String TYPE_WORK = "work"; // 工作电话、工作地址

    public final static String TYPE_CELL = "cell"; // 手机

    public final static String TYPE_HOME_FAX = "home,fax"; // 家庭传真

    public final static String TYPE_WORK_FAX = "work,fax"; // 工作传真

    public final static String TYPE_PAGER = "pager"; // 寻呼机

    public final static String TYPE_CAR = "car"; // 车载电话

    public final static String TYPE_OTHER = "other"; // 其他电话类型

    public final static String TYPE_FAX_SUFFIX = "fax";

    public final static String TYPE_EMAIL_SUFFIX = "internet";

    public final static String TYPE_HOME_INTERNET = "internet,home";

    public final static String TYPE_WORK_INTERNET = "internet,work";

    public final static String TYPE_OTHER_INTERNET = "internet";

    // 电话标签类型
    public final static int TYPE_PHONE_CUSTOM = 8;

    public final static int TYPE_PHONE_HOME = 1;

    public final static int TYPE_PHONE_MOBILE = 2;

    public final static int TYPE_PHONE_WORK = 3;

    public final static int TYPE_PHONE_FAX_WORK = 4;

    public final static int TYPE_PHONE_FAX_HOME = 5;

    public final static int TYPE_PHONE_PAGER = 6;

    public final static int TYPE_PHONE_OTHER = 7;

    // 地址标签类型
    public final static int TYPE_ADDR_CUSTOM = 0;

    public final static int TYPE_ADDR_HOME = 1;

    public final static int TYPE_ADDR_WORK = 2;

    public final static int TYPE_ADDR_OTHER = 3;

    // email标签类型
    public final static int TYPE_EMAIL_CUSTOM = 0;

    public final static int TYPE_EMAIL_HOME = 1;

    public final static int TYPE_EMAIL_WORK = 2;

    public final static int TYPE_EMAIL_OTHER = 3;

    // IM协议
    public final static int PROTOCOL_91U = -1; // 自定义的91u协议,同时也是系统定义自订类型的协议号

    public final static int PROTOCOL_AIM = 0;

    public final static int PROTOCOL_MSN = 1;

    public final static int PROTOCOL_YAHOO = 2;

    public final static int PROTOCOL_SKYPE = 3;

    public final static int PROTOCOL_QQ = 4;

    public final static int PROTOCOL_GTALK = 5;

    public final static int PROTOCOL_ICQ = 6;

    public final static int PROTOCOL_JABBER = 7;

    // im 协议标签
    public final static String LABEL_IM_PROTOCOL_91U = "91u";

    public final static String LABEL_IM_PROTOCOL_AIM = "AIM";

    public final static String LABEL_IM_PROTOCOL_MSN = "MSN";

    public final static String LABEL_IM_PROTOCOL_YAHOO = "YAHOO";

    public final static String LABEL_IM_PROTOCOL_SKYPE = "SKYPE";

    public final static String LABEL_IM_PROTOCOL_QQ = "QQ";

    public final static String LABEL_IM_PROTOCOL_GTALK = "GTALK";

    public final static String LABEL_IM_PROTOCOL_ICQ = "ICQ";

    public final static String LABEL_IM_PROTOCOL_JABBER = "JABBER";

    // website类型
    public final static int TYPE_WEBSITE_HOMEPAGE = 1;

    public final static int TYPE_WEBSITE_BLOG = 2;

    public final static int TYPE_WEBSITE_PROFILE = 3;

    public final static int TYPE_WEBSITE_HOME = 4;

    public final static int TYPE_WEBSITE_WORK = 5;

    public final static int TYPE_WEBSITE_FTP = 6;

    public final static int TYPE_WEBSITE_OTHER = 7;

    public final static int TYPE_WEBSITE_SINA_WEIBO = 8;

    public final static int TYPE_WEBSITE_QQ_WEIBO = 9;

    // website标签
    public final static String LABEL_WEBSITE_HOMEPAGE = "homepage";

    public final static String LABEL_WEBSITE_HOME = "home";

    public final static String LABEL_WEBSITE_WORK = "work";

    public final static String LABEL_WEBSITE_FTP = "ftp";

    public final static String LABEL_WEBSITE_BLOG = "blog";

    public final static String LABEL_WEBSITE_PROFILE = "profile";

    public final static String LABEL_WEBSITE_OTHER = "other";

    public final static String LABEL_WEBSITE_SINA_WEIBO = "weibo.com";

    public final static String LABEL_WEBSITE_QQ_WEIBO = "t.qq.com";

    // 纪念日类型
    public final static int TYPE_ANNIVERSARY = 1;

    public final static int TYPE_ANNIVERSARY_OTHER = 2;

    // 纪念日标签
    public final static String LABEL_ANNIVERSARY = "anniversary";

    public final static String LABEL_ANNIVERSARY_OTHER = "other";

    // 关系类型
    public final static int TYPE_RELATED_ASSISTANT = 1;

    public final static int TYPE_RELATED_BROTHER = 2;

    public final static int TYPE_RELATED_CHILD = 3;

    public final static int TYPE_RELATED_DOMESTIC_PARTNER = 4;

    public final static int TYPE_RELATED_FATHER = 5;

    public final static int TYPE_RELATED_FRIEND = 6;

    public final static int TYPE_RELATED_MANAGER = 7;

    public final static int TYPE_RELATED_MOTHER = 8;

    public final static int TYPE_RELATED_PARENT = 9;

    public final static int TYPE_RELATED_PARTNER = 10;

    public final static int TYPE_RELATED_REFERRED_BY = 11;

    public final static int TYPE_RELATED_RELATIVE = 12;

    public final static int TYPE_RELATED_SISTER = 13;

    public final static int TYPE_RELATED_SPOUSE = 14;

    public final static int TYPE_RELATED_OTHER = 15; // 这个类型在2.1中没有

    // 关系类型标签
    public final static String LABEL_RELATED_SPOUSE = "spouse";

    public final static String LABEL_RELATED_CHILD = "child";

    public final static String LABEL_RELATED_FATHER = "father";

    public final static String LABEL_RELATED_MOTHER = "mother";

    public final static String LABEL_RELATED_PARENT = "parent";

    public final static String LABEL_RELATED_BROTHER = "brother";

    public final static String LABEL_RELATED_SISTER = "sister";

    public final static String LABEL_RELATED_FRIEND = "friend";

    public final static String LABEL_RELATED_RELATIVE = "relative";

    public final static String LABEL_RELATED_DOMESTIC_PARTNER = "domestic_partner";

    public final static String LABEL_RELATED_MANAGER = "manager";

    public final static String LABEL_RELATED_ASSISTANT = "assistant";

    public final static String LABEL_RELATED_PARTNER = "partner";

    public final static String LABEL_RELATED_REFERRED_BY = "referred_by";

    public final static String LABEL_RELATED_OTHER = "other";

    // im类型和标签在android版本中没用到
    // im类型
    public final static int TYPE_IM_HOME = 1;

    public final static int TYPE_IM_WORK = 2;

    public final static int TYPE_IM_OTHER = 3;

    // im标签
    public final static String LABEL_IM_HOME = "home";

    public final static String LABEL_IM_WORK = "work";

    public final static String LABEL_IM_OTHER = "other";

    public final static Map<Integer, String> PHONE_MAP = new HashMap<Integer, String>() {
        {
            put(TYPE_PHONE_HOME, TYPE_HOME);
            put(TYPE_PHONE_WORK, TYPE_WORK);
            put(TYPE_PHONE_MOBILE, TYPE_CELL);
            put(TYPE_PHONE_FAX_HOME, TYPE_HOME_FAX);
            put(TYPE_PHONE_FAX_WORK, TYPE_WORK_FAX);
            put(TYPE_PHONE_PAGER, TYPE_PAGER);
            put(TYPE_PHONE_OTHER, TYPE_OTHER);
        }
    };

    public final static Map<Integer, String> EMAIL_MAP = new HashMap<Integer, String>() {
        {
            put(ContactsContract.CommonDataKinds.Email.TYPE_HOME, TYPE_HOME_INTERNET);
            put(ContactsContract.CommonDataKinds.Email.TYPE_WORK, TYPE_WORK_INTERNET);
            put(ContactsContract.CommonDataKinds.Email.TYPE_OTHER, TYPE_OTHER_INTERNET);
        }
    };

    public final static Map<Integer, String> ADDR_MAP = new HashMap<Integer, String>() {
        {
            put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME, TYPE_HOME);
            put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK, TYPE_WORK);
            put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER, TYPE_OTHER);
        }
    };

    public final static Map<Integer, Integer> IM_PROTOCOL = new HashMap<Integer, Integer>() {
        {
            put(PROTOCOL_AIM, RTIME_AIM);
            put(PROTOCOL_GTALK, RTIME_GTALK);
            put(PROTOCOL_ICQ, RTIME_ICQ);
            put(PROTOCOL_JABBER, RTIME_JABBER);
            put(PROTOCOL_MSN, RTIME_MSN);
            put(PROTOCOL_QQ, RTIME_QQ);
            put(PROTOCOL_SKYPE, RTIME_SKYPE);
            put(PROTOCOL_YAHOO, RTIME_YAHOO);
            put(PROTOCOL_91U, RTIME_91U);
        }
    };

    public final static Map<Integer, String> IM_PROTOCOL_LABEL = new HashMap<Integer, String>() {
        {
            put(PROTOCOL_91U, LABEL_IM_PROTOCOL_91U);
            put(PROTOCOL_AIM, LABEL_IM_PROTOCOL_AIM);
            put(PROTOCOL_GTALK, LABEL_IM_PROTOCOL_GTALK);
            put(PROTOCOL_ICQ, LABEL_IM_PROTOCOL_ICQ);
            put(PROTOCOL_JABBER, LABEL_IM_PROTOCOL_JABBER);
            put(PROTOCOL_MSN, LABEL_IM_PROTOCOL_MSN);
            put(PROTOCOL_QQ, LABEL_IM_PROTOCOL_QQ);
            put(PROTOCOL_SKYPE, LABEL_IM_PROTOCOL_SKYPE);
            put(PROTOCOL_YAHOO, LABEL_IM_PROTOCOL_YAHOO);
        }
    };

    public final static Map<Integer, String> WEBSITE_MAP = new HashMap<Integer, String>() {
        {
            put(Data.TYPE_WEBSITE_HOMEPAGE, Data.LABEL_WEBSITE_HOMEPAGE);
            put(Data.TYPE_WEBSITE_HOME, Data.LABEL_WEBSITE_HOME);
            put(Data.TYPE_WEBSITE_WORK, Data.LABEL_WEBSITE_WORK);
            put(Data.TYPE_WEBSITE_BLOG, Data.LABEL_WEBSITE_BLOG);
            put(Data.TYPE_WEBSITE_FTP, Data.LABEL_WEBSITE_FTP);
            put(Data.TYPE_WEBSITE_PROFILE, Data.LABEL_WEBSITE_PROFILE);
            put(Data.TYPE_WEBSITE_OTHER, Data.LABEL_WEBSITE_OTHER);
            put(Data.TYPE_WEBSITE_SINA_WEIBO, Data.LABEL_WEBSITE_SINA_WEIBO);
            put(Data.TYPE_WEBSITE_QQ_WEIBO, Data.LABEL_WEBSITE_QQ_WEIBO);
        }
    };

    public final static Map<Integer, String> ANNIVERSARY_MAP = new HashMap<Integer, String>() {
        {
            put(Data.TYPE_ANNIVERSARY, Data.LABEL_ANNIVERSARY);
            put(Data.TYPE_ANNIVERSARY_OTHER, Data.LABEL_ANNIVERSARY_OTHER);
        }
    };

    public final static Map<Integer, String> RELATED_MAP = new HashMap<Integer, String>() {
        {

            put(Data.TYPE_RELATED_SPOUSE, Data.LABEL_RELATED_SPOUSE);
            put(Data.TYPE_RELATED_CHILD, Data.LABEL_RELATED_CHILD);
            put(Data.TYPE_RELATED_FATHER, Data.LABEL_RELATED_FATHER);
            put(Data.TYPE_RELATED_MOTHER, Data.LABEL_RELATED_MOTHER);
            put(Data.TYPE_RELATED_PARENT, Data.LABEL_RELATED_PARENT);
            put(Data.TYPE_RELATED_BROTHER, Data.LABEL_RELATED_BROTHER);
            put(Data.TYPE_RELATED_SISTER, Data.LABEL_RELATED_SISTER);
            put(Data.TYPE_RELATED_FRIEND, Data.LABEL_RELATED_FRIEND);
            put(Data.TYPE_RELATED_RELATIVE, Data.LABEL_RELATED_RELATIVE);
            put(Data.TYPE_RELATED_DOMESTIC_PARTNER, Data.LABEL_RELATED_DOMESTIC_PARTNER);
            put(Data.TYPE_RELATED_MANAGER, Data.LABEL_RELATED_MANAGER);
            put(Data.TYPE_RELATED_ASSISTANT, Data.LABEL_RELATED_ASSISTANT);
            put(Data.TYPE_RELATED_PARTNER, Data.LABEL_RELATED_PARTNER);
            put(Data.TYPE_RELATED_REFERRED_BY, Data.LABEL_RELATED_REFERRED_BY);
            put(Data.TYPE_RELATED_OTHER, Data.LABEL_RELATED_OTHER);
        }
    };

    public final static Map<Integer, String> IM_MAP = new HashMap<Integer, String>() {
        {
            put(Data.TYPE_IM_HOME, Data.LABEL_IM_HOME);
            put(Data.TYPE_IM_WORK, Data.LABEL_IM_WORK);
            put(Data.TYPE_IM_OTHER, Data.LABEL_IM_OTHER);
        }
    };

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    public long getContactId() {
        return phoneCid;
    }

    // this contactId is phone's system contactId
    public void setContactId(long contactId) {
        this.phoneCid = contactId;
    }

    public int getProperty() {
        return property;
    }

    public int getPropertyEx() {
        if (property >= RTIME_91U && property <= RTIME_JABBER)
            return IM;
        else
            return property;
    }

    public void setProperty(int property) {
        this.property = property;
    }

    public long getIdentifier() {
        return identifier;
    }

    public void setIdentifier(long identifier) {
        this.identifier = identifier;
    }

    public int getPoomRelate() {
        return poomRelate;
    }

    public void setPoomRelate(int poomRelate) {
        this.poomRelate = poomRelate;
    }

    public String getLabel() {
        if (label == null)
            return null;
        return label.toLowerCase();
    }

    public void setLabel(String label) {
        if (label == null) {
            this.label = "";
        } else {
            this.label = label;
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getVisitTimes() {
        return visitTimes;
    }

    public void setVisitTimes(int visitTimes) {
        this.visitTimes = visitTimes;
    }

    // 获取标签
    public static int getTypeByLabel(int property, String label) {
        if (label == null) {
            if (property >= RTIME_91U && property <= RTIME_JABBER) {
                return -2;
            }
            return -1;
        }
        switch (property) {
            case Data.PHONE:
                for (int i = 1, len = Data.PHONE_MAP.size(); i <= len; i++) {
                    if (label.trim().equalsIgnoreCase(Data.PHONE_MAP.get(i))) {
                        return i;
                    }
                }
                return Data.TYPE_PHONE_CUSTOM;
            case Data.EMAIL:
                for (int i = 1, len = Data.EMAIL_MAP.size(); i <= len; i++) {
                    if (label.equalsIgnoreCase(Data.EMAIL_MAP.get(i))) {
                        return i;
                    }
                }
                return Data.TYPE_EMAIL_CUSTOM;
            case Data.ADDR:
                for (int i = 1, len = Data.ADDR_MAP.size(); i <= len; i++) {
                    if (label.equalsIgnoreCase(Data.ADDR_MAP.get(i))) {
                        return i;
                    }
                }
                return Data.TYPE_ADDR_CUSTOM;
            case Data.INTERNET:
                for (int i = 1, len = Data.WEBSITE_MAP.size(); i <= len; i++) {
                    if (label.equalsIgnoreCase(Data.WEBSITE_MAP.get(i))) {
                        return i;
                    }
                }
                return -1; // website没有自定义的标签
            case Data.RELATE:
                for (int i = 1, len = Data.RELATED_MAP.size(); i <= len; i++) {
                    if (label.equalsIgnoreCase(Data.RELATED_MAP.get(i))) {
                        return i;
                    }
                }
                return -1; // relation没有自定义标签
            case Data.DAY:
                for (int i = 1, len = Data.ANNIVERSARY_MAP.size(); i <= len; i++) {
                    if (label.equalsIgnoreCase(Data.ANNIVERSARY_MAP.get(i))) {
                        return i;
                    }
                }
                return -1; // anniversary没有自定义标签
            case RTIME_91U:
            case RTIME_AIM:
            case RTIME_GTALK:
            case RTIME_ICQ:
            case RTIME_JABBER:
            case RTIME_MSN:
            case RTIME_QQ:
            case RTIME_SKYPE:
            case RTIME_YAHOO:
                for (int i = 1, len = Data.IM_MAP.size(); i <= len; i++) {
                    if (label.equalsIgnoreCase(Data.IM_MAP.get(i)))
                        return i;
                }
                return -1; // im的类型没有自定义标签

            default:
                return -1; // 这是个无效的数据类型
        }
    }

    public static String getLabelByType(int property, int type) {
        String label = "";
        if (type == 0) {
            return label;
        }
        switch (property) {
            case Data.PHONE:
                if (!PHONE_MAP.containsKey(type))
                    return label;
                label = PHONE_MAP.get(type);
                break;
            case Data.EMAIL:
                if (!EMAIL_MAP.containsKey(type))
                    return label;
                label = EMAIL_MAP.get(type);
                break;
            case Data.ADDR:
                if (!ADDR_MAP.containsKey(type))
                    return label;
                label = ADDR_MAP.get(type);
                break;
            case Data.INTERNET:
                if (!WEBSITE_MAP.containsKey(type))
                    return label;
                label = WEBSITE_MAP.get(type);
                break;
            case Data.RELATE:
                if (!RELATED_MAP.containsKey(type))
                    return label;
                label = RELATED_MAP.get(type);
                break;
            case Data.DAY:
                if (!ANNIVERSARY_MAP.containsKey(type))
                    return label;
                label = ANNIVERSARY_MAP.get(type);
                break;
            case RTIME_91U:
            case RTIME_AIM:
            case RTIME_GTALK:
            case RTIME_ICQ:
            case RTIME_JABBER:
            case RTIME_MSN:
            case RTIME_QQ:
            case RTIME_SKYPE:
            case RTIME_YAHOO:
                if (!IM_MAP.containsKey(type))
                    return label;
                label = IM_MAP.get(type);
                break;
            default:
                return label; // 这是个无效的数据类型
        }
        return label;
    }

    @Override
    public Data clone() {
        Data data = null;
        try {
            data = (Data)super.clone();
        } catch (CloneNotSupportedException e) {
            // log
        }
        return data;
    }

    // 对应1电话、2邮箱、3网址、4人员、5地址、6纪念日、7即时通讯等几个大类型的数据标签
    private static final HashMap<Integer, Integer> ghostDataMap;

    static {
        ghostDataMap = new HashMap<Integer, Integer>();
        // 属性值：1电话、2邮箱、3网址、4人员、5地址、6纪念日、7即时通讯
        for (int i = 1; i <= 6; i++) {
            ghostDataMap.put(i, i - 1);
        }

        // im类型特殊处理
        for (int i = 71; i <= 79; i++) {
            ghostDataMap.put(i, 6);
        }
    }

    public static int getPropertyLabelId(int property) {
        return ghostDataMap.get(property);
    }

    public static int getProtocolPropertyId(int protocol) {
        if (IM_PROTOCOL.containsKey(protocol)) {
            return IM_PROTOCOL.get(protocol);
        } else if (protocol == -1) {
            return RTIME_91U;
        } else {
            return 0; // other custom protocol
        }
    }

    public static String getProtocolLabel(int protocol) {
        if (IM_PROTOCOL_LABEL.containsKey(protocol)) {
            return IM_PROTOCOL_LABEL.get(protocol);
        } else {
            return ""; // other custom protocol
        }
    }

    public long getDataId() {
        return dataId;
    }

    public void setDataId(long dataId) {
        // TODO NOW DISABLED, NO SYNC
        // this.dataId = dataId;
    }
}
