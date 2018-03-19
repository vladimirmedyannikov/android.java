package ru.mos.polls.profile.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ilich.juggler.change.Add;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.profile.model.flat.Flat;
import ru.mos.polls.profile.state.NewFlatState;
import ru.mos.polls.profile.vm.NewFlatFragmentVM;

public class QuestActivity extends ToolbarAbstractActivity {
    @BindView(R.id.edit_flat_registration)
    TextView registration;
    @BindView(R.id.edit_flat_residence)
    TextView residence;
    AgUser agUser;
    public static final int REQUEST_ADD_FLAT = 122;

    public static void startActivityAddFlat(Fragment fragment) {
        Intent start = new Intent(fragment.getActivity(), QuestActivity.class);
        fragment.startActivityForResult(start, REQUEST_ADD_FLAT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_quest);
        ButterKnife.bind(this);
        setTitle(getString(R.string.update_location_activity));
        setResult(RESULT_OK);
        setListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        agUser = new AgUser(this);
        setRegistationFlatView(agUser.getRegistration());
        setResidenceFlatView(agUser.getRegistration(), agUser.getResidence());
    }

    private void setListener() {
        registration.setOnClickListener(v -> {
            navigateTo().state(Add.newActivityForResult(new NewFlatState(agUser.getRegistration(), NewFlatFragmentVM.FLAT_TYPE_REGISTRATION), BaseActivity.class, NewFlatFragmentVM.FLAT_TYPE_REGISTRATION));
        });
        residence.setOnClickListener(v -> {
            navigateTo().state(Add.newActivityForResult(new NewFlatState(agUser.getResidence(), NewFlatFragmentVM.FLAT_TYPE_RESIDENCE), BaseActivity.class, NewFlatFragmentVM.FLAT_TYPE_RESIDENCE));
        });
    }


    public void setRegistationFlatView(Flat flat) {
        setFlatView(flat, registration);
    }

    public void setResidenceFlatView(Flat registationFlat, Flat residenceFlat) {
        if (!registationFlat.isEmpty() && residenceFlat.compareByFullAddress(registationFlat)) {
            residence.setText(getString(R.string.coincidesAddressRegistration));
            return;
        }
        setFlatView(residenceFlat, residence);
    }


    public void setFlatView(Flat flat, TextView view) {
        view.setText("");
        if (!flat.isEmpty())
            view.setText(flat.getAddressTitle(this));
    }
}
