package im.momo.contact;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Base64;
import android.util.Log;

import java.util.List;

import cn.com.nd.momo.api.sync.LocalContactsManager;
import cn.com.nd.momo.api.types.Avatar;
import cn.com.nd.momo.api.types.Contact;
import cn.com.nd.momo.api.util.Utils;


/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testAvatar() {
        Contact c = new Contact();
        c.setFirstName("test");
        String avatarB64 = "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";

        Utils.saveGlobleContext(this.getSystemContext());

        byte[] image = Base64.decode(avatarB64, Base64.DEFAULT);
        Log.i("momo", "origin:" + avatarB64.length() + " binary:" + image.length);
        Avatar avatar = new Avatar(-1, null, image);
        c.setAvatar(avatar);

        long phoneID = LocalContactsManager.getInstance().addContact(c, null);

        List<Contact> contacts = LocalContactsManager.getInstance().getAllContactsListWithoutAccount();

        for (Contact c1 : contacts) {
            if (c1.getPhoneCid() == phoneID) {
                avatar = c1.getAvatar();
                if (avatar != null) {
                    image = avatar.getMomoAvatarImage();
                    if (image != null && image.length > 0 && image.length < 128*1024) {
                        avatarB64 = Base64.encodeToString(image, Base64.DEFAULT);
                        Log.i("momo", "saved:" + avatarB64.length() + " binary:" + image.length);
                    }
                }
            }
        }
        LocalContactsManager.getInstance().deleteContact(phoneID);
    }
}