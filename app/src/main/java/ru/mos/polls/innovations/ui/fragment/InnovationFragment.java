package ru.mos.polls.innovations.ui.fragment;

import android.os.Bundle;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.FragmentInnovationBinding;
import ru.mos.polls.innovations.vm.InnovationFragmentVM;

public class InnovationFragment extends NavigateFragment<InnovationFragmentVM, FragmentInnovationBinding> {

    public static final int REQUEST = 100;
    public static final String EXTRA_SHORT_INNOVATION = "extra_short_innovation";
    public static final String EXTRA_INNOVATION = "extra_innovation";

    public static InnovationFragment instance(long id) {
        InnovationFragment res = new InnovationFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_SHORT_INNOVATION, id);
        res.setArguments(bundle);
        return res;
    }

    @Override
    protected InnovationFragmentVM onCreateViewModel(FragmentInnovationBinding binding) {
        return new InnovationFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_innovation;
    }

    @Override
    public boolean onBackPressed() {
        getViewModel().onBackPressed();
        return true;
    }

    @Override
    public boolean onUpPressed() {
        getViewModel().onBackPressed();
        return true;
    }
}
