
package cn.com.nd.momo.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import cn.com.nd.momo.api.exception.MoMoException;
import cn.com.nd.momo.api.http.HttpTool;
import cn.com.nd.momo.api.oauth.OAuthHelper;
import cn.com.nd.momo.api.parsers.json.ChatBaseParser;
import cn.com.nd.momo.api.parsers.json.ChatParser;
import cn.com.nd.momo.api.parsers.json.ContactParser;
import cn.com.nd.momo.api.parsers.json.GroupParser;
import cn.com.nd.momo.api.parsers.json.RobotParser;
import cn.com.nd.momo.api.types.Attachment;
import cn.com.nd.momo.api.types.Chat;
import cn.com.nd.momo.api.types.Contact;
import cn.com.nd.momo.api.types.Group;
import cn.com.nd.momo.api.types.OAuthInfo;
import cn.com.nd.momo.api.types.Robot;
import cn.com.nd.momo.api.types.UpgradeInfo;
import cn.com.nd.momo.api.types.User;
import cn.com.nd.momo.api.types.UserList;
import cn.com.nd.momo.api.util.Log;
import cn.com.nd.momo.api.util.Utils;

/**
 * MoMo Http API 封装类
 * 
 * @author 曾广贤 (muroqiu@sina.com)
 */
public final class MoMoHttpApi {

    public static int APP_ID = 1;

    public static enum MOMO_CONFIG_TYPE {
        // 设置为内网
        IN(RequestUrl.IN_API, 10650764, RequestUrl.IN_API)
        // 设置为仿真
        , SIMULATE(RequestUrl.OUT_API_SIMULATE, 353, "http://m.simulate.momo.im")
        // 设置为外网真实环境
        , OUT(RequestUrl.OUT_API_V3, 353, "http://m.momo.im");

        private String url;

        private int secretaryUid;

        private String mobileUrl;

        private MOMO_CONFIG_TYPE(String url, int secretaryUid, String mobileUrl) {
            this.url = url;
            this.secretaryUid = secretaryUid;
            this.mobileUrl = mobileUrl;
        }

        public String getUrl() {
            return this.url;
        }

        private int getSecretaryUid() {
            return this.secretaryUid;
        }

        private String getMobileUrl() {
            return this.mobileUrl;
        }
    }

    // 升级检测时是否内测包条件
    public static final boolean UPGRADE_IS_BETA = false;

    // 升级检测渠道id
    public static final String UPGRADE_INSTALL_ID = "momo";

    /**
     * 新更改环境方法
     */
    public static final MOMO_CONFIG_TYPE configType = MOMO_CONFIG_TYPE.OUT;

    /**
     * 设置AppID
     * 
     * @param appID
     */
    public static final void setAppID(int appID) {
        APP_ID = appID;
    }

    /**
     * 设置认证资料
     * 
     * @param oauthToken
     */
    public static final void setOAuthInfo(OAuthInfo oAuthInfo) {
        AppInfo.setOAuthInfo(oAuthInfo);
    }

    public static final int getFeedUID() {
        return configType.getSecretaryUid();
    }

    /**
     * 获取3G地址
     * 
     * @return
     */
    public static final String get3GApi() {
        return configType.getMobileUrl();
    }

    /**
     * 获取API地址
     * 
     * @return
     */
    public static final String getApi() {
        return configType.getUrl();
    }

    /**
     * 根据手机号 + 密码登陆
     * 
     * @param zoneCode
     * @param phoneNum
     * @param pwd
     * @return OAuthInfo
     */
    public static OAuthInfo login(String zoneCode, String phoneNum, String pwd)
            throws MoMoException {
        return OAuthHelper.login(zoneCode, phoneNum, pwd);
    }

    /**
     * 通过带验证信息的url完成登录
     * 
     * @param url
     * @return OAuthInfo
     * @throws MoMoException
     */
    public static OAuthInfo loginByUrl(String url) throws MoMoException {
        return OAuthHelper.loginByUrl(url);
    }

