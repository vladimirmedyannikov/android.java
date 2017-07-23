package ru.mos.polls.newprofile.vm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutUserTabProfileBinding;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.newprofile.model.UserStatistics;
import ru.mos.polls.newprofile.ui.adapter.UserStatisticsAdapter;
import ru.mos.polls.newprofile.ui.fragment.UserTabFragment;

/**
 * Created by Trunks on 08.06.2017.
 */

public class UserTabFragmentVM extends BaseTabFragmentVM<UserTabFragment, LayoutUserTabProfileBinding> implements AvatarPanelClickListener {


    private SwitchCompat enableProfileVisibility;
    AppCompatTextView fi;
    LinearLayout achivementLayer;
    AppCompatTextView achivementsValue;

    public UserTabFragmentVM(UserTabFragment fragment, LayoutUserTabProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutUserTabProfileBinding binding) {
        recyclerView = binding.agUserProfileList;
        super.initialize(binding);
        enableProfileVisibility = binding.agUserProfileVisibility;
        circleImageView = binding.agUserAvatarPanel.agUserImage;
        fi = binding.agUserStatusInfoPanel.agUserFi;
        achivementLayer = binding.agUserStatusInfoPanel.agUserAchivementLayer;
        achivementsValue = binding.agUserStatusInfoPanel.agUserAchivementValue;
        binding.setAgUser(saved);
        binding.setClickListener(this);
        setRecyclerList(recyclerView);
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
    public void onResume() {
        super.onResume();
        fi.setText(saved.getSurnameAndFirstName());
        mockUserStatsList();
        setAvatar();
    }

    public void setAchivementLayerView() {
        achivementLayer.removeAllViews();
        achievementList.subscribeOn(Schedulers.io())
                .take(4)
                .filter(achievement -> {
                    return !achievement.isNext();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(achievement -> {
                    addAchivements(achivementLayer, achievement.getImageUrl(), getActivity().getBaseContext());
                }, throwable -> throwable.printStackTrace());
    }

    public void addAchivements(LinearLayout linearLayout, String url, Context context) {
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
