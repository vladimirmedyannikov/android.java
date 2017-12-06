package ru.mos.polls.newquests.vm;

import ru.mos.polls.base.vm.PullablePaginationFragmentVM;
import ru.mos.polls.databinding.LayoutQuestsBinding;
import ru.mos.polls.newquests.adapter.QuestsItemAdapter;
import ru.mos.polls.newquests.ui.QuestsFragment;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 06.12.17.
 */

public class QuestsFragmentVM extends PullablePaginationFragmentVM<QuestsFragment, LayoutQuestsBinding, QuestsItemAdapter> {

    public QuestsFragmentVM(QuestsFragment fragment, LayoutQuestsBinding binding) {
        super(fragment, binding);
    }

    @Override
    public void doRequest() {

    }
}
