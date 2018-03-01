package ru.mos.polls.ourapps.ui;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.FragmentOurAppsBinding;
import ru.mos.polls.ourapps.vm.OurAppsFragmentVM;


public class OurAppsFragment extends NavigateFragment<OurAppsFragmentVM, FragmentOurAppsBinding> {

    public static OurAppsFragment getInstance() {
        return new OurAppsFragment();
    }

    @Override
    protected OurAppsFragmentVM onCreateViewModel(FragmentOurAppsBinding binding) {
        return new OurAppsFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_our_apps;
    }
}
