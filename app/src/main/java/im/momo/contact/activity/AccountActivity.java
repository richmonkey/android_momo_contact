package im.momo.contact.activity;

import android.os.Bundle;

import im.momo.contact.tools.event.BusProvider;
import im.momo.contact.tools.event.LoginSuccessEvent;
import com.squareup.otto.Subscribe;

/**
 * Created by tsung on 12/2/14.
 */
public class AccountActivity extends BaseActivity {
    final Object messageHandler = new Object() {
        @Subscribe
        public void onLoginSuccess(LoginSuccessEvent event) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(messageHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(messageHandler);
    }
}