    /**
     * 发送注册信息
     * 
     * @param mobile
     * @return
     * @throws MoMoException
     */
    public static void register(String zoneCode, String mobile) throws MoMoException {
        OAuthHelper.register(zoneCode, mobile);
    }

    /**
     * 完成注册校验
     * 
     * @param zoneCode
     * @param mobile
     * @param verifyCode
     * @return OAuthInfo
     */
    public static OAuthInfo registerVerify(String zoneCode, String mobile, String verifyCode)
            throws MoMoException {
        return OAuthHelper.registerVerify(zoneCode, mobile, verifyCode);
    }

    /**
     * 完善个人信息
     * 
     * @param realName
     * @param pwd
     * @return 用户状态
     * @throws MoMoException
     */
    public static int updateUserInfo(String realName, String pwd) throws MoMoException {
        int result = 0;

        HttpTool http = new HttpTool(RequestUrl.USER_UPDATE);
        JSONObject param = new JSONObject();
        try {
            param.put("realname", realName);
            param.put("password", pwd);

            http.DoPost(param);
            String responseContent = http.GetResponse();
            JSONObject jsonResponse = new JSONObject(responseContent);
            result = jsonResponse.optInt("user_status");
        } catch (JSONException e) {
            throw new MoMoException(e);
        }
        return result;
    }

    /**
     * 重置密码
     * 
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @return
     * @throws MoMoException
     */
    public static void resetPwd(String oldPwd, String newPwd) throws MoMoException {
        OAuthHelper.resetPwd(oldPwd, newPwd);
    }

    /**
     * 重置密码（不需提供原始密码，需登录后才能做）
     * 
     * @param newPwd 新密码
     * @return OAuthInfo
     * @throws MoMoException
     */
    public static OAuthInfo resetPwd(String zoneCode, String mobile, String newPwd)
            throws MoMoException {
        return OAuthHelper.resetPwd(zoneCode, mobile, newPwd);
    }

    /**
     * 通过短信重发密码
     * 
     * @param zoneCode
     * @param mobile
     * @return
     * @throws MoMoException
     */
    public static void getPwdBySms(String zoneCode, String mobile) throws MoMoException {
        OAuthHelper.getPwdBySms(zoneCode, mobile);
    }

    /**
     * 获取可用短信条数
     * 
     * @return 可用短信条数
     * @throws MoMoException
     */
    public static int getSmsCount() throws MoMoException {
        return OAuthHelper.getSmsCount();
    }

    /**
     * 根据手机号码批量获取用户ID
     * 
     * @param userMap
     * @return
     * @throws MoMoException
     */
    public static List<User> getIDList(Map<String, User> userMap) throws MoMoException {
        return OAuthHelper.getIDList(userMap);
    }

    /**
     * 绑定微博
     * 
     * @param site
     * @param accountName
     * @param accountPassword
     * @param followMoMo
     * @return
     * @throws MoMoException
     */
    public static void bindWeibo(String site, String accountName, String accountPassword,
            boolean followMoMo) throws MoMoException {
        OAuthHelper.bindWeibo(site, accountName, accountPassword, followMoMo);
    }

    /**
     * 获取升级信息
     * 
     * @param version
     * @return UpgradeInfo
     */
    public static UpgradeInfo getUpgradeInfo(String version) throws MoMoException {
        UpgradeInfo result = null;

        JSONObject param = new JSONObject();
        HttpTool http = new HttpTool(RequestUrl.UPGRADE);
        try {
            param.put("source", APP_ID);
            param.put("version", version);
            param.put("is_beta", UPGRADE_IS_BETA);
            param.put("install_id", UPGRADE_INSTALL_ID);
            param.put("phone_model", android.os.Build.MODEL);
            param.put("os", android.os.Build.VERSION.RELEASE);

            http.DoPost(param);
            JSONObject jsonResponse = new JSONObject(http.GetResponse());
            result = new UpgradeInfo();
            result.downloadUrl = jsonResponse.getString("download_url");
            result.remark = jsonResponse.getString("remark");
            result.publishDate = jsonResponse.getString("publish_date");
            result.fileSize = jsonResponse.getString("file_size");
            result.downloadUrl = jsonResponse.getString("download_url");
            result.currentVersion = jsonResponse.getString("current_version");

        } catch (Exception e) {
            throw new MoMoException(e);
        }

        return result;
    }

