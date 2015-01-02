
package cn.com.nd.momo.api.util.gps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import cn.com.nd.momo.api.util.Log;

/**
 * <br>
 * Title:定位功能类 <br>
 * Description:根据 wifi 基站 gps 方式定位 <br>
 * Author:hexy <br>
 * Date:2011-3-31上午11:55:19 学习资料:
 */
public class LocManager {
    private TelephonyManager mTelephonyManager = null;

    private LocationManager mLocationManager = null;

    private static final String TAG = "LocManager";

    public Context mContext;

    private String mlanguage = "zh_cn";

    public String getLanguage() {
        return mlanguage;
    }

    public void setLanguage(String mlanguage) {
        this.mlanguage = mlanguage;
    }

    public static class Result {
        public final static String EXTRAL_LATITUDE = "LATITUDE";

        public final static String EXTRAL_LONGITUDE = "LONGITUDE";

        public double latitude;

        public double longitude;

        public String country;

        public String countryCode;

        public String region;

        public String county;

        public String city;

        public String street;

        public String streetNumber;

        public String postalCode;

        public String accessToken;

        public double accuracy;
    }

    public LocManager(Context context) {
        mContext = context;
        mLocationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        mTelephonyManager = (TelephonyManager)mContext.getSystemService(Service.TELEPHONY_SERVICE);
    }

    public Result getLocationPointRepeat(int count) {
        Result result = null;
        for (int i = 0; i < count; i++) {
            result = getLocationPoint();
            if (result != null)
                break;
        }
        return result;
    }

    /**
     * <br>
     * Description:只获取 latitude and longitude,速度很快! 适用于微博,啥啥啥定位计算距离
     * 在这里先使用GPS定位方式获取location信息,如果获取不到就使用WIFI/基站方式获取: <br>
     * Author:hexy <br>
     * Date:2011-3-31上午11:54:46
     * 
     * @param context
     * @return
     */
    public Result getLocationPoint() {
        Result result = null;

        // 获取经纬度

        result = null;
        do {
            // result = getLocation2();
            // if (result!=null) {
            // Log.i(TAG, "point: ("+result.latitude+","+result.longitude);
            // break;
            // }
            // result = getLocation2();
            // if (result != null) {
            // break;
            // }
            // 第二种 GSM基站定位, 速度最慢, 但是可以获取到详细信息,
            result = getLocationByCellidWithGoogleApi();
            if (isValidResult(result)) {
                break;
            }

            // 第一种 用google自带的获取经纬度的办法
            result = getLastKnown(LocationManager.NETWORK_PROVIDER);
            if (isValidResult(result)) {
                break;
            }

            result = getLastKnown(LocationManager.GPS_PROVIDER);
            if (isValidResult(result)) {
                break;
            }

            // 第三种 CDMA基站定位
            if (result == null) {
                CdmaCellLocation cdma = getCdmaCellLocation();
                if (cdma != null && cdma.getBaseStationId() != -1) {
                    result = new Result();
                    result.latitude = (double)cdma.getBaseStationLatitude() / 14400;
                    result.longitude = (double)cdma.getBaseStationLongitude() / 14400;
                    break;
                }
            }
            break;

        } while (false);

        // 会出现经纬度为0的情况? 待定
        if (result != null && ((int)(result.latitude) == 0 && (int)(result.longitude) == 0)) {
            return null;
        }
        // Log.i(TAG, "Location ("+result.latitude+":"+result.longitude+")");
        return result;
    }

    private Result getLastKnown(String provider) {
        Result result = null;
        Location location = mLocationManager.getLastKnownLocation(provider);
        if (location != null) {
            result = new Result();
            result.latitude = location.getLatitude();
            result.longitude = location.getLongitude();
        }
        return result;
    }

    private boolean isValidResult(Result result) {
        return (result != null && result.latitude != 0.0 && result.longitude != 0.0);
    }

