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

import com.android.volley2.Response;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import ru.mos.elk.Dialogs;
import ru.mos.elk.netframework.request.StringRequest;
import ru.mos.elk.netframework.utils.StandartErrorListener;
import ru.mos.polls.api.API;
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
//                (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void configAction() {
        btnAction = findViewById(ru.mos.elk.R.id.btnAction);
        btnAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = Dialogs.showProgressDialog(AgChangePasswordActivity.this, R.string.elk_wait_changePassword);
                Response.Listener<String> listener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AgChangePasswordActivity.this, R.string.elk_succeeded_change, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                    }
                };
                addRequest(new StringRequest(API.getURL(PATH),
                        getQueryParams(), listener, new StandartErrorListener(AgChangePasswordActivity.this, R.string.elk_cant_changePassword, dialog)), dialog);
            }
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

    public JSONObject getQueryParams() {
        JSONObject params = new JSONObject();
        try {
            params.put("old_password", etOldPassword.getText().toString());
            params.put("new_password", etNewPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return params;
    }

    private void configInfoMessage() {
        TextView tv = (TextView) findViewById(R.id.tvInfo);
        String text = tv.getText().toString();
        String wholeText = String.format(text, MIN_PASSWORD_LENGTH);
        tv.setText(wholeText);
    }
}
