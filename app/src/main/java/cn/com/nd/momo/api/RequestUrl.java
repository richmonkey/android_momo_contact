
package cn.com.nd.momo.api;

/**
 * Http 请求地址常量单元
 * 
 * @author 曾广贤 (muroqiu@sina.com)
 */
public final class RequestUrl {
    static public String URL_API = "http://api.contacts.momo.im";



    /**
     * 联系人同步
     */
    static public final String UPDATE_CONTACTS_URL = URL_API + "/contact/update/";

    static public final String BATCH_ADD_CONTACTS_URL = URL_API + "/contact/create_batch.json";

    static public final String BATCH_DELETE_CONTACTS_URL = URL_API + "/contact/destroy_batch.json";

    static public final String RETRIEVE_CONTACTS_SIMPLE_INFO_URL = URL_API + "/contact.json";

    static public final String BATCH_RETRIEVE_CONTACTS_URL = URL_API + "/contact/show_batch.json";

    static public final String RETRIEVE_CONTACTS_COUNT = URL_API + "/contact/count.json";

}
