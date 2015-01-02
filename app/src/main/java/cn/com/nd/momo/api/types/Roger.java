
package cn.com.nd.momo.api.types;

import cn.com.nd.momo.api.MoMoHttpApi;

public class Roger extends MQMessage {
    public Roger() {
        this.setKind(MQMessage.KIND_ROGER);
    }

    private String sender;

    private String receiver;

    private long timestamp;

    private int client_id = MoMoHttpApi.APP_ID;

    private Status status = new Status();

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public static class Status {
        public static final int TYPE_SMS_ERROR = 1;

        public static final int TYPE_SMS_SEND = 2;

        public static final int TYPE_MSG_INBOXED = 3;

        public static final int TYPE_MSG_RECEIVED = 4;

        private int type;

        private String id;

        private int smsCount;

        public boolean isSmsError() {
            return type == TYPE_SMS_ERROR;
        }

        public boolean isSmsSend() {
            return type == TYPE_SMS_SEND;
        }

        public boolean isMsgInboxed() {
            return type == TYPE_MSG_INBOXED;
        }

        public boolean isMsgReceived() {
            return type == TYPE_MSG_RECEIVED;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setSmsCount(int smsCount) {
            this.smsCount = smsCount;
        }

        public int getSmsCount() {
            return smsCount;
        }
    }
}
