package ru.mos.polls.navigation.drawer;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ru.mos.polls.R;
import ru.mos.polls.badge.model.BadgesSource;

public class NavigationMenuAdapter extends BaseAdapter {

    private final Context context;
    private BadgesSource badgesSource;
    private final NavigationMenuItem[] items;


    public NavigationMenuAdapter(Context c, NavigationMenuItem[] items) {
        this.context = c;
        badgesSource = BadgesSource.getInstance();
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public NavigationMenuItem getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        final NavigationMenuItem item = getItem(position);
        return item.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_navmenu, null);
        }

        final NavigationMenuItem item = getItem(position);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        TextView tvCount = (TextView) convertView.findViewById(R.id.tvCount);
        String bageText = badgesSource.get(item.getBageTag());
        if (TextUtils.isEmpty(bageText)) {
            tvCount.setVisibility(View.GONE);
        } else {
            tvCount.setText(bageText);
            tvCount.setVisibility(View.VISIBLE);
        }
        tvTitle.setText(item.getTextId());
        return convertView;
    }

}
