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

public class FriendsAdapter extends BaseRecyclerAdapter<RecyclerBaseViewModel> {

    public interface Type {
        int ITEM_ADD_FRIEND_BUTTON = 0;
        int ITEM_FRIEND = 1;
    }

    public static List<RecyclerBaseViewModel> getStubRVM(Context context) {
        List<Friend> friends = getStub(context);
        List<RecyclerBaseViewModel> result = new ArrayList<>();
        result.add(new FriendAddItemVW(null));
        for (Friend friend : friends) {
            result.add(new FriendItemVM(friend));
        }
        return result;
    }

    public static List<Friend> getStub(Context context) {
        return new Gson().fromJson(
                StubUtils.fromRawAsJsonArray(context, R.raw.friends_my).toString(),
                new TypeToken<List<Friend>>() {}.getType()
        );
    }

    public void add(List<Friend> friends) {
        List<RecyclerBaseViewModel> content = new ArrayList<>();
        for (Friend friend : friends) {
            content.add(new FriendItemVM(friend));
        }
        addData(content);
    }

    public void add(Friend friend) {
        add(new FriendItemVM(friend));
    }

    public boolean has(String phone) {
        boolean result = false;
        for (RecyclerBaseViewModel item : list) {
            if (item instanceof FriendItemVM) {
                FriendItemVM friendItemVM = (FriendItemVM) item;
                if (friendItemVM.getModel().getPhone().equalsIgnoreCase(phone)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}
