package ru.mos.polls.electronichouse.vm;

import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentHousePollBinding;
import ru.mos.polls.electronichouse.ui.fragment.HousePollFragment;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 01.12.17.
 */

public class HousePollFragmentVM extends UIComponentFragmentViewModel<HousePollFragment, FragmentHousePollBinding>{

    public HousePollFragmentVM(HousePollFragment fragment, FragmentHousePollBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentHousePollBinding binding) {

    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return null;
    }
}
