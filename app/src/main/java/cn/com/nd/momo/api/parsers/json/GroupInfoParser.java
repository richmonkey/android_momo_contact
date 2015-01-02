
package cn.com.nd.momo.api.parsers.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.nd.momo.api.types.GroupInfo;

/**
 * 群组数据解析
 * 
 * @author 曾广贤 (muroqiu@sina.com)
 */
public class GroupInfoParser extends AbstractParser<GroupInfo> {

    @Override
    public GroupInfo parse(JSONObject json) throws JSONException {
        GroupInfo groupInfo = new GroupInfo();

        if (!json.isNull("id")) {
            groupInfo.setGroupID(json.optInt("id"));
        }
        if (!json.isNull("name")) {
            groupInfo.setGroupName(json.optString("name"));
        }
        if (!json.isNull("notice")) {
            groupInfo.setNotice(json.optString("notice"));
        }
        if (!json.isNull("introduction")) {
            groupInfo.setIntroduction(json.optString("introduction"));
        }
        if (!json.isNull("privacy")) {
            groupInfo.setType(json.optInt("privacy"));
        }
        if (!json.isNull("created_at")) {
            groupInfo.setCreateTime(json.optLong("created_at"));
        }
        if (!json.isNull("modified_at")) {
            groupInfo.setModifyTime(json.optLong("modified_at"));
        }
        if (!json.isNull("creator")) {
            JSONObject jsonCreator = json.optJSONObject("creator");
            if (jsonCreator != null) {
                groupInfo.setCreatorID(jsonCreator.optLong("id"));
                groupInfo.setCreatorName(jsonCreator.optString("name"));
            }
        }
        if (!json.isNull("master")) {
            JSONObject jsonMaster = json.optJSONObject("master");
            if (jsonMaster != null) {
                groupInfo.setMasterID(jsonMaster.optLong("id"));
                groupInfo.setMasterName(jsonMaster.optString("name"));
            }
        }
        if (!json.isNull("manager")) {
            JSONArray jsonArrayManager = json.getJSONArray("manager");
            if (jsonArrayManager != null && jsonArrayManager.length() > 0) {
                // 群管理员暂时无用，不做解析
            }
        }
        if (!json.isNull("member_count")) {
            groupInfo.setMemberCount(json.optInt("member_count"));
        }
        if (!json.isNull("is_hide")) {
            groupInfo.setIsHide(json.optInt("is_hide") == 1);
        }
        return groupInfo;
    }

    @Override
    public JSONObject toJSONObject(GroupInfo t) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", t.getGroupID());
        json.put("name", t.getGroupName());
        json.put("notice", t.getNotice());
        json.put("introduction", t.getIntroduction());
        json.put("member_count", t.getMemberCount());

        JSONObject jsonCreator = new JSONObject();
        jsonCreator.put("id", t.getCreatorID());
        jsonCreator.put("name", t.getCreatorName());

        json.put("creator", jsonCreator);

        return json;
    }

}
