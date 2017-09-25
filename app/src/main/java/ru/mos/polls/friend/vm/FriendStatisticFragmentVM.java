package ru.mos.polls.friend.vm;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.profile.Achievements;
import ru.mos.elk.profile.Statistics;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.PullableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.ui.rvdecoration.UIhelper;
import ru.mos.polls.databinding.LayoutFriendProfileBinding;
import ru.mos.polls.friend.ui.adapter.FriendProfileAdapter;
import ru.mos.polls.friend.ui.fragment.FriendProfileTabFragment;
import ru.mos.polls.friend.ui.fragment.FriendStatisticFragment;
import ru.mos.polls.friend.ui.utils.FriendGuiUtils;
import ru.mos.polls.newprofile.ui.adapter.UserStatisticsAdapter;
import ru.mos.polls.newprofile.vm.AchievementTabFragmentVM;
import ru.mos.polls.rxhttp.rxapi.config.AgApiBuilder;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.friends.Friend;
import ru.mos.polls.rxhttp.rxapi.model.friends.Param;
import ru.mos.polls.rxhttp.rxapi.model.friends.service.FriendProfile;

/**
 * @author Sergey Elizarov (elizarov1988@gmail.com)
 *         on 14.08.17 21:30.
 */

public class FriendStatisticFragmentVM extends UIComponentFragmentViewModel<FriendStatisticFragment, LayoutFriendProfileBinding> {
    private FriendProfileAdapter adapter;

    CircleImageView friendImage;
    AppCompatTextView friendFI;
    AppCompatTextView friendRegDate;
    AppCompatTextView friendRating;
    AppCompatTextView friendStatus;
    LinearLayout achievementLayer;
    AppCompatTextView achievementsValue;
    View achievementPanel;
    RecyclerView recyclerView;
    View friendStatusInfo;
    Friend friend;

    public FriendStatisticFragmentVM(FriendStatisticFragment fragment, LayoutFriendProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutFriendProfileBinding binding) {
        Bundle extras = getFragment().getArguments();
        if (extras != null) {
            friend = (Friend) extras.getSerializable(FriendProfileTabFragment.ARG_FRIEND);
        }
        recyclerView = binding.list;
        UIhelper.setRecyclerList(recyclerView, getActivity());
        friendImage = binding.friendAvatar.avatar;
        friendFI = binding.friendStatusInfoPanel.agUserFi;
        friendRegDate = binding.friendStatusInfoPanel.agUserRegistrationDate;
        friendRating = binding.friendStatusInfoPanel.agUserRatingValue;
        friendStatus = binding.friendStatusInfoPanel.agUserStatusValue;
        achievementLayer = binding.friendStatusInfoPanel.agUserAchievementLayer;
        achievementsValue = binding.friendStatusInfoPanel.agUserAchievementValue;
        achievementPanel = binding.friendStatusInfoPanel.agUserAchievementPanel;
        friendStatusInfo = binding.friendStatusInfoPanel.agUserStatusLayout;
        adapter = new FriendProfileAdapter();
        recyclerView.setAdapter(adapter);
        loadFriendProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
        setFiView(FriendGuiUtils.getTitle(friend));
    }

    private void friendsStatsList(List<Param> params) { //todo привести в порядок убрать param
        List<Statistics> list = new ArrayList<>();
        for (Param param : params) {
            list.add(new Statistics(param.getTitle(), param.getValue()));
        }
        UserStatisticsAdapter userStatisticsAdapter = new UserStatisticsAdapter(list);
        recyclerView.setAdapter(userStatisticsAdapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(new PullableUIComponent(() -> {
                    progressable = getPullableProgressable();
                    loadFriendProfile();
                }))
                .with(new ProgressableUIComponent())
                .build();
    }

    public void setAchievementLayerView(List<Achievements> list) {
        achievementLayer.removeAllViews();
        if (list.size() > 0) {
            for (Achievements achievements : list) {
                UIhelper.addAchievements(achievementLayer, achievements.getImageUrl(), getActivity().getBaseContext());
            }
        } else {
            achievementPanel.setVisibility(View.GONE);
        }
    }

    private void loadFriendProfile() {
        HandlerApiResponseSubscriber<FriendProfile.Response.Result> handler
                = new HandlerApiResponseSubscriber<FriendProfile.Response.Result>(getFragment().getContext(), progressable) {
            @Override
            protected void onResult(FriendProfile.Response.Result result) {
                if (result.isProfileVisible()) {
                    if (result.getAchievements() != null) {
                        setAchievementLayerView(result.getAchievements().getLast());
                        setAchievementsCountView(result.getAchievements().getCount());
                    }
                    setFiView(result.getPersonal().getSurname() + result.getPersonal().getFirstName());
                    friendRegDate.setText(result.getPersonal().getRegistrationDate());
                    friendRating.setText(String.valueOf(result.getStatistics().getRating()));
                    friendStatus.setText(result.getStatistics().getStatus());
                    FriendGuiUtils.loadAvatar(friendImage, AgApiBuilder.resourceURL(result.getPersonal().getAvatar()));
                    friendsStatsList(result.getStatistics().getParams());
                } else {
                    setFriendInvisibleView();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                setFriendInvisibleView();
            }
        };
        AGApplication
                .api
                .friendProfile(new FriendProfile.Request(friend.getId()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }

    public void setFriendInvisibleView() {
        AGApplication.bus().send(new Events.FriendEvents(Events.FriendEvents.FRIEND_INVISIBLE));
        friendImage.setImageResource(R.drawable.ic_avatar_default);
        recyclerView.setVisibility(View.GONE);
        friendStatusInfo.setVisibility(View.INVISIBLE);
    }

    public void setAchievementsCountView(int count) {
        int value = count - 3;
        if (value > 0) {
            achievementsValue.setText("+" + String.valueOf(value));
            achievementsValue.setVisibility(View.VISIBLE);
        } else {
            achievementsValue.setVisibility(View.GONE);
        }
    }

    private void setFiView(String text) {
        friendFI.setText(text);
    }
}