package ru.mos.polls.newprofile.base;

/**
 * Created by wlTrunks on 07.06.2017.
 */

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BindingFragment<VM extends FragmentViewModel, B extends ViewDataBinding>
        extends Fragment {

    protected abstract VM onCreateViewModel(B binding);

    private B binding;
    private VM viewModel;

    private Bundle savedInstanceState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

    public abstract int getVariable();

    public abstract int getLayoutResources();
}