package ru.mos.polls.newprofile.ui.fragment;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.ilich.juggler.change.Add;
import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.VoidParams;
import ru.mos.polls.AGApplication;
import ru.mos.polls.BR;
import ru.mos.polls.MainActivity;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutNewProfileBinding;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.newprofile.base.ui.BindingFragment;
import ru.mos.polls.newprofile.state.EditProfileState;
import ru.mos.polls.newprofile.ui.vm.ProfileFragmentVM;

/**
 * Created by Trunks on 06.06.2017.
 */

public class ProfileFragment extends BindingFragment<ProfileFragmentVM, LayoutNewProfileBinding> {

    public static ProfileFragment newInstance() {
        ProfileFragment f = new ProfileFragment();
        return f;
    }

    public ProfileFragment() {
        AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof Events.ProfileEvents) {
                        Events.ProfileEvents action = (Events.ProfileEvents) o;
                        switch (action.getAction()) {
                            case Events.ProfileEvents.EDIT_USER_INFO:
                                navigateTo().state(Add.deeper(new EditProfileState(VoidParams.instance())));
                                break;
                        }
                    }
                });
    }

    @Override
    protected ProfileFragmentVM onCreateViewModel(LayoutNewProfileBinding binding) {
        return new ProfileFragmentVM(this, getBinding());
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_new_profile;
    }
}
