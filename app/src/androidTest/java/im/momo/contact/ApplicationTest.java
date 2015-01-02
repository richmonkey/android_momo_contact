package im.momo.contact;

import android.app.Application;
import android.test.ApplicationTestCase;

import im.momo.contact.model.ContactDB;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
    public void testContactDB() {

        ContactDB cdb = ContactDB.getInstance();
        cdb.setContentResolver(getSystemContext().getContentResolver());
        cdb.monitorConctat(getSystemContext());
        cdb.loadContacts();
    }
}