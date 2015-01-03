package im.momo.contact.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.nd.momo.manager.GlobalUserInfo;
import im.momo.contact.R;
import im.momo.contact.MainActivity;
import im.momo.contact.Token;
import im.momo.contact.api.IMHttp;
import im.momo.contact.api.IMHttpFactory;
import im.momo.contact.api.body.PostAuthToken;
import im.momo.contact.api.types.User;
import im.momo.contact.model.UserDB;
import im.momo.contact.tools.event.BusProvider;
import im.momo.contact.tools.event.LoginSuccessEvent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class VerifyActivity extends AccountActivity implements TextView.OnEditorActionListener {
    static final String TAG = VerifyActivity.class.getSimpleName();
    static String EXTRA_PHONE = "im.phone";

    public static Intent newIntent(Context context, String phone) {
        Intent intent = new Intent();
        intent.setClass(context, VerifyActivity.class);
        intent.putExtra(EXTRA_PHONE, phone);
        return intent;
    }

    String phone;
    @InjectView(R.id.verify_code)
    EditText verifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        ButterKnife.inject(this);
        phone = getIntent().getStringExtra(EXTRA_PHONE);

        verifyCode.setOnEditorActionListener(this);
    }

    @OnClick(R.id.btn_login)
    void onLogin() {
        final String code = verifyCode.getText().toString();
        if (phone.length() == 0 || code.length() == 0) {
            return;
        }

        final ProgressDialog dialog = ProgressDialog.show(this, null, "Request...");

        PostAuthToken postAuthToken = new PostAuthToken();
        postAuthToken.code = code;
        postAuthToken.zone = "86";
        postAuthToken.number = phone;
        IMHttp imHttp = IMHttpFactory.Singleton();
        imHttp.postAuthToken(postAuthToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Token>() {
                    @Override
                    public void call(Token token) {
                        dialog.dismiss();

                        Token t = Token.getInstance();
                        t.accessToken = token.accessToken;
                        t.refreshToken = token.refreshToken;
                        t.expireTimestamp = token.expireTimestamp;
                        t.uid = token.uid;

                        t.save();

                        User u = new User();
                        u.uid = token.uid;
                        u.number = phone;
                        u.zone = "86";
                        UserDB.getInstance().addUser(u);
                        //中国区不加区号 和momo保持兼容
                        GlobalUserInfo.setPhoneNumber(phone);

                        Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        BusProvider.getInstance().post(new LoginSuccessEvent());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i(TAG, "auth token fail");
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                    }
                });
        Log.i(TAG, "code:" + code);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_verify, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_GO) {
            onLogin();
        }
        return false;
    }
}
