package ru.mos.polls.profile.gui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.BirthDateParser;
import ru.mos.elk.profile.DatePickerFragment;
import ru.mos.polls.R;

/**
 * Работа с личными данными пользователя (ФИО, пол, дата рождения,
 * семейное положение, количество детей, даты рождения детей, автомобиль, профессия
 * социальные данные)
 *
 * @since 1.9
 */
public class UserPersonalFragment extends AbstractProfileFragment {
    public static AbstractProfileFragment newInstance() {
        return new UserPersonalFragment();
    }

    @BindView(R.id.lastname)
    EditText lastname;
    @BindView(R.id.firstname)
    EditText firstname;
    @BindView(R.id.middlename)
    EditText middlename;
    @BindView(R.id.tvBirthDate)
    TextView birthdate;
    @BindView(R.id.gender)
    Spinner gender;

    private BirthDateParser dbp;
    private DatePickerFragment birthDayDialogFragment = null;

    private AgUser.Gender selectedGender;
    private AgUser.Gender.GenderAdapter genderAdapter;

    private GenderListener genderListener;

    public void setGenderListener(GenderListener genderListener) {
        this.genderListener = genderListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbp = new BirthDateParser(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_personal, null);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViews(view);
        AgUser agUser = new AgUser(getActivity());
        refreshUI(agUser);
    }

    @Override
    public void refreshUI(AgUser agUser) {
        lastname.setText(agUser.getSurname());
        firstname.setText(agUser.getFirstName());
        middlename.setText(agUser.getMiddleName());
        displayGender(agUser);
        displayBirthday(agUser);
    }

    @Override
    public void updateAgUser(AgUser agUser) {
        agUser.setFirstName(firstname.getText().toString())
                .setMiddleName(middlename.getText().toString())
                .setSurname(lastname.getText().toString())
                .setBirthday(birthdate.getText().toString())
                .setGender(selectedGender);

    }

    @Override
    public boolean isFilledAndChanged(AgUser old, AgUser changed) {
        boolean result = false;
        try {
            boolean isFilledPersonal = !changed.isEmptyPersonal();
            boolean isChangedPersonal = !changed.equalsPersonal(old);
            result = isFilledPersonal && isChangedPersonal;
        } catch (Exception ignored) {
        }
        return result;
    }

    private void findViews(View v) {
        displayGender(v);
    }

    private void displayBirthday(AgUser agUser) {
        birthdate.setText(dbp.format(agUser.getBirthday()));
        String tag = agUser.getBirthday();
        if (TextUtils.isEmpty(tag)) {
            tag = "01.01.1990";
        }
        birthdate.setTag(tag);
    }

    private void displayGender(AgUser agUser) {
        int selected = genderAdapter.getPosition(agUser.getGender());
        gender.setSelection(selected);
    }

    /**
     * Спинер для выбора пола, после выбора пола обновляем список для спинера семейного положения
     */
    private void displayGender(View v) {
        genderAdapter = (AgUser.Gender.GenderAdapter) AgUser.Gender.getSexAdapter(getActivity());
        gender.setAdapter(genderAdapter);
    }

    @OnItemSelected(R.id.gender)
    void setGenderListener(int position) {
        selectedGender = genderAdapter.getItem(position);
        if (genderListener != null) {
            genderListener.onChange(selectedGender);
        }
        changeListener.onChange(USER_PERSONAL_ID);
    }

    @OnClick(R.id.tvBirthDate)
    void setBirthDateListener(View v) {
        if (birthDayDialogFragment != null) {
            birthDayDialogFragment.dismiss();
            birthDayDialogFragment = null;
        }
        /**
         * Откатываем время назад
         */
        Calendar cal = Calendar.getInstance();


        birthDayDialogFragment = new DatePickerFragment();
        Bundle extras = new Bundle();
        /**
         * если пользователь ранее устанавливал дату рождения
         */
        extras.putString("curBirthDate", (String) birthdate.getTag());
        extras.putInt("viewId", v.getId());
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 10);
        extras.putLong(DatePickerFragment.MAXDATE, cal.getTimeInMillis());
        extras.putParcelable("BirthDateParser", dbp);
        /**
         * устанавливаем минимальное значение года
         */
        cal.set(Calendar.YEAR, 1900);
        extras.putLong(DatePickerFragment.MINDATE, cal.getTimeInMillis());
        birthDayDialogFragment.setArguments(extras);
        birthDayDialogFragment.show(getActivity().getSupportFragmentManager(), "SdatePicker");
    }

    @OnTextChanged(value = {R.id.lastname, R.id.firstname, R.id.middlename, R.id.tvBirthDate},
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void setTextChange() {
        changeListener.onChange(USER_PERSONAL_ID);
    }

    public interface GenderListener {
        void onChange(AgUser.Gender gender);
    }
}
