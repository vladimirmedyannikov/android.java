package ru.mos.polls.base.ui;

/**
 * Created by wlTrunks on 07.06.2017.
 */

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import butterknife.ButterKnife;
import me.ilich.juggler.gui.JugglerFragment;
import pub.devrel.easypermissions.EasyPermissions;
import ru.mos.polls.base.vm.FragmentViewModel;

public abstract class BindingFragment<VM extends FragmentViewModel, B extends ViewDataBinding> extends JugglerFragment  {

    protected abstract VM onCreateViewModel(B binding);

    private B binding;
    private VM viewModel;

    private Bundle savedInstanceState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, inflater.inflate(getLayoutResources(), container, false));
        binding = DataBindingUtil.inflate(inflater, getLayoutResources(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        binding.setVariable(getVariable(), getUpdatedViewModel());
        binding.executePendingBindings();
        viewModel.onViewCreated();
    }

    public B getBinding() {
        return binding;
    }

    private VM getUpdatedViewModel() {
        if (viewModel == null) viewModel = onCreateViewModel(binding);
        else viewModel.updateBinding(binding);
        return viewModel;
    }

    public VM getViewModel() {
        return viewModel;
    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    public void resetViewModel() {
        viewModel = onCreateViewModel(binding);
        getBinding().setVariable(getVariable(), viewModel);
    }

    @Override
    public void onPause() {
        viewModel.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    @Override
    public void onDestroyView() {
        viewModel.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public abstract int getVariable();

    public abstract int getLayoutResources();

    public static void hideKeyboard(Fragment fragment) {
        IBinder view = fragment.getView().getRootView().getWindowToken();
        InputMethodManager imm = (InputMethodManager) fragment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view, 0);
    }

    @Override
    public boolean onBackPressed() {
        hideKeyboard(this);
        return super.onBackPressed();
    }

    @Override
    public boolean onUpPressed() {
        hideKeyboard(this);
        return super.onUpPressed();
    }
}