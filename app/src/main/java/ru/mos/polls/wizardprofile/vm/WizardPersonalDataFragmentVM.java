package ru.mos.polls.wizardprofile.vm;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.profile.model.BirthDateParser;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentWizardPersonalDataBinding;
import ru.mos.polls.base.ui.dialog.DatePickerFragment;
import ru.mos.polls.base.vm.FragmentViewModel;
import ru.mos.polls.profile.service.model.Personal;
import ru.mos.polls.profile.ui.fragment.EditPersonalInfoFragment;
import ru.mos.polls.profile.vm.EditPersonalInfoFragmentVM;
import ru.mos.polls.base.ui.dialog.OnDateSetCallback;
import ru.mos.polls.wizardprofile.ui.fragment.WizardPersonalDataFragment;

/**
 * Created by Trunks on 31.07.2017.
 */

public class WizardPersonalDataFragmentVM extends FragmentViewModel<WizardPersonalDataFragment, FragmentWizardPersonalDataBinding> {
    EditPersonalInfoFragment personalInfoFragment;
    AgUser agUser;
    TextInputEditText birthdayDate;
    AppCompatSpinner editGender;
    BirthDateParser dbp;
    ArrayAdapter genderAdapter;
    Personal personal;

    public WizardPersonalDataFragmentVM(WizardPersonalDataFragment fragment, FragmentWizardPersonalDataBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentWizardPersonalDataBinding binding) {
        Bundle extras = getFragment().getArguments();
        if (extras != null) {
            agUser = (AgUser) extras.get(WizardPersonalDataFragment.ARG_AGUSER);
        } else {
            agUser = new AgUser(getActivity());
        }
        dbp = new BirthDateParser(getActivity());
        birthdayDate = binding.birthdayDate;
        editGender = binding.editGender;
        personal = new Personal();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        FragmentTransaction ft = getFragment().getChildFragmentManager().beginTransaction();
        personalInfoFragment = EditPersonalInfoFragment.newInstanceForWizard(agUser, EditPersonalInfoFragmentVM.PERSONAL_FIO);
        ft.replace(R.id.container, personalInfoFragment);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        agUser = new AgUser(getActivity());
        displayBirthday();
        setGenderView(agUser.getGender());
    }

    public void displayBirthday() {
        birthdayDate.setOnClickListener(v -> setBirthDayDate());
        birthdayDate.setText(dbp.format(agUser.getBirthday()));
        String tag = agUser.getBirthday();
        if (TextUtils.isEmpty(tag)) {
            tag = "01.01.1990";
        }
        birthdayDate.setTag(tag);
    }

    public void setBirthDayDate() {
        OnDateSetCallback listener = () -> {
            agUser.setBirthday(birthdayDate.getText().toString());
            personal.setBirthday(agUser.getBirthday());
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

    int selectedGender;

    public void setGenderView(AgUser.Gender userGender) {
        genderAdapter = getGenderAdapter();
        editGender.setAdapter(genderAdapter);
        selectedGender = genderAdapter.getPosition(userGender);
        editGender.setSelection(selectedGender, false);
        editGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != selectedGender) {
                    AgUser.Gender gender = (AgUser.Gender) genderAdapter.getItem(position);
                    selectedGender = position;
                    personal.setSex(gender == AgUser.Gender.NULL ? "" : gender.getValue());
                    agUser.setGender(gender);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public ArrayAdapter getGenderAdapter() {
        List<AgUser.Gender> list = new ArrayList<>(Arrays.asList(AgUser.Gender.getGenderItems()));
        ArrayAdapter<AgUser.Gender> ad = new ArrayAdapter<>(getActivity(), R.layout.layout_spinner_view, list);
        ad.setDropDownViewResource(R.layout.layout_spinner_item);
        return ad;
    }


    public boolean checkField() {
//        if (birthdayDate.getText().length() == 0) {
//            Toast.makeText(getActivity(), "Укажите дату рождения", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (selectedGender == 0) {
//            Toast.makeText(getActivity(), "Укажите пол", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }

    public void wizardAction() {
        if (checkField()) {
            personalInfoFragment.getViewModel().setPersonal(personal);
            personalInfoFragment.getViewModel().setAgUser(agUser);
            personalInfoFragment.getViewModel().confirmAction();
        }
    }
}
