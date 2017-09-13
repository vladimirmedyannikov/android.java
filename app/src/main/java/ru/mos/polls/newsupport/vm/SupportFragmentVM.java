package ru.mos.polls.newsupport.vm;

import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.LayoutSupportBinding;
import ru.mos.polls.newsupport.ui.fragment.SupportFragment;

/**
 * Created by matek3022 on 13.09.17.
 */

public class SupportFragmentVM extends UIComponentFragmentViewModel<SupportFragment, LayoutSupportBinding> {

    public SupportFragmentVM(SupportFragment fragment, LayoutSupportBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutSupportBinding binding) {

    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return null;
    }
}
