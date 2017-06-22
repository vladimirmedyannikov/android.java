package ru.mos.polls.newprofile.vm;

import android.content.Intent;
import android.support.v7.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.AGApplication;
import ru.mos.polls.databinding.LayoutUserTabProfileBinding;
import ru.mos.polls.newprofile.model.UserStatistics;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.newprofile.ui.adapter.UserStatisticsAdapter;
import ru.mos.polls.newprofile.ui.fragment.AvatarPanelClickListener;
import ru.mos.polls.newprofile.ui.fragment.UserTabFragment;

/**
 * Created by Trunks on 08.06.2017.
 */

public class UserTabFragmentVM extends BaseTabFragmentVM<UserTabFragment, LayoutUserTabProfileBinding> implements AvatarPanelClickListener {

    private SwitchCompat enableProfileVisibility;

    public UserTabFragmentVM(UserTabFragment fragment, LayoutUserTabProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutUserTabProfileBinding binding) {
        recyclerView = binding.agUserProfileList;
        super.initialize(binding);
        enableProfileVisibility = binding.agUserProfileVisibility;
        circleImageView = binding.agUserAvatarPanel.agUserImage;
        binding.setAgUser(saved);
        binding.setClickListener(this);
        setRecyclerList();
    }

    private void mockUserStatsList() {
        List<UserStatistics> list = new ArrayList<>();
        list.add(new UserStatistics("Заполненность профиля", "95%"));
        list.add(new UserStatistics("Пройдено голосований", "257"));
        list.add(new UserStatistics("Оценено новинок", "257"));
        list.add(new UserStatistics("Оценено новинок", "17"));
        list.add(new UserStatistics("Посещено мероприятий", "5"));
        list.add(new UserStatistics("Приглашено друзей", "0"));
        list.add(new UserStatistics("Активность в социальных сетях", "0"));
        list.add(new UserStatistics("Получено баллов", "0"));
        list.add(new UserStatistics("Потрачено баллов", "0"));
        UserStatisticsAdapter userStatisticsAdapter = new UserStatisticsAdapter(list);
        recyclerView.setAdapter(userStatisticsAdapter);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        mockUserStatsList();
        setAvatar();
    }
    @Override
    public void makePhoto() {
        showChooseMediaDialog();
    }

    @Override
    public void editUserInfo() {
        AGApplication.bus().send(new Events.ProfileEvents(Events.ProfileEvents.EDIT_USER_INFO));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getCropedUri(requestCode, resultCode, data);
    }
}
