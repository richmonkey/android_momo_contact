
package cn.com.nd.momo.api.oauth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.telephony.TelephonyManager;
import cn.com.nd.momo.api.AppInfo;
import cn.com.nd.momo.api.MoMoHttpApi;
import cn.com.nd.momo.api.RequestUrl;
import cn.com.nd.momo.api.exception.MoMoException;
import cn.com.nd.momo.api.http.HttpTool;
import cn.com.nd.momo.api.types.Contact;
import cn.com.nd.momo.api.types.OAuthInfo;
import cn.com.nd.momo.api.types.User;
import cn.com.nd.momo.api.util.Utils;
import cn.com.nd.momo.manager.GlobalUserInfo;

public class OAuthHelper {
    public static final String CONSUMER_KEY = "9e5d178d8e8029e5e05d2a3cd035c63d04ddb4c21";

    private static final String CONSUMER_SECRET = "c7c2aecad62ecc82d51d638e27c9af7d";

    public OAuthHelper() {
        // do nothing
    }

    /**
     * 生成OAuth认证串
     * 
     * @param url
     * @param httpMethod
     * @return
     */
    public static String getAuthHeader(String url, String httpMethod) {
        OAuth a = new OAuth(CONSUMER_KEY, CONSUMER_SECRET);
        String strToken = a.generateAuthorizationHeader(httpMethod,
                url,
                null,
                // OAUTH_NONCE,
                RndString(5),
                String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000),
                null,
                AppInfo.getOAuthToken());

