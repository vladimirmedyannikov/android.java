package ru.mos.polls.navigation.actionbar;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;

import ru.mos.elk.BaseActivity;
import ru.mos.polls.R;
import ru.mos.polls.helpers.ActionBarHelper;

/**
 * Контроллер, упрощающий интеграцию на экране навигации через выпадающее меню в action bar
 * <p/>
 * ВАЖНО:
 * При использвоании навигации в actionBar во фрагментах
 * следует вызвать на onCreate ActionBarHelper.setNavigationModeList()
 * и в onDestroyView ActionBarNavigationController.destroyNavigation()
 *
 * @since 1.9
 */
public abstract class ActionBarNavigationController {
    /**
     * Переключение фрагментов для экрана списка городских новинок,
     *
     * @param activity
     */
    public static void setNoveltyNavigation(BaseActivity activity) {
        setNavigation(activity, ActionBarNavigationItem.Innovation.create(activity), ActionBarNavigationItem.Innovation.ACTIVE);
    }

    /**
     * Переключение фрагментов для экрана списков голосований
     *
     * @param activity
     */
    public static void setPollsNavigation(BaseActivity activity) {
        setNavigation(activity, ActionBarNavigationItem.Poll.create(activity), ActionBarNavigationItem.Poll.ACTIVE);
    }

    /**
     * Переключение фрагментов для экрана мероприятий
     *
     * @param activity
     */
    public static void setEventNavigation(BaseActivity activity) {
        setNavigation(activity, ActionBarNavigationItem.Event.create(activity), ActionBarNavigationItem.Event.CURRENT);
    }

    public static void setEventNavigationFromPast(BaseActivity activity) {
        setNavigation(activity, ActionBarNavigationItem.Event.create(activity), ActionBarNavigationItem.Event.PAST);
    }


    public static void setNavigation(BaseActivity activity, ActionBarNavigationItem[] items, int defaultItemId) {
        setNavigation(activity, items, R.id.container, defaultItemId, null);
    }

    public static void setNavigation(final BaseActivity activity, ActionBarNavigationItem[] items, final int parentLayoutId, int defaultItemId, final ItemSelectedListener itemSelectedListener) {
        if (activity != null) {
            final ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                ActionBarHelper.setNavigationModeList(activity);
                final FragmentManager fm = activity.getSupportFragmentManager();
                final ActionBarNavigationAdapter adapter = new ActionBarNavigationAdapter(activity, items);
                ActionBar.OnNavigationListener listener = new ActionBar.OnNavigationListener() {
                    @Override
                    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                        ActionBarNavigationItem item = adapter.getItem(itemPosition);
                        fm.beginTransaction()
                                .replace(parentLayoutId, item.getFragment(), item.getFragment().getClass().getName())
                                .commit();
                        if (itemSelectedListener != null) {
                            itemSelectedListener.onItemSelected(itemId);
                        }
                        return true;
                    }
                };
                actionBar.setListNavigationCallbacks(adapter, listener);
                if (defaultItemId != -1) {
                    ActionBarNavigationItem item = adapter.getItemById(defaultItemId);
                    int position = adapter.getPosition(item);
                    actionBar.setSelectedNavigationItem(position);
                    fm.beginTransaction()
                            .replace(parentLayoutId, item.getFragment(), item.getFragment().getClass().getName())
                            .commit();
                }
            }
        }
    }

    public static void destroyNavigation(BaseActivity activity) {
        ActionBarHelper.setNavigationModeStandard(activity);
    }

    public interface ItemSelectedListener {
        void onItemSelected(long itemId);
    }
}
