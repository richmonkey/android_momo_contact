
package cn.com.nd.momo.api.parsers.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.nd.momo.api.types.Group;
import cn.com.nd.momo.api.types.MomoType;

public class GroupParser<S extends MomoType> extends AbstractParser<Group<S>> {
    private Parser<S> mSubParser;

    public GroupParser(Parser<S> subParser) {
        mSubParser = subParser;
    }

    @Override
    public Group<S> parse(JSONObject json) throws JSONException {
        return null;
    }

    @Override
    public Group<S> parse(JSONArray array) throws JSONException {
        Group<S> group = new Group<S>();
        parseArray(group, array);

        return group;
    }

    private void parseArray(Group<S> group, JSONArray array) throws JSONException {
        for (int i = 0, m = array.length(); i < m; i++) {
            Object element = array.get(i);
            S item = null;
            if (element instanceof JSONArray) {
                item = mSubParser.parse((JSONArray)element);
            } else if (element instanceof JSONObject) {
                item = mSubParser.parse((JSONObject)element);
            }

            group.add(item);
        }
    }

    @Override
    public JSONObject toJSONObject(Group<S> group) throws JSONException {
        return null;
    }

    @Override
    public JSONArray toJSONArray(Group<S> group) throws JSONException {
        JSONArray json = new JSONArray();
        for (S item : group) {
            if (item instanceof Group) {
                json.put(mSubParser.toJSONArray(item));
            } else {
                json.put(mSubParser.toJSONObject(item));
            }
        }
        return json;
    }
}
