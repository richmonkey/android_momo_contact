
package cn.com.nd.momo.api.parsers.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.nd.momo.api.types.Chat;
import cn.com.nd.momo.api.types.IChat;
import cn.com.nd.momo.api.types.UserList;

/**
 * 拉取的时候没kind字段
 * 
 * @date Oct 8, 2011
 * @author Tsung Wu <tsung.bz@gmail.com>
 */
public class ChatBaseParser extends AbstractParser<Chat> {
    private boolean isTimeSeconds = false;

    public ChatBaseParser() {
        this.isTimeSeconds = false;
    }

    public ChatBaseParser(boolean is) {
        this.isTimeSeconds = is;
    }

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    @Override
    public Chat parse(JSONObject json) throws JSONException {
        Chat chat = new Chat();

        if (json.has("state"))
            chat.setState(json.getInt("state"));
        if (json.has("client_id"))
            chat.setClientid(json.getInt("client_id"));
        if (json.has("timestamp"))
            chat.setTimestamp(json.getLong("timestamp") * (this.isTimeSeconds ? 1000 : 1));
        if (json.has("id"))
            chat.setId(json.getString("id"));
        if (json.has("receiver")) {
            JSONArray user = json.getJSONArray("receiver");
            UserList users = new UserList();
            users.setList(new GroupParser(new UserParser()).parse(user));
            chat.setReceiver(users);
        }
        if (json.has("sender")) {
            chat.setSender(new UserParser().parse(json.getJSONObject("sender")));
        }

        if (json.has("content")) {
            chat.setContent(new ChatContentParser().parse(json.getJSONObject("content")));
        }

        if (json.has("status")) {
            JSONObject status = json.getJSONObject("status");
            if (chat.isOut()) {
                if (status.has("msg_read") && status.getInt("msg_read") == 1) {
                    chat.setState(IChat.STATE_RECEIVED);
                } else if (status.has("sms_send")
                        && (status.getInt("sms_send") == 1 || status.getInt("sms_send") == 2)) {
                    if (status.getInt("sms_send") == 1) {
                        chat.setState(IChat.STATE_SMS_SENT);
                    } else if (status.getInt("sms_send") == 2) {
                        chat.setState(IChat.STATE_SMS_FAIL);
                    }
                } else if (status.has("msg_read") && status.getInt("msg_read") == 0) {
                    chat.setState(IChat.STATE_SENT);
                } else {
                    // TODO default value sent
                    chat.setState(IChat.STATE_SENT);
                }
            } else {
                if (status.has("msg_read")) {
                    if (status.getInt("msg_read") == 1) {
                        chat.setState(IChat.STATE_READ);
                    } else {
                        chat.setState(IChat.STATE_UNREAD);
                    }
                }
            }
        }

        return chat;
    }

    @Override
    public JSONObject toJSONObject(Chat t) throws JSONException {
        // TODO Auto-generated method stub
        return null;
    }

}
