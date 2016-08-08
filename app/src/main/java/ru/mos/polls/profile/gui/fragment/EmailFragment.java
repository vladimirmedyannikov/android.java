package ru.mos.polls.profile.gui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.R;

/**
 * Сохранение email- а пользователя
 *
 * @since 1.9
 */
public class EmailFragment extends AbstractProfileFragment {
    @BindView(R.id.email)
    EditText email;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_email, null);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @OnTextChanged(value = R.id.email, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void emailEdit() {
        changeListener.onChange(EMAIL_ID);
    }

    @Override
    public void refreshUI(AgUser agUser) {
        email.setText(agUser.getEmail());
    }

    @Override
    public void updateAgUser(AgUser agUser) {
        agUser.setEmail(email.getText().toString());
    }
}
