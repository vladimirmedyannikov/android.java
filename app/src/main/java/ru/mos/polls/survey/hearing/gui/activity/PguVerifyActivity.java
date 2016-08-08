package ru.mos.polls.survey.hearing.gui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import ru.mos.polls.AbstractActivity;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.helpers.FunctionalHelper;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.survey.hearing.controller.HearingApiController;


public class PguVerifyActivity extends ToolbarAbstractActivity {
    public static final int REQUEST_AUTH_RESULT = 100;
    public static final String EXTRA_AUTH_RESULT = "extra_auth_result";

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, PguVerifyActivity.class);
        activity.startActivityForResult(intent, REQUEST_AUTH_RESULT);
    }

    public static void startActivity(Fragment fragment) {
        Intent intent = new Intent(fragment.getActivity(), PguVerifyActivity.class);
        fragment.startActivityForResult(intent, REQUEST_AUTH_RESULT);
    }

    public static boolean isAuth(int resultCode, int requestCode, Intent data) {
        boolean result = false;
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_AUTH_RESULT) {
            result = isAuth(data);
        }
        return result;
    }

    public static boolean isAuth(Intent data) {
        boolean result = false;
        if (data != null) {
            result = data.getBooleanExtra(EXTRA_AUTH_RESULT, false);
        }
        return result;
    }

    @BindView(R.id.tvError)
    TextView error;
    @BindView(R.id.etLogin)
    EditText login;
    @BindView(R.id.etPassword)
    EditText password;
    @BindView(R.id.auth)
    Button auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pgu_auth);
        ButterKnife.bind(this);
        findViews();
        TitleHelper.setTitle(this, R.string.title_pgu_verify);
    }

    @OnClick(R.id.auth)
    void authPGU() {
        auth();
    }

    @OnTextChanged(value = {R.id.etLogin, R.id.etPassword}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void setLogPassChangeTextListener() {
        auth.setEnabled(login.getText().length() > 0 && password.getText().length() > 0);
    }

    @OnFocusChange(value = {R.id.etLogin, R.id.etPassword})
    void setFocus(boolean hasFocus) {
        if (hasFocus)
            error.setVisibility(View.GONE);
    }

    @OnClick(R.id.help)
    void help(View v) {
        AbstractActivity.hideSoftInput(PguVerifyActivity.this, v);
        FunctionalHelper.startBrowser(PguVerifyActivity.this, R.string.pgu_link_questions_and_answers);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    private void auth() {
        startProgress();
        HearingApiController.PguAuthListener listener = new HearingApiController.PguAuthListener() {
            @Override
            public void onSuccess(QuestMessage questMessage) {
                stopProgress();
                setResult(true);
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                error.setText(errorMessage);
                error.setVisibility(View.VISIBLE);
                stopProgress();
            }
        };
        HearingApiController.pguAuth(this, login.getText().toString(), password.getText().toString(), listener);
        AbstractActivity.hideSoftInput(PguVerifyActivity.this, password);
    }

    private void setResult(boolean isAuth) {
        Intent result = new Intent();
        result.putExtra(EXTRA_AUTH_RESULT, isAuth);
        setResult(RESULT_OK, result);
    }
}
