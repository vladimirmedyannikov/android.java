package ru.mos.polls.profile.vm;


import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.databinding.FragmentInfoTabProfileBinding;
import ru.mos.polls.profile.model.UserInfo;
import ru.mos.polls.profile.ui.adapter.UserInfoAdapter;
import ru.mos.polls.profile.ui.fragment.InfoTabFragment;
import ru.mos.polls.social.model.AppSocial;
import ru.mos.polls.util.AgTextUtil;
import ru.mos.polls.util.GuiUtils;

/**
 * Created by Trunks on 16.06.2017.
 */

public class InfoTabFragmentVM extends BaseProfileTabFragmentVM<InfoTabFragment, FragmentInfoTabProfileBinding> implements AvatarPanelClickListener {
    LinearLayout socialBindingLayer;
    Observable<List<AppSocial>> socialListObservable;
    AppCompatTextView percentFilledTitle;
    AppCompatTextView socialBindingStatus;
    ProgressBar percentFilledPb;

    public InfoTabFragmentVM(InfoTabFragment fragment, FragmentInfoTabProfileBinding binding) {
        super(fragment, binding);
    }

    private View.OnClickListener onItemClickListener = v -> showDialogEditProfile();

    public void showDialogEditProfile() {
        GuiUtils.displayYesOrNotDialog(getFragment().getContext(),
                R.string.title_dialog_edit_profile,
                (dialog, which) -> editUserInfo(), null);
    }

    @Override
    protected void initialize(FragmentInfoTabProfileBinding binding) {
        recyclerView = binding.agUserProfileList;
        socialBindingLayer = binding.agUserSocialBindingLayer;
        circleImageView = binding.agUserAvatarPanel.agUserImage;
        avatarProgress = binding.agUserAvatarPanel.avatarProgress;
        if (avatarProgress != null) {
            (avatarProgress).getIndeterminateDrawable()
                    .setColorFilter(getFragment().getContext().getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        }
        avatarContainer = binding.agUserAvatarPanel.avatarContainer;
        percentFilledTitle = binding.agUserProfilePercentFillTitle;
        socialBindingStatus = binding.agUserSocialValue;
        percentFilledPb = binding.agUserProfileProgressbar;
        saved = new AgUser(getActivity());
        super.initialize(binding);
        binding.setClickListener(this);
    }

    private void userInfoList() {
        List<UserInfo> list = new ArrayList<>();
        list.add(new UserInfo("телефон", AgTextUtil.getPhoneFormat(saved.getPhone())));
        list.add(new UserInfo("e-mail", TextUtils.isEmpty(saved.getEmail()) ? getFragment().getString(R.string.non_selected_male) : saved.getEmail()));
        list.add(new UserInfo("фамилия", TextUtils.isEmpty(saved.getSurname()) ? getFragment().getString(R.string.non_selected_female) : saved.getSurname()));
        list.add(new UserInfo("имя", TextUtils.isEmpty(saved.getFirstName()) ? getFragment().getString(R.string.non_selected) : saved.getFirstName()));
        list.add(new UserInfo("отчество", TextUtils.isEmpty(saved.getMiddleName()) ? getFragment().getString(R.string.non_selected) : saved.getMiddleName()));
        list.add(new UserInfo("дата рождения", TextUtils.isEmpty(saved.getBirthday()) ? getFragment().getString(R.string.non_selected_female) : saved.getBirthday()));
        list.add(new UserInfo("пол", saved.getGender().toString()));
        list.add(new UserInfo("семейное положение", saved.getMaritalStatus().toString(saved.getGender())));
        if (saved.getChildCount() > 0)
            list.add(new UserInfo("количество детей", String.valueOf(saved.getChildCount())));
        list.add(new UserInfo("адрес регистрации", saved.getRegistration().getAddressTitle(getActivity().getBaseContext())));
        String residenceFlat = getRegistrationAddressTitle();
        list.add(new UserInfo("адрес проживания", residenceFlat));
        list.add(new UserInfo("род деятельности", AgSocialStatus.fromPreferences(getActivity().getBaseContext()).get(saved.getAgSocialStatus()).getTitle()));
        list.add(new UserInfo("адрес работы/учебы", saved.getWork().getAddressTitle(getFragment().getContext())));
        list.add(new UserInfo("адреса собственности", saved.getOwnPropertyList(getActivity()).size() > 0 ? "Указаны" : "Не указаны"));  //вернуть в версии 2.5.0
        String pguConnected = saved.isPguConnected() ? "Подключено" : "Не указана";
        list.add(new UserInfo("связь с mos.ru", pguConnected));
        UserInfoAdapter userStatisticsAdapter = new UserInfoAdapter(onItemClickListener, list);
        recyclerView.setAdapter(userStatisticsAdapter);
    }

    public String getRegistrationAddressTitle() {
        if (saved.getResidence().isEmpty() && saved.getRegistration().isEmpty())
            return getActivity().getString(R.string.address_not_specified);
        return saved.getRegistration().compareByFullAddress(saved.getResidence()) || saved.getResidence().isEmpty()
                ? "Совпадает с адресом регистрации" : saved.getResidence().getAddressTitle(getActivity().getBaseContext());
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

    public void addSocialToLayer(AppSocial social) {
        int socialIcon = AppSocial.getSocialIcon(social.getId());
        addSocialBindingIcon(socialBindingLayer, socialIcon, getFragment().getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshProfile();
    }

    public void updateView() {
        socialListObservable = AppSocial.getObservableSavedSocials(getFragment().getContext());
        getBinding().setAgUser(saved);
        getBinding().executePendingBindings();
        userInfoList();
        setSocialBindingLayerRx();
        setAvatar();
        setProfileFillPercentView(saved.getPercentFillProfile());
    }

    public void setProfileFillPercentView(int percent) {
        percentFilledTitle.setText(String.format(getActivity().getString(R.string.profile_filled_title), percent));
        percentFilledPb.setProgress(percent);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        progressable = getProgressable();
        setProfileFillPercentView(0);
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
        disposables.add(socialListObservable
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::fromIterable)
                .filter(social -> social.isLogon())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::addSocialToLayer, Throwable::printStackTrace, this::setSocialBindingStatusView));
    }

    private void setSocialBindingStatusView() {
        socialBindingStatus.setText(socialBindingLayer.getChildCount() > 0 ? "Подключено" : "Не указано");
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
