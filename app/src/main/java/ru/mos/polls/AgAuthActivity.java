package ru.mos.polls;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import ru.mos.elk.auth.AuthActivity;
import ru.mos.elk.auth.RestoreActivity;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.event.gui.activity.EventActivity;
import ru.mos.polls.helpers.AppsFlyerConstants;
import ru.mos.polls.innovation.gui.activity.InnovationActivity;
import ru.mos.polls.popup.PopupController;
import ru.mos.polls.profile.gui.activity.AchievementActivity;
import ru.mos.polls.survey.SurveyActivity;


public class AgAuthActivity extends AuthActivity {
    private GoogleStatistics.Auth statistics = new GoogleStatistics.Auth();
    @BindView(R.id.cbAgreeOffer)
    CheckBox cbAgreeOffer;

    @OnCheckedChanged(R.id.cbAgreeOffer)
    void offerCheckBox() {
        checkForEnable();
    }

    @Override
    protected void configureEdits() {
        Spanned spanned = Html.fromHtml(getString(R.string.ag_agree_offer));
        TextView offer = ButterKnife.findById(this, R.id.tvOffer);
        OfferLinkMovementMethod movementMethod = OfferLinkMovementMethod.getInstance();
        movementMethod.setLinkListener(new OfferLinkMovementMethod.LinkListener() {
            @Override
            public void onClicked() {
                statistics.offerClick(AgAuthActivity.this);
            }
        });
        offer.setMovementMethod(movementMethod);
        offer.setText(spanned);
        String phone = AgUser.getPhone(this);
        if (phone != null && phone.length() > 1) {
            etLogin.setText(phone.substring(1));
            etPassword.requestFocus();
        }
    }

    @OnEditorAction(R.id.etPassword)
    boolean actionListener(int actionId) {
        if ((cbAgreeOffer.isChecked() && etLogin.getText().length() > 0 && etPassword.getText().length() > 0) &&
                (actionId == ru.mos.elk.R.id.actionLogin || actionId == EditorInfo.IME_ACTION_DONE)) {
            onClickLogin(null);
            return true;
        }
        return false;
    }

    @OnClick(R.id.help)
    void help() {
        AbstractActivity.hideSoftInput(AgAuthActivity.this, etLogin);
        String phone = etLogin.getText().toString();
        PopupController.authOrRegistry(AgAuthActivity.this, phone);
        statistics.helpClick(AgAuthActivity.this);
    }

    @OnTextChanged(value = {R.id.etLogin, R.id.etPassword}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void textChangeListener() {
        checkForEnable();
    }

    @OnFocusChange(value = {R.id.etLogin, R.id.etPassword})
    void setFocusListener(boolean hasFocus) {
        if (hasFocus)
            tvError.setVisibility(View.GONE);
    }

    private void checkForEnable() {
        btnLogin.setEnabled(cbAgreeOffer.isChecked() && etLogin.getText().length() > 0 && etPassword.getText().length() > 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @OnClick(R.id.btnRegister)
    void newRegister() {
        statistics.registryClick(AgAuthActivity.this);
        Intent intent = new Intent(AgAuthActivity.this, AgRegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected String makeLogin(String loginString) {
        try {
            Long.parseLong(loginString);
            return "7" + loginString;
        } catch (NumberFormatException e) {
            return loginString;
        }
    }

    @Override
    protected void onLogon() {
        try {
            super.onLogon();
        } catch (Exception ignored) {
        }
        startFromUri(getIntent());
        Statistics.auth(etLogin.getText().toString().trim(), true);
        statistics.check(this, true);
    }

    @Override
    protected void onLoginFault(String errorMessage) {
        super.onLoginFault(errorMessage);
        Statistics.auth(etLogin.getText().toString().trim(), false);
        statistics.check(this, false);
        statistics.errorOccurs(this, errorMessage);
    }

    @Override
    protected void showErrorDialog(int error) {
        super.showErrorDialog(error);
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
                        intent.putExtra("phone", etLogin.getText());
                        startActivityForResult(intent, REQUEST_RESTORE);
                    }
                }).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
