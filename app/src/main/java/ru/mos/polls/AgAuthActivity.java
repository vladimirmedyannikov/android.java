package ru.mos.polls;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;
import com.appsflyer.AppsFlyerLib;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import me.ilich.juggler.change.Add;
import pub.devrel.easypermissions.EasyPermissions;
import ru.mos.polls.util.Dialogs;
import ru.mos.elk.netframework.request.StringRequest;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.api.API;
import ru.mos.polls.auth.state.AuthState;
import ru.mos.polls.event.gui.activity.EventActivity;
import ru.mos.polls.helpers.AppsFlyerConstants;
import ru.mos.polls.innovations.ui.activity.InnovationActivity;
import ru.mos.polls.maskedettext.MaskedEditText;
import ru.mos.polls.support.state.SupportState;
import ru.mos.polls.profile.ui.activity.AchievementActivity;
import ru.mos.polls.survey.SurveyActivity;
import ru.mos.polls.util.GuiUtils;


public class AgAuthActivity extends BaseActivity {
    private static final int SMS_PERMISSION_REQUEST = 9825;
    protected Toolbar toolbar;
    private static final String[] SMS_PERMS = {
            Manifest.permission.RECEIVE_SMS
    };
    public static final String SKIP_ACTIVITY = "ru.mos.polls.auth.SKIP_ACTIVITY";
    public static final String PASSED_ACTIVITY = "ru.mos.polls.auth.PASSED_ACTIVITY";
    public static final String JUST_AUTHORIZE = "ru.mos.polls.auth.JUST_AUTHORIZE";
    private GoogleStatistics.Auth statistics = new GoogleStatistics.Auth();
    protected static final int ERROR_CODE_423 = 423;
    protected static final int REQUEST_REGISTER = 1;
    protected static final int REQUEST_RESTORE = 2;
    @BindView(R.id.cbAgreeOffer)
    CheckBox cbAgreeOffer;

    @BindView(R.id.et_code_country)
    MaskedEditText codeCountry;
    @BindView(R.id.etLogin)
    MaskedEditText etLogin;
    @BindView(R.id.tvError)
    TextView tvError;

    public static void start(Context context) {
        Intent activity = new Intent(context, AgAuthActivity.class);
        context.startActivity(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ваш телефон");
        configureEdits();
        /**
         * код по умолчанию +7, при этом без возможности редактирования
         */
        codeCountry.setText("7");
        codeCountry.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GuiUtils.showKeyboard(etLogin);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.confirm, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        GuiUtils.hideKeyboard(etLogin);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                doRequestAction();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void doRequestAction() {
        if (checkCodeText() && checkPhoneText()) {
            onClickLogin(etLogin);
        } else {
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
        }
    }

    protected void configureEdits() {
        TextView offer = ButterKnife.findById(this, R.id.tvOffer);
        offer.setPaintFlags(offer.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuiUtils.hideKeyboard(v);
                statistics.offerClick();
                WebViewActivity.startActivity(AgAuthActivity.this,
                        getString(R.string.title_offer),
                        getString(R.string.url_offer),
                        null,
                        false,
                        false);
            }
        });
        String phone = AgUser.getPhone(this);
        if (phone != null && phone.length() > 1) {
            etLogin.setText(phone.substring(1));
        }
        checkPermissions();
    }

