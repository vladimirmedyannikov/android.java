package ru.mos.polls.profile.ui.fragment;

import android.os.Bundle;

import com.android.databinding.library.baseAdapters.BR;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.FragmentBindSocialBinding;
import ru.mos.polls.profile.vm.BindingSocialFragmentVM;

/**
 * Экран привязки аккаунтов соцсетей к аккаунту аг
 *
 * @since 1.9
 */
public class BindingSocialFragment extends BindingFragment<BindingSocialFragmentVM, FragmentBindSocialBinding> {
    private static final String IS_TASK = "is_task";

    public static BindingSocialFragment newInstance(boolean isTask) {
        final BindingSocialFragment fragment = new BindingSocialFragment();
        Bundle args = new Bundle(1);
        args.putBoolean(IS_TASK, isTask);
        fragment.setArguments(args);
        return fragment;
    }

    public static BindingSocialFragment newInstance() {
        return new BindingSocialFragment();
    }

    @Override
    protected BindingSocialFragmentVM onCreateViewModel(FragmentBindSocialBinding binding) {
        return new BindingSocialFragmentVM(this, binding, getArguments() != null && getArguments().getBoolean(IS_TASK, false));
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_bind_social;
    }

    public boolean isQuestExecuted() {
        return getViewModel().isQuestExecuted();
    }
}
