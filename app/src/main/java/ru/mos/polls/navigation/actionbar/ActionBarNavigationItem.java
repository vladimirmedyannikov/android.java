package ru.mos.polls.navigation.actionbar;

import android.content.Context;
import android.support.v4.app.Fragment;

import ru.mos.polls.R;
import ru.mos.polls.event.gui.fragment.CurrentEventsFragment;
import ru.mos.polls.event.gui.fragment.PastEventsFragment;
import ru.mos.polls.event.gui.fragment.VisitedEventsFragment;
import ru.mos.polls.innovation.gui.fragment.ActiveInnovationsFragment;
import ru.mos.polls.innovation.gui.fragment.OldInnovationsFragment;
import ru.mos.polls.innovation.gui.fragment.PassedInnovationsFragment;
import ru.mos.polls.profile.gui.fragment.AchievementsFragment;
import ru.mos.polls.profile.gui.fragment.ProfileFragment;

/**
 * Структура данных, описывающая пункт выпадающего меню, исопльзуемого для навигации в action bar
 * Пункты навигации, сгруппированы по экранам на которых исопльзуются
 *
 * @since 1.9
 */
public class ActionBarNavigationItem {
    public static class Innovation {
        public static final int ACTIVE = 0;
        public static final int PASSED = 1;
        public static final int OLD = 2;

        public static ActionBarNavigationItem[] create(Context context) {
            return new ActionBarNavigationItem[]{
                    new ActionBarNavigationItem(ACTIVE, context.getString(R.string.innovation_active), new ActiveInnovationsFragment()),
                    new ActionBarNavigationItem(PASSED, context.getString(R.string.innovation_passed), new PassedInnovationsFragment()),
                    new ActionBarNavigationItem(OLD, context.getString(R.string.innovation_old), new OldInnovationsFragment())
            };
        }
    }

    public static class Event {
        public static final int CURRENT = 0;
        public static final int VISITED = 1;
        public static final int PAST = 2;

        public static ActionBarNavigationItem[] create(Context context) {
            return new ActionBarNavigationItem[]{
                    new ActionBarNavigationItem(CURRENT, context.getString(R.string.events_current), CurrentEventsFragment.newInstance()),
                    new ActionBarNavigationItem(VISITED, context.getString(R.string.events_visited), VisitedEventsFragment.newInstance()),
                    new ActionBarNavigationItem(PAST, context.getString(R.string.events_past), PastEventsFragment.newInstance())
            };
        }
    }

    public static class Poll {
        public static final int ACTIVE = 0;
        public static final int PASSED = 1;
        public static final int OLD = 2;

        public static ActionBarNavigationItem[] create(Context context) {
            return new ActionBarNavigationItem[]{
            };
        }

    }

    public static class Profile {
        public static final int STATISTICS = 0;
        public static final int ACHIEVEMENTS = 1;
        public static final int PROFILE = 2;

        public static ActionBarNavigationItem[] create(Context context) {
            return new ActionBarNavigationItem[]{
                    new ActionBarNavigationItem(PROFILE, context.getString(R.string.profile), ProfileFragment.newInstance()),
                    new ActionBarNavigationItem(ACHIEVEMENTS, context.getString(R.string.achievements), AchievementsFragment.newInstance())
            };
        }

    }

    private int id;
    private String title;
    private Fragment fragment;

    public ActionBarNavigationItem(int id, String title, Fragment fragment) {
        this.id = id;
        this.title = title;
        this.fragment = fragment;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Fragment getFragment() {
        return fragment;
    }

    @Override
    public String toString() {
        return title;
    }
}
