package ru.mos.polls.profile.gui.fragment.location;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.R;
import ru.mos.polls.helpers.FragmentHelper;
import ru.mos.polls.profile.gui.fragment.AbstractProfileFragment;

/**
 * Работа с адресами пользователя (адрес регистрации
 * адрес проживания)
 *
 * @since 1.9
 */
public class FlatsFragment extends AbstractProfileFragment {
    public static final String EXTRA_HIDE_WARNING_FOR_ADD_FLATS = "extra_show_warning_for_add_flars";

    public static AbstractProfileFragment newInstanceWithoutWarning() {
        AbstractProfileFragment result = new FlatsFragment();
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_HIDE_WARNING_FOR_ADD_FLATS, true);
        result.setArguments(args);
        return result;
    }

    public static AbstractProfileFragment newInstance() {
        return new FlatsFragment();
    }

    @BindView(R.id.residenceContainer)
    View residenceContainer;
    @BindView(R.id.equalsContainer)
    View equalsContainer;
    @BindView(R.id.btnRegistration)
    TextView registrationView;
    @BindView(R.id.btnResidence)
    TextView residenceView;
    @BindView(R.id.checkСoincides)
    CheckBox equalsFlats;
    //    private Unbinder unbinder;
    private Flat registration, residence;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Flat newFlat = NewAddressActivity.onResult(requestCode, resultCode, data);
        if (newFlat != null) {
            if (newFlat.isRegistration()) {
                registration = newFlat;
                registration.save(getActivity());
                if (registration.compareByFullAddress(residence)) {
                    cloneResidenceFromRegistration();
                }
            }
            if (newFlat.isResidence()) {
                residence = newFlat;
                residence.save(getActivity());
            }
        }
        refreshUI();
        changeListener.onChange(ADDRESS_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_flats, null);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViews(view);
        refreshUI();
    }

    @Override
    public void updateAgUser(AgUser agUser) {
        agUser.setRegistrationFlat(registration)
                .setResidenceFlat(residence);
    }

    @Override
    public void refreshUI(AgUser agUser) {
        refreshUI();
    }

    @Override
    public boolean isFilledAndChanged(AgUser old, AgUser changed) {
        boolean result = false;
        try {
            boolean isChangedRegistration = isChanged(old.getRegistration(), changed.getRegistration());
            boolean isChangedResidence = isChanged(old.getResidence(), changed.getResidence());
            result = isChangedRegistration || isChangedResidence;
        } catch (Exception ignored) {
        }

        return result;
    }

    private boolean isChanged(Flat old, Flat edited) {
        return (!edited.isEmpty() && !old.equals(edited))
                || (edited.isEmpty() && !old.isEmpty());
    }

    private boolean isHideWarnings() {
        boolean result = false;
        if (getArguments() != null) {
            result = getArguments().getBoolean(EXTRA_HIDE_WARNING_FOR_ADD_FLATS);
        }
        return result;
    }

    @OnCheckedChanged(R.id.checkСoincides)
    void checkEquals(boolean isChecked) {
        if (isChecked) {
            if (!equalsFlats()) {
                showConfirmForDeleteResidence();
            } else {
                refreshUI();
            }
        } else {
            residenceContainer.setVisibility(View.VISIBLE);
        }
    }


    private void findViews(View v) {
    }

    @OnClick(R.id.btnRegistration)
    void registration() {
        Fragment fragment = FragmentHelper.getParentFragment(FlatsFragment.this);
        if (fragment == null) {
            fragment = FlatsFragment.this;
        }
        NewAddressActivity.startActivity(fragment, registration, isHideWarnings());
    }

    @OnClick(R.id.btnResidence)
    void residence() {
        Fragment fragment = FragmentHelper.getParentFragment(FlatsFragment.this);
        if (fragment == null) {
            fragment = FlatsFragment.this;
        }
        NewAddressActivity.startActivity(fragment, residence, isHideWarnings());
    }

    private void refreshUI() {
        registration = Flat.getRegistration(getActivity());
        registrationView.setText(registration.getViewTitle(getActivity()));
        int drawable = registration.isEmpty() ? R.drawable.ag_home : 0;
        registrationView.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);

        residence = Flat.getResidence(getActivity());
        String title = residence.getViewTitle(getActivity());
        if (registration.compareByFullAddress(residence)) {
            title = getString(R.string.add_address);
        }
        residenceView.setText(title);
        drawable = residence.isEmpty() ? R.drawable.ag_home : 0;
        residenceView.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);

        displayEqualsFlats();
    }

    private boolean equalsFlats() {
        return registration.compareByFullAddress(residence)
                || residence.isEmpty();
    }

    private void showConfirmForDeleteResidence() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.whould_you_like_to_delete_residence_address);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                equalsFlats.setChecked(false);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                equalsFlats.setChecked(false);
            }
        });
        builder.setPositiveButton(R.string.ag_continue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                residence.delete(getActivity());
                residence = Flat.getResidence(getActivity());
                refreshUI();
            }
        });
        builder.show();
    }

    private void cloneResidenceFromRegistration() {
        if (residence != null && residence.isEmpty()) {
            residence = Flat.getRegistration(getActivity());
            residence.setType(Flat.Type.RESIDENCE);
            residence.save(getActivity());
        }
    }

    private void displayEqualsFlats() {
        boolean equals = equalsFlats();
        equalsFlats.setChecked(equals);
        residenceContainer.setVisibility(equals ? View.GONE : View.VISIBLE);
        equalsContainer.setVisibility(View.VISIBLE);
    }
}
