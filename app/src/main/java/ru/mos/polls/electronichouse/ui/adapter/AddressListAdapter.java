package ru.mos.polls.electronichouse.ui.adapter;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.MainActivity;
import ru.mos.polls.R;
import ru.mos.polls.electronichouse.model.ItemAddress;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 01.12.17.
 */

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.Holder> {

    private List<ItemAddress> addresses;
    private MainActivity mainActivity;

    public AddressListAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        addresses = ItemAddress.getDefault(mainActivity);
    }

    @Override
    public AddressListAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_house_address, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(AddressListAdapter.Holder holder, int position) {
        holder.title.setText(addresses.get(position).getTitle());
        holder.value.setText(addresses.get(position).getValue());
        holder.textContainer.setOnClickListener(v -> addresses.get(position).doAction());
    }

    public void refreshData() {
        addresses = ItemAddress.getDefault(mainActivity);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.value)
        TextView value;
        @BindView(R.id.text_container)
        View textContainer;
        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
