package ru.mos.polls.newquests.ui;

import com.android.databinding.library.baseAdapters.BR;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.LayoutQuestsBinding;
import ru.mos.polls.newquests.vm.QuestsFragmentVM;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 06.12.17.
 */

public class QuestsFragment extends BindingFragment<QuestsFragmentVM, LayoutQuestsBinding> {

    public static QuestsFragment instance() {
        return new QuestsFragment();
    }

    @Override
    protected QuestsFragmentVM onCreateViewModel(LayoutQuestsBinding binding) {
        return new QuestsFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_quests;
    }
}
