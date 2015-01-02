
package cn.com.nd.momo.api.exception;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 异常类封装
 * 
 * @author 曾广贤 (muroqiu@sina.com)
 */
public class MoMoException extends Exception {
    private static final long serialVersionUID = -8618791505361792493L;

    private int mCode = 0;

    /**
     * 手机号码格式不对
     */
    public static int MOBILE_TYPE_INVALID = 400116;

    public MoMoException(Exception cause) {
        super(cause instanceof JSONException ? "数据解析错误" : cause.getMessage());
        if (cause instanceof MoMoException) {
            mCode = ((MoMoException)cause).getCode();
        }
    }

    public MoMoException(String message) {
        super(message);
    }

    public MoMoException(JSONObject json) {
        super(json.optString("error"));
        mCode = json.optInt("error_code");
    }

    public MoMoException(int code, String message) {
        super(message);
        mCode = code;
    }

    /**
     * 返回异常代码
     * 
     * @return
     */
    public int getCode() {
        return mCode;
    }

    /**
     * 返回异常的简明信息
     * 
     * @return
     */
    public String getSimpleMsg() {
        String msg = this.getMessage();
        if (msg != null && msg.startsWith("400")) {
            int pos = msg.indexOf(":");
            if (pos > 0) {
                return msg.substring(pos + 1);
            }
        }

        return msg;
    }
}
