package ru.mos.polls.changepassword.vm;

import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.changepassword.service.ChangePassword;
import ru.mos.polls.changepassword.ui.ChangePasswordFragment;
import ru.mos.polls.databinding.FragmentChangepasswordBinding;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.util.GuiUtils;


public class ChangePasswordFragmentVM extends UIComponentFragmentViewModel<ChangePasswordFragment, FragmentChangepasswordBinding> {
    TextInputEditText oldPass, newPass, repeatPass;

    public ChangePasswordFragmentVM(ChangePasswordFragment fragment, FragmentChangepasswordBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentChangepasswordBinding binding) {
        oldPass = binding.oldpass;
        newPass = binding.newpass;
        repeatPass = binding.repeatpass;
        oldPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().equals(charSequence.toString())) {
                    oldPass.setText(charSequence.toString().trim());
                    oldPass.setSelection(oldPass.getText().toString().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        newPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().equals(charSequence.toString())) {
                    newPass.setText(charSequence.toString().trim());
                    newPass.setSelection(newPass.getText().toString().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        repeatPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().equals(charSequence.toString())) {
                    repeatPass.setText(charSequence.toString().trim());
                    repeatPass.setSelection(repeatPass.getText().toString().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(new ProgressableUIComponent())
                .build();
    }

    @Override
    public void onOptionsItemSelected(int menuItemId) {
        if (!checkOldPass()) {
            GuiUtils.displayOkMessage(getActivity(), "Введите текущий пароль.", null);
            return;
        }
        if (!checkNewPass()) {
            GuiUtils.displayOkMessage(getActivity(), "Введите новый пароль.", null);
            return;
        }
        if (!checkNewPassEqualsRepeatPass()) {
            GuiUtils.displayOkMessage(getActivity(), "Пароли не совпадают.", null);
            return;
        }
        HandlerApiResponseSubscriber<String> handler = new HandlerApiResponseSubscriber<String>(getActivity(), getProgressable()) {
            @Override
            protected void onResult(String result) {
                GuiUtils.displayOkMessage(getActivity(), "Пароль успешно изменён.", (dialogInterface, i) -> {
                    getActivity().finish();
                });
            }
        };
        disposables.add(AGApplication.api
                .changePassword(new ChangePassword.Request(oldPass.getText().toString().trim(), newPass.getText().toString().trim()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public boolean checkOldPass() {
        return !oldPass.getText().toString().trim().isEmpty();
    }
    public boolean checkNewPass() {
        return !newPass.getText().toString().trim().isEmpty();
    }
    public boolean checkNewPassEqualsRepeatPass() {
        return newPass.getText().toString().trim().equals(repeatPass.getText().toString().trim());
    }

}
