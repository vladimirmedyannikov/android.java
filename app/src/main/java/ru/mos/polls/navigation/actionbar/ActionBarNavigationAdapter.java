package ru.mos.polls.navigation.actionbar;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import butterknife.ButterKnife;
import ru.mos.polls.R;

/**
 * Адаптер, отвечающий за отображение выпадающего меню для навигации в action bar
 * Каждый пункт меню описан стуртурой данных {@link ru.mos.polls.navigation.actionbar.ActionBarNavigationItem}
 *
 * @since 1.9
 */
public class ActionBarNavigationAdapter extends ArrayAdapter<ActionBarNavigationItem> {
    public ActionBarNavigationItem[] items;

    public ActionBarNavigationAdapter(Context context, ActionBarNavigationItem[] objects) {
        super(context, R.layout.listitem_actionbar, objects);
        items = objects;
    }

    @Override
    public ActionBarNavigationItem getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public ActionBarNavigationItem getItemById(int id) {
        ActionBarNavigationItem result = null;
        for (int i = 0; i < items.length; ++i) {
            if (getItem(i).getId() == id) {
                result = getItem(i);
                break;
            }
        }
        return result;
    }

    public void setFragment(ActionBar actionBar, FragmentManager fragmentManager, int itemId) {
        ActionBarNavigationItem item = getItemById(itemId);
        if (item != null) {
            int position = getPosition(item);
            actionBar.setSelectedNavigationItem(position);
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container, item.getFragment())
                    .commit();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.listitem_actionbar, null);
        }
        TextView title = ButterKnife.findById(convertView, android.R.id.text1);
        title.setText(getItem(position).getTitle());
        return convertView;
    }

}
