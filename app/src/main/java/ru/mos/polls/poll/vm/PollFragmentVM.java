package ru.mos.polls.poll.vm;


import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.base.vm.FragmentViewModel;
import ru.mos.polls.databinding.FragmentPollBinding;
import ru.mos.polls.poll.ui.PollFragment;
import ru.mos.polls.poll.ui.PollBaseFragment;
import ru.mos.polls.profile.ui.adapter.PagerAdapter;

/**
 * Created by Trunks on 14.09.2017.
 */

public class PollFragmentVM extends FragmentViewModel<PollFragment, FragmentPollBinding> {
    private ViewPager pager;
    private TabLayout slidingTabs;
    List<PagerAdapter.Page> list;

    public PollFragmentVM(PollFragment fragment, FragmentPollBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentPollBinding binding) {
        getFragment().getActivity().setTitle(R.string.polls_title);
        List<PagerAdapter.Page> pages = getPages();
        pager = binding.pager;
        PagerAdapter adapter = new PagerAdapter(getFragment().getChildFragmentManager(), pages);
        pager.setAdapter(adapter);

        slidingTabs = binding.slidingTabs;
        slidingTabs.setupWithViewPager(pager);
        for (int index = 0; index < pages.size(); ++index) {
            slidingTabs
                    .getTabAt(index)
                    .setText(getActivity().getString(pages.get(index).getTitleResId()));
        }
    }

    protected List<PagerAdapter.Page> getPages() {
        list = new ArrayList<>();
        list.add(new PagerAdapter.Page(PollBaseFragment.newInstance(PollBaseFragmentVM.ARG_ACTIVE_POLL), R.string.polls_active));
        list.add(new PagerAdapter.Page(PollBaseFragment.newInstance(PollBaseFragmentVM.ARG_OLD_POLL), R.string.polls_old));
        return list;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PagerAdapter.Page page = list.get(pager.getCurrentItem());
        Fragment currFr = page.getFragment();
        currFr.onActivityResult(requestCode, resultCode, data);
    }
}
