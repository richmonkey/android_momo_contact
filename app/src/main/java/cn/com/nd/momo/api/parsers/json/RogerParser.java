
package cn.com.nd.momo.api.parsers.json;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.nd.momo.api.types.MQMessage;
import cn.com.nd.momo.api.types.Roger;
import cn.com.nd.momo.api.types.Roger.Status;

/**
 * 消息状态反馈转化类
 * 
 * @date Oct 6, 2011
 * @author Tsung Wu <tsung.bz@gmail.com>
 */
public class RogerParser extends AbstractParser<Roger> {

    @Override
    public Roger parse(JSONObject json) throws JSONException {
        Roger roger = new Roger();
        if (json == null)
            return roger;
        JSONObject data = json.getJSONObject("data");
        roger.setClient_id(data.getInt("client_id"));
        roger.setReceiver(data.getString("receiver"));
        roger.setSender(data.getString("sender"));
        // roger.setTimestamp(json.getLong("timestamp"));

        JSONObject jStatus = data.getJSONObject("status");
        Status status = new Status();

        if (jStatus.has("sms_error")) {
            status.setType(Status.TYPE_SMS_ERROR);
            JSONObject msg = jStatus.getJSONObject("sms_error");
            if (msg.has("id"))
                status.setId(msg.getString("id"));
        } else if (jStatus.has("sms_send")) {
            status.setType(Status.TYPE_SMS_SEND);
            JSONObject msg = jStatus.getJSONObject("sms_send");
            if (msg.has("sms_count"))
                status.setSmsCount(msg.getInt("sms_count"));
            if (msg.has("id"))
                status.setId(msg.getString("id"));
        } else if (jStatus.has("msg_receive")) {
            status.setType(Status.TYPE_MSG_INBOXED);
            JSONObject msg = jStatus.getJSONObject("msg_receive");
            if (msg.has("id"))
                status.setId(msg.getString("id"));
        } else if (jStatus.has("msg_read")) {
            status.setType(Status.TYPE_MSG_RECEIVED);
            if (jStatus.get("msg_read") instanceof JSONObject) {
                JSONObject msg = jStatus.getJSONObject("msg_read");
                if (msg.has("id")) {
                    status.setId(msg.getString("id"));
                }
            }
        }

        roger.setStatus(status);

        return roger;
    }

    @Override
    public JSONObject toJSONObject(Roger roger) throws JSONException {
        JSONObject json = new JSONObject();

        json.put("kind", MQMessage.KIND_ROGER);

        JSONObject data = new JSONObject();
        data.put("sender", roger.getSender());
        data.put("receiver", roger.getReceiver());
        data.put("client_id", roger.getClient_id());

        JSONObject status = new JSONObject();
        if (roger.getStatus().isMsgReceived()) {
            JSONObject msg = new JSONObject();
            if (roger.getStatus().getId() != null && !roger.getStatus().getId().equals("")) {
                msg.put("id", roger.getStatus().getId());
            }
            status.put("msg_read", msg);
        } else if (roger.getStatus().isMsgInboxed()) {
            JSONObject msg = new JSONObject();
            msg.put("id", roger.getStatus().getId());
            status.put("msg_receive", msg);
        } else if (roger.getStatus().isSmsError()) {
            JSONObject msg = new JSONObject();
            msg.put("id", roger.getStatus().getId());
            status.put("sms_error", msg);
        } else if (roger.getStatus().isSmsSend()) {
            JSONObject msg = new JSONObject();
            msg.put("id", roger.getStatus().getId());
            msg.put("sms_count", roger.getStatus().getSmsCount());
            status.put("sms_send", msg);
        }

        data.put("status", status);

        json.put("data", data);

        return json;
    }

}
