package ru.mos.polls.support.ui.fragment;

import android.os.Bundle;

import me.ilich.juggler.gui.JugglerFragment;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.MenuBindingFragment;
import ru.mos.polls.databinding.FragmentFeedbackBinding;
import ru.mos.polls.support.vm.FeedBackFragmentVM;

public class FeedBackFragment extends MenuBindingFragment<FeedBackFragmentVM, FragmentFeedbackBinding> {


    private static final String ARG_START_WITH_NEW_ACTIVITY = "ru.mos.polls.newsupport.ui.fragment.start_with_new_activity";

    public static JugglerFragment instance(boolean startWithNewActivity) {
        FeedBackFragment f = new FeedBackFragment();
        Bundle b = new Bundle(1);
        b.putBoolean(ARG_START_WITH_NEW_ACTIVITY, startWithNewActivity);
        f.setArguments(b);
        return f;
    }

    public boolean isStartWithNewActivity() {
        return getArguments().getBoolean(ARG_START_WITH_NEW_ACTIVITY);
    }

    @Override
    public int getMenuResource() {
        return R.menu.feedback;
    }

    @Override
    protected FeedBackFragmentVM onCreateViewModel(FragmentFeedbackBinding binding) {
        return new FeedBackFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_feedback;
    }
}
