package ru.mos.polls;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import me.ilich.juggler.change.Add;
import ru.mos.polls.auth.state.AgAuthState;
import ru.mos.polls.auth.vm.AgAuthFragmentVM;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.popup.PopupController;
import ru.mos.polls.util.Dialogs;

public class AgRegisterActivity extends BaseActivity {
    private GoogleStatistics.Registry statistics = new GoogleStatistics.Registry();

    private EditText phone;
    private CheckBox cbAgreeOffer;
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

        Spanned spanned = Html.fromHtml(getString(R.string.ag_agree_offer_link));
        TextView offer = (TextView) findViewById(R.id.tvOffer);
        AgAuthFragmentVM.OfferLinkMovementMethod movementMethod = AgAuthFragmentVM.OfferLinkMovementMethod.getInstance();
        movementMethod.setLinkListener(new AgAuthFragmentVM.OfferLinkMovementMethod.LinkListener() {
            @Override
            public void onClicked() {
                statistics.offerClick();
            }
        });
        offer.setMovementMethod(movementMethod);
        offer.setText(spanned);

        final Button btnRegistry = (Button) findViewById(R.id.btnAction);
        phone = (EditText) findViewById(R.id.etPhoneNumber);

        cbAgreeOffer = (CheckBox) findViewById(R.id.cbAgreeOffer);
        cbAgreeOffer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnRegistry.setEnabled(cbAgreeOffer.isChecked() && phone.getText().length() > 0);
            }
        });

        TextWatcher watcher = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                btnRegistry.setEnabled(cbAgreeOffer.isChecked() && phone.getText().length() > 0);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        };
        phone.addTextChangedListener(watcher);


        findViewById(R.id.auth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final View help = findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbstractActivity.hideSoftInput(AgRegisterActivity.this, help);
                PopupController.authOrRegistry(AgRegisterActivity.this, phone.getText().toString());
                statistics.helpClick();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void startAuth() {
//        Intent authIntent = new Intent(AgRegisterActivity.this, AgAuthActivity.class);
//        authIntent.putExtra(AgAuthActivity.PASSED_ACTIVITY, MainActivity.class);
//        EditText phone = (EditText) findViewById(R.id.etPhoneNumber);
//        authIntent.putExtra("phone", phone.getText().toString());
//        authIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(authIntent);
        navigateTo().state(Add.newActivity(new AgAuthState(), ru.mos.polls.base.ui.BaseActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    public void setMessages(int waitId, int successId) {
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
        if (cbAgreeOffer.isChecked() && phone.getText().length() > 0) {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etPhoneNumber.getWindowToken(), 0);
            tvError.setVisibility(View.GONE);

            dialog = Dialogs.showProgressDialog(this, waitId);
        }
    }

    protected int getLayout() {
        return R.layout.activity_register;
    }
}
