
package cn.com.nd.momo.api.types;

import android.accounts.Account;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Description:自定义联系人帐号类
 * @author chenjp
 */
public class MyAccount extends Account {
    private final static String TAG = "account";

    public static String getSystemProperty(String propName){
        String line = null;
        BufferedReader input = null;
        try
        {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        }
        catch (IOException ex)
        {
            Log.e(TAG, "Unable to read sysprop " + propName, ex);
            return null;
        }
        finally
        {
            if(input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                    Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    public static boolean isH2OS() {
        String version = getSystemProperty("ro.rom.version");
        if (version != null) {
            return version.indexOf("H2OS") != -1;
        } else {
            return false;
        }
    }

    public static String ACCOUNT_MOBILE_NAME = "mobile";
    public static String ACCOUNT_MOBILE_TYPE = "mobile";

    static {
        if (isH2OS()) {
            ACCOUNT_MOBILE_NAME = "PHONE";
            ACCOUNT_MOBILE_TYPE = "com.android.localphone";
            Log.i(TAG, "oneplus h2os");
        }
    }


    public static String ALL_ACCOUNT_TYPE = "cn.com.nd.allAccount";

    public static MyAccount MOBILE_ACCOUNT = new MyAccount(ACCOUNT_MOBILE_NAME, ACCOUNT_MOBILE_TYPE);

    public static MyAccount HTC_MOBILE_ACCOUNT = new MyAccount("pcsc", "com.htc.android.pcsc");

    public static MyAccount MOTO_MOBILE_ACCOUNT = new MyAccount("vnd.sec.contact.phone",
            "vnd.sec.contact.phone");

    public static MyAccount SANSUMG_MOBILE_ACCOUNT = new MyAccount("Contacts",
            "com.motorola.blur.contacts.UNCONNECTED_ACCOUNT");

    public static MyAccount COOLPAD_MOBILE_ACCOUNT = new MyAccount("local-contacts",
            "com.coolpad.contacts");

    public static MyAccount MEIZU_MOBILE_ACCOUNT = new MyAccount("DeviceOnly",
            "DeviceOnly");

    public static MyAccount SONYERICSSON_MOBILE_ACCOUNT = new MyAccount("Phone contacts",
            "com.sonyericsson.localcontacts");

    public static MyAccount HUAWEI_MOBILE_ACCOUNT = new MyAccount("Phone",
            "com.android.huawei.phone");

    public static MyAccount[] KNOWN_SUPPORT_VENDOR_LIST = new MyAccount[] {
            HTC_MOBILE_ACCOUNT, MOTO_MOBILE_ACCOUNT, SANSUMG_MOBILE_ACCOUNT,
            COOLPAD_MOBILE_ACCOUNT, MEIZU_MOBILE_ACCOUNT, SONYERICSSON_MOBILE_ACCOUNT,
            HUAWEI_MOBILE_ACCOUNT
    };

    public MyAccount(String name, String type) {
        super(name, type);
    }

    private int count = 0;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isEqual(Account account) {
        if (account == null) {
            return false;
        }
        if (this.name.equals(account.name) && this.type.equals(account.type)) {
            return true;
        }
        return false;
    }

}
