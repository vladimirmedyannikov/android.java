package ru.mos.polls.newprofile.vm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.profile.AgSocialStatus;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.BirthDateParser;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.ui.dialog.DatePickerFragment;
import ru.mos.polls.base.view.DictionaryView;
import ru.mos.polls.base.view.model.DictionaryItem;
import ru.mos.polls.databinding.FragmentNewEditProfileBinding;
import ru.mos.polls.newprofile.service.ProfileSet;
import ru.mos.polls.newprofile.service.model.Personal;
import ru.mos.polls.newprofile.state.AddPrivatePropertyState;
import ru.mos.polls.newprofile.state.EditPersonalInfoState;
import ru.mos.polls.newprofile.state.NewFlatState;
import ru.mos.polls.newprofile.state.PguAuthState;
import ru.mos.polls.newprofile.ui.adapter.MaritalStatusAdapter;
import ru.mos.polls.newprofile.ui.fragment.EditProfileFragment;
import ru.mos.polls.profile.gui.activity.UpdateSocialActivity;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.social.model.AppSocial;

/**
 * Created by wlTrunks on 14.06.2017.
 */

public class EditProfileFragmentVM extends UIComponentFragmentViewModel<EditProfileFragment, FragmentNewEditProfileBinding> {
    DictionaryView gender;
    DictionaryView maritalStatus;
    ArrayAdapter genderAdapter;
    MaritalStatusAdapter martialStatusAdapter;
    AgUser savedUser;
    View kidsLayer;
    TextView birthdayDate;
    BirthDateParser dbp;
    TextView registration;
    TextView residence;
    TextView work;
    TextView email;
    TextView fio;
    TextView phone;
    TextView kidsCount;
    TextView kidsCountTitle;
    TextView socialStatus;
    TextView kidsDateValue;
    TextView kidsDate;
    TextView bindingMostTitle;
    TextView bindingMostStatus;
    TextView privateProperty;
    TextView privatePropertyStatus;
    View kidsDateLayer;
    TextView socialBindTitle;
    LinearLayout socialBindingLayer;
    Observable<List<AppSocial>> socialListObserable;
    public static final String PREFS = "profile_prefs";
    public static final String TIME_SYNQ = "time_synq";
    public static final long INTERVAL_SYNQ = 15 * 60 * 1000;

