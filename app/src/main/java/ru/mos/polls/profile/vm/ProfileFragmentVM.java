package ru.mos.polls.profile.vm;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.rxjava.RxEventDisposableSubscriber;
import ru.mos.polls.base.ui.rvdecoration.UIhelper;
import ru.mos.polls.base.vm.FragmentViewModel;
import ru.mos.polls.databinding.FragmentNewProfileBinding;
import ru.mos.polls.profile.ui.adapter.PagerAdapter;
import ru.mos.polls.profile.ui.fragment.AchievementTabFragment;
import ru.mos.polls.profile.ui.fragment.InfoTabFragment;
import ru.mos.polls.profile.ui.fragment.ProfileFragment;
import ru.mos.polls.profile.ui.fragment.UserTabFragment;

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
        pager.setOffscreenPageLimit(3);
        PagerAdapter adapter = new PagerAdapter(getFragment().getChildFragmentManager(), pages);
        pager.setAdapter(adapter);

        slidingTabs = binding.slidingTabs;
        slidingTabs.setupWithViewPager(pager);
        for (int index = 0; index < pages.size(); ++index) {
            slidingTabs
                    .getTabAt(index)
                    .setIcon(pages.get(index).getIconResId());
        }
        subscribeEventsBus();
    }

    private void subscribeEventsBus() {
        disposables.add(AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxEventDisposableSubscriber() {
                    @Override
                    public void onNext(Object o) {
                        if (o instanceof Events.ProfileEvents) {
                            Events.ProfileEvents action = (Events.ProfileEvents) o;
                            switch (action.getEventType()) {
                                case Events.ProfileEvents.PROFILE_LOADED:
                                    UIhelper.hideWithFadeView(false, getBinding().rootTab, getBinding().slidingTabs);
                                    break;
                            }
                        }
                    }
                }));
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        selectTab(getFragment().getStartPage());
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
