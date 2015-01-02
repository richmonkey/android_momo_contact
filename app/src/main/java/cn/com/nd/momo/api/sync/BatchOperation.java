
package cn.com.nd.momo.api.sync;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;

public class BatchOperation {

    private final ContentResolver mResolver;

    ArrayList<ContentProviderOperation> mOperations;

    public BatchOperation(Context context, ContentResolver resolver) {
        mResolver = resolver;
        mOperations = new ArrayList<ContentProviderOperation>();
    }

    public int size() {
        return mOperations.size();
    }

    public void add(ContentProviderOperation cpo) {
        mOperations.add(cpo);
    }

    public void add(int index, ContentProviderOperation cpo) {
        mOperations.add(index, cpo);
    }

    public void remove(int index) {
        mOperations.remove(index);
    }

    public ContentProviderResult[] execute() {
        if (mOperations.size() == 0) {
            return null;
        }
        ContentProviderResult[] result = null;
        try {
            result = mResolver.applyBatch(ContactsContract.AUTHORITY, mOperations);
        } catch (final OperationApplicationException e1) {
            if (null != e1)
                e1.printStackTrace();
        } catch (final RemoteException e2) {
            if (null != e2)
                e2.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mOperations.clear();
        }

        return result;

    }

    public void clear() {
        if (mOperations != null) {
            mOperations.clear();
        }
    }

}
