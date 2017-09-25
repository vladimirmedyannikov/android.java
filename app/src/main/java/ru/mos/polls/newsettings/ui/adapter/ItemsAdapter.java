package ru.mos.polls.newsettings.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.newsettings.model.Item;

/**
 * Created by matek3022 on 25.09.17.
 */

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    private Item[] items;
    private ItemClickListener listener;
    public ItemsAdapter(Item[] items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settings, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemSettings.setText(items[position].getTextId());
        holder.itemSettings.setCompoundDrawablesWithIntrinsicBounds(items[position].getDrawableId(), 0, 0, 0);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(items[position]);
        });
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public void setOnItemClickListener (ItemClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text)
        TextView itemSettings;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemClickListener {
        void onClick(Item item);
    }
}