    /**
     * 获取所有机器人列表
     * 
     * @return
     * @throws MoMoException
     */
    public static ArrayList<Robot> getRobotList() throws MoMoException {
        ArrayList<Robot> robotList = new ArrayList<Robot>();
        HttpTool httpTool = new HttpTool(RequestUrl.GET_ALL_ROBOT_URL);
        int responseCode = httpTool.DoGet();
        String response = httpTool.GetResponse();
        Log.d("code:" + responseCode + " response:" + response);
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonResult = jsonArray.getJSONObject(i);

                long id = jsonResult.optLong("robot_id");
                String name = jsonResult.optString("name");
                String avatar = jsonResult.optString("avatar");
                boolean isSubscribed = jsonResult.optBoolean("is_subscribed");
                Robot robot = new Robot();
                robot.setId(id);
                robot.setName(name);
                robot.setAvatar(avatar);
                robot.setIsSubscribed(isSubscribed);

                if (robot != null) {
                    robotList.add(robot);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MoMoException(e);
        } finally {
            Log.i("get robots complete!");
        }
        return robotList;
    }

    /**
     * 获取订阅的应用机器人列表
     * 
     * @return
     */
    public static ArrayList<Robot> getSubscriptRobotList() throws MoMoException {
        ArrayList<Robot> robotList = new ArrayList<Robot>();
        HttpTool httpTool = new HttpTool(RequestUrl.GET_SUBSCRIPTION_ROBOT_LIST_URL);
        int responseCode = httpTool.DoGet();
        String response = httpTool.GetResponse();
        Log.d("code:" + responseCode + " response:" + response);
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.optJSONObject(i);
                RobotParser psr = new RobotParser();
                Robot robot = psr.parse(object);
                if (robot != null) {
                    robotList.add(robot);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MoMoException(e);
        }
        return robotList;
    }

    /**
     * 获取应用详情
     * 
     * @param robotId
     * @return
     * @deprecated
     */
    @Deprecated
    public static Robot getRobotDetailsFromServer(long robotId) throws MoMoException {
        Robot robot = null;
        if (robotId < 1) {
            return robot;
        }
        StringBuilder url = new StringBuilder(RequestUrl.GET_ROBOT_URL).append(robotId).append(
                ".json");
        HttpTool httpTool = new HttpTool(url.toString());
        int responseCode = httpTool.DoGet();
        String response = httpTool.GetResponse();
        Log.d("code:" + responseCode + " response:" + response);
        RobotParser psr = new RobotParser();
        try {
            robot = psr.parse(new JSONObject(response));
        } catch (JSONException e) {
            e.printStackTrace();
            throw new MoMoException(e);
        }
        return robot;
    }

    /**
     * 更新头像
     * 
     * @param bmpAvatar 大头像
     * @param bmpAvatarBig 原图
     * @return 图片url地址
     * @throws MoMoException
     */
    public static String uploadMyAvatar(Bitmap bmpAvatar, Bitmap bmpAvatarBig) throws MoMoException {
        String result = null;
        ByteArrayOutputStream streamImg = new ByteArrayOutputStream();
        ByteArrayOutputStream streamImgBig = new ByteArrayOutputStream();
        if (!bmpAvatar.compress(Bitmap.CompressFormat.JPEG, 100, streamImg)) {
            Log.e("UploadBitmap: get small bmp compress data failed");
            throw new MoMoException("图像压缩错误");
        }
        if (!bmpAvatarBig.compress(Bitmap.CompressFormat.JPEG, 80, streamImgBig)) {
            Log.e("UploadBitmap: get big bmp compress data failed");
            throw new MoMoException("图像压缩错误");
        }

        String strData = new String(Base64.encodeBase64(streamImg.toByteArray()));
        String strDataBig = new String(Base64.encodeBase64(streamImgBig.toByteArray()));

        try {
            streamImg.close();
            streamImgBig.close();
        } catch (IOException e) {
            Log.e(e.getMessage());
        }

        HttpTool http = new HttpTool(RequestUrl.IMAGE_URL);
        JSONObject param = new JSONObject();

        try {
            param.put("middle_content", strData);
            param.put("original_content", strDataBig);

            http.DoPost(param);
            JSONObject jsonResponse = new JSONObject(http.GetResponse());
            result = jsonResponse.optString("src");
        } catch (JSONException e) {
            Log.e("uploadBitmap: " + e.getMessage());
            throw new MoMoException(e);
        }

        return result;
    }

