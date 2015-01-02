
package cn.com.nd.momo.api.types;

import java.util.HashMap;

/**
 * 群组
 * 
 * @author 曾广贤 (muroqiu@sina.com)
 */
public class GroupInfo implements Comparable<GroupInfo>, MomoType {
    /**
     * 群ID
     */
    private int mGroupID = 0;

    /**
     * 群名称
     */
    private String mGroupName = null;

    /**
     * 群公告
     */
    private String mNotice = null;

    /**
     * 群介绍
     */
    private String mIntroduction = null;

    /**
     * 群类型
     */
    private int mType = GROUP_TYPE_PUBLIC;

    /**
     * 公开群
     */
    public static int GROUP_TYPE_PUBLIC = 1;

    /**
     * 私密群
     */
    public static int GROUP_TYPE_PRIVATE = 2;

    /**
     * 创建时间戳
     */
    private long mCreateTime = 0;

    /**
     * 修改时间戳
     */
    private long mModifyTime = 0;

    /**
     * 创建者ID
     */
    private long mCreatorID = 0;

    /**
     * 创建者名称
     */
    private String creatorName = null;

    /**
     * 群主ID
     */
    private long mMasterID = 0;

    /**
     * 群主名称
     */
    private String masterName = null;

    /**
     * 群管理员列表
     */
    private HashMap<Long, String> managerMap = new HashMap<Long, String>();

    /**
     * 成员数
     */
    private int mMemberCount = 0;

    /**
     * 群是否隐藏
     */
    private boolean isHide = false;

    private byte[] image;

    private String imageUrl;

    /**
     * @return the mGroupID
     */
    public int getGroupID() {
        return mGroupID;
    }

    /**
     * @param mGroupID the mGroupID to set
     */
    public void setGroupID(int mGroupID) {
        this.mGroupID = mGroupID;
    }

    /**
     * @return the mGroupName
     */
    public String getGroupName() {
        return mGroupName;
    }

    /**
     * @param mGroupName the mGroupName to set
     */
    public void setGroupName(String mGroupName) {
        this.mGroupName = mGroupName;
    }

    /**
     * @return the mMemberNum
     */
    public int getMemberCount() {
        return mMemberCount;
    }

    /**
     * @param mMemberNum the mMemberNum to set
     */
    public void setMemberCount(int count) {
        this.mMemberCount = count;
    }

    /**
     * @return the mNotice
     */
    public String getNotice() {
        return mNotice;
    }

    /**
     * @param mNotice the mNotice to set
     */
    public void setNotice(String mNotice) {
        this.mNotice = mNotice;
    }

    /**
     * @return the mIntroduction
     */
    public String getIntroduction() {
        return mIntroduction;
    }

    /**
     * @param mIntroduction the mIntroduction to set
     */
    public void setIntroduction(String mIntroduction) {
        this.mIntroduction = mIntroduction;
    }

    /**
     * @return the mCreateTime
     */
    public long getCreateTime() {
        return mCreateTime;
    }

    /**
     * @param mCreateTime the mCreateTime to set
     */
    public void setCreateTime(long mCreateTime) {
        this.mCreateTime = mCreateTime;
    }

    /**
     * @return the mModifyTime
     */
    public long getModifyTime() {
        return mModifyTime;
    }

    /**
     * @param mModifyTime the mModifyTime to set
     */
    public void setModifyTime(long mModifyTime) {
        this.mModifyTime = mModifyTime;
    }

    /**
     * @return the mCreatorID
     */
    public long getCreatorID() {
        return mCreatorID;
    }

    /**
     * @param mCreatorID the mCreatorID to set
     */
    public void setCreatorID(long mCreatorID) {
        this.mCreatorID = mCreatorID;
    }

    /**
     * @return the creatorName
     */
    public String getCreatorName() {
        return creatorName;
    }

    /**
     * @param creatorName the creatorName to set
     */
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    /**
     * @return the mMasterID
     */
    public long getMasterID() {
        return mMasterID;
    }

    /**
     * @param mMasterID the mMasterID to set
     */
    public void setMasterID(long mMasterID) {
        this.mMasterID = mMasterID;
    }

    /**
     * @return the masterName
     */
    public String getMasterName() {
        return masterName;
    }

    /**
     * @param masterName the masterName to set
     */
    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    /**
     * @return the managerid
     */
    public HashMap<Long, String> getManagerMap() {
        return managerMap;
    }

    /**
     * @param image the image to set
     */
    public void setManagerMap(HashMap<Long, String> managerMap) {
        this.managerMap = managerMap;
    }

    /**
     * @return the image
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(byte[] image) {
        this.image = image;
    }

    /**
     * @return the imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * @param imageUrl the imageUrl to set
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setIsHide(boolean isHide) {
        this.isHide = isHide;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    @Override
    public int compareTo(GroupInfo another) {
        // TODO Auto-generated method stub
        return 0;
    }

}
