
package cn.com.nd.momo.api;

/**
 * Http 请求地址常量单元
 * 
 * @author 曾广贤 (muroqiu@sina.com)
 */
public final class RequestUrl {
    static public String URL_API = "http://api.contacts.momo.im";

    static public final String IN_API = "http://192.168.94.26";

    static public final String OUT_API = "http://api.momo.im";

    static public final String OUT_API_V2 = "http://v3.api.momo.im";

    static public final String OUT_API_V3 = "http://v3.api.momo.im";

    static public final String OUT_API_SIMULATE = "http://api.simulate.momo.im";

    // 注册
    static public final String REGIST_SEND_VERIFY_CODE_URL = URL_API + "/register/create.json";

    static public final String REGIST_VERIFY_URL = URL_API + "/register/verify.json";

    static public final String APPLY_VERIFYCODE = URL_API + "/user/apply_verifycode.json";
    static public final String BIND_MOBILE = URL_API + "/user/bind_mobile.json";
    static public final String CHECK_OAP = URL_API + "/user/check_oap.json";
    static public final String TOKEN_LOGIN = URL_API + "/user/token_login.json";
    
    // 登录
    static public final String LOGIN = URL_API + "/user/login.json";

    static public final String LOGIN_BY_URL = URL_API + "/user/auto_verify.json";

    static public final String USER_UPDATE = URL_API + "/user/personal.json";

    static public final String IMAGE_URL = URL_API + "/photo/update_avatar.json";

    static public final String FORGET_PASSWORD = URL_API + "/user/forget_pass.json";

    static public final String RESET_PASSWORD_NEW = URL_API + "/user/reset_password";

    static public final String RESET_PASSWORD_WITHOUT_OLD = URL_API
            + "/user/reset_password_without_old.json";

    static public final String GET_CREATE_AT_LIST = URL_API + "/register/create_at.json";

    static public final String GET_USER_SMS_COUNT = URL_API + "/user/sms_count.json";

    static public final String UPGRADE = URL_API + "/upgrade.json";

    static public final String PHOTO_URL = URL_API + "/photo/bp_upload.json";

    static public final String HELP_URL = "http://m.momo.im/t/user/help?isreturn=0";

    /**
     * 名片
     */
    static public final String RETRIEVE_USER_CARD_URL = URL_API + "/user/show/";

    static public final String RETRIEVE_USER_CARD_BY_MOBILE_URL = URL_API
            + "/user/show_by_mobile.json";

    static public final String BATCH_GET_CARD_LIST = URL_API + "/user/show_batch_by_mobile.json";

    static public final String UPDATE_USER_CARD_URL = URL_API + "/user/update.json";

    static public final String WEIBO_BIND_URL = URL_API + "/bind/create.json";

    /**
     * 应用机器人
     */
    static public final String GET_SUBSCRIPTION_ROBOT_LIST_URL = URL_API + "/app/subscription.json";

    static public final String GET_ROBOT_URL = URL_API + "/app/show/";

    static public final String GET_ALL_ROBOT_URL = URL_API + "/app.json";

    // 动态相关
    static public final String PHOTO_ORIGIN_URL = URL_API + "/photo/origin.json";

    static public final String STATUSES_GET = URL_API + "/statuses/index.json";

    static public final String STATUSES_CREATE = URL_API + "/record/create.json";

    static public final String STATUSES_STORE = URL_API + "/statuses/store.json";

    static public final String STATUSES_DEl = URL_API + "/statuses/destroy/";

    static public final String STATUSES_HIDE = URL_API + "/statuses/hide.json";

    static public final String STATUSES_PRAISE = URL_API + "/praise/create.json";

    static public final String STATUSES_USER = URL_API + "/statuses/user.json";

    static public final String STATUSES_GROUP = URL_API + "/statuses/group.json";
    
    static public final String STATUSES_LONG_TEXT = URL_API + "/statuses/long_text.json";

    static public final String COMMENT_CREATE = URL_API + "/comment/create.json";

    static public final String COMMENT_GET = URL_API + "/comment.json";

    static public final String COMMENT_DEL = URL_API + "/comment/destroy/";


    static public final String AVATAR_PREV_URL = URL_API + "/avatar";

    static public final String MESSAGE_NEW_URL = URL_API + "/message/newmsg";

    static public final String ACTIVITY = URL_API + "/activity.json?filter=all";

    static public final String SAVE_USER_CARD_TO_CONTACT_URL = URL_API
            + "/contact/save.json";

    static public final String MENTION_GET_URL = URL_API + "/statuses/aboutme_alone.json?new=0"; // 获取关于我的

    static public final String MOME_GET_URL = URL_API + "/statuses/my_mo.json?pagesize=100"; // 获取关于我的

    /**
     * 群组列表
     */
    static public final String GROUP_LIST = URL_API + "/group.json?type=";

