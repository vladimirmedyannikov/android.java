package ru.mos.polls.newpoll.vm;


import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.base.vm.FragmentViewModel;
import ru.mos.polls.databinding.FragmentPollBinding;
import ru.mos.polls.newpoll.ui.PollFragment;
import ru.mos.polls.newprofile.ui.adapter.PagerAdapter;
import ru.mos.polls.newprofile.ui.fragment.AchievementTabFragment;
import ru.mos.polls.newprofile.ui.fragment.UserTabFragment;

/**
 * Created by Trunks on 14.09.2017.
 */

public class PollFragmentVM extends FragmentViewModel<PollFragment, FragmentPollBinding> {
    private ViewPager pager;
    private TabLayout slidingTabs;

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
        List<PagerAdapter.Page> result = new ArrayList<>();
        result.add(new PagerAdapter.Page(UserTabFragment.newInstance(), R.string.polls_active));
        result.add(new PagerAdapter.Page(new AchievementTabFragment(), R.string.polls_old));
        return result;
    }
}
