
package cn.com.nd.momo.api.parsers.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import cn.com.nd.momo.api.types.User;

public class UserParser extends AbstractParser<User> {

    @Override
    public User parse(JSONObject json) throws JSONException {
        User user = new User();
        if (json.has("id"))
            user.setId(json.getString("id"));
        if (json.has("mobile"))
            user.setMobile(json.getString("mobile"));
        user.setZoneCode(json.optString("zone_code"));
        if (user.isSecretary()) {
            user.setName(User.SECRETARY_NAME);
        } else {
            if (json.has("name") && !TextUtils.isEmpty(json.getString("name"))) {
                user.setName(json.getString("name"));
            } else {
                user.setName("体验者");
            }
        }
        if (json.has("avatar"))
            user.setAvatar(json.getString("avatar"));
        return user;
    }

    @Override
    public JSONObject toJSONObject(User t) throws JSONException {
        JSONObject json = new JSONObject();
        if (t.getId() != null)
            json.put("id", t.getId());
        if (t.getMobile() != null)
            json.put("mobile", t.getMobile());
        if (t.getZoneCode() != null) {
            json.put("zone_code", t.getZoneCode());
        }
        if (t.isSecretary()) {
            json.put("name", User.SECRETARY_NAME);
        } else {
            if (TextUtils.isEmpty(t.getName())) {
                // TODO localize
                json.put("name", "体验者");
            } else {
                json.put("name", t.getName());
            }
        }
        json.put("avatar", t.getAvatar());
        return json;
    }

}
