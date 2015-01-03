package im.momo.contact;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.com.nd.momo.api.sync.ContactSyncManager;
import cn.com.nd.momo.api.sync.LocalContactsManager;
import cn.com.nd.momo.api.sync.MoMoContactsManager;
import cn.com.nd.momo.api.types.Contact;
import cn.com.nd.momo.api.util.ConfigHelper;
import cn.com.nd.momo.api.util.Utils;
import cn.com.nd.momo.manager.GlobalUserInfo;
import im.momo.contact.activity.AccountsBindActivity;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "contact";

    private static final int ACCOUNT_BIND_REQUEST = 100;

    private ArrayList<Account> selectedAccounts;


    public class SyncTask extends AsyncTask<Void, Void, Boolean> {
        SyncTask() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i(TAG, "contact sync...");
            Account account = Utils.getCurrentAccount();
            if (!Utils.isBindedAccountExist(account)) {
                cn.com.nd.momo.api.util.Log.w(TAG, "momo account is't exist, please add momo account first!!!");
                return false;
            }
            ContactSyncManager.getInstance().syncContacts();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSyncTask = null;
            if (success) {
                Log.i(TAG, "sync success");
            } else {
                Log.i(TAG, "sync fail");
            }
        }

        @Override
        protected void onCancelled() {
            mSyncTask = null;
        }
    }

    private SyncTask mSyncTask;

    private Handler handler;
    private ContentObserver contentObserver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConfigHelper ch = ConfigHelper.getInstance(this.getApplicationContext());
        boolean import_setting = ch.loadBooleanKey(ConfigHelper.CONFIG_KEY_IMPORT_ACCOUNTS, false);

        Set<String> set = ch.loadStringSetKey(ConfigHelper.CONFIG_KEY_SELECTED_ACCOUNTS, new HashSet<String>());

        selectedAccounts = new ArrayList<>();
        for (String s : set) {
            String[] a = s.split("\\|\\|", 2);
            if (a == null || a.length != 2) {
                Log.w(TAG, "invalid account:" + s);
                continue;
            }
            Log.i(TAG, "account type:" + a[0] + " name:" + a[1]);
            String name = a[1];
            String type = a[0];
            selectedAccounts.add(new Account(name, type));
        }

        //observe contact
        this.handler = new Handler() {

        };
        this.contentObserver = new ContentObserver(this.handler) {
            @Override
            public void onChange(boolean selfChange) {
                Log.i(TAG, "contact changed");
                if (mSyncTask == null) {
                    //todo 更新本地联系人个数
                }
            }
        };
        ContentResolver resolver = this.getApplicationContext().getContentResolver();
        resolver.registerContentObserver(ContactsContract.Contacts.CONTENT_VCARD_URI, false, contentObserver);

        boolean created = ch.loadBooleanKey(ConfigHelper.CONFIG_KEY_MOMO_ACCOUNT_CREATED, false);
        Account account = Utils.getCurrentAccount();
        if (!Utils.isBindedAccountExist(account)) {
            if (created) {
                Toast.makeText(getApplicationContext(), "momo account disappear, what's your rom?",
                        Toast.LENGTH_SHORT).show();
            }
            Log.i(TAG, "clear momo contact db");
            MoMoContactsManager.getInstance().deleteAllContacts();
            Log.i(TAG, "add account type:" + account.type + " name:" + account.name);
            addAccount(this, account);
            return;
        }

        if (!import_setting) {
            Intent intent = new Intent(MainActivity.this, AccountsBindActivity.class);
            startActivityForResult(intent, ACCOUNT_BIND_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "result:" + resultCode + " request:" + requestCode);

        if (requestCode == ACCOUNT_BIND_REQUEST && resultCode == RESULT_OK) {
            ConfigHelper ch = ConfigHelper.getInstance(this.getApplicationContext());
            ch.saveBooleanKey(ConfigHelper.CONFIG_KEY_IMPORT_ACCOUNTS, true);

            if (data == null) {
                return;
            }
            ArrayList<Account> accounts = data.getParcelableArrayListExtra("accounts");
            if (accounts == null) {
                Log.i(TAG, "accounts is null");
                return;
            }

            Set<String> s = new HashSet<>();

            //debug
            for (Account account: accounts) {
                Log.i(TAG, "name:" + account.name + " type:" + account.type);
                s.add(account.type + "||" + account.name);
            }

            ch.saveStringSetKey(ConfigHelper.CONFIG_KEY_SELECTED_ACCOUNTS, s);

            this.selectedAccounts = accounts;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void beginSync() {
        Account account = Utils.getCurrentAccount();
        if (!Utils.isBindedAccountExist(account)) {
            Log.i(TAG, "momo account is't exist, can't begin sync contact");
            return;
        }
        if (mSyncTask == null) {
            mSyncTask = new SyncTask();
            mSyncTask.execute();
        } else {
            Log.i(TAG, "sync....");
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sync) {
            beginSync();
            return true;
        } else if (id == R.id.action_setting) {
            Intent intent = new Intent(MainActivity.this, AccountsBindActivity.class);
            startActivityForResult(intent, ACCOUNT_BIND_REQUEST);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Bundle addAccount(Activity activity, Account account) {
        AccountManager accountManager = AccountManager.get(this);
        String accountType = GlobalUserInfo.MOMO_ACCOUNT_TYPE;
        String accountName = GlobalUserInfo.getPhoneNumber();
        Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
        String[] features = new String[] {
                GlobalUserInfo.getPhoneNumber()
        };
        accountManager.addAccount(accountType, Token.getInstance().accessToken, features, bundle,
                activity,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> amfuture) {
                        try {
                            Bundle b = amfuture.getResult();
                            if (b != null) {
                                String name = b.getString(AccountManager.KEY_ACCOUNT_NAME);
                                String type = b.getString(AccountManager.KEY_ACCOUNT_TYPE);
                                Log.i(TAG, "add account success type:" + type + " name:" + name);

                                ConfigHelper ch = ConfigHelper.getInstance(MainActivity.this.getApplicationContext());
                                ch.saveBooleanKey(ConfigHelper.CONFIG_KEY_MOMO_ACCOUNT_CREATED, true);

                                boolean import_setting = ch.loadBooleanKey(ConfigHelper.CONFIG_KEY_IMPORT_ACCOUNTS, false);
                                if (!import_setting) {
                                    Intent intent = new Intent(MainActivity.this, AccountsBindActivity.class);
                                    startActivityForResult(intent, ACCOUNT_BIND_REQUEST);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, null);
        return bundle;
    }

}
