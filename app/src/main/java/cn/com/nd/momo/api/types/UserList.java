
package cn.com.nd.momo.api.types;

import java.util.Collections;

import android.text.TextUtils;

/**
 * 辅助类，非MomoType，只是为了方便缓存id以及对应的一些功能函数封装 请一定不要改为继承MomoType
 * 
 * @date Oct 5, 2011
 * @author Tsung Wu <tsung.bz@gmail.com>
 */
public class UserList {
    public static final String SPLIT_USER_ID = ",";

    public static final String SPLIT_USER_NAME = "、";

    private Group<User> list = new Group<User>();

    /**
     * 缓存住id窜，以免再次生成
     */
    private String mId;

    /**
     * 缓存住mobile窜，以免再次生成
     */
    private String mMobile;

    /**
     * 获取列表用户id（经过排序）
     * 
     * @param force 是否强制更新内存缓存
     * @return
     */
    public String getId(boolean force) {
        if (force || TextUtils.isEmpty(mId)) {
            createIDString();
        }
        return mId;
    }

    public String getId() {
        return getId(false);
    }

    private void createIDString() {
        mId = "";
        this.sort();
        if (this.getList() != null) {
            for (User u : this.getList()) {
                mId = mId + (TextUtils.isEmpty(mId) ? "" : SPLIT_USER_ID) + u.getId();
            }
        }
    }

    /**
     * 用户都有电话号码
     * 
     * @return
     */
    public boolean isMobileAvaible() {
        for (User u : this.getList()) {
            if (TextUtils.isEmpty(u.getMobile())) {
                return false;
            }
        }
        return true;
    }

    public String getMobile() {
        if (TextUtils.isEmpty(mMobile)) {
            createMobileString();
        }
        return mMobile;
    }

    private void createMobileString() {
        mMobile = "";
        this.sort();
        if (this.getList() != null) {
            for (User u : this.getList()) {
                mMobile = mMobile + (TextUtils.isEmpty(mMobile) ? "" : SPLIT_USER_ID)
                        + u.getMobile();
            }
        }
    }

    private void sort() {
        if (this.getList() != null)
            Collections.sort(this.getList());
    }

    /**
     * 列表里有id不合法或者未缓存
     * 
     * @return
     */
    public boolean wrongUid() {
        for (User u : this.getList()) {
            if (User.wrongUid(u.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 有id未缓存
     * 
     * @return
     */
    public boolean unCachedUid() {
        for (User u : this.getList()) {
            if (User.unCachedUid(u.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 有id不合法
     * 
     * @return
     */
    public boolean invalidUid() {
        for (User u : this.getList()) {
            if (User.inValidUid(u.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是小秘
     * 
     * @return
     */
    public boolean isSecretary() {
        return this.getList() != null && this.getList().size() == 1
                && this.getList().get(0).isSecretary();
    }

    public static UserList createSingle(User u) {
        UserList ul = new UserList();
        ul.add(u);
        return ul;
    }

    public String getName() {
        String result = "";
        if (this.getList() != null) {
            for (User u : this.getList()) {
                result = result + (TextUtils.isEmpty(result) ? "" : SPLIT_USER_NAME)
                        + u.getName();
            }
        }
        return result;
    }

    public boolean isSingle() {
        return this.getList() != null && this.getList().size() == 1;
    }

    public boolean add(User u) {
        if (this.getList() != null) {
            boolean result = this.getList().add(u);

            this.createIDString();

            return result;
        } else {
            return false;
        }
    }

    public void setList(Group<User> list) {
        this.list = list;
    }

    public Group<User> getList() {
        return list;
    }

    public int size() {
        return this.getList().size();
    }

    public User get(int where) {
        return this.getList().get(where);
    }
}
