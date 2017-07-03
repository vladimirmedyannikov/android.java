package ru.mos.polls.newprofile.base.vm;

import android.app.Activity;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.ViewDataBinding;
import android.support.v4.app.Fragment;

import io.reactivex.disposables.CompositeDisposable;
import me.ilich.juggler.gui.JugglerFragment;

/**
 * Created by wlTrunks on 07.06.2017.
 */

public abstract class FragmentViewModel<F extends JugglerFragment, B extends ViewDataBinding> extends BaseObservable {

    protected abstract void initialize(B binding);

    protected CompositeDisposable disposables;
    private F fragment;
    private B binding;
    private Activity activity;

    public FragmentViewModel(F fragment, B binding) {
        this.fragment = fragment;
        this.binding = binding;
        this.activity = this.fragment.getActivity();
        disposables = new CompositeDisposable();
        initialize(binding);
    }

    public F getFragment() {
        return fragment;
    }

    public Fragment getParentFragment() {
        return fragment.getParentFragment();
    }

    public B getBinding() {
        return binding;
    }

    public Activity getActivity() {
        return activity;
    }

    public void updateBinding(B binding) {
        this.binding = binding;
        initialize(binding);
    }

    public void onViewCreated() {

    }

    public void onDestroy() {
        disposables.clear();
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onDestroyView() {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

}