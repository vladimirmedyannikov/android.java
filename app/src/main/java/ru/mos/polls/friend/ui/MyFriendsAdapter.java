package ru.mos.polls.friend.ui;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.friend.vm.FriendAddItemVW;
import ru.mos.polls.friend.vm.FriendItemVM;
import ru.mos.polls.rxhttp.rxapi.model.friends.Friend;
import ru.mos.polls.util.StubUtils;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 13.07.17 13:02.
 */

public class MyFriendsAdapter extends BaseRecyclerAdapter<RecyclerBaseViewModel> {

    public MyFriendsAdapter(Context context) {
        Gson gson = new Gson();
        List<Friend> friends = gson.fromJson(
                StubUtils.fromRawAsJsonArray(context, R.raw.friends_my).toString(),
                new TypeToken<List<Friend>>() {}.getType()
        );
        List<RecyclerBaseViewModel> content = new ArrayList<>();
        content.add(new FriendAddItemVW());
        for (Friend friend : friends) {
            content.add(new FriendItemVM(friend));
        }
        list = content;
    }

}
