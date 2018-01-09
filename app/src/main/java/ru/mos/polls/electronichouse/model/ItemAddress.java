package ru.mos.polls.electronichouse.model;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import me.ilich.juggler.change.Add;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.MainActivity;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BaseActivity;
import ru.mos.polls.profile.state.AddPrivatePropertyState;
import ru.mos.polls.profile.state.NewFlatState;
import ru.mos.polls.profile.state.PguAuthState;
import ru.mos.polls.profile.vm.NewFlatFragmentVM;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 01.12.17.
 */

public class ItemAddress {
    private String title;
    private String value;
    private boolean checked;

    public ItemAddress(String title, String value, boolean checked) {
        this.title = title;
        this.value = value;
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static List<ItemAddress> getDefault(final MainActivity mainActivity) {
        List<ItemAddress> res = new ArrayList<>();
        AgUser user = new AgUser(mainActivity);

        String registerAddress = user.getRegistration().getAddressTitle(mainActivity.getBaseContext());
        res.add(new ItemAddress(mainActivity.getString(R.string.title_registration), registerAddress, !user.getRegistration().isEmpty()));

        res.add(new ItemAddress(mainActivity.getString(R.string.title_residance), getRegistrationAddressTitle(user, mainActivity), !user.getResidence().isEmpty()));

        String ownAddress = AgUser.getOwnPropertyList(mainActivity).size() > 0 ? "Указаны" : "Не указаны";
        res.add(new ItemAddress(mainActivity.getString(R.string.property_addresses), ownAddress, AgUser.getOwnPropertyList(mainActivity).size() > 0));

        String pguConnected = user.isPguConnected() ? "Подключено" : "Не указана";
        res.add(new ItemAddress(mainActivity.getString(R.string.profile_pgu), pguConnected, user.isPguConnected()));
        return res;
    }

    private static String getRegistrationAddressTitle(AgUser user, Activity activity) {
        if (user.getResidence().isEmpty() && user.getRegistration().isEmpty())
            return activity.getString(R.string.address_not_specified);
        return user.getRegistration().compareByFullAddress(user.getResidence()) || user.getResidence().isEmpty()
                ? "Совпадает с адресом регистрации" : user.getResidence().getAddressTitle(activity.getBaseContext());
    }
}
