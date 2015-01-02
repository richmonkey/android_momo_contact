
package cn.com.nd.momo.api.parsers.json;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.nd.momo.api.types.Robot;

public class RobotParser extends AbstractParser<Robot> {

    @Override
    public Robot parse(JSONObject json) throws JSONException {
        Robot robot = new Robot();
        robot.setId(json.optLong("robot_id"));
        robot.setName(json.optString("name"));
        robot.setAvatar(json.optString("avatar"));
        robot.setCommand(json.optString("command"));
        robot.setCommandType(json.optString("command_type"));
        robot.setAutoQueryCommand(json.optString("auto_query_command"));
        robot.setIsSubscribed(json.optBoolean("is_subscribed"));
        return robot;
    }

    @Override
    public JSONObject toJSONObject(Robot t) throws JSONException {
        JSONObject json = new JSONObject();
        if (t.getId() != 0)
            json.put("robot_id", t.getId());
        if (t.getName() != null)
            json.put("name", t.getName());
        if (t.getAvatar() != null)
            json.put("avatar", t.getAvatar());
        if (t.getCommand() != null)
            json.put("command", t.getCommand());
        if (t.getCommandType() != null)
            json.put("command_type", t.getCommandType());
        if (t.getAutoQueryCommand() != null)
            json.put("auto_query_command", t.getAutoQueryCommand());
        json.put("is_subscribed", t.isSubscribed() ? true : false);

        return json;
    }

}
