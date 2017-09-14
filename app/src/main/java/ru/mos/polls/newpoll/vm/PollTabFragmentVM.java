package ru.mos.polls.newpoll.vm;

import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.PullableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentTabPollBinding;
import ru.mos.polls.newpoll.ui.PollTabFragment;
import ru.mos.polls.newpoll.ui.adapter.PollAdapter;

/**
 * Created by Trunks on 14.09.2017.
 */

public class PollTabFragmentVM extends UIComponentFragmentViewModel<PollTabFragment, FragmentTabPollBinding> {
    PollAdapter adapter;

    public PollTabFragmentVM(PollTabFragment fragment, FragmentTabPollBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentTabPollBinding binding) {

    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(new PullableUIComponent(() -> {
                    progressable = getPullableProgressable();
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                }))
                .with(new ProgressableUIComponent())
                .build();
    }
}