    /**
     * 上传图片文件
     * 
     * @param bytes
     * @return 图片文件url
     * @throws MoMoException
     */
    public static Attachment upLoadPhoto(byte[] bytes) throws MoMoException {
        return PhotoUpLoad.upLoadPhotoByte(bytes);
    }
    
    public static Attachment upLoadPhoto(String file) throws MoMoException {
        return PhotoUpLoad.uploadPhotoFile(file);
    }

    /**
     * 上传音频文件
     * 
     * @param bytes
     * @return 音频文件url
     * @throws MoMoException
     */
    public static String upLoadAutioFile(byte[] bytes) throws MoMoException {
        // 音频文件类型为1，不用传文件名
        return upLoadFile(bytes, 1, null);
    }

    /**
     * 上传文件
     * 
     * @param bytes
     * @param fileType
     * @param fileName
     * @return 文件url
     * @throws MoMoException
     */
    private static String upLoadFile(byte[] bytes, int fileType, String fileName)
            throws MoMoException {
        return FileUpLoad.upLoadFileByte(bytes, fileType, fileName);
    }

    /**
     * 下载文件
     * 
     * @param url
     * @return 文件内容byte[]
     * @throws MoMoException
     */
    public static byte[] DownLoadBytes(String url) throws MoMoException {
        String headers = OAuthHelper.getAuthHeader(url, "GET");
        return HttpTool.DownLoadBytes(url, headers);
    }

    /**
     * 根据用户id获取用户名片
     * 
     * @param uid
     * @return
     * @throws MoMoException
     */
    public static Contact getUserCardByID(long uid) throws MoMoException {
        Contact result = null;
        HttpTool http = new HttpTool(new StringBuilder(RequestUrl.RETRIEVE_USER_CARD_URL)
                .append(uid).append(".json").toString());

        try {
            http.DoGet();
            JSONObject jsonResponse = new JSONObject(http.GetResponse());
            result = new ContactParser().parse(jsonResponse);

        } catch (Exception ex) {
            throw new MoMoException(ex);
        }

        return result;
    }

    /**
     * 根据用户名、手机号码获取名片
     * 
     * @param name
     * @param mobile
     * @return
     * @throws MoMoException
     */
    public static Contact getUserCardByMobile(String name, String mobile) throws MoMoException {
        Contact result = null;
        HttpTool http = new HttpTool(RequestUrl.RETRIEVE_USER_CARD_BY_MOBILE_URL);
        JSONObject param = new JSONObject();

        try {
            param.put("name", name);
            param.put("mobile", mobile);

            http.DoPost(param);
            JSONObject jsonResponse = new JSONObject(http.GetResponse());
            result = new ContactParser().parse(jsonResponse);

        } catch (Exception ex) {
            throw new MoMoException(ex);
        }

        return result;
    }

