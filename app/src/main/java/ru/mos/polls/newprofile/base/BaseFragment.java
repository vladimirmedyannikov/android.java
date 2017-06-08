package ru.mos.polls.newprofile.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.ilich.juggler.gui.JugglerFragment;

/**
 * Created by Trunks on 06.06.2017.
 */

public abstract class BaseFragment<B extends ViewDataBinding> extends JugglerFragment {

    protected abstract int getLayoutResources();
    private B binding;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutResources(), container, false);
        return binding.getRoot();
    }

    public ViewDataBinding getBinding() {
        return binding;
    }
}
