
package cn.com.nd.momo.api.parsers.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.nd.momo.api.types.Chat;
import cn.com.nd.momo.api.types.MQMessage;

/**
 * 带kind的解析
 * 
 * @date Oct 8, 2011
 * @author Tsung Wu <tsung.bz@gmail.com>
 */
public class ChatParser extends AbstractParser<Chat> {

    @Override
    public Chat parse(JSONObject all) throws JSONException {
        return new ChatBaseParser().parse(all.getJSONObject("data"));
    }

    @Override
    public JSONObject toJSONObject(Chat chat) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("kind", chat.getKind());

        JSONObject data = new JSONObject();

        if (chat.getSender() != null) {
            data.put("sender", new UserParser().toJSONObject(chat.getSender()));
        }
        if (chat.getReceiver() != null) {
            @SuppressWarnings({
                    "rawtypes", "unchecked"
            })
            JSONArray receiver = new GroupParser(new UserParser()).toJSONArray(chat.getReceiver()
                    .getList());
            data.put("receiver", receiver);
            if (receiver.length() > 1)
                json.put("kind", MQMessage.KIND_IM_GROUP);
            else
                json.put("kind", MQMessage.KIND_IM);
        }
        if (chat.getTimestamp() != 0)
            data.put("timestamp", chat.getTimestamp());
        data.put("client_id", chat.getClientid());
        data.put("id", chat.getId());
        data.put("state", chat.getState());

        if (chat.getContent() != null) {
            data.put("content", new ChatContentParser().toJSONObject(chat.getContent()));
        }

        json.put("data", data);

        return json;
    }
}
