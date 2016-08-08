package ru.mos.elk.auth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley2.Response.Listener;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.Dialogs;
import ru.mos.elk.R;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.request.StringRequest;
import ru.mos.elk.netframework.utils.StandartErrorListener;

public class ChangePasswordActivity extends BaseActivity {
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
    }

    private void configAction() {
        btnAction = findViewById(R.id.btnAction);
        btnAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = Dialogs.showProgressDialog(ChangePasswordActivity.this, R.string.elk_wait_changePassword);
                Listener<String> listener = new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ChangePasswordActivity.this, R.string.elk_succeeded_change, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                    }
                };
                addRequest(new StringRequest(API.getURL(PATH),
                        getQueryParams(), listener, new StandartErrorListener(ChangePasswordActivity.this, R.string.elk_cant_changePassword, dialog)), dialog);
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
