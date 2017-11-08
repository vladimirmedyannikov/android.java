package ru.mos.polls.profile.vm;

import android.app.Activity;
import android.widget.TextView;

import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.R;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentProfileQuestBinding;
import ru.mos.polls.profile.state.NewFlatState;
import ru.mos.polls.profile.ui.fragment.ProfileQuestFragment;

/**
 * Created by Trunks on 08.11.2017.
 */

public class ProfileQuestFragmentVM extends UIComponentFragmentViewModel<ProfileQuestFragment, FragmentProfileQuestBinding> {

    TextView registration;
    TextView residence;
    AgUser agUser;

    public ProfileQuestFragmentVM(ProfileQuestFragment fragment, FragmentProfileQuestBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentProfileQuestBinding binding) {
        registration = binding.editFlatRegistration;
        residence = binding.editFlatResidence;
        setListener();
        getActivity().setResult(Activity.RESULT_OK);
    }

    @Override
    public void onResume() {
        super.onResume();
        agUser = new AgUser(getActivity());
        setRegistationFlatView(agUser.getRegistration());
        setResidenceFlatView(agUser.getRegistration(), agUser.getResidence());
    }

    private void setListener() {
        registration.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new NewFlatState(agUser.getRegistration(), NewFlatFragmentVM.FLAT_TYPE_REGISTRATION), NewFlatFragmentVM.FLAT_TYPE_REGISTRATION);
        });
        residence.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new NewFlatState(agUser.getResidence(), NewFlatFragmentVM.FLAT_TYPE_RESIDENCE), NewFlatFragmentVM.FLAT_TYPE_RESIDENCE);
        });
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return null;
    }

    public void setRegistationFlatView(Flat flat) {
        setFlatView(flat, registration);
    }

    public void setResidenceFlatView(Flat registationFlat, Flat residenceFlat) {
        if (!registationFlat.isEmpty() && residenceFlat.compareByFullAddress(registationFlat)) {
            residence.setText(getActivity().getString(R.string.coincidesAddressRegistration));
            return;
        }
        setFlatView(residenceFlat, residence);
    }


    public void setFlatView(Flat flat, TextView view) {
        view.setText("");
        if (!flat.isEmpty())
            view.setText(flat.getAddressTitle(getActivity().getBaseContext()));
    }
}
