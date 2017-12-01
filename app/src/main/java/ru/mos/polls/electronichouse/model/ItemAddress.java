package ru.mos.polls.electronichouse.model;

import java.util.ArrayList;
import java.util.List;

import ru.mos.elk.profile.AgUser;
import ru.mos.polls.MainActivity;
import ru.mos.polls.R;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 01.12.17.
 */

public class ItemAddress {
    private String title;
    private Runnable action;
    public ItemAddress(String title, Runnable action) {
        this.title = title;
        this.action = action;
    }

    public void doAction() {
        action.run();
    }

    public String getTitle() {
        return title;
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
        res.add(new ItemAddress(mainActivity.getString(R.string.title_registration), () -> {
            // TODO: 01.12.17 навигация в адрес регистрации 
        }));
        res.add(new ItemAddress(mainActivity.getString(R.string.title_residance), () -> {
            // TODO: 01.12.17 навигация в адрес проживания 
        }));
        res.add(new ItemAddress(mainActivity.getString(R.string.property_addresses), () -> {
            // TODO: 01.12.17 навигация в адреса собственности 
        }));
        res.add(new ItemAddress(mainActivity.getString(R.string.profile_pgu), () -> {
            // TODO: 01.12.17 навигация в привязку к пгу
        }));
        return res;
    }
}
