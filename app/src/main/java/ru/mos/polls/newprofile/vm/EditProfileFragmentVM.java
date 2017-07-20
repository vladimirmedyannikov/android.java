package ru.mos.polls.newprofile.vm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.profile.AgSocialStatus;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.BirthDateParser;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.databinding.LayoutNewEditProfileBinding;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.newprofile.base.ui.dialog.DatePickerFragment;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.service.ProfileSet;
import ru.mos.polls.newprofile.service.model.FlatsEntity;
import ru.mos.polls.newprofile.service.model.Personal;
import ru.mos.polls.newprofile.state.EditPersonalInfoState;
import ru.mos.polls.newprofile.ui.fragment.EditProfileFragment;
import ru.mos.polls.profile.gui.activity.UpdateSocialActivity;
import ru.mos.polls.profile.gui.fragment.location.NewAddressActivity;
import ru.mos.polls.social.model.Social;

/**
 * Created by wlTrunks on 14.06.2017.
 */

public class EditProfileFragmentVM extends FragmentViewModel<EditProfileFragment, LayoutNewEditProfileBinding> {
    AppCompatSpinner gender;
    AppCompatSpinner martialStatus;
    ArrayAdapter genderAdapter;
    AgUser.MaritalStatus.MaritalStatusAdapter martialStatusAdapter;
    AgUser savedUser, changedUser;
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
    View kidsDateLayer;
    TextView socialBindTitle;
    LinearLayout socialBindingLayer;
    Observable<List<Social>> socialListObserable;
    public static final String PREFS = "profile_prefs";
    public static final String TIME_SYNQ = "time_synq";
    public static final long INTERVAL_SYNQ = 15 * 60 * 1000;

    public EditProfileFragmentVM(EditProfileFragment fragment, LayoutNewEditProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutNewEditProfileBinding binding) {
        savedUser = new AgUser(getFragment().getContext());
        changedUser = new AgUser(getFragment().getContext());
        dbp = new BirthDateParser(getActivity());
        gender = binding.editGender;
        martialStatus = binding.editMartialStatus;
        kidsLayer = binding.editKidsLayer;
        kidsDateLayer = binding.editKidsDateLayer;
        birthdayDate = binding.editBirthdayDate;
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
    }

