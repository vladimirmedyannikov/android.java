package ru.mos.polls.auth.vm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley2.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.Dialogs;
import ru.mos.elk.netframework.request.Session;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.ProfileManager;
import ru.mos.elk.push.GCMHelper;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.MainActivity;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.auth.ui.AuthFragment;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentAuthServiceBinding;
import ru.mos.polls.maskedettext.MaskedEditText;
import ru.mos.polls.tutorial.TutorialActivity;
import ru.mos.polls.tutorial.TutorialFragment;
import ru.mos.polls.util.GuiUtils;

/**
 * Created by Trunks on 21.12.2017.
 */

public class AuthFragmentVM extends UIComponentFragmentViewModel<AuthFragment, FragmentAuthServiceBinding> {

    MaskedEditText edCodeCountry, phoneNumber;
    AppCompatEditText password;
    TextView tvError;
    GoogleStatistics.Auth statistics = new GoogleStatistics.Auth();
    public static final int CONFIRM_CODE_NOT_VALID = 401;

    public AuthFragmentVM(AuthFragment fragment, FragmentAuthServiceBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentAuthServiceBinding binding) {
        edCodeCountry = binding.etCodeCountry;
        phoneNumber = binding.etLogin;
        password = binding.password;
        edCodeCountry.setText("7");
        tvError = binding.tvError;
        progressable = new ProgressableUIComponent();
        Bundle bundle = getFragment().getArguments();
        if (bundle != null) {
            phoneNumber.setText(bundle.getString(AuthFragment.ARG_PHONE));
        }
        GuiUtils.showKeyboard(phoneNumber);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with((UIComponent) progressable)
                .build();
    }

    @Override
    public void onOptionsItemSelected(int menuItemId) {
        if (checkLoginAndPassword()) {
            onAction();
        }
    }

    public boolean checkCodeText() {
        return edCodeCountry.getText().length() > 0;
    }

    public boolean checkPhoneText() {
        return phoneNumber.getUnmaskedText().length() == 10;
    }

    public boolean checkPassword() {
        return password.getText().toString().trim().length() > 0;
    }

    public boolean checkLoginAndPassword() {
        if (!checkCodeText() && checkPhoneText()) {
            Toast.makeText(getActivity(), "Введите номер телефона", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!checkPassword()) {
            Toast.makeText(getActivity(), "Введите пароль", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void onAction() {
        final ProgressDialog dialog = Dialogs.showProgressDialog(getActivity(), ru.mos.elk.R.string.elk_wait_authorization);
        GuiUtils.hideKeyboard(password);
        tvError.setVisibility(View.GONE);

        ProfileManager.AgUserListener agUserListener = new ProfileManager.AgUserListener() {
            @Override
            public void onLoaded(AgUser agUser) {
                Statistics.auth(phoneNumber.getUnmaskedText(), true);
                GoogleStatistics.Auth.auth(phoneNumber.getUnmaskedText(), true);
                statistics.check(true);
                dialog.dismiss();
                ru.mos.elk.Statistics.logon();
                onAuthCompleted();
                /**
                 * Дублируем сессию из {@link ru.mos.elk.netframework.request.ru.mos.elk.netframework.request.Session}
                 * в {@link ru.mos.polls.rxhttp.session.Session}
                 */
                ru.mos.polls.rxhttp.session.Session.get().setSession(ru.mos.elk.netframework.request.Session.getSession(getActivity()));
            }

            @Override
            public void onError(VolleyError error) {
                dialog.dismiss();
                Statistics.auth(phoneNumber.getUnmaskedText(), false);
                statistics.check(false);
                statistics.errorOccurs(error.getMessage());
                String errorMessage = error.getMessage();
                if (error.getErrorCode() == CONFIRM_CODE_NOT_VALID) {
                    errorMessage = getActivity().getString(R.string.auth_error_confirm_code_not_correct);
                }
                tvError.setText(errorMessage);
                tvError.setVisibility(View.VISIBLE);
                tvError.requestFocus();
            }
        };
        ProfileManager.getProfile((BaseActivity) getActivity(), getQueryParams(), agUserListener, true);
    }

    private void onAuthCompleted() {
        Intent intent;
        if (!TutorialFragment.Manager.wasShow(getActivity())) {
            intent = TutorialActivity.getTutorialActivityIntent(getActivity());
        } else {
            intent = new Intent(getActivity(), MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
    }

    private JSONObject getQueryParams() {
        JSONObject params = new JSONObject();
        JSONObject auth = new JSONObject();
        try {
            auth.put("login", "7" + phoneNumber.getUnmaskedText());
            auth.put("password", password.getText().toString());
            auth.put(GCMHelper.GUID, getActivity().getSharedPreferences(GCMHelper.PREFERENCES, Activity.MODE_PRIVATE).getString(GCMHelper.GUID, null));
            params.put(Session.AUTH, auth);
            SharedPreferences gcmPrefs = getActivity().getSharedPreferences(GCMHelper.PREFERENCES, Activity.MODE_PRIVATE);
            if (!gcmPrefs.getBoolean(GCMHelper.PROPERTY_ON_SERVER, false)) {
                JSONObject deviceInfo = new JSONObject();
                deviceInfo.put("guid", gcmPrefs.getString(GCMHelper.GUID, null));
                deviceInfo.put("object_id", gcmPrefs.getString(GCMHelper.PROPERTY_REG_ID, null));
                deviceInfo.put("user_agent", "Android");
                deviceInfo.put("app_version", GCMHelper.getAppVersionName(getActivity()));
                params.put("device_info", deviceInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return params;
    }
}
