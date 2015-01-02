package im.momo.contact;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import cn.com.nd.momo.api.sync.ContactSyncManager;
import cn.com.nd.momo.api.sync.LocalContactsManager;
import cn.com.nd.momo.api.sync.MoMoContactsManager;
import cn.com.nd.momo.api.types.Contact;
import im.momo.contact.model.ContactDB;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "contact";


    public class SyncTask extends AsyncTask<Void, Void, Boolean> {
        SyncTask() {

        }
        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i(TAG, "contact sync...");
            ContactSyncManager.getInstance().syncContacts();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSyncTask = null;
            if (success) {
                //debug
                List<Contact> contacts = LocalContactsManager.getInstance().getAllContactsList();
                for (Contact c : contacts){
                    Log.i(TAG, "name:" + c.getFormatName());
                }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //debug
        List<Contact> contacts = LocalContactsManager.getInstance().getAllContactsList();
        for (Contact c : contacts){
            Log.i(TAG, "name:" + c.getFormatName());
        }

        mSyncTask = new SyncTask();
        mSyncTask.execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
