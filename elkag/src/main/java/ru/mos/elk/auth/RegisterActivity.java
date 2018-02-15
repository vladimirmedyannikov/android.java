package ru.mos.elk.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley2.Response.ErrorListener;
import com.android.volley2.Response.Listener;
import com.android.volley2.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.Dialogs;
import ru.mos.elk.R;
import ru.mos.elk.Statistics;
//import ru.mos.elk.profile.AgUser;

public class RegisterActivity extends BaseActivity {
    private EditText etPhoneNumber;
    protected TextView tvError;

    private View btnAction;
    private int waitId = R.string.elk_wait_registration;
    private int successId = R.string.elk_succeeded_register;

    private ProgressDialog dialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(getLayout());

    	configActionButton();
        configPhoneEdit();
        tvError = (TextView) findViewById(R.id.tvError);
        Statistics.authEnterRegistration();
    }
    
    private void configActionButton() {
    	btnAction = findViewById(R.id.btnAction);
    	btnAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				register();
			}
		});
	}

	public void setMessages(int waitId, int successId){
    	this.waitId = waitId;
    	this.successId = successId;
    }

    private void configPhoneEdit() {
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        TextWatcher watcher = new TextWatcher() {

			@Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnAction.setEnabled(etPhoneNumber.getText().length() == 10);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
        etPhoneNumber.addTextChangedListener(watcher);
        etPhoneNumber.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (etPhoneNumber.getText().length() == 10 && (actionId == R.id.actionLogin || actionId == EditorInfo.IME_ACTION_DONE)) {
                	register();
                    return true;
                }

                return false;
            }
        });
    }

    public JSONObject getQueryParams() {
    	JSONObject params = new JSONObject();
        try {
			params.put("msisdn", "7" + etPhoneNumber.getText().toString());
            JSONObject deviceInfo = new JSONObject();
            deviceInfo.put("os", "Android " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")");
            deviceInfo.put("device", Build.MODEL + " (" + Build.MANUFACTURER + ")");
            params.put("client_info", deviceInfo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        return params;
    }

    public EditText getPhoneEdit() {
        return etPhoneNumber;
    }

    protected void register() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etPhoneNumber.getWindowToken(), 0);
        tvError.setVisibility(View.GONE);
        
        dialog = Dialogs.showProgressDialog(this, waitId);
        Listener<String> listener = new Listener<String>() {

			@Override
			public void onResponse(String response) {
                onRegistrationSuccess();
		    }

        };
		ErrorListener errListener = new ErrorListener(){
			
			@Override
			public void onErrorResponse(VolleyError error) {
                onRegistrationFail(error, dialog);
			}
		};
//		String url = API.getURL("json/v0.3/auth/user/recoverypassword");
//        addRequest(new StringRequest(url, getQueryParams(), listener, errListener, false), dialog);
    }

    protected void onRegistrationFail(VolleyError error, ProgressDialog dialog) {
        dialog.dismiss();
        tvError.setText(error.getMessage());
        tvError.setVisibility(View.VISIBLE);
    }

    protected void onRegistrationSuccess() {
        if(successId== R.string.elk_succeeded_register)
            Statistics.regitrated();
        else if(successId==R.string.elk_succeeded_restore)
            Statistics.passwRecovered();
        dialog.dismiss();
        Toast.makeText(RegisterActivity.this, successId, Toast.LENGTH_LONG).show();
        String phone = etPhoneNumber.getText().toString();

//        SharedPreferences prefs = getSharedPreferences(AgUser.PREFS, Activity.MODE_PRIVATE);
//        prefs.edit().putString(AgUser.PHONE, "7"+phone).commit();
    }

    protected int getLayout() {
        return R.layout.activity_register;
    }
}




