package ru.mos.polls.ourapps;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.FragmentOurAppsBinding;


public class OurAppsFragment extends NavigateFragment<OurAppsFragmentVM, FragmentOurAppsBinding> {
    @Override
    protected OurAppsFragmentVM onCreateViewModel(FragmentOurAppsBinding binding) {
        return new OurAppsFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_our_apps;
    }
}