    /**
     * 单个群信息
     */
    static public final String GROUP_INFO = URL_API + "/group/get/:id.json";

    /**
     * 群成员列表
     */
    static public final String GROUP_MEMBER_LIST = URL_API + "/group_member/:id.json";

    /**
     * 退出群 (群成员)
     */
    static public final String GROUP_QUIT = URL_API + "/group/quit/:id.json";

    /**
     * 解散群(群主)
     */
    static public final String GROUP_DESTROY = URL_API + "/group/destroy/:id.json";

    //
    // /**
    // * 删除群成员
    // */
    // static public final String GROUP_MEMBER_DEL = URL_API +
    // "/group_member/delete/:id.json";
    //
    // /**
    // * 增加群成员
    // */
    // static public final String GROUP_MEMBER_ADD = URL_API +
    // " /group_member/add/:id.json";
    //
    // static public final String ACTIVITY = URL_API +
    // "/activity.json?filter=all";
    // static public final String PHOTO_ORIGIN_URL = URL_API +
    // "/photo/origin.json";
    // static public final String FEED_URL = URL_API + "/statuses/index.json";
    // static public final String FEED_URL_TYPE = URL_API +
    // "/statuses/type.json";
    // static public final String GROUP_URL = URL_API + "/statuses/group.json";
    // static public final String USER_URL = URL_API + "/statuses/user.json";
    // static public final String ACTION_URL = URL_API +
    // "/statuses/action.json";
    // static public final String STATUSES_DESTORY_URL = URL_API +
    // "/statuses/destroy/:id.json";
    // static public final String RECORD_URL = URL_API + "/record/create.json";
    // static public final String PRAISE_URL = URL_API + "/praise/create.json";
    // static public final String HIDE_URL = URL_API + "/statuses/hide.json";
    // static public final String SMS_URL = URL_API + "/statuses/at_sms.json";
    // static public final String COMMENT_CREATE_URL = URL_API +
    // "/comment/create.json";
    // static public final String COMMENT_URL = URL_API + "/comment.json";
    // static public final String FRIEND_URL = URL_API + "/friend.json";
    // static public final String AVATAR_PREV_URL = URL_API + "/avatar";
    // static public final String FAV_URL = URL_API + "/statuses/store.json";
    // static public final String MESSAGE_NEW_URL = URL_API + "/message/newmsg";

    /**
     * 私聊
     */
    static public final String HTTP_RESULT_TYPE = ".json";

    static public final String FILE_UPLOAD_URL = URL_API + "/file/bp_upload" + HTTP_RESULT_TYPE;

    static public final String USER_CARD_SEND = URL_API + "/user/send_card" + HTTP_RESULT_TYPE;

    static public final String CONVERSATION_LIST = URL_API + "/im/all" + HTTP_RESULT_TYPE;

    static public final String URL_CONVERSATION_DETAIL_LIST_SINGLE = URL_API + "/im/more/";

    static public final String URL_CONVERSATION_LONGTEXT = URL_API
            + "/transfer/apiserver.php?class=im&method=show_text&source=" + MoMoHttpApi.APP_ID;

    /**
     * 短信拦截
     */
    // 通过短信内容获取消息
    static public final String URL_GET_MSG = URL_API + "/im/get_message_by_sms" + HTTP_RESULT_TYPE;

    // 通过短信url批量获取聊天对象
    static public final String URL_GET_MSG_BATCH = URL_API + "/im/get_message_by_sms_batch"
            + HTTP_RESULT_TYPE;

    // 发送消息
    static public final String URL_SEND_MSG = URL_API + "/im/send_message" + HTTP_RESULT_TYPE;

    // 删除消息
    static public final String URL_DEL_MSG = URL_API + "/im/delete" + HTTP_RESULT_TYPE;

    // 删除所有与某人消息
    static public final String URL_DEL_MSG_ALL = URL_API + "/im/delete_all" + HTTP_RESULT_TYPE;

    static public String M_API = MoMoHttpApi.get3GApi();

    static public final String ROBOT_3G_URL = M_API + "/t/app/";

    /**
     * 联系人同步
     */
    static public final String UPDATE_CONTACTS_URL = URL_API + "/contact/update/";

    static public final String BATCH_ADD_CONTACTS_URL = URL_API + "/contact/create_batch.json";

    static public final String BATCH_DELETE_CONTACTS_URL = URL_API + "/contact/destroy_batch.json";

    static public final String RETRIEVE_CONTACTS_SIMPLE_INFO_URL = URL_API + "/contact.json";

    static public final String BATCH_RETRIEVE_CONTACTS_URL = URL_API + "/contact/show_batch.json";

    static public final String RETRIEVE_CONTACTS_COUNT = URL_API + "/contact/count.json";

}
