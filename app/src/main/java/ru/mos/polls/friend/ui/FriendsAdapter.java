package ru.mos.polls.friend.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.databinding.FriendItemBinding;
import ru.mos.polls.rxhttp.rxapi.model.friends.Friend;
import ru.mos.polls.util.StubUtils;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 12.07.17 11:44.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriedItemViewHolder> {

    public static FriendsAdapter createStub(Context context) {
        Gson gson = new Gson();
        List<Friend> content = gson.fromJson(
                StubUtils.fromRawAsJsonArray(context, R.raw.friends_my).toString(),
                new TypeToken<List<Friend>>() {}.getType()
        );
        FriendsAdapter result = new FriendsAdapter();
        result.addContent(content);
        return result;
    }

    private List<Friend> content = new ArrayList<>();

    @Override
    public FriedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        FriendItemBinding binding = FriendItemBinding.inflate(inflater, parent, false);
        return new FriedItemViewHolder(binding.getRoot());
    }

    public List<Friend> getContent() {
        return content;
    }

    @Override
    public void onBindViewHolder(final FriedItemViewHolder holder, int position) {
        Friend friend = getContent().get(position);
        holder.binding.setFriend(friend);
        holder.binding.setCallback(new Callback() {
            @Override
            public void onClick(Friend selectedFriend) {

            }
        });
    }

    public void addContent(List<Friend> content) {
        this.content.addAll(content);
    }

    public void clearContent() {
        content.clear();
    }

    boolean hasData() {
        return getItemCount() > 0;
    }

    @Override
    public int getItemCount() {
        return content.size();
    }

    class FriedItemViewHolder extends RecyclerView.ViewHolder {
        FriendItemBinding binding;

        FriedItemViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public interface Callback {
        void onClick(Friend friend);
    }
}
