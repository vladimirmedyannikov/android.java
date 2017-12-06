package ru.mos.polls.electronichouse.model;

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
        res.add(new ItemAddress(mainActivity.getString(R.string.title_registration), () -> {
            AgUser user = new AgUser(mainActivity);
            mainActivity.navigateTo().state(Add.newActivity(new NewFlatState(user.getRegistration(), NewFlatFragmentVM.FLAT_TYPE_REGISTRATION), BaseActivity.class));
        }));
        res.add(new ItemAddress(mainActivity.getString(R.string.title_residance), () -> {
            AgUser user = new AgUser(mainActivity);
            mainActivity.navigateTo().state(Add.newActivity(new NewFlatState(user.getResidence(), NewFlatFragmentVM.FLAT_TYPE_RESIDENCE), BaseActivity.class));
        }));
        res.add(new ItemAddress(mainActivity.getString(R.string.property_addresses), () -> {
            mainActivity.navigateTo().state(Add.newActivity(new AddPrivatePropertyState(null), BaseActivity.class));
        }));
        res.add(new ItemAddress(mainActivity.getString(R.string.profile_pgu), () -> {
            mainActivity.navigateTo().state(Add.newActivity(new PguAuthState(PguAuthState.PGU_STATUS), BaseActivity.class));
        }));
        return res;
    }
}