    @Override
    public void onViewCreated() {
        setClickListener();
        AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof Events.ProfileEvents) {
                        Events.ProfileEvents action = (Events.ProfileEvents) o;
                        switch (action.getAction()) {
                            case Events.ProfileEvents.UPDATE_USER_INFO:
                                AgUser changed = action.getAgUser();
                                this.changedUser = changed;
                                refreshView(changed);
                                changedUser.save(getActivity().getBaseContext());
//                                sendProfile(changed);
                                sendProfile(new ProfileSet.Request(new Personal(changedUser)));
                                break;
                        }
                    }
                });
    }

    public void sendProfile(ProfileSet.Request request) {
        Observable<ProfileSet.Response> responseObservabl =
                AGApplication.api.setProfile(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservabl.subscribeWith(setProfileObserver));
    }

    /**
     * обсервер для сохранения профиля
     *
     */

    DisposableObserver setProfileObserver = new DisposableObserver<ProfileSet.Response>() {

            @Override
            public void onNext(@NonNull ProfileSet.Response response) {
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_BAGES_FROM_SERVER));
                SharedPreferences prefs = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                prefs.edit().putLong(TIME_SYNQ, System.currentTimeMillis() + INTERVAL_SYNQ).apply();
            }
        };

    public void sendProfile(AgUser agUser) {
        Observable<ProfileSet.Response> responseObservabl =
                AGApplication.api.setProfile(new ProfileSet.Request(new Personal(agUser)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());

        disposables.add(responseObservabl.subscribeWith(setProfileObserver));
    }

    @Override
    public void onResume() {
        super.onResume();
        socialListObserable = Social.getObservableSavedSocials(getFragment().getContext());
        refreshView(changedUser);
    }

    public void setClickListener() {
        registration.setOnClickListener(v -> {
            NewAddressActivity.startActivity(getFragment(), savedUser.getRegistration());
        });
        residence.setOnClickListener(v -> {
            NewAddressActivity.startActivity(getFragment(), savedUser.getResidence());
        });
        work.setOnClickListener(v -> {
            NewAddressActivity.startActivity(getFragment(), savedUser.getWork());
        });
        email.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new EditPersonalInfoState(changedUser, EditPersonalInfoFragmentVM.PERSONAL_EMAIL), EditPersonalInfoFragmentVM.PERSONAL_EMAIL);
        });
        fio.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new EditPersonalInfoState(changedUser, EditPersonalInfoFragmentVM.PERSONAL_FIO), EditPersonalInfoFragmentVM.PERSONAL_FIO);
        });
        kidsCountTitle.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new EditPersonalInfoState(changedUser, EditPersonalInfoFragmentVM.COUNT_KIDS), EditPersonalInfoFragmentVM.COUNT_KIDS);
        });
        socialStatus.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new EditPersonalInfoState(changedUser, EditPersonalInfoFragmentVM.SOCIAL_STATUS), EditPersonalInfoFragmentVM.SOCIAL_STATUS);
        });
        kidsDate.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new EditPersonalInfoState(changedUser, EditPersonalInfoFragmentVM.BIRTHDAY_KIDS), EditPersonalInfoFragmentVM.BIRTHDAY_KIDS);
        });
        socialBindTitle.setOnClickListener(v -> {
            UpdateSocialActivity.startActivity(getActivity());
        });
    }

    public void refreshView(AgUser agUser) {
        setGenderView(agUser.getGender());
        setMartialStatusView(agUser.getGender());
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
        socialStatus.setText(list.get(idSocialStatus).getTitle());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Flat newFlat = NewAddressActivity.onResult(requestCode, resultCode, data);
        if (newFlat != null) {
            if (newFlat.isRegistration()) {
                setRegistationFlatView(newFlat);
                changedUser.setRegistrationFlat(newFlat);
            }
            if (newFlat.isResidence()) {
                setResidenceFlatView(savedUser.getRegistration(), newFlat);
                changedUser.setResidenceFlat(newFlat);
            }
            if (newFlat.isWork()) {
                setWorkFlatView(newFlat);
                changedUser.setWorkFlat(newFlat);
            }
            sendProfile(new ProfileSet.Request(new FlatsEntity(newFlat)));
            changedUser.save(getActivity().getBaseContext());

        }
    }


    public void setRegistationFlatView(Flat flat) {
        setFlatView(flat, registration);
    }

    public void setResidenceFlatView(Flat registationFlat, Flat residenceFlat) {
        if (registationFlat.compareByFullAddress(residenceFlat)) {
            if (!registationFlat.isEmpty())
                residence.setText(getFragment().getString(R.string.coincidesAddressRegistration));
        } else setFlatView(residenceFlat, residence);
    }

    public void setWorkFlatView(Flat flat) {
        setFlatView(flat, work);
    }


    public void setFlatView(Flat flat, TextView view) {
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

    public void setGenderView(AgUser.Gender userGender) {
        genderAdapter = getGenderAdapter();
        gender.setAdapter(genderAdapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AgUser.Gender gender = (AgUser.Gender) genderAdapter.getItem(position);
                martialStatusAdapter.setGender(gender);
                changedUser.setGender(gender);
                martialStatusAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        int selected = genderAdapter.getPosition(userGender);
        gender.setSelection(selected);
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

    public void setMartialStatusView(AgUser.Gender gender) {
        martialStatusAdapter = (AgUser.MaritalStatus.MaritalStatusAdapter) getMartialStatusAdapter(gender);
        martialStatus.setAdapter(martialStatusAdapter);
        martialStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AgUser.MaritalStatus maritalStatus = martialStatusAdapter.getItem(position);
                changedUser.setMaritalStatus(maritalStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        int selectedMarital = martialStatusAdapter.getPosition(savedUser.getMaritalStatus());
        martialStatus.setSelection(selectedMarital);
    }


    public ArrayAdapter getGenderAdapter() {
        List<AgUser.Gender> list = new ArrayList<>(Arrays.asList(AgUser.Gender.getGenderItems()));
//        mBirthdayKidsList.add(AgUser.Gender.HINT);
        ArrayAdapter<AgUser.Gender> ad = new ArrayAdapter<>(getActivity(), R.layout.layout_spinner_view, list);
        ad.setDropDownViewResource(R.layout.layout_spinner_item);
        return ad;
    }

    public ArrayAdapter getMartialStatusAdapter(AgUser.Gender gender) {
        ArrayAdapter<AgUser.MaritalStatus> ad = (AgUser.MaritalStatus.MaritalStatusAdapter)
                AgUser.MaritalStatus.getMaritalStatusAdapter(getActivity(), gender);
        ad.setDropDownViewResource(R.layout.layout_spinner_item);
        return ad;
    }

    public void setBirthDayDate() {
        OnDateSetCallback listener = () -> {
            changedUser.setBirthday(birthdayDate.getText().toString());
            changedUser.save(getActivity().getBaseContext());
            sendProfile(changedUser);
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

    public void addSocialToLayer(Social social) {
        int socialIcon = Social.getSocialIcon(social.getSocialId());
        addSocialBindingIcon(socialBindingLayer, socialIcon, getFragment().getContext());
    }
}
