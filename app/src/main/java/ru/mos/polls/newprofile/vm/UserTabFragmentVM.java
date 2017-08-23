package ru.mos.polls.newprofile.vm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.profile.Achievements;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.Statistics;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.PullableUIComponent;
import ru.mos.polls.base.component.UIComponentHolder;
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

    private void mockUserStatsList() {
        List<Statistics> list = new ArrayList<>();
        list.addAll(saved.getStatisticList(getActivity()));
        if (list.size() == 0) {
            list.add(new Statistics("Заполненность профиля", "95%"));
            list.add(new Statistics("Пройдено голосований", "257"));
            list.add(new Statistics("Оценено новинок", "257"));
            list.add(new Statistics("Оценено новинок", "17"));
            list.add(new Statistics("Посещено мероприятий", "5"));
            list.add(new Statistics("Приглашено друзей", "0"));
            list.add(new Statistics("Активность в социальных сетях", "0"));
            list.add(new Statistics("Получено баллов", "0"));
            list.add(new Statistics("Потрачено баллов", "0"));
        }
        UserStatisticsAdapter userStatisticsAdapter = new UserStatisticsAdapter(list);
        recyclerView.setAdapter(userStatisticsAdapter);
    }

//    public void refreshProfile() {
//        ProfileManager.AgUserListener agUserListener = new ProfileManager.AgUserListener() {
//            @Override
//            public void onLoaded(AgUser loadedAgUser) {
//                try {
//                    saved = loadedAgUser;
//                    progressable.end();
//                    updateView();
//                } catch (Exception ignored) {
//                }
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//                try {
//                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                } catch (Exception ignored) {
//                }
//            }
//        };
//        ProfileManager.getProfile((BaseActivity) getActivity(), agUserListener);
//    }

    @Override
    public void onResume() {
        super.onResume();
        saved = new AgUser(getActivity());
        updateView();
    }

    public void updateView() {
        getBinding().setAgUser(saved);
        getBinding().executePendingBindings();
        mockUserStatsList();
        setAvatar();
    }

    public void setAchievementLayerView() {
        List<Achievements> list = saved.getAchievementsList(getActivity());
        achievementLayer.removeAllViews();
        if (list.size() == 0) {
            for (Achievements achievements : AchievementTabFragmentVM.mockList(getActivity())) {
                if (list.size() > 2) break;
                list.add(achievements);
            }
        }
        if (list.size() > 0) {
            for (Achievements achievements : list) {
                addAchievements(achievementLayer, achievements.getImageUrl(), getActivity().getBaseContext());
            }
        } else {
            achievementPanel.setVisibility(View.GONE);
        }
    }

    public void addAchievements(LinearLayout linearLayout, String url, Context context) {
        ImageView image = new ImageView(context);
        int sizeInPixel = context.getResources().getDimensionPixelSize(R.dimen.vs_xxsmall);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(sizeInPixel, sizeInPixel);
        layoutParams.setMargins(6, 6, 6, 6);
        image.setLayoutParams(layoutParams);
        linearLayout.addView(image);
        ImageLoader imageLoader = AGApplication.getImageLoader();
        imageLoader.loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                image.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });
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
