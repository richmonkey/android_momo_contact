
package cn.com.nd.momo.account.authenticator;

import cn.com.nd.momo.manager.GlobalUserInfo;
import im.momo.contact.Token;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

public class AccountAuthenticator extends AbstractAccountAuthenticator {

    private String TAG = "AccountAuthenticator";

    private Context mContext = null;

    public AccountAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
            String authTokenType, String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {
        Log.d(TAG, "addAccount");
        Bundle result = null;
        try {
            String accountName = "";
            if (options != null && options.containsKey(AccountManager.KEY_ACCOUNT_NAME)) {
                accountName = options.getString(AccountManager.KEY_ACCOUNT_NAME);
            }
            if (accountType == null || accountType.length() < 1 || accountName == null
                    || accountName.length() < 1) {
                Log.e(TAG, "account is null or empty");
                return null;
            }
            Log.d(TAG, "accountType:" + accountType + " accountName:" + accountName);
            Account account = new Account(accountName, accountType);
            AccountManager am = AccountManager.get(mContext);

            if (am.addAccountExplicitly(account, Token.getInstance().accessToken, null)) {
                ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, false);
                result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                Log.d(TAG, AccountManager.KEY_ACCOUNT_NAME + ":" + accountName + " "
                        + AccountManager.KEY_ACCOUNT_TYPE + ":" + accountType);
                Log.d(TAG, "Bundle:" + result.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
            Bundle options) {
        Log.d(TAG, "confirmCredentials");
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        Log.d(TAG, "editProperties");
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle loginOptions) throws NetworkErrorException {
        Log.d(TAG, "getAuthToken");
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        Log.d(TAG, "getAuthTokenLabel");
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
            String[] features) throws NetworkErrorException {
        Log.d(TAG, "hasFeatures");
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle loginOptions) {
        Log.d(TAG, "updateCredentials");
        return null;
    }

    @Override
    public Bundle getAccountRemovalAllowed(AccountAuthenticatorResponse response, Account account)
            throws NetworkErrorException {
        Bundle result = super.getAccountRemovalAllowed(response, account);
        Log.e(TAG, "result:" + result.toString());
        if (result != null && result.containsKey(AccountManager.KEY_BOOLEAN_RESULT)
                && !result.containsKey(AccountManager.KEY_INTENT)) {
            final boolean removalAllowed = result.getBoolean(AccountManager.KEY_BOOLEAN_RESULT);

            if (removalAllowed) {
                // Do my removal stuff here
                Log.e(TAG, "removalAllowed:" + removalAllowed);
            }
        }
        return null;
        // return super.getAccountRemovalAllowed(response, account);
    }

}
