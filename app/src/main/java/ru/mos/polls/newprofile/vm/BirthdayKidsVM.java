package ru.mos.polls.newprofile.vm;

import android.support.v4.app.FragmentManager;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableField;
import android.view.View;

import java.util.Calendar;

import ru.mos.polls.databinding.ItemBirthdayKidsBinding;
import ru.mos.polls.newprofile.base.ui.dialog.YearDialogFragment;
import ru.mos.polls.newprofile.model.BirthdayKids;

/**
 * Created by Trunks on 10.07.2017.
 */

public class BirthdayKidsVM extends BaseObservable {
    BirthdayKids birthdayKids;
    ItemBirthdayKidsBinding binding;
    ObservableField<BirthdayKids> birthdayKidsField = new ObservableField<>();
    FragmentManager fr;

    public BirthdayKidsVM(BirthdayKids birthdayKids, ItemBirthdayKidsBinding binding, FragmentManager fr) {
        this.birthdayKids = birthdayKids;
        this.binding = binding;
        this.fr = fr;
    }

    @Bindable
    public String getDate() {
        return Long.toString(birthdayKids.getBirthdayYear());
    }

    public String getHint() {
        return birthdayKids.getBirtdayHints();
    }

    public String getTitle() {
        return birthdayKids.getBirthdayYear() == 0 ? "" : birthdayKids.getBirthDayTitle();
    }

    public String getYear() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(birthdayKids.getBirthdayYear());
        String yearString = String.valueOf(c.get(Calendar.YEAR));
        return birthdayKids.getBirthdayYear() == 0 ? "" : yearString;
    }

    public void onClick(View view) {
        YearDialogFragment ydf = new YearDialogFragment();
        ydf.show(fr, "kid_year");
    }

}
