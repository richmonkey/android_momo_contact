
package cn.com.nd.momo.api.types;

/**
 * 附件类
 * 
 * @author 曾广贤 (muroqiu@sina.com)
 */
public class Attachment {

    /**
     * ID
     */
    private String ID;

    /**
     * 名称
     */
    private String name;

    /**
     * Url
     */
    private String Url;

    /**
     * 类型
     */
    private String type;

    /**
     * 宽度（图片） 
     */
    private int width;
    /**
     * 高度（图片） 
     */
    private int height;
    /**
     * 大小（文件） 
     */
    private int size;
    
    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }
}