    // LocationMgr locMgr= new LocationMgr();
    // private void init2(){
    // locMgr.init(mContext);
    // }
    // public Result getLocation2(){
    // locMgr.init(mContext);
    // Location loc = locMgr.getlocation();
    // if (loc!=null) {
    // Result result = new Result();
    // result.latitude = loc.getLatitude();
    // result.longitude = loc.getLongitude();
    // return result;
    // }
    // return null;
    // }

    /**
     * <br>
     * Description:获取详细的路径信息 <br>
     * Author:hexy <br>
     * Date:2011-5-18下午12:46:25
     * 
     * @return
     */
    public Result getLocationDetial() {
        Result result = getLocationPoint();
        if (result == null) {
            return null;
        }

        if (result.city != null && result.city.length() > 0) {
            return result;
        }

        return getDetailByLatitudeAndLongitude(result.latitude, result.longitude);
    }

    /**
     * <br>
     * Description:获取cdma location <br>
     * Author:hexy <br>
     * Date:2011-5-18上午10:47:24
     * 
     * @return
     */
    private CdmaCellLocation getCdmaCellLocation() {
        int type = mTelephonyManager.getPhoneType();

        CdmaCellLocation cdma = null;
        if (type == TelephonyManager.PHONE_TYPE_CDMA) {
            cdma = ((CdmaCellLocation)mTelephonyManager.getCellLocation());
        }

        return cdma;
    }

    /**
     * 废弃 不使用CELLID 定位了 <br>
     * Description:获取gsmlocaton <br>
     * Author:hexy <br>
     * Date:2011-5-18上午10:47:37
     * 
     * @return
     */
    private GsmCellLocation getGsmCellLocation() {
        int type = mTelephonyManager.getPhoneType();

        GsmCellLocation gsm = null;
        if (type == TelephonyManager.PHONE_TYPE_GSM) {
            gsm = (GsmCellLocation)mTelephonyManager.getCellLocation();
        }

        return gsm;
    }

