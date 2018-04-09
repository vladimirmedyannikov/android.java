package ru.mos.polls.news.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.FragmentNewsBinding;
import ru.mos.polls.news.vm.NewsFragmentVM;


public class NewsFragment extends BindingFragment<NewsFragmentVM, FragmentNewsBinding> {

    public static NewsFragment newInstance() {
        NewsFragment f = new NewsFragment();
        return f;
    }

    @Override
    protected NewsFragmentVM onCreateViewModel(FragmentNewsBinding binding) {
        return new NewsFragmentVM(this, binding);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /**
         * Помечаем все новости как прочитанные, такая логика используется с версии 1.9.6<br/>
         */
        BadgeManager.uploadAllNewsAsReaded((BaseActivity) getActivity());
        if (getActivity() != null)
            getActivity().setTitle(R.string.mainmenu_news);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_news;
    }
}
