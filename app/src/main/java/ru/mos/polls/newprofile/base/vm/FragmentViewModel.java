package ru.mos.polls.newprofile.base.vm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import io.reactivex.disposables.CompositeDisposable;
import me.ilich.juggler.gui.JugglerFragment;
import ru.mos.polls.R;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;

/**
 * Created by wlTrunks on 07.06.2017.
 */

public abstract class FragmentViewModel<F extends JugglerFragment, B extends ViewDataBinding> extends BaseObservable {

    protected abstract void initialize(B binding);

    protected CompositeDisposable disposables;
    private F fragment;
    private B binding;
    private Activity activity;
    public ProgressDialog pd;

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
        pd = new ProgressDialog(getFragment().getContext(), R.style.ProgressBar);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
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

    public void doRequest() {

    }

    public Progressable progressable = new Progressable() {
        @Override
        public void begin() {
            if (pd != null)
                pd.show();
        }

        @Override
        public void end() {
            if (pd != null)
                pd.dismiss();
        }
    };
}