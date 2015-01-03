
package im.momo.contact.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import cn.com.nd.momo.api.sync.ContactSyncManager;
import cn.com.nd.momo.api.sync.LocalContactsManager;
import cn.com.nd.momo.api.types.Contact;
import im.momo.contact.R;
import cn.com.nd.momo.api.types.MyAccount;
import cn.com.nd.momo.api.util.ConfigHelper;
import cn.com.nd.momo.api.util.Log;
import cn.com.nd.momo.api.util.Utils;

/**
 * 导入同步帐号联系人Activity
 * 
 * @author jiaolei
 */
public class AccountsBindActivity extends Activity implements OnClickListener {
    private String TAG = "AccountsBindActivity";

    private static final int MSG_GET_ADD_CONTACT_COMPLETE = 1;

    private ListView mAccountsListView = null;

    private List<MyAccount> mArrayAccount = new ArrayList<MyAccount>();

    private LayoutInflater mInflater = null;

    private ProgressDialog m_progressDlg = null;

    private AccountsAdapter mAccountsAdapter = new AccountsAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.accounts_bind);

        mInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        mAccountsListView = (ListView)findViewById(R.id.account_list);
        Log.d(TAG, "get account list begin");
        getAccounts();
        mAccountsAdapter.setData(mArrayAccount);
        mAccountsListView.setAdapter(mAccountsAdapter);
        mAccountsListView.setOnItemClickListener(new OnAccountListItemClick());

        // button ok
        Button btnOK = (Button)findViewById(R.id.btn_accounts_bind_ok);
        btnOK.setOnClickListener(this);
        Button btnCancel = (Button)findViewById(R.id.btn_accounts_bind_cancel);
        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setClickable(true);
        btnCancel.setOnClickListener(this);

    }

    private class OnAccountListItemClick implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mAccountsAdapter.setChecked(arg2);
        }
    }

    private void getAccounts() {
        mArrayAccount = Utils.getAccounts();
        Account currentAccount = Utils.getCurrentAccount();
        Iterator<MyAccount> ite = mArrayAccount.iterator();
        while (ite.hasNext()) {
            MyAccount account = ite.next();
            if (currentAccount.name.equals(account.name)
                    && currentAccount.type.equals(account.type)) {
                ite.remove();
                continue;
            }
        }
        MyAccount simAccount = new MyAccount("sim卡", "sim");
        int count = LocalContactsManager.getInstance().getSimContactsCount();
        simAccount.setCount(count);
        mArrayAccount.add(simAccount);
        count = LocalContactsManager.getInstance().getContactCountByAccount(currentAccount);
        MyAccount syncAccount = new MyAccount(currentAccount.name, currentAccount.type);
        syncAccount.setCount(count);
        mArrayAccount.add(0, syncAccount);
        Log.d(TAG, "get account list end");

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_ADD_CONTACT_COMPLETE:
                    Log.d(TAG, "add contact list end");
                    if (m_progressDlg.isShowing()) {
                        m_progressDlg.dismiss();
                    }

                    finish();
                    break;
            }
        }
    };


    private class ImportTask extends AsyncTask<Void, Void, Boolean> {
        private ArrayList<MyAccount> selectedAccounts;

        ImportTask(ArrayList<MyAccount> accounts) {
            this.selectedAccounts = accounts;
        }

        private void importContact(List<MyAccount> accounts) {
            if (accounts == null || accounts.size() == 0) {
                return;
            }
            List<Contact> mContactList = new ArrayList<Contact>();
            for (Account account : accounts) {
                if (account.type.equals("sim")) {
                    mContactList.addAll(LocalContactsManager.getInstance().getSimContacts());
                } else {
                    mContactList.addAll(LocalContactsManager.getInstance().getAllContactsListByAccount(account));
                }
            }
            android.util.Log.d(TAG, "before crc, add contact size:" + mContactList.size());
            if (mContactList.size() < 1) {
                android.util.Log.d(TAG, "import account contacts complete");
            }
            Account account = Utils.getCurrentAccount();
            List<Contact> syncAccountContacts = LocalContactsManager.getInstance().getAllContactsListByAccount(account);

            android.util.Log.d(TAG, "syc account contact size:" + syncAccountContacts.size());
            List<Long> contactCrcList = new ArrayList<Long>();
            for (Contact contact : syncAccountContacts) {
                contactCrcList.add(contact.generateProperCRC());
            }
            // crc去重,不比较头像，是否收藏，分组
            Iterator<Contact> ite = mContactList.iterator();
            while (ite.hasNext()) {
                Contact addContact = ite.next();
                long crc = addContact.generateProperCRC();
                if (contactCrcList.contains(crc)) {
                    ite.remove();
                }
            }
            android.util.Log.d(TAG, "after crc, add contact size:" + mContactList.size());

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

                    android.util.Log.d(TAG, "account name:" + account.name + " account type:"
                            + account.type);
                    android.util.Log.d(TAG, "list size:" + list.size());

                    LocalContactsManager.getInstance().batchAddContacts(list, account);
                }
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            android.util.Log.i(TAG, "contact sync...");
            importContact(selectedAccounts);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mImportTask = null;

            Log.d(TAG, "import contact list end");
            if (m_progressDlg.isShowing()) {
                m_progressDlg.dismiss();
            }

            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("accounts", selectedAccounts);
            setResult(RESULT_OK, intent);

            finish();
        }

        @Override
        protected void onCancelled() {
            mImportTask = null;
            Log.e(TAG, "import task cancelled");
        }
    }

    private ImportTask mImportTask;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accounts_bind_ok:
                ConfigHelper.getInstance(getApplicationContext()).saveKey(
                        ConfigHelper.CONFIG_KEY_SYNC_MODE, ConfigHelper.SYNC_MODE_TWO_WAY);
                ConfigHelper.getInstance(getApplicationContext()).commit();

                if (mImportTask != null) {
                    Log.i(TAG, "importing...");
                    return;
                }

                ArrayList<MyAccount> selectedAccounts = (ArrayList<MyAccount>)mAccountsAdapter.getSelectAccountList();

                mImportTask = new ImportTask(selectedAccounts);
                mImportTask.execute();

                m_progressDlg = ProgressDialog.show(this, "", "正在导入选中帐号联系人...");
                m_progressDlg.setCancelable(false);
                break;
            case R.id.btn_accounts_bind_cancel:
                Log.i(TAG, "cancel bind");
                setResult(RESULT_CANCELED);
                finish();
                break;
            default:
                break;
        }
    }

    class AccountsAdapter extends BaseAdapter {
        private List<Boolean> mCheckedList = new ArrayList<Boolean>();

        private List<MyAccount> mAccountList = new ArrayList<MyAccount>();

        private HashMap<Integer, View> mMap = new HashMap<Integer, View>();

        public void setData(List<MyAccount> myAccountList) {
            if (myAccountList != null) {
                mAccountList.clear();
                mAccountList = new ArrayList<MyAccount>(Arrays.asList(new MyAccount[myAccountList
                        .size()]));
                Collections.copy(mAccountList, myAccountList);
                mCheckedList.clear();
                for (int i = 0; i < mAccountList.size(); i++) {
                    mCheckedList.add(true);
                }
            }
        }

        public List<MyAccount> getSelectAccountList() {
            List<MyAccount> selectedAccounts = new ArrayList<MyAccount>();
            for (int i = 0; i < mCheckedList.size(); i++) {
                if (mCheckedList.get(i)) {
                    MyAccount account = (MyAccount)getItem(i);
                    Account currentAccount = Utils.getCurrentAccount();
                    if(currentAccount == null) {
                        if (account != null && !account.isEqual(MyAccount.MOBILE_ACCOUNT)) {
                            Log.d(TAG, "import account name:" + account.name);
                            selectedAccounts.add(account);
                        }
                    } else {
                        if (account != null && !account.isEqual(currentAccount)) {
                            Log.d(TAG, "import account name:" + account.name);
                            selectedAccounts.add(account);
                        } 
                    }
                }
            }
            return selectedAccounts;
        }

        public void setChecked(int position) {
            MyAccount myAccount = mAccountList.get(position);
            Account currentAccount = Utils.getCurrentAccount();
            if(currentAccount == null) {
                if (!myAccount.isEqual(MyAccount.MOBILE_ACCOUNT)) {
                    boolean isChecked = mCheckedList.get(position);
                    mCheckedList.set(position, !isChecked);
                    notifyDataSetChanged();
                }
            } else {
                if (!myAccount.isEqual(currentAccount)) {
                    boolean isChecked = mCheckedList.get(position);
                    mCheckedList.set(position, !isChecked);
                    notifyDataSetChanged();
                }
            }
        }

        @Override
        public int getCount() {
            return mAccountList.size();
        }

        @Override
        public Object getItem(int position) {
            int count = getCount();
            if (count == 0 || position < 0 || position > count - 1) {
                return null;
            }
            return mAccountList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            ViewHolder holder = null;
            if (mMap.get(position) == null) {
                view = mInflater.inflate(R.layout.account_list_item, null);
                holder = new ViewHolder();
                holder.selected = (CheckBox)view.findViewById(R.id.account_select);
                holder.name = (TextView)view.findViewById(R.id.account_name);
                mMap.put(position, view);
                view.setTag(holder);
            } else {
                view = mMap.get(position);
                holder = (ViewHolder)view.getTag();
            }
            holder.selected.setClickable(false);
            holder.selected.setFocusable(false);
            holder.name.setClickable(false);
            holder.selected.setChecked(mCheckedList.get(position));
            MyAccount account = mAccountList.get(position);
            Account currentAccount = Utils.getCurrentAccount();
            if(currentAccount == null) {
                if (account.isEqual(MyAccount.MOBILE_ACCOUNT)) {
                    holder.name.setText("同步帐号:" + getString(R.string.txt_phone) + "("
                            + account.getCount() + ")");
                    holder.selected.setVisibility(View.INVISIBLE);
                } else {
                    holder.name.setText(account.name + "(" + account.getCount() + ")");
                    holder.selected.setVisibility(View.VISIBLE);
                }
            } else {
                if (account.isEqual(currentAccount)) {
                    String syncAccountName = currentAccount.name;
                    if (MyAccount.MOBILE_ACCOUNT.isEqual(currentAccount)) {
                        syncAccountName = getString(R.string.txt_phone);
                    }
                    holder.name.setText("同步帐号:" + syncAccountName + "(" + account.getCount() + ")");
                    holder.selected.setVisibility(View.INVISIBLE);
                } else {
                    holder.name.setText(account.name + "(" + account.getCount() + ")");
                    holder.selected.setVisibility(View.VISIBLE);
                }
            }

            return view;
        }
    }

    static class ViewHolder {
        CheckBox selected;

        TextView name;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (m_progressDlg != null && m_progressDlg.isShowing()) {
            m_progressDlg.dismiss();
        }
    }

}
