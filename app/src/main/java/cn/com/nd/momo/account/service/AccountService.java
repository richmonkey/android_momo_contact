
package cn.com.nd.momo.account.service;

import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import cn.com.nd.momo.account.authenticator.AccountAuthenticator;

public class AccountService extends Service {

    private AccountAuthenticator mAccountAuthenticator = null;

    @Override
    public IBinder onBind(Intent intent) {
        IBinder ibinder = null;
        if (intent != null && intent.getAction() != null
                && intent.getAction().equals(AccountManager.ACTION_AUTHENTICATOR_INTENT)) {
            ibinder = getAuthenticator().getIBinder();
        }
        return ibinder;
    }

    private AccountAuthenticator getAuthenticator() {
        if (mAccountAuthenticator == null) {
            mAccountAuthenticator = new AccountAuthenticator(this);
        }
        return mAccountAuthenticator;
    }

}
