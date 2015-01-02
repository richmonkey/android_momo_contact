
package cn.com.nd.momo.api.types;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import cn.com.nd.momo.api.util.Log;
import cn.com.nd.momo.api.util.PinyinHelper;

public class Country {
    public final static String[][] EMPATY_ARRAY = new String[0][];

    private int id = 1;

    private String[][] cnNamePinyin = EMPATY_ARRAY; // 中文国家名称拼音

    private String enName = ""; // 英文国家名称

    private String cnName = ""; // 中文国家名称

    private String iso = ""; //

    private String zoneCode = ""; // 国家地区码

    public Country() {

    }

    public Country(String countryJson) {
        if (!"".equals(countryJson)) {
            try {
                JSONObject json = new JSONObject(countryJson);
                this.cnNamePinyin = PinyinHelper.convertChineseToPinyinArray(cnName);
                this.enName = json.optString("en");
                this.cnName = json.optString("cn");
                this.iso = json.optString("iso");
                this.zoneCode = json.optString("ic");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Log.e("Country", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public Country(int id, String[][] cnNamePinyin, String enName, String cnName,
            String iso, String zoneCode) {
        this.id = id;
        this.cnNamePinyin = cnNamePinyin;
        this.enName = enName;
        this.cnName = cnName;
        this.iso = iso;
        this.zoneCode = zoneCode;
    }

    public static ArrayList<Country> getCountryList(Context context) {
        ArrayList<Country> countryList = new ArrayList<Country>();
        String countryJson = "";
        try {
            InputStream in = context.getResources().getAssets().open(
                    "countries.json");
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            countryJson = EncodingUtils.getString(buffer, "UTF-8");
        } catch (IOException e) {
            Log.e("Country", e.getMessage());
            e.printStackTrace();
            countryJson = "";
        }

        if (!"".equals(countryJson)) {
            try {
                JSONArray jsonArray = new JSONArray(countryJson);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    Country country = new Country();
                    JSONObject json = jsonArray.getJSONObject(i);
                    String cnName = json.opt("cn").toString();
                    country.setId(i + 1);
                    country.setEnName(json.opt("en").toString());
                    country.setCnName(cnName);
                    country.setIso(json.opt("iso").toString());
                    country.setZoneCode(json.opt("ic").toString());
                    country.setCnNamePinyin(PinyinHelper.convertChineseToPinyinArray(cnName));
                    countryList.add(country);
                }

            } catch (JSONException e) {
                Log.e("Country", e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                Log.e("Country", e.getMessage());
                e.printStackTrace();
            }
        }
        return countryList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[][] getCnNamePinyin() {
        return cnNamePinyin;
    }

    public void setCnNamePinyin(String[][] namePinyin) {
        this.cnNamePinyin = namePinyin;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }
}
