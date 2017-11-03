package ru.mos.polls.profile.vm;

import android.support.v4.app.FragmentManager;
import android.databinding.Bindable;
import android.view.View;

import java.util.Calendar;

import ru.mos.polls.BR;
import ru.mos.polls.databinding.ItemBirthdayKidsBinding;
import ru.mos.polls.base.ui.dialog.YearDialogFragment;
import ru.mos.polls.base.vm.BaseVM;
import ru.mos.polls.profile.model.BirthdayKids;

/**
 * Created by Trunks on 10.07.2017.
 */

public class BirthdayKidsVM extends BaseVM<BirthdayKids, ItemBirthdayKidsBinding> {
    FragmentManager fr;

    public BirthdayKidsVM(BirthdayKids birthdayKids, ItemBirthdayKidsBinding binding, FragmentManager fr) {
        super(birthdayKids, binding);
        this.fr = fr;
    }

    public String getHint() {
        return model.getBirtdayHints();
    }

    @Bindable
    public String getTitle() {
        return model.getBirthdayYear() == 0 ? "" : model.getBirthDayTitle();
    }

    @Bindable
    public String getYear() {
        String yearString = getYearString(model.getBirthdayYear());
        return model.getBirthdayYear() == 0 ? "" : yearString;
    }

    public void setYear(long year) {
        model.setBirthdayYear(year);
        notifyPropertyChanged(BR.year);
        notifyPropertyChanged(BR.title);
    }

    public void onClick(View view) {
        YearDialogFragment ydf = new YearDialogFragment();
        YearDialogFragment.OnItemClick listener = year -> {
            ydf.dismiss();
            setYear(getLongDate(year));
        };
        ydf.setListener(listener);
        ydf.show(fr, "birthdayKid");
    }

    public long getLongDate(int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.DAY_OF_YEAR, 1);
        return c.getTimeInMillis();
    }

    public String getYearString(long year) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(year);
        return String.valueOf(c.get(Calendar.YEAR));
    }

}
