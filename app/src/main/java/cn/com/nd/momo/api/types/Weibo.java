/**
 * 
 */

package cn.com.nd.momo.api.types;

/**
 * 微博模型类
 * 
 * @author chenjp
 */
public class Weibo {

    public static String SINA_WEIBO = "weibo.com";

    public static String TENCENT_WEIBO = "t.qq.com";

    public static String KAIXIN = "kaixin001.com";

    private String type;

    private String url;

    public Weibo(String type, String url) {
        super();
        this.type = type;
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
