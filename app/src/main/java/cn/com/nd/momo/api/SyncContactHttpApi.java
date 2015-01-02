
package cn.com.nd.momo.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.nd.momo.api.exception.MoMoException;
import cn.com.nd.momo.api.http.HttpTool;
import cn.com.nd.momo.api.oauth.OAuthHelper;
import cn.com.nd.momo.api.parsers.json.ContactParser;
import cn.com.nd.momo.api.types.Contact;
import cn.com.nd.momo.api.util.Log;

/**
 * MoMo Http API 封装类
 * 
 * @author 曾广贤 (muroqiu@sina.com)
 */
public final class SyncContactHttpApi {

    private static final String TAG = "SyncContactHttpApi";

    /**
     * 获取API地址
     * 
     * @return
     */
    public static final String getApi() {
        return MoMoHttpApi.configType.getUrl();
    }

    /**
     * 更新联系人信息到服务器
     * 
     * @param contact
     * @return http响应内容
     * @throws MoMoException
     */
    public static String updateContact(Contact contact) throws MoMoException {
        String url = RequestUrl.UPDATE_CONTACTS_URL + contact.getContactId() + ".json";
        HttpTool http = new HttpTool(url);
        JSONObject param = new JSONObject();
        try {
            param = new ContactParser().toContactJSONObject(contact);
            http.DoPost(param);
            return http.GetResponse();
        } catch (Exception ex) {
            throw new MoMoException(ex);
        }

    }

    /**
     * 添加联系人列表到服务器
     * 
     * @param contactsList
     * @return http响应内容
     * @throws MoMoException
     */
    public static String addContactList(final List<Contact> contactsList) throws MoMoException {
        String url = RequestUrl.BATCH_ADD_CONTACTS_URL;
        HttpTool http = new HttpTool(url);
        JSONObject queryParams = new JSONObject();
        JSONObject eachData = new JSONObject();
        try {
            if (null != contactsList) {
                int count = contactsList.size();
                for (int i = 0; i < count; i++) {
                    Contact contact = contactsList.get(i);
                    JSONObject contactData = new JSONObject();
                    contactData = new ContactParser().toContactJSONObject(contact);
                    eachData.put(String.valueOf(contact.getPhoneCid()), contactData);
                }
            }
            queryParams.put("data", eachData);
            http.DoPost(queryParams);
            return http.GetResponse();
        } catch (Exception ex) {
            throw new MoMoException(ex);
        }
    }

    /**
     * 删除服务器上联系人列表
     * 
     * @param toDeleteIdList 删除的联系人id列表
     * @return 删除联系人是否成功列表
     * @throws MoMoException
     */
    public static List<Boolean> deleteContactList(List<Long> toDeleteIdList) throws MoMoException {
        String url = RequestUrl.BATCH_DELETE_CONTACTS_URL;
        HttpTool httpTool = new HttpTool(url);
        JSONObject queryParams = new JSONObject();
        try {
            StringBuilder idStrs = new StringBuilder(20);
            int count = toDeleteIdList.size();
            for (int i = 0; i < count; i++) {
                long id = toDeleteIdList.get(i);
                idStrs.append(id);
                if (i < count - 1)
                    idStrs.append(",");
            }
            queryParams.put("ids", idStrs.toString());
        } catch (JSONException e) {
            throw new MoMoException(e);
        }
        Log.d(TAG, queryParams.toString());
        httpTool.DoPost(queryParams);
        List<Boolean> resultList = new ArrayList<Boolean>();

        String response = httpTool.GetResponse();
        Log.d(TAG, response);
        try {
            JSONArray jsonArray = new JSONArray(response);
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                boolean result = (200 == obj.getInt("status")) ? true : false;
                resultList.add(result);
            }
        } catch (JSONException e) {
            throw new MoMoException(e);
        }
        return resultList;
    }

    /**
     * 获取服务器上联系人简要信息列表
     * 
     * @return 联系人简要信息列表
     * @throws MoMoException
     */
    public static List<Contact> getAllContactSimpleInfoList() throws MoMoException {
        HttpTool httpTool = new HttpTool(RequestUrl.RETRIEVE_CONTACTS_SIMPLE_INFO_URL
                + "?contact_group_id=all&info=0");
        httpTool.DoGet();
        String response = httpTool.GetResponse();
        Log.d(TAG, response);
        List<Contact> contactsList = new ArrayList<Contact>();
        try {
            JSONArray responseCategoryList = new JSONArray(response);
            int length = responseCategoryList.length();
            for (int i = 0; i < length; ++i) {
                JSONObject info = responseCategoryList.getJSONObject(i);
                int contactId = info.getInt("id");
                long modified = info.optLong("modified_at");
                Contact contact = new Contact();
                contact.setContactId(contactId);
                contact.setModifyDate(modified);
                contactsList.add(contact);
            }
        } catch (JSONException e) {
            throw new MoMoException(e);
        }
        return contactsList;
    }

    /**
     * 获取联系人详细信息列表
     * 
     * @param list 联系人id列表
     * @return 联系人详细信息列表
     * @throws MoMoException
     */
    public static List<Contact> getContactDetailsList(List<Contact> list) throws MoMoException {
        HttpTool httpTool = new HttpTool(RequestUrl.BATCH_RETRIEVE_CONTACTS_URL);
        JSONObject queryParams = new JSONObject();
        StringBuilder ids = new StringBuilder();
        int size = list.size();
        String separtor = "";
        for (int j = 0; j < size; j++) {
            Contact contact = list.get(j);
            long id = contact.getContactId();
            ids.append(separtor).append(id);
            separtor = ",";
        }
        String idList = ids.toString();
        try {
            queryParams.put("ids", idList);
        } catch (JSONException e) {
            throw new MoMoException(e);
        }
        Log.d(TAG, queryParams.toString());
        httpTool.DoPost(queryParams);
        JSONArray jsonArrayList = null;
        String response = httpTool.GetResponse();
        Log.d(TAG, response);
        List<Contact> contactList = new ArrayList<Contact>();
        try {
            jsonArrayList = new JSONArray(response);
            int count = jsonArrayList.length();
            for (int i = 0; i < count; i++) {
                JSONObject json = jsonArrayList.getJSONObject(i);
                Contact contact = new ContactParser().parseContact(json);
                contactList.add(contact);
            }
        } catch (JSONException e) {
            throw new MoMoException(e);
        }
        return contactList;
    }

    /**
     * 下载联系人头像
     * 
     * @param avatarUrl
     * @return
     * @throws MoMoException
     */
    public static byte[] downloadContactAvatar(String avatarUrl) throws MoMoException {
        String headers = OAuthHelper.getAuthHeader(avatarUrl, "GET");
        return HttpTool.DownLoadBytes(avatarUrl, headers);
    }

}
