
package cn.com.nd.momo.api.parsers.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.nd.momo.api.types.MomoType;

/**
 * @date Oct 5, 2011
 * @author Tsung Wu <tsung.bz@gmail.com>
 * @param <T>
 */
public abstract class AbstractParser<T extends MomoType> implements Parser<T> {
    /**
     * 所有类必须实现这个parse
     */
    @Override
    public abstract T parse(JSONObject json) throws JSONException;

    @Override
    public abstract JSONObject toJSONObject(T t) throws JSONException;

    /**
     * GroupParser才需要
     */
    @Override
    public T parse(JSONArray array) throws JSONException {
        throw new JSONException("Unexpected JSONArray parse type encountered.");
    }

    @Override
    public JSONArray toJSONArray(T group) throws JSONException {
        throw new JSONException("Unexpected JSONArray parse type encountered.");
    }
}
