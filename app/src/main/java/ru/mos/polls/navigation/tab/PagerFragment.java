package ru.mos.polls.navigation.tab;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.mos.polls.R;
import ru.mos.polls.helpers.FragmentHelper;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.poll.gui.ActivePollsFragment;
import ru.mos.polls.poll.gui.OldPollsFragment;
import ru.mos.polls.profile.gui.fragment.AchievementsFragment;
import ru.mos.polls.profile.gui.fragment.ProfileFragment;

/**
 * Общий класс фрагмента, внутри которого
 * будут располагаться табы с другими фрагментами
 *
 * @see PagerFragment.Polls
 * @see PagerFragment.Profile
 * @since 1.9.2
 */
public abstract class PagerFragment extends Fragment {
    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;

    @BindView(R.id.pager)
    ViewPager pager;
    private Unbinder unbinder;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TitleHelper.setTitle(getActivity(), getTitle());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_pager, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
        pager.setAdapter(getAdapter());
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);
        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                pager.setCurrentItem(position);
            }
        });
        pager.setCurrentItem(0);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    abstract FragmentStatePagerAdapter getAdapter();

    abstract String getTitle();

    /**
     * Общий класс адаптера для отображения табов фрагментов
     *
     * @see PagerFragment.Profile.Adapter
     * @see PagerFragment.Polls.Adapter
     * @since 1.9.2
     */
    public abstract class StateAdapter extends FragmentStatePagerAdapter {
        private static final int TABS = 2;

        public StateAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return TABS;
        }
    }

    /**
     * Используется для отображения табов для профиля
     */
    public static class Profile extends PagerFragment {
        public static PagerFragment newInstance() {
            return new Profile();
        }

        @Override
        FragmentStatePagerAdapter getAdapter() {
            return new Adapter(getChildFragmentManager());
        }

        @Override
        String getTitle() {
            return getString(R.string.profile);
        }

        private class Adapter extends StateAdapter {

            public Adapter(FragmentManager fm) {
                super(fm);
            }

            @Override
            public Fragment getItem(int position) {
                Fragment result = new Fragment();
                switch (position) {
                    case 0:
                        result = ProfileFragment.newInstance();
                        break;
                    case 1:
                        result = AchievementsFragment.newInstance();
                        break;
                }
                return result;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                String result = "";
                switch (position) {
                    case 0:
                        result = getString(R.string.my_personal_data);
                        break;
                    case 1:
                        result = getString(R.string.achievements);
                        break;
                }
                return result;
            }
        }
    }

    /**
     * Ипользуется для отображения табов для голосований
     */
    public static class Polls extends PagerFragment {
        public static PagerFragment newInstance() {
            return new Polls();
        }

        @Override
        String getTitle() {
            return getString(R.string.polls_title);
        }

        @Override
        FragmentStatePagerAdapter getAdapter() {
            return new Adapter(getChildFragmentManager());
        }

        private class Adapter extends StateAdapter {
            private ActivePollsFragment activePollsFragment;

            public Adapter(FragmentManager fm) {
                super(fm);
            }

            @Override
            public Fragment getItem(int position) {
                Fragment result = new Fragment();
                switch (position) {
                    case 0:
                        result = ActivePollsFragment.newInstance();
                        activePollsFragment = (ActivePollsFragment) result;
                        break;
                    case 1:
                        result = OldPollsFragment.newInstance();
                        activePollsFragment.setPollRemoveListener((ActivePollsFragment.PollRemoveListener) result);
                        break;
                }
                return result;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                String result = "";
                switch (position) {
                    case 0:
                        result = getString(R.string.polls_active);
                        break;
                    case 1:
                        result = getString(R.string.polls_old);
                        break;
                }
                return result;
            }
        }
    }
}