    public void onClickLogin(View view) {
        GuiUtils.hideKeyboard(view);

        tvError.setVisibility(View.GONE);

        final ProgressDialog dialog = Dialogs.showProgressDialog(this, "Ожидайте..");
        Response.Listener<String> listener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                finish();
                AgPhoneConfirmActivity.start(AgAuthActivity.this, etLogin.getUnmaskedText());
            }

        };
        Response.ErrorListener errListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                if (error != null) {
                    if (error.getErrorCode() != ERROR_CODE_423)
                        onLoginFault(error.getMessage());
                    showErrorDialog(error.getErrorCode());
                }
            }
        };
        String url = API.getURL("json/v0.3/auth/user/recoverypassword");
        addRequest(new StringRequest(url, getQueryParams(), listener, errListener, false), dialog);
    }

    private JSONObject getQueryParams() {
        JSONObject params = new JSONObject();
        try {
            params.put("msisdn", codeCountry.getUnmaskedText() + etLogin.getUnmaskedText());
            JSONObject deviceInfo = new JSONObject();
            deviceInfo.put("os", "Android " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")");
            deviceInfo.put("device", Build.MODEL + " (" + Build.MANUFACTURER + ")");
            params.put("client_info", deviceInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

    @OnClick(R.id.registered_in_service)
    public void authInService() {
        navigateTo().state(Add.newActivity(new AuthState(etLogin.getUnmaskedText()), ru.mos.polls.base.ui.BaseActivity.class));
    }

    @OnEditorAction(R.id.etLogin)
    boolean actionLoginListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            doRequestAction();
        }
        return true;
    }

    @OnClick(R.id.help)
    void help() {
        AbstractActivity.hideSoftInput(AgAuthActivity.this, etLogin);
        statistics.helpClick();
        new GoogleStatistics.Auth().feedbackClick();
        navigateTo().state(Add.newActivity(new SupportState(true), ru.mos.polls.base.ui.BaseActivity.class));
    }

    @OnFocusChange(value = {R.id.etLogin, R.id.etPassword})
    void setFocusListener(boolean hasFocus) {
        if (hasFocus)
            tvError.setVisibility(View.GONE);
    }

    public boolean checkCodeText() {
        return codeCountry.getUnmaskedText().length() > 0;
    }

    public boolean checkPhoneText() {
        return etLogin.getUnmaskedText().length() == 10;
    }

    private void checkPermissions() {
        if (!EasyPermissions.hasPermissions(this, SMS_PERMS)) {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.get_permission_sms),
                    SMS_PERMISSION_REQUEST,
                    SMS_PERMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        etLogin.post(new Runnable() {
            @Override
            public void run() {
                etLogin.requestFocus();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @OnClick(R.id.btnRegister)
    void newRegister() {
        statistics.registryClick();
        Intent intent = new Intent(AgAuthActivity.this, AgRegisterActivity.class);
        startActivity(intent);
    }

    protected void onLoginFault(String errorMessage) {
        ScrollView sv = ButterKnife.findById(this, R.id.scrollView);
        sv.pageScroll(View.FOCUS_DOWN);
        tvError.setText(errorMessage);
        tvError.setVisibility(View.VISIBLE);
        tvError.requestFocus();
        Statistics.auth(etLogin.getUnmaskedText(), false);
        statistics.check(false);
        statistics.errorOccurs(errorMessage);
    }

    protected void showErrorDialog(int error) {
        switch (error) {
            case ERROR_CODE_423:
                AlertDialog.Builder dialog = new AlertDialog.Builder(AgAuthActivity.this);
                dialog.setMessage(R.string.auth_error_code_msg_423)
                        .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton(R.string.restore, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AgAuthActivity.this, AgRestoreActivity.class);
                        intent.putExtra("phone", etLogin.getUnmaskedText());
                        startActivityForResult(intent, REQUEST_RESTORE);
                    }
                }).show();
                break;
        }
    }

    @Override
    protected void onUserLeaveHint() {
        GuiUtils.hideKeyboard(etLogin);
        super.onUserLeaveHint();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_REGISTER:
            case REQUEST_RESTORE:
                if (resultCode == RESULT_OK)
                    etLogin.setText(data.getStringExtra("phone"));
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REGISTER && resultCode == RESULT_OK) {
            AppsFlyerLib.sendTrackingWithEvent(this, AppsFlyerConstants.REGISTRATION, "");
        }
    }

    /**
     * Запуск после логина по uri
     *
     * @param intent
     */
    private void startFromUri(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            String host = uri.getHost();
            Intent startIntent = new Intent(this, MainActivity.class);
            if ("events".equalsIgnoreCase(host)
                    || "pollTasks".equalsIgnoreCase(host)
                    || "news".equalsIgnoreCase(host)) {
                startIntent = new Intent(this, MainActivity.class);
            } else if ("event".equalsIgnoreCase(host)) {
                startIntent = new Intent(this, EventActivity.class);
            } else if ("new".equalsIgnoreCase(host) || "advertisement".equalsIgnoreCase(host)) {
                startIntent = new Intent(this, WebViewActivity.class);
            } else if ("poll".equalsIgnoreCase(host)) {
                startIntent = new Intent(this, SurveyActivity.class);
            } else if ("novelty".equalsIgnoreCase(host)) {
                startIntent = new Intent(this, InnovationActivity.class);
            } else if ("achievement".equalsIgnoreCase(host)) {
                startIntent = new Intent(this, AchievementActivity.class);
            }
            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.setData(uri);
            startActivity(startIntent);
            finish();
        }
    }

    /**
     * Перехват клика по ссылке html
     */
    public static class OfferLinkMovementMethod extends LinkMovementMethod {

        private static OfferLinkMovementMethod linkMovementMethod = new OfferLinkMovementMethod();

        private LinkListener linkListener;

        public void setLinkListener(LinkListener linkListener) {
            this.linkListener = linkListener;
        }

        public boolean onTouchEvent(android.widget.TextView widget, android.text.Spannable buffer, android.view.MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
                if (link.length != 0) {
                    String url = link[0].getURL();
                    if (url.startsWith("http")) {
                        if (linkListener != null) {
                            linkListener.onClicked();
                        }
                    }
                }
            }

            return super.onTouchEvent(widget, buffer, event);
        }

        public static OfferLinkMovementMethod getInstance() {
            return linkMovementMethod;
        }

        public interface LinkListener {
            void onClicked();
        }
    }
}
