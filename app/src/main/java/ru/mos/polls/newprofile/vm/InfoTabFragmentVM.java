package ru.mos.polls.newprofile.vm;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatTextView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.profile.AgSocialStatus;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.PullableUIComponent;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentInfoTabProfileBinding;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.newprofile.model.UserInfo;
import ru.mos.polls.newprofile.ui.adapter.UserInfoAdapter;
import ru.mos.polls.newprofile.ui.fragment.InfoTabFragment;
import ru.mos.polls.social.model.Social;

/**
 * Created by Trunks on 16.06.2017.
 */

public class InfoTabFragmentVM extends BaseProfileTabFragmentVM<InfoTabFragment, FragmentInfoTabProfileBinding> implements AvatarPanelClickListener {
    LinearLayout socialBindingLayer;
    Observable<List<Social>> socialListObserable;
    AppCompatTextView percentFilledTitle;

    public InfoTabFragmentVM(InfoTabFragment fragment, FragmentInfoTabProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentInfoTabProfileBinding binding) {
        recyclerView = binding.agUserProfileList;
        socialBindingLayer = binding.agUserSocialBindingLayer;
        circleImageView = binding.agUserAvatarPanel.agUserImage;
        percentFilledTitle = binding.agUserProfilePercentFillTitle;
        saved = new AgUser(getActivity());
        super.initialize(binding);
        binding.setClickListener(this);
    }

    private void userInfoList() {
        List<UserInfo> list = new ArrayList<>();
        list.add(new UserInfo("телефон", saved.getPhone()));
        list.add(new UserInfo("e-mail", saved.getEmail()));
        list.add(new UserInfo("фамилия", saved.getSurname()));
        list.add(new UserInfo("имя", saved.getFirstName()));
        list.add(new UserInfo("отчество", saved.getMiddleName()));
        list.add(new UserInfo("дата рождения", saved.getBirthday()));
        list.add(new UserInfo("пол", saved.getGender().toString()));
        list.add(new UserInfo("семейное положение", saved.getMaritalStatus().toString()));
        list.add(new UserInfo("адрес регистрации", saved.getRegistration().getAddressTitle(getActivity().getBaseContext())));
        String residenceFlat = saved.getRegistration().compareByFullAddress(saved.getResidence()) || saved.getResidence().isEmpty()
                ? "совпадает с адресом регистрации" : saved.getResidence().getAddressTitle(getActivity().getBaseContext());
        list.add(new UserInfo("адрес проживания", residenceFlat));
        list.add(new UserInfo("род деятельности", AgSocialStatus.fromPreferences(getActivity().getBaseContext()).get(saved.getAgSocialStatus()).getTitle()));
        list.add(new UserInfo("адрес работы/учебы", saved.getWork().getAddressTitle(getFragment().getContext())));
        String pguConnected = saved.isPguConnected() ? "подключено" : "не указано";
        list.add(new UserInfo("связь с mos.ru", pguConnected));
        UserInfoAdapter userStatisticsAdapter = new UserInfoAdapter(list);
        recyclerView.setAdapter(userStatisticsAdapter);
    }

    public void addSocialBindingIcon(LinearLayout linearLayout, @DrawableRes int res, Context context) {
        CircleImageView civ = new CircleImageView(context);
        int sizeInPixel = context.getResources().getDimensionPixelSize(R.dimen.pd_xlarge);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(sizeInPixel, sizeInPixel);
        layoutParams.setMargins(6, 6, 6, 6);
        civ.setLayoutParams(layoutParams);
        civ.setImageResource(res);
        linearLayout.addView(civ);
    }

    public void addSocialToLayer(Social social) {
        int socialIcon = Social.getSocialIcon(social.getSocialId());
        addSocialBindingIcon(socialBindingLayer, socialIcon, getFragment().getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        saved = new AgUser(getActivity());
        updateView();
    }

    public void updateView() {
        socialListObserable = Social.getObservableSavedSocials(getFragment().getContext());
        getBinding().setAgUser(saved);
        getBinding().executePendingBindings();
        userInfoList();
        setSocialBindingLayerRx();
        setAvatar();
        setProfileFillPercentView();
    }

    public void setProfileFillPercentView() {
        percentFilledTitle.setText(String.format(getActivity().getString(R.string.profile_filled_title), saved.getPercentFillProfile()));
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        progressable = getProgressable();
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

    public void setSocialBindingLayerRx() {
        socialBindingLayer.removeAllViews();
        disposables.add(socialListObserable
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::fromIterable)
                .filter(social -> social.isLogon())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::addSocialToLayer));
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
