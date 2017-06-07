package ru.mos.polls.newprofile;

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

    protected abstract int getLayoutId();
    private B binding;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return binding.getRoot();
    }

    public ViewDataBinding getBinding() {
        return binding;
    }
}
