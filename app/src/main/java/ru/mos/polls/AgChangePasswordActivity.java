package ru.mos.polls;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.changepassword.service.ChangePassword;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.util.Dialogs;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.helpers.TitleHelper;


public class AgChangePasswordActivity extends BaseActivity {
    private static final int MIN_PASSWORD_LENGTH = 5;

    private static final String PATH = "json/v0.2/auth/user/updatepassword";
    private EditText etOldPassword;
    private EditText etNewPassword;

    private View btnAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passwd);
        configureEdits();
        configInfoMessage();
        configAction();
        init();
        TitleHelper.setTitle(this, R.string.change_password);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void configAction() {
        btnAction = findViewById(R.id.btnAction);
        btnAction.setOnClickListener(v -> {
            final ProgressDialog dialog = Dialogs.showProgressDialog(AgChangePasswordActivity.this, R.string.elk_wait_changePassword);
            HandlerApiResponseSubscriber<String> handler = new HandlerApiResponseSubscriber<String>(AgChangePasswordActivity.this, null) {
                @Override
                protected void onResult(String result) {
                    Toast.makeText(AgChangePasswordActivity.this, R.string.elk_succeeded_change, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                }

                @Override
                public void onErrorListener() {
                    dialog.dismiss();
                }
            };
            disposables.add(AGApplication.api
                    .changePassword(new ChangePassword.Request(etOldPassword.getText().toString(), etNewPassword.getText().toString()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(handler));
        });
    }

    private void configureEdits() {
        etOldPassword = (EditText) findViewById(R.id.etOldPassword);
        etNewPassword = (EditText) findViewById(R.id.etNewPassword);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnAction.setEnabled(etOldPassword.getText().length() > 0 && etNewPassword.getText().length() >= MIN_PASSWORD_LENGTH);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };

        etOldPassword.addTextChangedListener(watcher);
        etNewPassword.addTextChangedListener(watcher);
    }

    private void configInfoMessage() {
        TextView tv = (TextView) findViewById(R.id.tvInfo);
        String text = tv.getText().toString();
        String wholeText = String.format(text, MIN_PASSWORD_LENGTH);
        tv.setText(wholeText);
    }
}
