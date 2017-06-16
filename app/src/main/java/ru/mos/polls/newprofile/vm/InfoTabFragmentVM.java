package ru.mos.polls.newprofile.vm;

import ru.mos.polls.databinding.LayoutInfoTabProfileBinding;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.ui.fragment.InfoTabFragment;

/**
 * Created by Trunks on 16.06.2017.
 */

public class InfoTabFragmentVM extends FragmentViewModel<InfoTabFragment, LayoutInfoTabProfileBinding> {
    public InfoTabFragmentVM(InfoTabFragment fragment, LayoutInfoTabProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutInfoTabProfileBinding binding) {

    }
}
