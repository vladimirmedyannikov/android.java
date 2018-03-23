package ru.mos.polls.profile.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.android.databinding.library.baseAdapters.BR;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.FragmentBindSocialBinding;
import ru.mos.polls.profile.vm.BindingSocialFragmentVM;
import ru.mos.polls.quests.controller.QuestStateController;

/**
 * Экран привязки аккаунтов соцсетей к аккаунту аг
 *
 * @since 1.9
 */
public class BindingSocialFragment extends NavigateFragment<BindingSocialFragmentVM, FragmentBindSocialBinding> {
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

    @Override
    public boolean onUpPressed() {
        return onBackPressed();
    }

    @Override
    public boolean onBackPressed() {
        if (getViewModel().isQuestExecuted()) {
            QuestStateController.getInstance().updateSocialUnavaible();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.quest_task_done);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    navigateToCloseCurrActivity();
                }
            });
            builder.show();
            return true;
        } else return super.onBackPressed();
    }
}
