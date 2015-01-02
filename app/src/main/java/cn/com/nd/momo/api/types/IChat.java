
package cn.com.nd.momo.api.types;

public interface IChat {
    /**
     * 消息状态
     */
    public static final int STATE_UNREAD = 0;

    public static final int STATE_READ = 1;

    public static final int STATE_DRAFT = 2;

    public static final int STATE_SENT = 3;

    public static final int STATE_SENDING = 4;

    public static final int STATE_RECEIVED = 5;

    public static final int STATE_FAILED = 6;

    // 已送达，在设备上接收到了，可能没点开
    public static final int STATE_INBOXED = 7;

    // 已用短信发出
    public static final int STATE_SMS_SENT = 8;

    public static final int STATE_SMS_FAIL = 9;

    public static final int STATE_SMS_SENDING = 10;

    /**
     * 是否是小秘
     * 
     * @param oid
     * @return
     */
    public boolean isSecretary();

    /**
     * 根据状态判断是发出去的还是收进来的消息
     * 
     * @return
     */
    public boolean isOut();

    public String getKind();

    /**
     * 获取聊天对象是谁
     * 
     * @return
     */
    public UserList getOther();

    public String getId();

    public IChat setId(String id);

    public User getSender();

    public IChat setSender(User sender);

    public UserList getReceiver();

    public IChat setReceiver(UserList receiver);

    public long getTimestamp();

    public IChat setTimestamp(long timestamp);

    public int getClientid();

    public IChat setClientid(int clientid);

    public ChatContent getContent();

    public IChat setContent(ChatContent content);

    public IChat setState(int state);

    public int getState();

}
