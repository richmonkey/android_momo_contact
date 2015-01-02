
package cn.com.nd.momo.api.types;

/**
 * 认证信息封装类
 * 
 * @author 曾广贤 (muroqiu@sina.com)
 */
public class OAuthInfo {
    private String mUid;

    private String mFinalKey;

    private String mFinalSecret;

    private String mUserName;

    private String mAvatarName;

    private String mQueueName;

    private String mStatus;

    private String mZoneCode;

    private String mMobile;

    private int mNeedResetPassword;

    private boolean mIsBindedMobile = true;
    
    public String getUid() {
        return mUid;
    }

    public void setUid(String mUid) {
        this.mUid = mUid;
    }

    public String getFinalKey() {
        return mFinalKey;
    }

    public void setFinalKey(String mFinalKey) {
        this.mFinalKey = mFinalKey;
    }

    public String getFinalSecret() {
        return mFinalSecret;
    }

    public void setFinalSecret(String mFinalSecret) {
        this.mFinalSecret = mFinalSecret;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getQueueName() {
        return mQueueName;
    }

    public void setQueueName(String mQueueName) {
        this.mQueueName = mQueueName;
    }

    public String getAvatarName() {
        return mAvatarName;
    }

    public void setAvatarName(String mAvatarName) {
        this.mAvatarName = mAvatarName;
    }

    public String getZoneCode() {
        return mZoneCode;
    }

    public void setZoneCode(String mZoneCode) {
        this.mZoneCode = mZoneCode;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getMobile() {
        return mMobile;
    }

    public void setMobile(String mMobile) {
        this.mMobile = mMobile;
    }

    public int getNeedResetPassword() {
        return mNeedResetPassword;
    }

    public void setNeedResetPassword(int mNeedResetPassword) {
        this.mNeedResetPassword = mNeedResetPassword;
    }

    /**
     * @return the mIsBindedMobile
     */
    public boolean isBindedMobile() {
        return mIsBindedMobile;
    }

    /**
     * @param mIsBindedMobile the mIsBindedMobile to set
     */
    public void setBindedMobile(boolean mIsBindedMobile) {
        this.mIsBindedMobile = mIsBindedMobile;
    }

}
