package ru.mos.polls.quests.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.databinding.library.baseAdapters.BR;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.MenuBindingFragment;
import ru.mos.polls.databinding.LayoutQuestsBinding;
import ru.mos.polls.quests.vm.QuestsFragmentVM;


public class QuestsFragment extends MenuBindingFragment<QuestsFragmentVM, LayoutQuestsBinding> {

    private QuestsFragmentVM.Listener listener = QuestsFragmentVM.Listener.STUB;

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

    @Override
    public int getMenuResource() {
        return R.menu.main;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getViewModel() != null) {
            getViewModel().setListener(listener);
        }
    }

    public void setListener(QuestsFragmentVM.Listener listener) {
        this.listener = listener;
    }
}
