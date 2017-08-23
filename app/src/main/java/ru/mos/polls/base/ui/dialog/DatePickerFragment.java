package ru.mos.polls.base.ui.dialog;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import ru.mos.elk.profile.BirthDateParser;
import ru.mos.polls.newprofile.vm.OnDateSetCallback;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public static final String MINDATE = "minDate";
    public static final String MAXDATE = "maxDate";
    private static final String CURBIRTHDATE = "curBirthDate";
    private static final String BIRTHDATEPARSER = "BirthDateParser";
    private static final String VIEWID = "viewId";
    private int id;
    private DatePickerDialog dialog;
    OnDateSetCallback listener;

    public DatePickerFragment() {
    }


    @SuppressLint("NewApi")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year, month, day;
        String curBirthDate = null;
        Bundle args = getArguments();
        if (args != null)
            curBirthDate = args.getString(CURBIRTHDATE);
        id = args.getInt(VIEWID);
        if (curBirthDate == null || curBirthDate.length() < 5) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        } else {
            String[] elem = curBirthDate.split("\\.");
            day = Integer.parseInt(elem[0]);
            month = Integer.parseInt(elem[1]) - 1;
            year = Integer.parseInt(elem[2]);
        }

        // Create a new instance of DatePickerDialog and return it\
        dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) { //#12147. maybe user backport https://github.com/SimonVT/android-datepicker
            DatePicker picker = dialog.getDatePicker();
            long min = args.getLong(MINDATE, 0);
            if (min != 0) {
                picker.setMinDate(min);
            }
            long max = args.getLong(MAXDATE, 0);
            if (max != 0) {
                picker.setMaxDate(max);
            } else {
                picker.setMaxDate(System.currentTimeMillis());
            }
        }
        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        TextView birthDate = (TextView) getActivity().findViewById(id);
        Bundle args = getArguments();
        if (args != null && birthDate != null) {
            BirthDateParser dbp = args.getParcelable(BIRTHDATEPARSER);
            String value = dbp.make(day, month + 1, year);
            birthDate.setTag(value);
            birthDate.setText(dbp.format(value));
        }
        if (listener != null) listener.onDateSet();
    }

    public void setOkButtomListener(OnDateSetCallback listener) {
        this.listener = listener;
    }
}
