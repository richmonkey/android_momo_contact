
package cn.com.nd.momo.api.types;

public class MQMessage implements MomoType {
    public static final String KIND_IM = "sms";

    public static final String KIND_IM_GROUP = "group_sms";

    public static final String KIND_ROGER = "roger";

    private String kind;

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getKind() {
        return kind;
    }
}
