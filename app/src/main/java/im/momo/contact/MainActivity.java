package im.momo.contact;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.com.nd.momo.api.SyncContactHttpApi;
import cn.com.nd.momo.api.exception.MoMoException;
import cn.com.nd.momo.api.sync.ContactSyncManager;
import cn.com.nd.momo.api.sync.LocalContactsManager;
import cn.com.nd.momo.api.sync.MoMoContactsManager;
import cn.com.nd.momo.api.types.Contact;
import cn.com.nd.momo.api.types.MyAccount;
import cn.com.nd.momo.api.util.ConfigHelper;
import cn.com.nd.momo.api.util.Utils;
import cn.com.nd.momo.manager.GlobalUserInfo;
import im.momo.contact.activity.AccountsBindActivity;
import im.momo.contact.api.IMHttp;
import im.momo.contact.api.IMHttpFactory;
import im.momo.contact.api.body.PostAuthRefreshToken;
import im.momo.contact.model.ContactDB;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static android.os.SystemClock.uptimeMillis;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "contact";

    private static final int ACCOUNT_BIND_REQUEST = 100;


    public class SyncTask extends AsyncTask<Void, Void, Boolean> {
        private long serviceCount;

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
            boolean result = ContactSyncManager.getInstance().syncContacts();
            if (result) {
                try {
                    serviceCount = SyncContactHttpApi.getContactCount();
                } catch (MoMoException e) {
                    return false;
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            ImageButton imageButton = (ImageButton)MainActivity.this.findViewById(R.id.synImageBtn);
            imageButton.clearAnimation();
            mSyncTask = null;
            if (success) {
                Log.i(TAG, "sync success");
                serviceTextView.setText(String.valueOf(serviceCount));
                long c = LocalContactsManager.getInstance().getContactCountByAccount(Utils.getCurrentAccount());
                localTextView.setText(String.valueOf(c));
            } else {
                Log.i(TAG, "sync fail");
            }
        }

        @Override
        protected void onCancelled() {
            mSyncTask = null;
            ImageButton imageButton = (ImageButton)MainActivity.this.findViewById(R.id.synImageBtn);
            imageButton.clearAnimation();
        }
    }

    private SyncTask mSyncTask;

    private Handler handler;
    private ContentObserver contentObserver;

    private TextView localTextView;
    private TextView serviceTextView;

    private Timer refreshTokenTimer;
    private int refreshErrorCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localTextView = (TextView)findViewById(R.id.local);
        serviceTextView = (TextView)findViewById(R.id.service);

        ConfigHelper ch = ConfigHelper.getInstance();
        boolean import_setting = ch.loadBooleanKey(ConfigHelper.CONFIG_KEY_IMPORT_ACCOUNTS, false);

        //observe contact
        this.handler = new Handler() {

        };
        this.contentObserver = new ContentObserver(this.handler) {
            @Override
            public void onChange(boolean selfChange) {
                Log.i(TAG, "contact changed");
                if (mSyncTask == null) {
                    MainActivity.this.importContact();
                    long count = LocalContactsManager.getInstance().getContactCountByAccount(Utils.getCurrentAccount());
                    localTextView.setText(String.valueOf(count));
                }
            }
        };

        long count = LocalContactsManager.getInstance().getContactCountByAccount(Utils.getCurrentAccount());
        localTextView.setText(String.valueOf(count));

        ContentResolver resolver = this.getApplicationContext().getContentResolver();
        resolver.registerContentObserver(ContactsContract.Contacts.CONTENT_VCARD_URI, false, contentObserver);

        Token token = Token.getInstance();
        int now = getNow();
        if (now >= token.expireTimestamp + 60) {
            refreshToken();

            Handler h = new Handler();
            h.postAtTime(new Runnable() {
                @Override
                public void run() {
                    refreshServiceCount();
                }
            }, uptimeMillis()+30*1000);
        } else {
            int t = token.expireTimestamp - 60 - now;
            refreshTokenDelay(t);
            refreshServiceCount();
        }

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
        } else {
            importContact();
        }
    }

    private void refreshServiceCount() {

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            private long serviceCount;
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    serviceCount = SyncContactHttpApi.getContactCount();
                    Log.i(TAG, "get server contact count:" + serviceCount);
                } catch (MoMoException e) {
                    Log.i(TAG, "get server contact count fail");
                    return false;
                }
                return true;
            }
            @Override
            protected void onPostExecute(final Boolean success) {
                if (success) {
                    serviceTextView.setText(String.valueOf(serviceCount));
                }
            }
        };
        task.execute();
    }

    private void saveSelectedAccounts(ArrayList<Account> accounts) {
         if (accounts == null) {
            Log.i(TAG, "accounts is null");
            return;
        }
        ConfigHelper ch = ConfigHelper.getInstance();
        Set<String> s = new HashSet<>();
        //debug
        for (Account account: accounts) {
            Log.i(TAG, "name:" + account.name + " type:" + account.type);
            s.add(account.type + "||" + account.name);
        }
        ch.saveStringSetKey(ConfigHelper.CONFIG_KEY_SELECTED_ACCOUNTS, s);
    }
    private void saveImportedPhoneIDs(long[] ids) {
        if (ids == null) {
            return;
        }

        Set<String> idSet = new HashSet<>();
        for (int i = 0; i < ids.length; i++) {
            idSet.add(String.valueOf(ids[i]));
        }
        ConfigHelper.getInstance().saveStringSetKey(ConfigHelper.CONFIG_KEY_IMPORT_PHONE_IDS, idSet);
    }

    private void saveImportedPhoneIDs(ArrayList<Long> ids) {
        if (ids == null) {
            return;
        }

        Set<String> idSet = new HashSet<>();
        for (int i = 0; i < ids.size(); i++) {
            idSet.add(String.valueOf(ids.get(i)));
        }
        ConfigHelper.getInstance().saveStringSetKey(ConfigHelper.CONFIG_KEY_IMPORT_PHONE_IDS, idSet);
    }

    private ArrayList<Account> loadSelectedAccounts() {
        ConfigHelper ch = ConfigHelper.getInstance();
        Set<String> set = ch.loadStringSetKey(ConfigHelper.CONFIG_KEY_SELECTED_ACCOUNTS, new HashSet<String>());
        ArrayList<Account> selectedAccounts = new ArrayList<>();
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
        return selectedAccounts;
    }

    private Set<Long> loadImportedPhoneIDs() {
        ConfigHelper ch = ConfigHelper.getInstance();
        Set<String> idSet = ConfigHelper.getInstance().loadStringSetKey(ConfigHelper.CONFIG_KEY_IMPORT_PHONE_IDS, new HashSet<String>());
        HashSet<Long> importedPhoneIDs = new HashSet<>();
        for (String s : idSet) {
            importedPhoneIDs.add(Long.valueOf(s));
        }
        return importedPhoneIDs;
    }

    private void importContact() {
        List<Account> accounts;
        Set<Long> phoneIDs;
        accounts = loadSelectedAccounts();
        phoneIDs = loadImportedPhoneIDs();

        if (accounts == null || accounts.size() == 0) {
            return;
        }
        ArrayList<Long> newPhoneIDs = new ArrayList<Long>();
        List<Contact> mContactList = new ArrayList<Contact>();
        for (Account account : accounts) {
            //sim卡中新增的联系人不自动增加到momo账号中
            if (!account.type.equals("sim")) {
                List<Contact> contacts = LocalContactsManager.getInstance().getAllContactsListByAccount(account);
                for (Contact c : contacts) {
                    newPhoneIDs.add(c.getPhoneCid());
                }
                mContactList.addAll(contacts);
            }
        }
        saveImportedPhoneIDs(newPhoneIDs);
        if (mContactList.size() < 1) {
            return;
        }

        Iterator<Contact> ite = mContactList.iterator();
        while (ite.hasNext()) {
            Contact addContact = ite.next();
            if (phoneIDs.contains(addContact.getPhoneCid())) {
                ite.remove();
            }
        }
        Log.d(TAG, "import added contact size:" + mContactList.size());

        Account account = Utils.getCurrentAccount();
        if (Utils.isBindedAccountExist(account) && mContactList.size() > 0) {
            final int eachNum = 100;
            int count = mContactList.size();
            int times = count / eachNum + 1;

            for (int i = 0; i < times; i++) {
                List<Contact> list;
                int begin = i * eachNum;
                if (i == times - 1) {
                    list = mContactList.subList(begin, count);
                } else {
                    int end = (i + 1) * eachNum;
                    list = mContactList.subList(begin, end);
                }
                LocalContactsManager.getInstance().batchAddContacts(list, account);
            }
            Log.d(TAG, "import added contact count:" + mContactList.size());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "result:" + resultCode + " request:" + requestCode);

        if (requestCode == ACCOUNT_BIND_REQUEST && resultCode == RESULT_OK) {
            ConfigHelper ch = ConfigHelper.getInstance();
            ch.saveBooleanKey(ConfigHelper.CONFIG_KEY_IMPORT_ACCOUNTS, true);

            if (data == null) {
                return;
            }
            long[] ids = data.getLongArrayExtra("phone_ids");
            ArrayList<Account> accounts = data.getParcelableArrayListExtra("accounts");
            saveSelectedAccounts(accounts);
            saveImportedPhoneIDs(ids);
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

            ImageButton imageButton = (ImageButton)this.findViewById(R.id.synImageBtn);
            Animation ranim =  AnimationUtils.loadAnimation(this, R.anim.rotate);
            imageButton.startAnimation(ranim);
            mSyncTask.execute();
        } else {
            Log.i(TAG, "sync....");
        }
    }

    public void onSync(View view) {
        beginSync();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.import_account) {
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

                                ConfigHelper ch = ConfigHelper.getInstance();
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

    private void refreshToken() {
        PostAuthRefreshToken refreshToken = new PostAuthRefreshToken();
        refreshToken.refreshToken = Token.getInstance().refreshToken;
        IMHttp imHttp = IMHttpFactory.Singleton();
        imHttp.postAuthRefreshToken(refreshToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Token>() {
                    @Override
                    public void call(Token token) {
                        refreshErrorCount = 0;
                        onTokenRefreshed(token);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i(TAG, "refresh token error:" + throwable);
                        refreshErrorCount++;
                        refreshTokenDelay(60*refreshErrorCount);
                    }
                });
    }

    private void refreshTokenDelay(int t) {
        if (refreshTokenTimer != null) {
            refreshTokenTimer.suspend();
        }

        refreshTokenTimer = new Timer() {
            @Override
            protected void fire() {
                refreshToken();
            }
        };
        refreshTokenTimer.setTimer(uptimeMillis() + t*1000);
        refreshTokenTimer.resume();
    }

    protected void onTokenRefreshed(Token token) {
        int now = getNow();

        Token t = Token.getInstance();
        t.accessToken = token.accessToken;
        t.refreshToken = token.refreshToken;

        t.expireTimestamp = token.expireTimestamp + now;
        t.save();
        Log.i(TAG, "token refreshed");

        int ts = token.expireTimestamp - 60;
        if (ts <= 0) {
            Log.w(TAG, "expire timestamp:" + token.expireTimestamp);
            return;
        }
        refreshTokenDelay(ts);
    }

    public static int getNow() {
        Date date = new Date();
        long t = date.getTime();
        return (int)(t/1000);
    }

}
