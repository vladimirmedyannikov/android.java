package ru.mos.polls.electronichouse.ui.fragment;

import com.android.databinding.library.baseAdapters.BR;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.FragmentHousePollBinding;
import ru.mos.polls.electronichouse.vm.HousePollFragmentVM;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 01.12.17.
 */

public class HousePollFragment extends BindingFragment<HousePollFragmentVM, FragmentHousePollBinding> {

    public static HousePollFragment newInstance() {
        return new HousePollFragment();
    }

    @Override
    protected HousePollFragmentVM onCreateViewModel(FragmentHousePollBinding binding) {
        return new HousePollFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_house_poll;
    }
}