    /**
     * 根据手机号码批量获取名片
     * 
     * @param mobiles
     * @return 名片列表
     * @throws MoMoException
     */
    public static List<Contact> getUserCardListByMobile(List<String> mobileList)
            throws MoMoException {
        List<Contact> result = new ArrayList<Contact>();
        JSONArray paramArray = new JSONArray();

        try {
            JSONArray jsonArray = new JSONArray();
            for (String mobile : mobileList) {
                jsonArray.put(mobile);
                // 每次批量请求上限100笔
                if (jsonArray.length() == 100) {
                    paramArray.put(jsonArray);
                    jsonArray = new JSONArray();
                }

            }
            if (jsonArray.length() > 0) {
                paramArray.put(jsonArray);
            }

            for (int i = 0; i < paramArray.length(); i++) {
                HttpTool http = new HttpTool(RequestUrl.BATCH_GET_CARD_LIST);
                http.DoPostArray(paramArray.optJSONArray(i));
                String responseContent = http.GetResponse();
                JSONObject jsonResponse = new JSONObject(responseContent);
                Iterator<?> keyIter = jsonResponse.keys();
                while (keyIter.hasNext()) {
                    String mobile = keyIter.next().toString();
                    JSONObject jsObj = jsonResponse.optJSONObject(mobile);
                    Contact contact = null;
                    contact = new ContactParser().parse(jsObj);
                    contact.setMainPhone(mobile);
                    result.add(contact);
                }

            }

        } catch (Exception ex) {
            throw new MoMoException(ex);
        }

        return result;
    }

    /**
     * 修改名片
     * 
     * @param contact
     * @return
     * @throws MoMoException
     */
    public static void updateUserCard(Contact contact) throws MoMoException {
        HttpTool http = new HttpTool(RequestUrl.UPDATE_USER_CARD_URL);

        JSONObject param = new JSONObject();
        try {
            param = new ContactParser().toJSONObject(contact);

            http.DoPost(param);
        } catch (Exception ex) {
            throw new MoMoException(ex);
        }
    }

    /**
     * 批量删除聊天记录
     * 
     * @param chats
     * @return
     * @throws MoMoException
     */
    public static void postIMDelete(Group<Chat> chats) throws MoMoException {
        HttpTool http = new HttpTool(RequestUrl.URL_DEL_MSG);

        JSONArray ids = new JSONArray();
        for (Chat chat : chats) {
            ids.put(chat.getId());
        }
        if (ids.length() > 0) {
            http.DoPostArray(ids);
        }
    }

    /**
     * 删除和某人的所有聊天记录
     * 
     * @param id
     * @return
     * @throws MoMoException
     */
    public static void postIMDeleteAll(UserList receiver) throws MoMoException {
        HttpTool http = new HttpTool(RequestUrl.URL_DEL_MSG_ALL + "?uid="
                + Utils.getIMGroupID(receiver));
        http.DoPost(new JSONObject());
    }

    /**
     * 发送私聊消息
     * 
     * @param chat
     * @return
     * @throws MoMoException
     */
    public static Chat postIMSendMessage(Chat chat) throws MoMoException {
        Chat result = null;
        HttpTool http = new HttpTool(RequestUrl.URL_SEND_MSG);
        try {
            JSONObject params = new ChatParser().toJSONObject(chat).getJSONObject("data");
            http.DoPost(params);
            String response = http.GetResponse();
            JSONObject json = new JSONObject(response);
            result = new ChatBaseParser().parse(json);
        } catch (JSONException ex) {
            throw new MoMoException(ex);
        }
        return result;
    }

    /**
     * 用短信来获取聊天消息
     * 
     * @param sms
     * @return
     * @throws MoMoException
     */
    public static Chat postIMMessageBySms(String sms) throws MoMoException {
        Chat result = null;
        HttpTool http = new HttpTool(RequestUrl.URL_GET_MSG);
        try {
            JSONObject params = new JSONObject();
            params.put("sms", sms);
            http.DoPost(params);
            String response = http.GetResponse();
            JSONObject json = new JSONObject(response);
            result = new ChatBaseParser().parse(json);
        } catch (JSONException ex) {
            throw new MoMoException(ex);
        }
        return result;
    }

    /**
     * 根据URL列表获取消息列表，如果不是mo短信则该位置返回null
     * 
     * @param sms
     * @return
     * @throws MoMoException
     */
    public static Group<Chat> postIMMessageBySmsBatch(JSONArray urls) throws MoMoException {
        Group<Chat> chats = new Group<Chat>();
        HttpTool http = new HttpTool(RequestUrl.URL_GET_MSG_BATCH);
        try {
            http.DoPostArray(urls);
            String response = http.GetResponse();
            JSONArray jArray = new JSONArray(response);
            for (int i = 0; i < jArray.length(); ++i) {
                Chat chat = null;
                try {
                    chat = new ChatBaseParser(true).parse(jArray.getJSONObject(i));
                } catch (JSONException e) {
                    // maybe not a momo message
                    chat = null;
                }
                chats.add(chat);
            }
        } catch (JSONException ex) {
            throw new MoMoException(ex);
        }
        return chats;
    }

