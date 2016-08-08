package ru.mos.polls.about;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.R;

/**
 * Структура данных, описывающая пункт меню экрана "О приложении" {@link ru.mos.polls.about.AboutAppFragment}
 *
 * @since 1.9
 */
public class AboutItem {
    public static final int OUR_APPS = 0;
    public static final int ABOUT_PROJECT = 1;
    public static final int INSTRUCTION = 2;
    public static final int USER_GUIDE = 3;
    public static final int OFFER = 4;
    public static final int SHARE_SOCIAL = 5;
    public static final int RATE_APP = 6;
    public static final int FEEDBACK = 7;

    public static final AboutItem[] ITEMS = new AboutItem[]{
            new AboutItem(ABOUT_PROJECT, R.string.title_about_project),
            new AboutItem(INSTRUCTION, R.string.title_instruction),
            new AboutItem(USER_GUIDE, R.string.title_user_guide),
            new AboutItem(OUR_APPS, R.string.our_apps),
            new AboutItem(OFFER, R.string.title_offer),
            new AboutItem(SHARE_SOCIAL, R.string.title_tell_to_friends),
            new AboutItem(RATE_APP, R.string.title_rate_this_app),
            new AboutItem(FEEDBACK, R.string.feedback)
    };

    private int id;
    private int title;

    public AboutItem(int id, int title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public int getTitle() {
        return title;
    }

    public static ArrayAdapter getAdapter(Context context) {
        return new Adapter(context, ITEMS);
    }

    private static class Adapter extends ArrayAdapter<AboutItem> {

        public Adapter(Context context, AboutItem[] objects) {
            super(context, -1, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AboutItemHolder holder;
            if (convertView != null) {
                holder = (AboutItemHolder) convertView.getTag();
            } else {
                convertView = View.inflate(getContext(), R.layout.item_about, null);
                holder = new AboutItemHolder(convertView);
                convertView.setTag(holder);
            }
            AboutItem popupItem = getItem(position);
            holder.text.setText(popupItem.getTitle());
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }
    }

    static class AboutItemHolder {
        @BindView(R.id.text)
        TextView text;

        public AboutItemHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
