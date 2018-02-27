package ru.mos.polls.auth.vm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import ru.mos.polls.profile.ProfileManagerRX;
import ru.mos.polls.util.Dialogs;
import ru.mos.polls.profile.model.AgUser;
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
        if (!checkPhoneText()) {
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
        ProfileManagerRX.AgUserListener listener = new ProfileManagerRX.AgUserListener() {
            @Override
            public void onLoaded(AgUser agUser) {
                Statistics.auth(phoneNumber.getUnmaskedText(), true);
                GoogleStatistics.Auth.auth(phoneNumber.getUnmaskedText(), true);
                statistics.check(true);
                dialog.dismiss();
                Statistics.logon();
                onAuthCompleted();
            }

            @Override
            public void onError(String message, int code) {
                dialog.dismiss();
                Statistics.auth(phoneNumber.getUnmaskedText(), false);
                statistics.check(false);
                statistics.errorOccurs(message);
                String errorMessage = message;
                if (code == CONFIRM_CODE_NOT_VALID) {
                    errorMessage = getActivity().getString(R.string.auth_error_confirm_code_not_correct);
                }
                tvError.setText(errorMessage);
                tvError.setVisibility(View.VISIBLE);
                tvError.requestFocus();
            }
        };
        ProfileManagerRX.login(disposables, getActivity(), ProfileManagerRX.getRequest(getActivity(), phoneNumber.getUnmaskedText(), password.getText().toString()), listener);
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
}