    /**
     * 拉取未读的聊天记录
     * 
     * @return
     * @throws MoMoException
     */
    public static Group<Chat> getIMAll() throws MoMoException {
        Group<Chat> chats = null;
        HttpTool http = new HttpTool(RequestUrl.CONVERSATION_LIST);
        http.DoGet();
        JSONArray json;
        try {
            json = new JSONArray(http.GetResponse());
            chats = new GroupParser<Chat>(new ChatBaseParser(
                    true)).parse(json);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new MoMoException(e);
        }
        return chats;
    }

    /**
     * 获取聊天记录 TODO pagesize? 有可能在新版本API里去掉，暂时不处理，现在这种用page来取很容易重复数据
     * 
     * @param receiver
     * @return
     * @throws MoMoException
     */
    public static Group<Chat> getIMMore(UserList receiver) throws MoMoException {
        Group<Chat> chats = null;
        HttpTool http = new HttpTool(RequestUrl.URL_CONVERSATION_DETAIL_LIST_SINGLE
                + Utils.getIMGroupID(receiver) + RequestUrl.HTTP_RESULT_TYPE + "?pagesize=25");
        http.DoGet();
        try {
            JSONArray json = new JSONArray(http.GetResponse());
            chats = new GroupParser<Chat>(new ChatBaseParser(
                    true)).parse(json);
        } catch (JSONException ex) {
            throw new MoMoException(ex);
        }
        return chats;
    }

    /**
     * 发送名片请求（授权对方看自己名片）
     * 
     * @param who
     * @throws MoMoException
     */
    public static void postUserSendCard(String who) throws MoMoException {
        HttpTool http = new HttpTool(RequestUrl.USER_CARD_SEND);
        JSONObject params = new JSONObject();
        try {
            params.put("to", who);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new MoMoException(e);
        }
        http.DoPost(params);
    }

    
    /**
     * OAP帐号校验
     * 
     * @param sid
     * @return
     * @throws MoMoException
     */
    public static OAuthInfo checkOap(String sid) throws MoMoException {
        return OAuthHelper.checkOap(sid);
    }
    
    /**
     * 用户token登录 
     * 
     * @param sid
     * @return
     * @throws MoMoException
     */
    public static OAuthInfo tokenLogin(String sid, String uid, String oauthToken) throws MoMoException {
        return OAuthHelper.tokenLogin(sid, uid, oauthToken);
    }
    
    /**
     * 请求手机校验码
     * 
     * @param mobile
     * @return
     * @throws MoMoException
     */
    public static void applyVerifyCode(String consumeKey, String mobile) throws MoMoException {
        OAuthHelper.applyVerifyCode(consumeKey, mobile);
    }

    /**
     * 完成手机号绑定
     * 
     * @param zoneCode
     * @param mobile
     * @param verifyCode
     * @return OAuthInfo
     */
    public static OAuthInfo bindMobile(String consumerKey, String mobile, String verifyCode)
            throws MoMoException {
        return OAuthHelper.bindMobile(consumerKey, mobile, verifyCode);
    }
    
    /**
     * 获取长文本内容
     * @param id
     * @return
     * @throws MoMoException
     */
    public static String getFeedLongText(String id) throws MoMoException {
        HttpTool http = new HttpTool(RequestUrl.STATUSES_LONG_TEXT + "?statuses_id=" + id);
        int code = http.DoGet();
        if(code == 200) {
        	try {
        		return new JSONObject(http.GetResponse()).optString("text");
        	} catch (JSONException e) {
        		throw new MoMoException(e);
        	}
        } else {
        	throw new MoMoException(http.GetResponse());
        }
    }
}
