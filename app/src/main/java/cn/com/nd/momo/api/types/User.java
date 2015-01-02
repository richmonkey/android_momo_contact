
package cn.com.nd.momo.api.types;

/**
 * 用户类，带比较功能
 * 
 * @date Oct 6, 2011
 * @author Tsung Wu <tsung.bz@gmail.com>
 */
public class User implements Comparable<User>, MomoType {
    public static final int SECRETARY_ID = 353;

    public static final String SECRETARY_NAME = "小秘";

    private String id;

    private String name;

    private String avatar;

    private String mobile;

    private String zoneCode;

    public boolean isSecretary() {
        return isSecretary(this.getId());
    }

    public static boolean isSecretary(String oid) {
        return String.valueOf(SECRETARY_ID).equals(oid);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    @Override
    public int compareTo(User another) {
        assert this.getId() != null && another != null && another.getId() != null;
        long self = Long.parseLong(this.getId());
        long other = Long.parseLong(another.getId());
        if (self == other)
            return 0;
        else if (self > other)
            return 1;
        else
            return -1;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public static boolean wrongUid(String uid) {
        return (uid == null || inValidUid(uid) || unCachedUid(uid));
    }

    public static boolean inValidUid(String uid) {
        return (uid != null && uid.equals(String
                .valueOf(cn.com.nd.momo.api.types.Contact.DEFAULT_USER_ID_INVALID)));
    }

    public static boolean unCachedUid(String uid) {
        return (uid == null || uid.equals(String
                .valueOf(cn.com.nd.momo.api.types.Contact.DEFAULT_USER_ID_NOT_EXIST)));
    }
}
