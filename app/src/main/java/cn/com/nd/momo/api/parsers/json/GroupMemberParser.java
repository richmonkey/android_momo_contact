
package cn.com.nd.momo.api.parsers.json;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.nd.momo.api.types.GroupMember;

/**
 * 群成员数据解析
 * 
 * @author 曾广贤 (muroqiu@sina.com)
 */
public class GroupMemberParser extends AbstractParser<GroupMember> {

    @Override
    public GroupMember parse(JSONObject json) throws JSONException {
        GroupMember groupMember = new GroupMember();

        groupMember.setId(json.optLong("id"));
        groupMember.setName(json.optString("name"));
        groupMember.setAvatar(json.optString("avatar"));
        groupMember.setGrade(json.optInt("grade"));
        groupMember.setZoneCode(json.optString("zone_code"));
        groupMember.setMobile(json.optString("mobile"));

        return groupMember;
    }

    @Override
    public JSONObject toJSONObject(GroupMember t) throws JSONException {
        JSONObject json = new JSONObject();

        json.put("id", t.getId());
        json.put("name", t.getName());
        json.put("avatar", t.getAvatar());
        json.put("grade", t.getGrade());
        json.put("zone_code", t.getZoneCode());
        json.put("mobile", t.getMobile());

        return json;
    }

}
