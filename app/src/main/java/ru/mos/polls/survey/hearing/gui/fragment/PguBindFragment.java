package ru.mos.polls.survey.hearing.gui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import me.ilich.juggler.gui.JugglerFragment;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.maskedettext.MaskedEditText;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.AbstractActivity;
import ru.mos.polls.R;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.popup.PopupController;
import ru.mos.polls.survey.hearing.controller.HearingApiControllerRX;
import ru.mos.polls.util.AgTextUtil;
import ru.mos.polls.util.GuiUtils;


public class PguBindFragment extends JugglerFragment {
    public static final String EXTRA_AUTH_RESULT = "extra_auth_result";
    @BindView(R.id.container)
    RelativeLayout container;
    @BindView(R.id.tvError)
    TextView error;
    @BindView(R.id.etLogin)
    MaskedEditText login;
    @BindView(R.id.etPassword)
    TextInputEditText password;

    @BindView(R.id.etPassword_wrapper)
    TextInputLayout etPassword;
    @BindView(R.id.etLogin_wrapper)
    TextInputLayout etLogin;

    private Unbinder unbinder;
    private PguBindingListener pguBindingListener;

    private ProgressableUIComponent progressableUIComponent;

    public void setPguBindListener(PguBindingListener pguBindingListener) {
        if (pguBindingListener == null) {
            pguBindingListener = PguBindingListener.STUB;
        }
        this.pguBindingListener = pguBindingListener;
    }

    public void setVisibility(int visibility) {
        if (container != null) {
            container.setVisibility(visibility);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        progressableUIComponent = new ProgressableUIComponent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pgu_bind, null);
        unbinder = ButterKnife.bind(this, root);
        progressableUIComponent.onViewCreated(getContext(), root);
        etLogin.setError(getString(R.string.phone_in_format));
        login.setMask(getString(R.string.mos_ru_phone_mask));
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        GuiUtils.hideKeyboard(getView());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.confirm, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_confirm) {
            bind();
        }
        GuiUtils.hideKeyboard(getView());
        return true;
    }

    @OnClick(R.id.auth)
    void auth() {
        bind();
    }

    @OnClick(R.id.help)
    void help() {
        PopupController.pgu(getActivity());
    }

    @OnFocusChange(value = {R.id.etLogin, R.id.etPassword})
    void setFocus(View view, boolean hasFocus) {
        if (hasFocus)
            etPassword.setError("");
        if (view.getId() == R.id.etPassword && hasFocus) {

        }
    }

    public void checkLoginInput() {
        String inputText = login.getUnmaskedText();
        if (AgTextUtil.isEmailValid(inputText)) return;
        String digitText = AgTextUtil.stripDigit(inputText);
        if (digitText.length() < 9 || digitText.length() > 11) {
            //
        } else if (digitText.length() == 11) {

        } else {

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void bind() {
        setPguBindListener(pguBindingListener);
        pguBindingListener.onPrepare();
        progressableUIComponent.begin();
        HearingApiControllerRX.PguAuthListener listener1 = new HearingApiControllerRX.PguAuthListener() {
            @Override
            public void onSuccess(QuestMessage questMessage, int percent) {
                setResult(true);
                GuiUtils.hideKeyboard(password);
                AgUser.setPercentFillProfile(getActivity(), percent);
                AGApplication.bus().send(new Events.WizardEvents(Events.WizardEvents.WIZARD_PGU, percent));
                questMessage.show(getActivity(), true);
                progressableUIComponent.end();
                if (questMessage != null && !questMessage.isEmpty()) {
                    questMessage.show(getActivity(), true);
                } else {
                    onBackPressed();
                }
                AgUser.setPguConnected(getActivity());
                pguBindingListener.onSuccess(questMessage);
            }

            @Override
            public void onError(String error) {
                progressableUIComponent.end();
                etPassword.setError(error);
                pguBindingListener.onError();
            }
        };
        HearingApiControllerRX.pguBind(((BaseActivity) getActivity()).getDisposables(), getContext(), login.getText().toString(), password.getText().toString(), listener1);
    }

    private void setResult(boolean isAuth) {
        Intent result = new Intent();
        result.putExtra(EXTRA_AUTH_RESULT, isAuth);
        getActivity().setResult(Activity.RESULT_OK, result);
    }

    public interface PguBindingListener {
        PguBindingListener STUB = new PguBindingListener() {
            @Override
            public void onPrepare() {
            }

            @Override
            public void onSuccess(QuestMessage questMessage) {
            }

            @Override
            public void onError() {
            }
        };

        void onPrepare();

        void onSuccess(QuestMessage questMessage);

        void onError();
    }
}