    public EditProfileFragmentVM(EditProfileFragment fragment, FragmentNewEditProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentNewEditProfileBinding binding) {
        savedUser = new AgUser(getActivity());
        dbp = new BirthDateParser(getActivity());
        gender = binding.layoutDateGender.editGender;
        maritalStatus = binding.editMartialStatus;
        kidsLayer = binding.editKidsLayer;
        kidsDateLayer = binding.editKidsDateLayer;
        birthdayDate = binding.layoutDateGender.editBirthdayDate;
        registration = binding.editFlatRegistration;
        residence = binding.editFlatResidence;
        work = binding.editFlatWork;
        email = binding.editEmail;
        fio = binding.editFio;
        phone = binding.editPhone;
        kidsCount = binding.editKidsValue;
        kidsCountTitle = binding.editKidsTitle;
        socialStatus = binding.editSocialStatus;
        kidsDateValue = binding.editKidsDateValue;
        kidsDate = binding.editKidsDate;
        kidsDateLayer = binding.editKidsDateLayer;
        socialBindTitle = binding.editSocialBindTitle;
        socialBindingLayer = binding.editSocialBindLayer;
        bindingMostTitle = binding.editBindingMosTitle;
        bindingMostStatus = binding.editBindingMosStatus;
        privateProperty = binding.editPrivateProperty;
        privatePropertyStatus = binding.editPrivatePropertyStatus;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setClickListener();
        AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof Events.ProfileEvents) {
                        Events.ProfileEvents action = (Events.ProfileEvents) o;
                        switch (action.getEventType()) {
                            case Events.ProfileEvents.UPDATE_USER_INFO:
                                AgUser changed = action.getAgUser();
                                this.savedUser = changed;
                                refreshView(savedUser);
                                break;
                        }
                    }
                });
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(new ProgressableUIComponent())
                .build();
    }

    public void sendProfile(ProfileSet.Request request) {
        /**
         * обсервер для сохранения профиля
         */
        HandlerApiResponseSubscriber<ProfileSet.Response.Result> handler
                = new HandlerApiResponseSubscriber<ProfileSet.Response.Result>(getActivity(), getComponent(ProgressableUIComponent.class)) {
            @Override
            protected void onResult(ProfileSet.Response.Result result) {
                savedUser.setPercentFillProfile(result.getPercentFillProfile());
                savedUser.save(getActivity());
                sendBroadcastReLoadBadges(getActivity());
            }
        };
        Observable<ProfileSet.Response> responseObservabl =
                AGApplication.api.setProfile(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservabl.subscribeWith(handler));
    }

    public static void sendBroadcastReLoadBadges(Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_BAGES_FROM_SERVER));
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putLong(TIME_SYNQ, System.currentTimeMillis() + INTERVAL_SYNQ).apply();
    }

    public void sendProfile(Personal personal) {
        sendProfile(new ProfileSet.Request(personal));
    }


    @Override
    public void onResume() {
        super.onResume();
        savedUser = new AgUser(getActivity());
        socialListObserable = AppSocial.getObservableSavedSocials(getActivity());
        refreshView(savedUser);
    }

    public void setClickListener() {
        registration.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new NewFlatState(savedUser.getRegistration(), NewFlatFragmentVM.FLAT_TYPE_REGISTRATION), NewFlatFragmentVM.FLAT_TYPE_REGISTRATION);
        });
        residence.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new NewFlatState(savedUser.getResidence(), NewFlatFragmentVM.FLAT_TYPE_RESIDENCE), NewFlatFragmentVM.FLAT_TYPE_RESIDENCE);
        });
        work.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new NewFlatState(savedUser.getWork(), NewFlatFragmentVM.FLAT_TYPE_WORK), NewFlatFragmentVM.FLAT_TYPE_WORK);
        });
        email.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new EditPersonalInfoState(savedUser, EditPersonalInfoFragmentVM.PERSONAL_EMAIL), EditPersonalInfoFragmentVM.PERSONAL_EMAIL);
        });
        fio.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new EditPersonalInfoState(savedUser, EditPersonalInfoFragmentVM.PERSONAL_FIO), EditPersonalInfoFragmentVM.PERSONAL_FIO);
        });
        kidsLayer.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new EditPersonalInfoState(savedUser, EditPersonalInfoFragmentVM.COUNT_KIDS), EditPersonalInfoFragmentVM.COUNT_KIDS);
        });
        socialStatus.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new EditPersonalInfoState(savedUser, EditPersonalInfoFragmentVM.SOCIAL_STATUS), EditPersonalInfoFragmentVM.SOCIAL_STATUS);
        });
        kidsDate.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new EditPersonalInfoState(savedUser, EditPersonalInfoFragmentVM.BIRTHDAY_KIDS), EditPersonalInfoFragmentVM.BIRTHDAY_KIDS);
        });
        socialBindTitle.setOnClickListener(v -> {
            UpdateSocialActivity.startActivity(getActivity());
        });
        bindingMostTitle.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new PguAuthState(PguAuthState.PGU_STATUS), PguAuthFragmentVM.PGU_AUTH);
        });
        privateProperty.setOnClickListener(v -> getFragment().navigateToActivityForResult(new AddPrivatePropertyState(null), 6622));
    }

    public void refreshView(AgUser agUser) {
//        setGenderView(agUser.getGender());
        initGender(agUser);
//        setMartialStatusView(agUser.getGender());
        initMarital(agUser);
        displayBirthday();
        setRegistationFlatView(agUser.getRegistration());
        setResidenceFlatView(agUser.getRegistration(), agUser.getResidence());
        setWorkFlatView(agUser.getWork());
        setEmailView(agUser.getEmail());
        setFioView(agUser.getFullUserName());
        setPhoneView(agUser.getPhone());
        setKidsCountView(agUser.getChildCount());
        setSocialStatusView(agUser.getAgSocialStatus());
        setKidsDateLayerView(agUser);
        setSocialBindingLayerRx();
        setPguStatusView(agUser);
        setOwnPropertyView(agUser);
    }

    public void setOwnPropertyView(AgUser agUser) {
        privateProperty.setText(agUser.getOwnPropertyCount() > 0 ? getActivity().getString(R.string.property_addresses) : "");
        privatePropertyStatus.setVisibility(agUser.getOwnPropertyCount() > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    public void setPguStatusView(AgUser agUser) {
        if (agUser.isPguConnected()) {
            bindingMostTitle.setText(getActivity().getString(R.string.connection_mos_pgu_title));
            bindingMostStatus.setText(getActivity().getString(R.string.connection_mos_pgu_connected));
        }
    }

    public void setKidsDateLayerView(AgUser agUser) {
        if (agUser.getChildCount() > 0) {
            kidsDateLayer.setVisibility(View.VISIBLE);
            if (agUser.isChildBirthdaysFilled()) {
                kidsDate.setText(getActivity().getString(R.string.kids_birthdays));
                kidsDateValue.setText("указан");
            } else {
                kidsDateValue.setText("не указан");
            }
        } else {
            kidsDateLayer.setVisibility(View.GONE);
        }
    }

    public void setSocialStatusView(int idSocialStatus) {
        List<AgSocialStatus> list = AgSocialStatus.fromPreferences(getActivity().getBaseContext());
        socialStatus.setText(list.get(idSocialStatus).getTitle().equals(AgSocialStatus.NOTHING_SELECT_TEXT) ?
                getActivity().getString(R.string.social_status_non_select_item_title) :
                list.get(idSocialStatus).getTitle());
        socialStatus.setTextColor(list.get(idSocialStatus).getTitle().equals(AgSocialStatus.NOTHING_SELECT_TEXT) ?
                getActivity().getResources().getColor(R.color.text_hint) :
                getActivity().getResources().getColor(R.color.black_light));
    }

    public void setKidsCountView(int kidsCountValue) {
        kidsCount.setText(String.valueOf(kidsCountValue));
    }

    public void setEmailView(String userEmail) {
        email.setText(userEmail);
    }

    public void setFioView(String text) {
        fio.setText(text);
    }

    public void setPhoneView(String text) {
        phone.setText(text);
    }

    public void setRegistationFlatView(Flat flat) {
        setFlatView(flat, registration);
    }

    public void setResidenceFlatView(Flat registationFlat, Flat residenceFlat) {
        if (!registationFlat.isEmpty() && residenceFlat.compareByFullAddress(registationFlat)) {
            residence.setText(getActivity().getString(R.string.coincidesAddressRegistration));
            return;
        }
        setFlatView(residenceFlat, residence);
    }

    public void setWorkFlatView(Flat flat) {
        setFlatView(flat, work);
    }


    public void setFlatView(Flat flat, TextView view) {
        view.setText("");
        if (!flat.isEmpty())
            view.setText(flat.getAddressTitle(getActivity().getBaseContext()));
    }

    public void displayBirthday() {
        birthdayDate.setOnClickListener(v -> setBirthDayDate());
        birthdayDate.setText(dbp.format(savedUser.getBirthday()));
        String tag = savedUser.getBirthday();
        if (TextUtils.isEmpty(tag)) {
            tag = "01.01.1990";
        }
        birthdayDate.setTag(tag);
    }


    public void setSocialBindingLayerRx() {
        socialBindingLayer.removeAllViews();
        disposables.add(socialListObserable
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::fromIterable)
                .filter(social -> social.isLogon())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::addSocialToLayer, throwable -> throwable.printStackTrace(), this::setSocialBindingTitleView));
    }

    public void setSocialBindingTitleView() {
        socialBindTitle.setText(socialBindingLayer.getChildCount() > 0 ? getActivity().getString(R.string.socials) : "");
    }

    int selectedGender;

    private void initGender(AgUser user) {
        gender.setTitleFirstNothingElement(getActivity().getString(R.string.gender_non_select_item_title));
        gender.setAddFirstNothingElement(true);
        gender.setFirstSelectedElementIsSetHintFirstNothingElement(true);
        gender.setNonSelectFirstNothingElement(true);
        gender.setOnDictionarySelectedListener(new DictionaryView.OnDictionarySelectedListener() {
            @Override
            public void selected(DictionaryItem dictionaryItem) {
                if (dictionaryItem.getId() != selectedGender) {
                    AgUser.Gender gender = AgUser.Gender.parseLabel(dictionaryItem.getTitle());
                    user.setGender(gender);
                    refreshMarital(user);
                    selectedGender = dictionaryItem.getId();
                    sendProfile(new Personal().setSex(user.getGender() == AgUser.Gender.NULL ? "" : user.getGender().getValue()));
                }
            }

            @Override
            public void nothingSelected() {

            }
        });
        refreshGender(user);
    }

    private void refreshGender(AgUser user) {
        ArrayList<DictionaryItem> genderItems = new ArrayList<>();
        genderItems.add(new DictionaryItem(1, AgUser.Gender.NULL.toString()));
        genderItems.add(new DictionaryItem(2, AgUser.Gender.MALE.toString()));
        genderItems.add(new DictionaryItem(3, AgUser.Gender.FEMALE.toString()));
        gender.setData(genderItems);
        gender.setSelected(user.getGender() == AgUser.Gender.NULL ? 1 : user.getGender() == AgUser.Gender.MALE ? 2 : 3);
    }

    int selectedMartial;

    private void initMarital(AgUser user) {
        maritalStatus.setTitleFirstNothingElement(getActivity().getString(R.string.marital_non_select_item_title));
        maritalStatus.setAddFirstNothingElement(true);
        maritalStatus.setFirstSelectedElementIsSetHintFirstNothingElement(true);
        maritalStatus.setNonSelectFirstNothingElement(true);
        maritalStatus.setOnDictionarySelectedListener(new DictionaryView.OnDictionarySelectedListener() {
            @Override
            public void selected(DictionaryItem dictionaryItem) {
                if (dictionaryItem.getId() != selectedMartial) {
                    AgUser.MaritalStatus maritalStatus = AgUser.MaritalStatus.parseLabel(dictionaryItem.getTitle());
                    user.setMaritalStatus(maritalStatus);
                    selectedMartial = dictionaryItem.getId();
                    sendProfile(new Personal().setMarital_status(user.getMaritalStatus() == AgUser.MaritalStatus.NULL ? "" : user.getMaritalStatus().getValue()));
                }
            }

            @Override
            public void nothingSelected() {

            }
        });
        refreshMarital(user);
    }

    private void refreshMarital(AgUser user) {
        ArrayList<DictionaryItem> maritalItems = new ArrayList<>();
        maritalItems.add(new DictionaryItem(1, AgUser.MaritalStatus.NULL.toString(user.getGender())));
        maritalItems.add(new DictionaryItem(2, AgUser.MaritalStatus.SINGLE.toString(user.getGender())));
        maritalItems.add(new DictionaryItem(3, AgUser.MaritalStatus.MARRIED.toString(user.getGender())));
        maritalStatus.setData(maritalItems);
        maritalStatus.setSelected(user.getMaritalStatus() == AgUser.MaritalStatus.NULL ? 1 : user.getMaritalStatus() == AgUser.MaritalStatus.SINGLE ? 2 : 3);
    }

    public void setBirthDayDate() {
        OnDateSetCallback listener = () -> {
            savedUser.setBirthday(birthdayDate.getText().toString());
            Personal personal = new Personal();
            personal.setBirthday(savedUser.getBirthday());
            sendProfile(personal);
        };
        /**
         * Откатываем время назад
         */
        Calendar cal = Calendar.getInstance();
        BirthDateParser dbp = new BirthDateParser(getActivity());
        DatePickerFragment birthDayDialogFragment = new DatePickerFragment();
        Bundle extras = new Bundle();
        /**
         * если пользователь ранее устанавливал дату рождения
         */
        extras.putString("curBirthDate", (String) birthdayDate.getTag());
        extras.putInt("viewId", birthdayDate.getId());
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 10);
        extras.putLong(DatePickerFragment.MAXDATE, cal.getTimeInMillis());
        extras.putParcelable("BirthDateParser", dbp);
        /**
         * устанавливаем минимальное значение года
         */
        cal.set(Calendar.YEAR, 1900);
        extras.putLong(DatePickerFragment.MINDATE, cal.getTimeInMillis());
        birthDayDialogFragment.setArguments(extras);
        birthDayDialogFragment.setOkButtomListener(listener);
        birthDayDialogFragment.show(getFragment().getChildFragmentManager(), "SdatePicker");
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
        addSocialBindingIcon(socialBindingLayer, socialIcon, getActivity().getBaseContext());
    }
}
