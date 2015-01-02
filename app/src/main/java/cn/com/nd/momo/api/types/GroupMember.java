
package cn.com.nd.momo.api.types;

/**
 * 群成员
 * 
 * @author 曾广贤 (muroqiu@sina.com)
 */
public class GroupMember implements Comparable<GroupMember>, MomoType {

    /**
     * 成员ID
     */
    private Long id;

    /**
     * 群成员名称
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 手机区号
     */
    private String zoneCode;

    /**
     * 群成员权限(1表示普通成员,2管理员,3群主，整型)
     */
    private int grade = GRADE_NORMAL;

    /**
     * 普通成员
     */
    public static int GRADE_NORMAL = 1;

    /**
     * 管理员
     */
    public static int GRADE_MANAGER = 2;

    /**
     * 群主
     */
    public static int GRADE_MASTER = 3;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
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
    public int compareTo(GroupMember another) {
        assert this.getId() != null && another != null && another.getId() != null;
        long self = this.getId();
        long other = another.getId();
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

    /**
     * @return the grade
     */
    public int getGrade() {
        return grade;
    }

    /**
     * @param grade the grade to set
     */
    public void setGrade(int grade) {
        this.grade = grade;
    }
}
