
package cn.com.nd.momo.api;

/**
 * API接口响应类
 * 
 * @author 曾广贤 (muroqiu@sina.com)
 */
public class Response {

    /**
     * 无效响应码
     */
    public static final int INVALID_CODE = -1;

    /**
     * 响应码
     */
    int code;

    /**
     * 响应内容
     */
    String content;

    public Response() {
        setCode(INVALID_CODE);
        setContent("");
    }

    public Response(int code, String content) {
        setCode(code);
        setContent(content);
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
