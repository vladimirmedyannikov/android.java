package ru.mos.polls.survey.hearing.gui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import ru.mos.elk.BaseActivity;
import ru.mos.polls.AbstractActivity;
import ru.mos.polls.R;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.survey.hearing.controller.HearingApiController;


public class PguBindFragment extends Fragment {
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.tvError)
    TextView error;
    @BindView(R.id.etLogin)
    EditText login;
    @BindView(R.id.etPassword)
    EditText password;
    @BindView(R.id.auth)
    Button auth;
    private Unbinder unbinder;
    private PguBindingListener pguBindingListener;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pgu_bind, null);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @OnClick(R.id.auth)
    void auth() {
        bind();
    }

    @OnFocusChange(value = {R.id.etLogin, R.id.etPassword})
    void setFocus(boolean hasFocus) {
        if (hasFocus)
            error.setVisibility(View.GONE);
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
        pguBindingListener.onPrepare();
        HearingApiController.PguAuthListener listener = new HearingApiController.PguAuthListener() {
            @Override
            public void onSuccess(QuestMessage questMessage) {
                AbstractActivity.hideSoftInput(getActivity(), password);
                pguBindingListener.onSuccess(questMessage);
            }

            @Override
            public void onError(String errorMessage) {
                error.setText(errorMessage);
                error.setVisibility(View.VISIBLE);
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
