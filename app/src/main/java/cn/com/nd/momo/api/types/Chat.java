
package cn.com.nd.momo.api.types;

import android.text.TextUtils;
import cn.com.nd.momo.api.AppInfo;
import cn.com.nd.momo.api.MoMoHttpApi;

/**
 * 服务端传输数据体
 * 
 * @author tsung
 */
public class Chat extends MQMessage implements IChat {
    private int state;

    private String id;

    private User sender;

    private UserList receiver;

    private long timestamp = 0;

    private int clientid = MoMoHttpApi.APP_ID;

    private ChatContent content;

    public Chat() {
        this.setKind(MQMessage.KIND_IM);
    }

    /**
     * 是否是小秘
     * 
     * @param oid
     * @return
     */
    @Override
    public boolean isSecretary() {
        return (this.getOther().size() == 1) && this.getOther().get(0).isSecretary();
    }

    /**
     * 根据状态判断是发出去的还是收进来的消息
     * 
     * @return
     */
    @Override
    public boolean isOut() {
        if (this.getSender() != null) {
            if (!TextUtils.isEmpty(this.getSender().getId())) {
                return this.getSender().getId().equals(AppInfo.getOAuthInfo().getUid());
            }
        }

        return isOut(this.getState());
    }

    public static boolean isOut(int state) {
        boolean result = false;

        switch (state) {
            case IChat.STATE_DRAFT:
            case IChat.STATE_SENDING:
            case IChat.STATE_SENT:
            case IChat.STATE_FAILED:
            case IChat.STATE_SMS_FAIL:
            case IChat.STATE_SMS_SENT:
            case IChat.STATE_SMS_SENDING:
            case IChat.STATE_RECEIVED:
            case IChat.STATE_INBOXED:
            default:
                result = true;
                break;
            case IChat.STATE_READ:
            case IChat.STATE_UNREAD:
                result = false;
                break;
        }

        return result;
    }

    /**
     * 获取聊天对象是谁
     * 
     * @return
     */
    @Override
    public UserList getOther() {
        if (this.isOut()) {
            return this.getReceiver();
        } else {
            return UserList.createSingle(this.getSender());
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Chat setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public User getSender() {
        return sender;
    }

    @Override
    public Chat setSender(User sender) {
        this.sender = sender;
        return this;
    }

    @Override
    public UserList getReceiver() {
        return receiver;
    }

    @Override
    public Chat setReceiver(UserList receiver) {
        this.receiver = receiver;
        if (this.receiver != null && this.receiver.size() > 1)
            this.setKind(MQMessage.KIND_IM_GROUP);
        else
            this.setKind(MQMessage.KIND_IM);
        return this;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public Chat setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @Override
    public int getClientid() {
        return clientid;
    }

    @Override
    public Chat setClientid(int clientid) {
        this.clientid = clientid;
        return this;
    }

    @Override
    public ChatContent getContent() {
        return content;
    }

    @Override
    public Chat setContent(ChatContent content) {
        this.content = content;
        return this;
    }

    @Override
    public Chat setState(int state) {
        this.state = state;
        return this;
    }

    @Override
    public int getState() {
        return state;
    }
}
