package ru.mos.polls.newprofile.vm;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.mos.elk.profile.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutUserTabProfileBinding;
import ru.mos.polls.newprofile.model.UserStatistics;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.newprofile.ui.adapter.UserStatsAdapter;
import ru.mos.polls.newprofile.ui.fragment.UserTabClickListener;
import ru.mos.polls.newprofile.ui.fragment.UserTabFragment;

/**
 * Created by Trunks on 08.06.2017.
 */

public class UserTabFragmentVM extends FragmentViewModel<UserTabFragment, LayoutUserTabProfileBinding> implements UserTabClickListener {

    private AgUser changed, saved;
    private RecyclerView recyclerView;
    private SwitchCompat enableProfileVisibility;

    public UserTabFragmentVM(UserTabFragment fragment, LayoutUserTabProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutUserTabProfileBinding binding) {
        saved = new AgUser(getActivity());
        recyclerView = binding.agUserStatisticsList;
        enableProfileVisibility = binding.agUserProfileVisibility;
        binding.setAgUser(saved);
        binding.setClickListener(this);
        setRecyclerList();
        mockUserStatsList();
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
        UserStatsAdapter adapter = new UserStatsAdapter(list);
        recyclerView.setAdapter(adapter);
    }

    private void setRecyclerList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getFragment().getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        Drawable dividerDrawable = ContextCompat.getDrawable(getFragment().getContext(), R.drawable.divider);
        DividerItemDecoration did = new DividerItemDecoration(getFragment().getContext(), DividerItemDecoration.VERTICAL);
        did.setDrawable(dividerDrawable);
        recyclerView.addItemDecoration(did);
    }

    @Override
    public void makePhoto() {
        Toast.makeText(getFragment().getContext(), "make photo", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void editUserInfo() {
        AGApplication.bus().send(new Events.ProfileEvents(Events.ProfileEvents.EDIT_USER_INFO));
    }

    @Override
    public void enableProfileVisibility(boolean enable) {
        Toast.makeText(getFragment().getContext(), "enableProfileVisibility = " + enable, Toast.LENGTH_SHORT).show();
    }
}
