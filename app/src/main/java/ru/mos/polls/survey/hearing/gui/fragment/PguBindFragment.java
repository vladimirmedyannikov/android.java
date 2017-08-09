package ru.mos.polls.survey.hearing.gui.fragment;

import android.app.ProgressDialog;
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
import ru.mos.elk.BaseActivity;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.AbstractActivity;
import ru.mos.polls.R;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.newprofile.base.rxjava.Events;
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
    ProgressDialog progressDialog;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pgu_bind, null);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    public void startProgress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
    }

    public void stopProgress() {
        if (progressDialog != null) progressDialog.dismiss();
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
        startProgress();
        HearingApiController.PguAuthListener listener = new HearingApiController.PguAuthListener() {
            @Override
            public void onSuccess(QuestMessage questMessage) {
                AbstractActivity.hideSoftInput(getActivity(), password);
                AGApplication.bus().send(new Events.WizardEvents(Events.WizardEvents.WIZARD_PGU, 0));
                questMessage.show(getActivity(), true);
                stopProgress();
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
                stopProgress();
                etPassword.setError(errorMessage);
//                error.setText(errorMessage);
//                error.setVisibility(View.VISIBLE);
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