    // { "version": "1.1.0",
    // "host": "maps.google.com",
    // "request_address": true,
    // "address_language": "zh_cn",
    // "location": { "latitude": 51.0,
    // "longitude": -0.1 }}
    private Result getDetailByLatitudeAndLongitude(double latitude, double longitude) {

        // set params to json
        JSONObject locationJsonInfo = new JSONObject();
        try {

            locationJsonInfo.put("version", "1.1.0");
            locationJsonInfo.put("host", "maps.google.com");
            locationJsonInfo.put("address_language", mlanguage);
            locationJsonInfo.put("request_address", true);

            JSONObject location = new JSONObject();
            location.put("latitude", latitude);
            location.put("longitude", longitude);

            locationJsonInfo.put("location", location);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        // doPost
        HttpResult httpResult = doPost(locationJsonInfo, "http://www.google.com/loc/json");
        String response = httpResult.response;
        Log.i(TAG, response + "");
        Result result = null;
        if (httpResult.ret == 200) {
            result = new Result();
            try {
                // decode json to result
                JSONObject responseJson = new JSONObject(response);
                JSONObject locationJson = responseJson.getJSONObject("location");
                JSONObject addressJson = locationJson.getJSONObject("address");

                if (responseJson.has("access_token"))
                    result.accessToken = responseJson.getString("access_token");
                if (locationJson.has("latitude"))
                    result.latitude = locationJson.getDouble("latitude");
                if (locationJson.has("longitude"))
                    result.longitude = locationJson.getDouble("longitude");
                if (locationJson.has("accuracy"))
                    result.accuracy = locationJson.getDouble("accuracy");

                if (addressJson.has("country"))
                    result.country = addressJson.getString("country");
                if (addressJson.has("country_code"))
                    result.countryCode = addressJson.getString("country_code");
                if (addressJson.has("region"))
                    result.region = addressJson.getString("region");
                if (addressJson.has("county"))
                    result.county = addressJson.getString("county");
                if (addressJson.has("city"))
                    result.city = addressJson.getString("city");
                if (addressJson.has("street"))
                    result.street = addressJson.getString("street");
                if (addressJson.has("street_number"))
                    result.streetNumber = addressJson.getString("street_number");
                if (addressJson.has("postal_code"))
                    result.postalCode = addressJson.getString("postal_code");

            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        }

        return result;
    }

    public class WifiInfoManager {

        WifiManager wm;

        public WifiInfoManager() {
        }

        public ArrayList<WifiInfo> getWifiInfo(Context context) {
            ArrayList<WifiInfo> wifi = new ArrayList<WifiInfo>();
            wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = new WifiInfo();
            info.mac = wm.getConnectionInfo().getBSSID();
            info.ssid = wm.getConnectionInfo().getSSID();
            wifi.add(info);
            return wifi;
        }

        /* WifiInfo.java 封装了wifi的信息 */
        public class WifiInfo {

            public String mac;

            public String ssid;

            public WifiInfo() {
            }
        }
    }

    /**
     * <br>
     * Description:利用基站定位获取 中文详细路径信息, 不支持 电信的cdma <br>
     * Author:hexy <br>
     * Date:2011-3-31上午11:49:54
     * 
     * @param context
     * @return
     */
    private Result getLocationByCellidWithGoogleApi() {
        Result result = null;

        GsmCellLocation gsmLocation = getGsmCellLocation();

        if (gsmLocation == null)
            return null;

        // get params
        int cellId = 0;
        int locationAreaCode = 0;

        String networkOperator = mTelephonyManager.getNetworkOperator();
        int mobileCountryCode = -1;
        int mobileNetworkCode = -1;
        if (null != networkOperator && networkOperator.length() >= 5) {
            mobileCountryCode = Integer.valueOf(networkOperator.substring(0, 3)).intValue();
            mobileNetworkCode = Integer.valueOf(networkOperator.substring(3, 5)).intValue();
        }

        cellId = gsmLocation.getCid();
        locationAreaCode = gsmLocation.getLac();

        // set params to json
        JSONObject locationJsonInfo = new JSONObject();
        try {

            locationJsonInfo.put("version", "1.1.0");
            locationJsonInfo.put("host", "maps.google.com");
            locationJsonInfo.put("address_language", mlanguage);
            locationJsonInfo.put("request_address", false);

            JSONObject cellTowerJsonInfo = new JSONObject();
            cellTowerJsonInfo.put("cell_id", cellId);
            cellTowerJsonInfo.put("location_area_code", locationAreaCode);

            if (mobileCountryCode != -1) {
                cellTowerJsonInfo.put("mobile_country_code", mobileCountryCode);
            }
            if (mobileNetworkCode != -1) {
                cellTowerJsonInfo.put("mobile_network_code", mobileNetworkCode);
            }

            JSONArray cellTowersJsonArray = new JSONArray();
            cellTowersJsonArray.put(cellTowerJsonInfo);

            List<NeighboringCellInfo> neighCell = null;
            neighCell = mTelephonyManager.getNeighboringCellInfo();
            for (int i = 0; i < neighCell.size(); i++) {
                NeighboringCellInfo cellinfo = neighCell.get(i);
                JSONObject data = new JSONObject();
                data.put("cell_id", cellinfo.getCid());
                data.put("location_area_code", cellId);
                data.put("mobile_country_code", mobileCountryCode);// 460
                data.put("mobile_network_code", mobileNetworkCode);// 0
                data.put("signal_strength", cellinfo.getRssi());
                cellTowersJsonArray.put(data);
            }
            locationJsonInfo.put("cell_towers", cellTowersJsonArray);

            //
            // WifiInfoManager wm = new WifiInfoManager();
            // ArrayList<cn.com.shawn.utils.LocManager.WifiInfoManager.WifiInfo>
            // wifi = wm.getWifiInfo(mContext);
            // JSONArray wifiarray = new JSONArray();
            // if (wifi!=null&&wifi.size()>0) {
            // for(int i=0; i<wifi.size(); i++) {
            // if (wifi.get(i).mac != null) {
            // JSONObject data = new JSONObject();
            // data.put("mac_address", wifi.get(i).mac);
            // data.put("ssid", wifi.get(i).ssid);
            // // data.put("signal_strength", 8);
            // data.put("age", 0);
            //
            // wifiarray.put(data);
            // }
            // }
            //
            // locationJsonInfo.put("wifi_towers", wifiarray);
            // }
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        // doPost
        HttpResult httpResult = doPost(locationJsonInfo, "http://www.google.com/loc/json");
        String response = httpResult.response;

        if (httpResult.ret == 200) {
            result = new Result();
            try {
                // decode json to result

                JSONObject responseJson = new JSONObject(response);
                JSONObject locationJson = responseJson.getJSONObject("location");

                JSONObject addressJson = new JSONObject();
                if (locationJson.has("address"))
                    addressJson = locationJson.getJSONObject("address");

                if (responseJson.has("access_token"))
                    result.accessToken = responseJson.getString("access_token");
                if (locationJson.has("latitude"))
                    result.latitude = locationJson.getDouble("latitude");
                if (locationJson.has("longitude"))
                    result.longitude = locationJson.getDouble("longitude");
                if (locationJson.has("accuracy"))
                    result.accuracy = locationJson.getDouble("accuracy");

                if (addressJson.has("country"))
                    result.country = addressJson.getString("country");
                if (addressJson.has("country_code"))
                    result.countryCode = addressJson.getString("country_code");
                if (addressJson.has("region"))
                    result.region = addressJson.getString("region");
                if (addressJson.has("county"))
                    result.county = addressJson.getString("county");
                if (addressJson.has("city"))
                    result.city = addressJson.getString("city");
                if (addressJson.has("street"))
                    result.street = addressJson.getString("street");
                if (addressJson.has("street_number"))
                    result.streetNumber = addressJson.getString("street_number");
                if (addressJson.has("postal_code"))
                    result.postalCode = addressJson.getString("postal_code");

            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        }

        return result;
    }

    /**
     * <br>
     * Description:get two point's distance <br>
     * Author:hexy <br>
     * Date:2011-3-31上午11:08:51
     * 
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    private static final double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        // Log.i(TAG, "getDistance:"+ s +
        // " lat1:"+lat1+" lng1:"+lng1+" lat2:"+lat2+" lng2:"+lng2);
        // s = Math.round(s * 10000) / 10000;
        return s;
    }

    public static String formatDisTance(int distance) {
        String str = "";
        String strFormat = "";
        if (distance < 1000)
            str = distance + "米";
        else {
            strFormat = String.valueOf(((float)distance) / 1000);
            int index = strFormat.indexOf(".");
            if (index > 0 && index < strFormat.length() - 1) {
                strFormat = strFormat.substring(0, index + 2);
            }

            str = strFormat + "公里";
        }
        // else if (distance<10000){
        // strFormat = String.valueOf(((float)distance)/1000);
        // int index = strFormat.indexOf(".");
        // if (index>0&&index<strFormat.length()-1){
        // strFormat = strFormat.substring(0,index+2);
        // }
        //
        // str = strFormat +"千米";
        // }
        // else {
        // strFormat = String.valueOf(((float)distance)/10000);
        // int index = strFormat.indexOf(".");
        // if (index>0&&index<strFormat.length()-1){
        // strFormat = strFormat.substring(0,index+2);
        // }
        // str = (float)(((float)distance)/10000)+"万米";
        // }

        return str;
    }

    /**
     * <br>
     * Title:get response <br>
     * Description:get detail infomation from google api <br>
     * Author:hexy <br>
     * Date:2011-3-31上午11:53:07
     */
    private class HttpResult {
        public int ret;

        public String response;
    }

    private HttpResult doPost(JSONObject c, String strURL) {
        BufferedReader in = null;
        HttpResult result = new HttpResult();
        result.ret = 0;

        try {
            // make a POST client
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(strURL);

            // get json string and pass to entigy
            HttpEntity entity;
            StringEntity s = new StringEntity(c.toString());
            s.setContentType(new BasicHeader(HTTP.ASCII, "application/json"));
            entity = s;
            request.setEntity(entity);

            // POST and get response code
            HttpResponse response = client.execute(request);
            result.ret = response.getStatusLine().getStatusCode();

            // get response string
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            result.response = sb.toString();

            // record received byte count
            // HttpTransportListener.GetInstance().AddRcvCount(m_strResponse.getBytes("UTF-8").length);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    Log.e(TAG, ioe.toString());
                }
            }
        }

        return result;
    }

    /**
     * eg json google api { "version": "1.1.0", "host": "maps.google.com",
     * "home_mobile_country_code": 310, "home_mobile_network_code": 410,
     * "radio_type": "gsm", "carrier": "Vodafone", "request_address": true,
     * "address_language": "en_GB", "location": { "latitude": 51.0, "longitude":
     * -0.1 }, "cell_towers": [ { "cell_id": "42", "location_area_code": 415,
     * "mobile_country_code": 310, "mobile_network_code": 410, "age": 0,
     * "signal_strength": -60, "timing_advance": 5555 }, { "cell_id": "88",
     * "location_area_code": 415, "mobile_country_code": 310,
     * "mobile_network_code": 580, "age": 0, "signal_strength": -70,
     * "timing_advance": 7777 } ], "wifi_towers": [ { "mac_address":
     * "01-23-45-67-89-ab", "signal_strength": 8, "age": 0 }, { "mac_address":
     * "01-23-45-67-89-ac", "signal_strength": 4, "age": 0 } ] }
     */
    /**
     * { "location": { "latitude": 51, "longitude": -0.1, "address": {
     * "country": "（大不列颠）联合王国", "country_code": "GB", "region": "西萨塞克斯郡",
     * "county": "米德萨塞克斯", "city": "海沃兹希思", "street": "Fairford Close",
     * "street_number": "12", "postal_code": "RH16 3" } }, "access_token":
     * "2:PE3h3VCCmxGFWHcx:t3E9VBazyijiWu5A" } <uses-permission
     * android:name="android.permission.INTERNET" /> <uses-permission
     * android:name="android.permission.ACCESS_FINE_LOCATION" />
     * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"
     * />
     */

    /* CellIDInfo.java 封装了cellid的信息 */
    // public class CellIDInfo {
    //
    // public int cellId;
    // public String mobileCountryCode;
    // public String mobileNetworkCode;
    // public int locationAreaCode;
    // public String radioType;
    //
    // public CellIDInfo(){}
    // }
    //
    // public class CellIDInfoManager {
    // private TelephonyManager manager;
    // private PhoneStateListener listener;
    // private GsmCellLocation gsm;
    // private CdmaCellLocation cdma;
    // int lac;
    // String current_ci,mcc, mnc;
    //
    // public ArrayList<CellIDInfo> getCellIDInfo(Context context){
    //
    // listener = new PhoneStateListener();
    // mTelephonyManager.listen(listener, 0);
    // ArrayList<CellIDInfo> CellID = new ArrayList<CellIDInfo>();
    // CellIDInfo currentCell = new CellIDInfo();
    //
    // int type = mTelephonyManager.getNetworkType();
    //
    // if (type == TelephonyManager.NETWORK_TYPE_GPRS || type
    // ==TelephonyManager.NETWORK_TYPE_EDGE
    // || type ==TelephonyManager.NETWORK_TYPE_HSDPA) {
    // gsm = ((GsmCellLocation) mTelephonyManager.getCellLocation());
    // if (gsm == null) return null;
    // lac = gsm.getLac();
    // mcc = manager.getNetworkOperator().substring(0, 3);
    // mnc = manager.getNetworkOperator().substring(3, 5);
    //
    // currentCell.cellId = gsm.getCid();
    // currentCell.mobileCountryCode = mcc;
    // currentCell.mobileNetworkCode = mnc;
    // currentCell.locationAreaCode = lac;
    // currentCell.radioType = "gsm";
    // CellID.add(currentCell);
    //
    // return CellID;
    //
    // } else if (type == TelephonyManager.NETWORK_TYPE_CDMA || type
    // ==TelephonyManager.NETWORK_TYPE_1xRTT) {
    // cdma = ((CdmaCellLocation) manager.getCellLocation());
    // if (cdma == null) return null;
    //
    // if ("460".equals(manager.getSimOperator().substring(0, 3)))
    // return null;
    // }
    // return null;
    // }
    //
    // }

}
