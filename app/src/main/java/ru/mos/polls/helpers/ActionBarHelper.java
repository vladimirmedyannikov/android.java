package ru.mos.polls.helpers;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ru.mos.polls.R;
import ru.mos.polls.base.activity.BaseActivity;


public abstract class ActionBarHelper {
    private static ActionBar actionBar;

    public static void setNavigationModeList(BaseActivity actionBarActivity) {
        actionBar = actionBarActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setIcon(null);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        }
    }

    public static void setNavigationModeStandard(BaseActivity actionBarActivity) {
        actionBar = actionBarActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_USE_LOGO);
        }
    }

    public static void displayTextCustomActionBar(BaseActivity activity, String item, View.OnClickListener onClickListener) {
        String title = getTitle(activity);
        displayTextCustomActionBar(activity, title, item, onClickListener);
    }

    public static void displayIconCustomActionBar(BaseActivity activity, int icon, String hint, View.OnClickListener onClickListener) {
        String title = getTitle(activity);
        displayIconCustomActionBar(activity, title, icon, hint, onClickListener);
    }

    public static void displayTextCustomActionBar(BaseActivity activity, String title, String item, View.OnClickListener onClickListener) {
        View view = getTextItemView(activity, title, item, onClickListener);
        setupViewToActionBar(activity, view);
    }

    public static void displayIconCustomActionBar(BaseActivity activity, String title, int icon, String hint, View.OnClickListener onClickListener) {
        View view = getIconItemView(activity, title, icon, hint, onClickListener);
        setupViewToActionBar(activity, view);
    }

    public static void hideCustomActionBar() {
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
        }
    }

    public static void hideCustomActionBar(AppCompatActivity activity) {
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(false);
        }
    }

    private static String getTitle(Activity activity) {
        String result;
        ActionBar actionBar = ((android.support.v7.app.AppCompatActivity) activity).getSupportActionBar();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && actionBar != null) {
            result = actionBar.getTitle().toString();
        } else {
            result = activity.getTitle().toString();
        }
        return result;
    }

    private static void setupViewToActionBar(BaseActivity activity, View view) {
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setIcon(activity.getResources().getDrawable(R.drawable.nb_top_icon_old));
        actionBar.setCustomView(view);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    private static View getIconItemView(final BaseActivity activity, String title, int icon, final String hint, View.OnClickListener onClickListener) {
        View view = View.inflate(activity, R.layout.layout_icon_action_bar, null);

        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(title);

        LinearLayout itemSelector = (LinearLayout) view.findViewById(R.id.itemSelector);
        itemSelector.setOnClickListener(onClickListener);

        itemSelector.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(activity, hint, Toast.LENGTH_LONG).show();
                return true;
            }
        });

        ImageView itemView = (ImageView) view.findViewById(R.id.icon);
        itemView.setImageResource(icon);
        itemView.setOnClickListener(onClickListener);

        return view;
    }

    private static View getTextItemView(BaseActivity activity, String title, String item, View.OnClickListener onClickListener) {
        View view = View.inflate(activity, R.layout.layout_text_action_bar, null);

        LinearLayout itemSelector = (LinearLayout) view.findViewById(R.id.itemSelector);
        itemSelector.setOnClickListener(onClickListener);

        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(title);

        TextView itemView = (TextView) view.findViewById(R.id.item);
        itemView.setText(item.toUpperCase());

        return view;
    }
}
