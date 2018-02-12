package ru.mos.polls.survey.hearing.gui.fragment;

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
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.AbstractActivity;
import ru.mos.polls.R;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.popup.PopupController;
import ru.mos.polls.survey.hearing.controller.HearingApiController;
import ru.mos.polls.util.GuiUtils;


public class PguBindFragment extends JugglerFragment {
    @BindView(R.id.container)
    RelativeLayout container;
    @BindView(R.id.tvError)
    TextView error;
    @BindView(R.id.etLogin)
    TextInputEditText login;
    @BindView(R.id.etPassword)
    TextInputEditText password;
    @BindView(R.id.auth)
    Button auth;

    @BindView(R.id.etPassword_wrapper)
    TextInputLayout etPassword;

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
            if (checkField()) bind();
        }
        GuiUtils.hideKeyboard(getView());
        return true;
    }

    public boolean checkField() {
        if (login.getText().toString().length() == 0) {
            GuiUtils.displayOkMessage(getActivity(), String.format("Поле %s пустое.", getActivity().getString(R.string.pgu_hint_email)), null);
            return false;
        }
        if (password.getText().toString().length() == 0) {
            GuiUtils.displayOkMessage(getActivity(), getString(R.string.warning_enter_password), null);
            return false;
        }
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
    void setFocus(boolean hasFocus) {
        if (hasFocus)
            etPassword.setError("");
    }

    @OnTextChanged(value = {R.id.etLogin, R.id.etPassword}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void setLogPassTextChangeListener() {
        auth.setEnabled(login.getText().length() > 0 && password.getText().length() > 0);
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
        HearingApiController.PguAuthListener listener = new HearingApiController.PguAuthListener() {
            @Override
            public void onSuccess(QuestMessage questMessage, int percent) {
                AbstractActivity.hideSoftInput(getActivity(), password);
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
            public void onError(String errorMessage) {
                progressableUIComponent.end();
                etPassword.setError(errorMessage);
                pguBindingListener.onError();
            }
        };
        HearingApiController.pguBind((BaseActivity) getActivity(), login.getText().toString(), password.getText().toString(), listener);
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
