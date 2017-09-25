package ru.mos.polls.newabout.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.R;

/**
 * Структура данных, описывающая пункт меню экрана "О приложении" {@link ru.mos.polls.newabout.vm.AboutAppFragmentVM}
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
            /*new AboutItem(INSTRUCTION, R.string.title_instruction),*/
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

    public static RecyclerView.Adapter getAdapter(RVAdapter.OnItemClickListener listener) {
        return new RVAdapter(ITEMS, listener);
    }

    public static class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder>{
        OnItemClickListener listener;
        List<AboutItem> list;

        public RVAdapter(AboutItem[] objects, OnItemClickListener listener) {
            this.listener = listener;
            list = new ArrayList<>();
            for (int i = 0; i < objects.length; i++) {
                list.add(objects[i]);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_about, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(list.get(position).getTitle());
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onClick(list.get(position).getId(), position);
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.text)
            TextView text;
            ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public long getItemId(int position) {
            return list.get(position).getId();
        }

        public interface OnItemClickListener{
            void onClick(int id, int position);
        }
    }
}
