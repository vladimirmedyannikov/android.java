package ru.mos.polls.newprofile.vm;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentNewProfileBinding;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.ui.adapter.PagerAdapter;
import ru.mos.polls.newprofile.ui.fragment.AchievementTabFragment;
import ru.mos.polls.newprofile.ui.fragment.InfoTabFragment;
import ru.mos.polls.newprofile.ui.fragment.ProfileFragment;
import ru.mos.polls.newprofile.ui.fragment.UserTabFragment;

/**
 * Created by wlTrunks on 07.06.2017.
 */

public class ProfileFragmentVM extends FragmentViewModel<ProfileFragment, FragmentNewProfileBinding> {
    private ViewPager pager;
    private TabLayout slidingTabs;

    public ProfileFragmentVM(ProfileFragment fragment, FragmentNewProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentNewProfileBinding binding) {
        List<PagerAdapter.Page> pages = getPages();
        pager = binding.pager;
        PagerAdapter adapter = new PagerAdapter(getFragment().getChildFragmentManager(), pages);
        pager.setAdapter(adapter);

        slidingTabs = binding.slidingTabs;
        slidingTabs.setupWithViewPager(pager);
        for (int index = 0; index < pages.size(); ++index) {
            slidingTabs
                    .getTabAt(index)
                    .setIcon(pages.get(index).getIconResId());
        }
    }

    protected void selectTab(int tabNumber) {
        slidingTabs.setScrollPosition(tabNumber, 0f, true);
        pager.setCurrentItem(tabNumber);
    }

    protected List<PagerAdapter.Page> getPages() {
        List<PagerAdapter.Page> result = new ArrayList<>();
        result.add(new PagerAdapter.Page(R.drawable.ic_user, UserTabFragment.newInstance()));
        result.add(new PagerAdapter.Page(R.drawable.ic_trophy, new AchievementTabFragment()));
        result.add(new PagerAdapter.Page(R.drawable.ic_list, new InfoTabFragment()));
        return result;
    }
}
