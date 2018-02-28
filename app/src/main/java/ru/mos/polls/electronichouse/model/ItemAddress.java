package ru.mos.polls.electronichouse.model;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.MainActivity;
import ru.mos.polls.R;
import ru.mos.polls.profile.model.AgUser;


public class ItemAddress {
    private String title;
    private String value;
    private Runnable action;
    public ItemAddress(String title, String value, Runnable action) {
        this.title = title;
        this.value = value;
        this.action = action;
    }

    public void doAction() {
        action.run();
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

    public Runnable getAction() {
        return action;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public static List<ItemAddress> getDefault(final MainActivity mainActivity) {
        List<ItemAddress> res = new ArrayList<>();
        AgUser user = new AgUser(mainActivity);

        String registerAddress = user.getRegistration().getAddressTitle(mainActivity.getBaseContext());
        res.add(new ItemAddress(mainActivity.getString(R.string.title_registration), registerAddress, () -> {
            mainActivity.navigateTo().state(Add.newActivity(new NewFlatState(user.getRegistration(), NewFlatFragmentVM.FLAT_TYPE_REGISTRATION), BaseActivity.class));
        }));

        res.add(new ItemAddress(mainActivity.getString(R.string.title_residance), getRegistrationAddressTitle(user, mainActivity), () -> {
            mainActivity.navigateTo().state(Add.newActivity(new NewFlatState(user.getResidence(), NewFlatFragmentVM.FLAT_TYPE_RESIDENCE), BaseActivity.class));
        }));

        String ownAddress = AgUser.getOwnPropertyList(mainActivity).size() > 0 ? "Указаны" : "Не указаны";
        res.add(new ItemAddress(mainActivity.getString(R.string.property_addresses), ownAddress, () -> {
            mainActivity.navigateTo().state(Add.newActivity(new AddPrivatePropertyState(null), BaseActivity.class));
        }));

        String pguConnected = user.isPguConnected() ? "Подключено" : "Не указана";
        res.add(new ItemAddress(mainActivity.getString(R.string.profile_pgu), pguConnected, () -> {
            mainActivity.navigateTo().state(Add.newActivity(new PguAuthState(PguAuthState.PGU_STATUS), BaseActivity.class));
        }));
        return res;
    }

    private static String getRegistrationAddressTitle(AgUser user, Activity activity) {
        if (user.getResidence().isEmpty() && user.getRegistration().isEmpty())
            return activity.getString(R.string.address_not_specified);
        return user.getRegistration().compareByFullAddress(user.getResidence()) || user.getResidence().isEmpty()
                ? "Совпадает с адресом регистрации" : user.getResidence().getAddressTitle(activity.getBaseContext());
    }
}
