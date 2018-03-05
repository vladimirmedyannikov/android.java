package ru.mos.polls.profile.ui.adapter;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.profile.model.UserInfo;
import ru.mos.polls.profile.vm.UserInfoVM;

public class UserInfoAdapter extends BaseRecyclerAdapter<UserInfoVM> {

    private View.OnClickListener onItemClickListener;

    public UserInfoAdapter(View.OnClickListener onItemClickListener, List<UserInfo> list) {
        this.onItemClickListener = onItemClickListener;
        add(list);
    }

    public void add(List<UserInfo> list) {
        List<UserInfoVM> content = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            UserInfo item = list.get(i);
            UserInfoVM userInfoVM = new UserInfoVM(item, i != 0 ?onItemClickListener : null);
            content.add(userInfoVM);
        }
        addData(content);
    }
}
