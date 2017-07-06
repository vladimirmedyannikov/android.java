package ru.mos.polls.newprofile.vm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.BirthDateParser;
import ru.mos.elk.profile.DatePickerFragment;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutNewEditProfileBinding;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.state.EditPersonalInfoState;
import ru.mos.polls.newprofile.ui.fragment.EditProfileFragment;
import ru.mos.polls.profile.gui.fragment.location.NewAddressActivity;

/**
 * Created by wlTrunks on 14.06.2017.
 */

public class EditProfileFragmentVM extends FragmentViewModel<EditProfileFragment, LayoutNewEditProfileBinding> {
    AppCompatSpinner gender;
    AppCompatSpinner martialStatus;
    ArrayAdapter genderAdapter;
    AgUser.MaritalStatus.MaritalStatusAdapter martialStatusAdapter;
    AgUser savedUser, changedUser;
    LinearLayout kidsLayer;
    View kidsDateLayer;
    TextView birthdayDate;
    BirthDateParser dbp;
    TextView registration;
    TextView residence;
    TextView email;
    TextView fio;
    TextView phone;
    TextView kidsCount;
    TextView kidsCountTitle;

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
        email = binding.editEmail;
        fio = binding.editFio;
        phone = binding.editPhone;
        kidsCount = binding.editKidsValue;
        kidsCountTitle = binding.editKidsTitle;
    }

    @Override
    public void onViewCreated() {
        setClickListener();
        refreshView(savedUser);
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
                                break;
                        }
                    }
                });
    }

    public void setClickListener() {
        registration.setOnClickListener(v -> {
            NewAddressActivity.startActivity(getFragment(), savedUser.getRegistration());
        });
        residence.setOnClickListener(v -> {
            NewAddressActivity.startActivity(getFragment(), savedUser.getResidence());
        });
        email.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new EditPersonalInfoState(changedUser, EditPersonalInfoFragmentVM.PERSONAL_EMAIL), EditPersonalInfoFragmentVM.PERSONAL_EMAIL);
        });
        fio.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new EditPersonalInfoState(changedUser, EditPersonalInfoFragmentVM.PERSONAL_FIO), EditPersonalInfoFragmentVM.PERSONAL_FIO);
        });
        kidsCountTitle.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new EditPersonalInfoState(changedUser, EditPersonalInfoFragmentVM.PERSONAL_CHILDS), EditPersonalInfoFragmentVM.PERSONAL_CHILDS);
        });

    }

    public void refreshView(AgUser agUser) {
        setGenderView(agUser.getGender());
        setMartialStatusView(agUser.getGender());
        displayBirthday();
        setRegistationFlatView(agUser.getRegistration());
        setResidenceFlatView(agUser.getRegistration(), agUser.getResidence());
        setEmailView(agUser.getEmail());
        setFioView(agUser.getFullUserName());
        setPhoneView(agUser.getPhone());
        setKidsCountView(agUser.getChildCount());
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
        }
        if (requestCode == Activity.RESULT_OK) {
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


    public void setFlatView(Flat flat, TextView view) {
        if (!flat.isEmpty())
            view.setText(flat.getAddressTitle(getFragment().getContext()));
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
                martialStatusAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        int selected = genderAdapter.getPosition(userGender);
        gender.setSelection(selected);
    }

    public void setMartialStatusView(AgUser.Gender gender) {
        martialStatusAdapter = (AgUser.MaritalStatus.MaritalStatusAdapter) getMartialStatusAdapter(gender);
        martialStatus.setAdapter(martialStatusAdapter);
        martialStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
//        list.add(AgUser.Gender.HINT);
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
        birthDayDialogFragment.show(getFragment().getChildFragmentManager(), "SdatePicker");
    }
}