        return strToken;
    }

    /**
     * 登录验证
     * 
     * @param zoneCode
     * @param mobile
     * @param pwd
     * @param imei
     * @return OAuthInfo
     * @throws MoMoException
     */
    public static OAuthInfo login(String zoneCode, String mobile, String pwd) throws MoMoException {
        OAuthInfo result = null;

        HttpTool http = new HttpTool(RequestUrl.LOGIN);
        JSONObject param = new JSONObject();
        try {
            param.put("consumer_key", OAuthHelper.CONSUMER_KEY);
            param.put("zone_code", zoneCode);
            param.put("mobile", mobile);
            param.put("password", pwd);
            param.put("client_id", MoMoHttpApi.APP_ID);
            param.put("device_id", getDeviceIMEI());
            param.put("phone_model", android.os.Build.MODEL);
            param.put("os", android.os.Build.VERSION.RELEASE);

            http.DoPost(param, null);
            String responseContent = http.GetResponse();
            JSONObject jsonResponse = new JSONObject(responseContent);

            result = new OAuthInfo();
            result.setNeedResetPassword(jsonResponse.optInt("reset_password"));
            result.setUid(jsonResponse.optString("uid"));
            result.setUserName(jsonResponse.optString("name"));
            result.setAvatarName(jsonResponse.optString("avatar"));
            result.setFinalKey(jsonResponse.optString("oauth_token"));
            result.setFinalSecret(jsonResponse.optString("oauth_token_secret"));
            result.setQueueName(jsonResponse.optString("qname"));
            result.setStatus(jsonResponse.optString("status"));
            result.setZoneCode(zoneCode);
            result.setMobile(mobile);

            AppInfo.setOAuthInfo(result);
        } catch (Exception ex) {
            throw new MoMoException(ex);
        }

        return result;
    }

    /**
     * 通过带验证信息的url完成登录
     * 
     * @param url
     * @return OAuthInfo
     * @throws MoMoException
     */
    public static OAuthInfo loginByUrl(String url) throws MoMoException {
        OAuthInfo result = null;

        HttpTool http = new HttpTool(RequestUrl.LOGIN_BY_URL);
        JSONObject param = new JSONObject();
        try {
            param.put("consumer_key", OAuthHelper.CONSUMER_KEY);
            param.put("url", url);
            param.put("secret_key", CONSUMER_KEY);
            param.put("client_id", MoMoHttpApi.APP_ID);
            param.put("phone_model", android.os.Build.MODEL);
            param.put("os", android.os.Build.VERSION.RELEASE);

            http.DoPost(param, null);
            String responseContent = http.GetResponse();
            JSONObject jsonResponse = new JSONObject(responseContent);

            result = new OAuthInfo();
            result.setNeedResetPassword(jsonResponse.optInt("reset_password"));
            result.setUid(jsonResponse.optString("uid"));
            result.setUserName(jsonResponse.optString("name"));
            result.setAvatarName(jsonResponse.optString("avatar"));
            result.setFinalKey(jsonResponse.optString("oauth_token"));
            result.setFinalSecret(jsonResponse.optString("oauth_token_secret"));
            result.setQueueName(jsonResponse.optString("qname"));
            result.setStatus(jsonResponse.optString("status"));
            result.setZoneCode(jsonResponse.optString("zone_code"));
            result.setMobile(jsonResponse.optString("mobile"));

            AppInfo.setOAuthInfo(result);
        } catch (Exception ex) {
            throw new MoMoException(ex);
        }

        return result;
    }

    /**
     * 发送注册信息
     * 
     * @param mobile
     * @return > 0 表示注册成功
     * @throws MoMoException
     */
    public static void register(String zoneCode, String mobile) throws MoMoException {
        HttpTool http = new HttpTool(RequestUrl.REGIST_SEND_VERIFY_CODE_URL);
        JSONObject param = new JSONObject();

        try {
            param.put("mobile", mobile);
            param.put("zone_code", zoneCode);

            param.put("device_id", getDeviceIMEI());
            param.put("source", MoMoHttpApi.APP_ID);
            param.put("phone_model", android.os.Build.MODEL);
            param.put("os", android.os.Build.VERSION.RELEASE);

            http.DoPost(param, null);
        } catch (Exception ex) {
            throw new MoMoException(ex);
        }
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
        OAuthInfo result = null;

        HttpTool http = new HttpTool(RequestUrl.REGIST_VERIFY_URL);
        JSONObject param = new JSONObject();
        try {
            param.put("consumer_key", OAuthHelper.CONSUMER_KEY);
            param.put("zone_code", zoneCode);
            param.put("mobile", mobile);
            param.put("verifycode", verifyCode);

            http.DoPost(param, null);
            String responseContent = http.GetResponse();
            JSONObject jsonResponse = new JSONObject(responseContent);

            result = new OAuthInfo();
            result.setUid(jsonResponse.optString("uid"));
            result.setFinalKey(jsonResponse.optString("oauth_token"));
            result.setFinalSecret(jsonResponse.optString("oauth_token_secret"));
            result.setQueueName(jsonResponse.optString("qname"));
            result.setStatus(jsonResponse.optString("user_status"));
            result.setZoneCode(zoneCode);
            result.setMobile(mobile);

            AppInfo.setOAuthInfo(result);
        } catch (Exception ex) {
            throw new MoMoException(ex);
        }

        return result;
    }

    //
    // public String getAuthHeader(String url, String httpMethod) {
    // OAuthToken t = new OAuthToken(GlobalUserInfo.getOAuthKey(),
    // GlobalUserInfo.getOAuthSecret());
    //
    // OAuth a = new OAuth(CONSUMER_KEY, CONSUMER_SECRET);
    // String strToken = a.generateAuthorizationHeader(httpMethod,
    // url,
    // null,
    // // OAUTH_NONCE,
    // RndString(5),
    // String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000),
    // null,
    // t);
    //
    // return strToken;
    // }

    private static String RndString(int strLength) {

        // 因1与l不容易分清楚，所以去除
        String strChar = "2,3,4,5,6,7,8,9,a,b,c,d,e,f,g,h,j,k,m,n,p,q,r,s,t,u,v,w,x,y,z"
                + ",A,B,C,D,E,F,G,H,J,K,L,M,N,P,Q,R,S,T,U,W,X,Y,Z";

        String[] aryChar = strChar.split(",");

        String strRandom = "";
        Random Rnd;
        Rnd = new Random();

        // 生成随机字符串
        for (int i = 0; i < strLength; i++) {
            strRandom += aryChar[Rnd.nextInt(aryChar.length)];
        }

        return strRandom;
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
        HttpTool http = new HttpTool(RequestUrl.FORGET_PASSWORD);
        JSONObject param = new JSONObject();

        try {
            param.put("mobile", mobile);
            param.put("zone_code", zoneCode);

            http.DoPost(param, null);
        } catch (Exception ex) {
            throw new MoMoException(ex);
        }
    }

    /**
     * 重置密码
     * 
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @return > 0 表示重置成功
     * @throws MoMoException
     */
    public static void resetPwd(String oldPwd, String newPwd) throws MoMoException {
        HttpTool http = new HttpTool(RequestUrl.RESET_PASSWORD_NEW);
        JSONObject param = new JSONObject();

        try {
            param.put("old_password", oldPwd);
            param.put("new_password", newPwd);
            param.put("block", 0);

            http.DoPost(param);
        } catch (Exception ex) {
            throw new MoMoException(ex);
        }
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
        OAuthInfo result = null;

        HttpTool http = new HttpTool(RequestUrl.RESET_PASSWORD_WITHOUT_OLD);
        JSONObject param = new JSONObject();
        try {
            param.put("consumer_key", OAuthHelper.CONSUMER_KEY);
            param.put("new_password", newPwd);
            param.put("client_id", MoMoHttpApi.APP_ID);
            param.put("device_id", getDeviceIMEI());
            param.put("phone_model", android.os.Build.MODEL);
            param.put("os", android.os.Build.VERSION.RELEASE);

            http.DoPost(param);
            String responseContent = http.GetResponse();
            JSONObject jsonResponse = new JSONObject(responseContent);

            result = new OAuthInfo();
            result.setUid(jsonResponse.optString("uid"));
            result.setUserName(jsonResponse.optString("name"));
            result.setAvatarName(jsonResponse.optString("avatar"));
            result.setFinalKey(jsonResponse.optString("oauth_token"));
            result.setFinalSecret(jsonResponse.optString("oauth_token_secret"));
            result.setQueueName(jsonResponse.optString("qname"));
            result.setStatus(jsonResponse.optString("status"));
            result.setZoneCode(zoneCode);
            result.setMobile(mobile);

            AppInfo.setOAuthInfo(result);
        } catch (Exception ex) {
            throw new MoMoException(ex);
        }

        return result;
    }

    private static String getDeviceIMEI() {
        TelephonyManager tm = (TelephonyManager)Utils.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        return imei;
    }

    /**
     * 获取可用短信条数
     * 
     * @return 可用短信条数
     * @throws MoMoException
     */
    public static int getSmsCount() throws MoMoException {
        int result = 0;
        HttpTool http = new HttpTool(RequestUrl.GET_USER_SMS_COUNT);

        try {
            http.DoGet();
            String responseContent = http.GetResponse();
            JSONObject jsonResponse = new JSONObject(responseContent);
            result = jsonResponse.optInt("count");
        } catch (Exception ex) {
            throw new MoMoException(ex);
        }

        return result;
    }

    /**
     * 根据手机号码批量获取用户ID
     * 
     * @param userMap
     * @return
     * @throws MoMoException
     */
    public static List<User> getIDList(Map<String, User> userMap) throws MoMoException {
        List<User> result = new ArrayList<User>();

        try {
            JSONArray paramArray = new JSONArray();
            JSONArray jsonArray = new JSONArray();
            for (String mobile : userMap.keySet()) {
                if (userMap.get(mobile).getId().equals(Contact.DEFAULT_USER_ID_NOT_EXIST + "")) {
                    User user = userMap.get(mobile);
                    JSONObject object = new JSONObject();
                    object.put("name", user.getName());
                    object.put("mobile", mobile);
                    jsonArray.put(object);
                    // 每次批量请求上限100笔
                    if (jsonArray.length() == 100) {
                        paramArray.put(jsonArray);
                        jsonArray = new JSONArray();
                    }
                }
            }
            if (jsonArray.length() > 0) {
                paramArray.put(jsonArray);
            }

            for (int i = 0; i < paramArray.length(); i++) {
                HttpTool http = new HttpTool(RequestUrl.GET_CREATE_AT_LIST);
                http.DoPostArray(paramArray.optJSONArray(i));
                String responseContent = http.GetResponse();
                JSONArray jsonArrayResponse = new JSONArray(responseContent);
                for (int j = 0; j < jsonArrayResponse.length(); j++) {
                    JSONObject jsonResponse = jsonArrayResponse.optJSONObject(j);

                    String mobile = jsonResponse.optString("mobile");
                    long userId = jsonResponse.optLong("user_id");
                    String error = jsonResponse.optString("error");
                    if (error.contains("" + MoMoException.MOBILE_TYPE_INVALID)) {
                        userId = Contact.DEFAULT_USER_ID_INVALID;
                    } else if (mobile.length() < 1) {
                        continue;
                    }

                    User user = new User();
                    user.setId(userId + "");
                    user.setMobile(mobile);
                    result.add(user);
                }
            }

        } catch (Exception ex) {
            throw new MoMoException(ex);
        }

        return result;
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
        HttpTool http = new HttpTool(RequestUrl.WEIBO_BIND_URL);

        JSONObject param = new JSONObject();
        try {
            param.put("username", accountName);
            param.put("password", accountPassword);
            param.put("site", site);
            param.put("follow", followMoMo ? 1 : 0);

            http.DoPost(param);

        } catch (Exception ex) {
            throw new MoMoException(ex);
        }
    }
    
    /**
     * OAP帐号校验
     * 
     * @param sid
     * @return
     * @throws MoMoException
     */
    public static OAuthInfo checkOap(String sid) throws MoMoException {
        OAuthInfo result = null;
        HttpTool http = new HttpTool(RequestUrl.CHECK_OAP);
        JSONObject param = new JSONObject();

        try {
            param.put("consumer_key", OAuthHelper.CONSUMER_KEY);
            param.put("sid", sid);
            param.put("appid", MoMoHttpApi.APP_ID);
            param.put("client_id", MoMoHttpApi.APP_ID);
            param.put("apply_token", 1);

            http.DoPost(param, null);

            String responseContent = http.GetResponse();
            JSONObject jsonResponse = new JSONObject(responseContent);

            result = new OAuthInfo();
            result.setUid(jsonResponse.optString("uid"));
            result.setBindedMobile(jsonResponse.optInt("bind_mobile") == 1);
            result.setFinalKey(jsonResponse.optString("oauth_token"));
            result.setFinalSecret(jsonResponse.optString("oauth_token_secret"));

            AppInfo.setOAuthInfo(result);
        } catch (Exception ex) {
            throw new MoMoException(ex);
        }

        return result;
    }

    /**
     * 用户token登录
     * 
     * @param sid
     * @return
     * @throws MoMoException
     */
    public static OAuthInfo tokenLogin(String sid, String uid, String oauthToken)
            throws MoMoException {
        OAuthInfo result = null;
        HttpTool http = new HttpTool(RequestUrl.TOKEN_LOGIN);
        JSONObject param = new JSONObject();

        try {
            param.put("uid", uid);
            param.put("token", oauthToken);
            //param.put("key", "47a9c621d64955c99e7251e72c4d3d2a050a61522");
            param.put("key", "sds234s343sjjhsdfk*ss&123^&3dfd");

            http.DoPost(param, null);

            String responseContent = http.GetResponse();
            JSONObject jsonResponse = new JSONObject(responseContent);

            result = new OAuthInfo();
            result.setUid(jsonResponse.optString("uid"));
            result.setUserName(jsonResponse.optString("name"));
            result.setAvatarName(jsonResponse.optString("avatar"));
            result.setZoneCode(jsonResponse.optString("zone_code"));
            result.setMobile(jsonResponse.optString("mobile"));
            result.setQueueName(jsonResponse.optString("qname"));
            result.setStatus(jsonResponse.optString("status"));
        } catch (Exception ex) {
            throw new MoMoException(ex);
        }

        return result;
    }

    /**
     * 请求手机校验码
     * 
     * @param mobile
     * @return
     * @throws MoMoException
     */
    public static void applyVerifyCode(String consumeKey, String mobile) throws MoMoException {
        HttpTool http = new HttpTool(RequestUrl.APPLY_VERIFYCODE);
        JSONObject param = new JSONObject();

        try {
            param.put("mobile", mobile);
            param.put("consumer_key", consumeKey);

            http.DoPost(param);
        } catch (Exception ex) {
            throw new MoMoException(ex);
        }
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
        OAuthInfo result = null;

        HttpTool http = new HttpTool(RequestUrl.BIND_MOBILE);
        JSONObject param = new JSONObject();
        try {
            param.put("consumer_key", consumerKey);
            param.put("mobile", mobile);
            param.put("verifycode", verifyCode);
            param.put("client_id", MoMoHttpApi.APP_ID);

            // 设备信息
            JSONObject param_device = new JSONObject();
            param_device.put("device_id", getDeviceIMEI());
            param_device.put("phone_model", android.os.Build.MODEL);
            param_device.put("os", android.os.Build.VERSION.RELEASE);
            param.put("device", param_device);

            http.DoPost(param);
            String responseContent = http.GetResponse();
            JSONObject jsonResponse = new JSONObject(responseContent);

            result = new OAuthInfo();
            result.setUid(jsonResponse.optString("uid"));
            result.setUserName(jsonResponse.optString("name"));
            result.setAvatarName(jsonResponse.optString("avatar"));
            result.setFinalKey(jsonResponse.optString("oauth_token"));
            result.setFinalSecret(jsonResponse.optString("oauth_token_secret"));
            result.setQueueName(jsonResponse.optString("qname"));
            result.setStatus(GlobalUserInfo.STATUS_VERIFY_USER + "");
            result.setMobile(mobile);

            AppInfo.setOAuthInfo(result);
        } catch (Exception ex) {
            throw new MoMoException(ex);
        }

        return result;
    }

}
