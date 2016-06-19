package im.momo.contact.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import im.momo.contact.MainActivity;
import im.momo.contact.R;
import im.momo.contact.api.IMHttp;
import im.momo.contact.api.IMHttpFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class PasswordActivity extends ActionBarActivity {
    static final String TAG = "contact";

    EditText password;
    EditText passwordRepeat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.inject(this);

        password = (EditText)findViewById(R.id.password);
        passwordRepeat = (EditText)findViewById(R.id.password_repeat);
    }

    @OnClick(R.id.btn_setting)
    public void onSetting() {
        String password1 = password.getText().toString();
        String password2 = passwordRepeat.getText().toString();

        if (password1.length() < 6 || password1.length() > 16) {
            Toast.makeText(this, "密码输入错误", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password2.length() < 6 || password2.length() > 16) {
            Toast.makeText(this, "密码输入错误", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password1.equals(password2)) {
            Toast.makeText(this, "密码输入错误", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog dialog = ProgressDialog.show(this, null, "setting...");

        IMHttp.Password p = new IMHttp.Password();
        p.password = password1;

        IMHttp imHttp = IMHttpFactory.Singleton();

        imHttp.setPassword(p)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object token) {
                        dialog.dismiss();
                        Intent intent = new Intent(PasswordActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i(TAG, "set password fail");
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "设置密码失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @OnClick(R.id.btn_ignore)
    public void onIgnore() {
        Intent intent = new Intent(PasswordActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
