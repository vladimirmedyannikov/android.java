package ru.mos.polls.auth.vm;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.MainActivity;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.auth.state.AgAuthState;
import ru.mos.polls.auth.ui.AgPhoneConfirmFragment;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.broadcast.SmsBroadcastReceiver;
import ru.mos.polls.databinding.FragmentAgPhoneConfirmBinding;
import ru.mos.polls.profile.ProfileManagerRX;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.support.state.SupportState;
import ru.mos.polls.tutorial.TutorialActivity;
import ru.mos.polls.tutorial.TutorialFragment;
import ru.mos.polls.util.Dialogs;
import ru.mos.polls.util.GuiUtils;

public class AgPhoneConfirmFragmentVM extends UIComponentFragmentViewModel<AgPhoneConfirmFragment, FragmentAgPhoneConfirmBinding>{
    public static final int CONFIRM_CODE_NOT_VALID = 401;

    private TextView tvPhone;
    private EditText etCode;
    private Button action;
    private TextView help;
    private TextView tvError;
    private GoogleStatistics.Auth statistics = new GoogleStatistics.Auth();
    private String phone;

    private BroadcastReceiver smsAuthReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra(SmsBroadcastReceiver.EXTRA_CODE);
            etCode.setText(code);
        }
    };


    public AgPhoneConfirmFragmentVM(AgPhoneConfirmFragment fragment, FragmentAgPhoneConfirmBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentAgPhoneConfirmBinding binding) {
        tvPhone = binding.tvPhone;
        etCode = binding.etCode;
        action = binding.btnAction;
        help = binding.help;
        tvError = binding.tvError;
        binding.feedback.setOnClickListener(v -> {
            getFragment().navigateTo(new SupportState(true), ru.mos.polls.base.activity.BaseActivity.class);
        });
        action.setOnClickListener(v -> onAction());
        help.setOnClickListener(v -> {
            getFragment().navigateTo(new AgAuthState(), ru.mos.polls.base.activity.BaseActivity.class);
            getFragment().navigateToCloseCurrActivity();
        });
        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                action.setEnabled(etCode.getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_NEXT) {
                    doRequestAction();
                }
                return true;
            }
        });
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().build();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        LocalBroadcastManager.getInstance(getFragment().getContext())
                .registerReceiver(smsAuthReceiver, new IntentFilter(SmsBroadcastReceiver.ACTION_CODE_CONFIRMATION_RECEIVED));
        phone = getFragment().getExtraPhone();
        tvPhone.setText(formatPhone());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getFragment().getContext())
                .unregisterReceiver(smsAuthReceiver);
    }

    @Override
    public void onOptionsItemSelected(int menuItemId) {
        switch (menuItemId) {
            case R.id.action_confirm:
                doRequestAction();
                break;
        }
        super.onOptionsItemSelected(menuItemId);
    }

    public void doRequestAction() {
        if (checkCodeText()) {
            onAction();
        } else {
            Toast.makeText(getFragment().getContext(), "Введите проверочный код", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatPhone() {
        return "+7 (" +
                phone.substring(0, 3) +
                ") " +
                phone.substring(3, 6) +
                "-" +
                phone.substring(6, 8) +
                "-" +
                phone.substring(8);
    }

    public boolean checkCodeText() {
        return etCode.getText().length() > 0;
    }

    public void onAction() {
        final ProgressDialog dialog = Dialogs.showProgressDialog(getFragment().getContext(), R.string.elk_wait_authorization);
        GuiUtils.hideKeyboard(etCode);
        tvError.setVisibility(View.GONE);

        ProfileManagerRX.AgUserListener listener = new ProfileManagerRX.AgUserListener() {
            @Override
            public void onLoaded(AgUser agUser) {
                Statistics.auth(phone, true);
                GoogleStatistics.Auth.auth(phone, true);
                statistics.check(true);
                dialog.dismiss();
                Statistics.logon();
                onAuthCompleted();
            }

            @Override
            public void onError(String message, int code) {
                dialog.dismiss();
                Statistics.auth(phone, false);
                statistics.check(false);
                statistics.errorOccurs(message);
                String errorMessage = message;
                if (code == CONFIRM_CODE_NOT_VALID) {
                    errorMessage = getFragment().getString(R.string.auth_error_confirm_code_not_correct);
                }
                tvError.setText(errorMessage);
                tvError.setVisibility(View.VISIBLE);
                tvError.requestFocus();
            }
        };
        ProfileManagerRX.login(disposables, getFragment().getContext(), ProfileManagerRX.getRequest(getFragment().getContext(), phone, etCode.getText().toString()), listener);
    }

    private void onAuthCompleted() {
        if (!TutorialFragment.Manager.wasShow(getFragment().getContext())) {
            TutorialActivity.start(getFragment().getContext());
        } else {
            Intent activity = new Intent(getFragment().getContext(), MainActivity.class);
            getFragment().startActivity(activity);
        }
        getFragment().navigateToCloseCurrActivity();
    }
}
