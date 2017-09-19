package ru.mos.polls.newprofile.vm;

import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.profile.Achievements;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.Statistics;
import ru.mos.polls.AGApplication;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.PullableUIComponent;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.base.ui.rvdecoration.UIhelper;
import ru.mos.polls.databinding.FragmentUserTabProfileBinding;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.newprofile.service.EmptyResponse;
import ru.mos.polls.newprofile.service.VisibilitySet;
import ru.mos.polls.newprofile.service.model.EmptyResult;
import ru.mos.polls.newprofile.ui.adapter.UserStatisticsAdapter;
import ru.mos.polls.newprofile.ui.fragment.UserTabFragment;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;

/**
 * Created by Trunks on 08.06.2017.
 */

public class UserTabFragmentVM extends BaseProfileTabFragmentVM<UserTabFragment, FragmentUserTabProfileBinding> implements AvatarPanelClickListener {

    private SwitchCompat enableProfileVisibility;
    AppCompatTextView fi;
    LinearLayout achievementLayer;
    AppCompatTextView achievementsValue;
    View achievementPanel;

    public UserTabFragmentVM(UserTabFragment fragment, FragmentUserTabProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentUserTabProfileBinding binding) {
        recyclerView = binding.agUserProfileList;
        super.initialize(binding);
        enableProfileVisibility = binding.agUserProfileVisibility;
        circleImageView = binding.agUserAvatarPanel.agUserImage;
        fi = binding.agUserStatusInfoPanel.agUserFi;
        achievementLayer = binding.agUserStatusInfoPanel.agUserAchievementLayer;
        achievementsValue = binding.agUserStatusInfoPanel.agUserAchievementValue;
        achievementPanel = binding.agUserStatusInfoPanel.agUserAchievementPanel;
        saved = new AgUser(getActivity());
        binding.setClickListener(this);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        progressable = getProgressable();
        setView();
        setListener();
        setAchievementLayerView();
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(new PullableUIComponent(() -> {
                    progressable = getPullableProgressable();
                    refreshProfile();
                }))
                .with(new ProgressableUIComponent())
                .build();
    }

    public void setView() {
        enableProfileVisibility.setChecked(saved.isProfileVisible());
    }

    public void setListener() {
        enableProfileVisibility.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sendVisibilityProfileRequest(isChecked);
        });
    }

    public void sendVisibilityProfileRequest(boolean visibility) {
        HandlerApiResponseSubscriber<EmptyResult[]> handler
                = new HandlerApiResponseSubscriber<EmptyResult[]>(getActivity(), progressable) {
            @Override
            protected void onResult(EmptyResult[] result) {
                String message = "Профиль скрыт";
                if (visibility) message = "Профиль доступен для просмотра";
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                saved.setProfileVisible(visibility);
                saved.setProfileVisible(getActivity(), visibility);
            }
        };
        Observable<EmptyResponse> responseObservable = AGApplication.api
                .setProfileVisibility(new VisibilitySet.Request(visibility))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservable.subscribeWith(handler));
    }

    private void setUserStatsListView() {
        List<Statistics> list = new ArrayList<>();
        list.addAll(saved.getStatisticList(getActivity()));
        UserStatisticsAdapter userStatisticsAdapter = new UserStatisticsAdapter(list);
        recyclerView.setAdapter(userStatisticsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        saved = new AgUser(getActivity());
        updateView();
    }

    public void updateView() {
        getBinding().setAgUser(saved);
        getBinding().executePendingBindings();
        setUserStatsListView();
        setAvatar();
    }

    public void setAchievementLayerView() {
        List<Achievements> list = saved.getAchievementsList(getActivity());
        achievementLayer.removeAllViews();
        if (list.size() > 0) {
            for (Achievements achievements : list) {
                UIhelper.addAchievements(achievementLayer, achievements.getImageUrl(), getActivity().getBaseContext());
            }
        } else {
            achievementPanel.setVisibility(View.GONE);
        }
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
