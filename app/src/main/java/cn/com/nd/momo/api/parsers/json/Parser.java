
package cn.com.nd.momo.api.parsers.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.nd.momo.api.types.MomoType;

/**
 * parser接口列表
 * 
 * @date Oct 5, 2011
 * @author Tsung Wu <tsung.bz@gmail.com>
 * @param <T>
 */
public interface Parser<T extends MomoType> {
    public abstract T parse(JSONObject json) throws JSONException;

    public T parse(JSONArray array) throws JSONException;

    public abstract JSONObject toJSONObject(T t) throws JSONException;

    public JSONArray toJSONArray(T group) throws JSONException;
}
