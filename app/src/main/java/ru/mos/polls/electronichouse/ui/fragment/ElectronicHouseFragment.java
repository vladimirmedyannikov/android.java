package ru.mos.polls.electronichouse.ui.fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.LayoutElectronicHouseBinding;
import ru.mos.polls.electronichouse.vm.ElectronicHouseFragmentVM;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 01.12.17.
 */

public class ElectronicHouseFragment extends NavigateFragment<ElectronicHouseFragmentVM, LayoutElectronicHouseBinding> {

    public static ElectronicHouseFragment newInstance() {
        return new ElectronicHouseFragment();
    }


    @Override
    protected ElectronicHouseFragmentVM onCreateViewModel(LayoutElectronicHouseBinding binding) {
        return new ElectronicHouseFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_electronic_house;
    }
}
