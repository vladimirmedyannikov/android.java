package ru.mos.polls.newprofile.vm;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley2.VolleyError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.databinding.FragmentCustomFlatBinding;
import ru.mos.polls.newprofile.service.ProfileSet;
import ru.mos.polls.newprofile.service.model.FlatsEntity;
import ru.mos.polls.newprofile.ui.fragment.CustomFlatFragment;
import ru.mos.polls.newprofile.ui.fragment.NewFlatFragment;
import ru.mos.polls.profile.controller.FlatApiController;
import ru.mos.polls.profile.model.Reference;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;

/**
 * Created by Trunks on 03.08.2017.
 */

public class CustomFlatFragmentVM extends UIComponentFragmentViewModel<CustomFlatFragment, FragmentCustomFlatBinding> {
    boolean forWizard;
    Flat flat;
    TextInputEditText street;
    TextInputEditText building;
    Spinner districtSpinner;
    Spinner areaSpinner;
    TextView areaLabel;
    View arealayout;
    private List<String> districtList;
    private List<Reference> districtReference;
    private List<Reference> areaReference;
    public static final String USER_ID = "userid";
    String savedStreet;
    String savedBuilding;
    int flatType;

    public CustomFlatFragmentVM(CustomFlatFragment fragment, FragmentCustomFlatBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentCustomFlatBinding binding) {
        street = binding.street;
        building = binding.building;
        areaSpinner = binding.areaSpinner;
        districtSpinner = binding.districtSpinner;
        areaLabel = binding.areaLabel;
        arealayout = binding.areaLayout;
        Bundle extras = getFragment().getArguments();
        if (extras != null) {
            forWizard = extras.getBoolean(WizardProfileFragment.ARG_FOR_WIZARD);
            savedStreet = extras.getString(CustomFlatFragment.EXTRA_STREET);
            savedBuilding = extras.getString(CustomFlatFragment.EXTRA_HOUSE);
            flat = (Flat) extras.getSerializable(CustomFlatFragment.EXTRA_FLAT);
            flatType = extras.getInt(NewFlatFragment.ARG_FLAT_TYPE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestDistrictList();
        street.setText(savedStreet);
        building.setText(savedBuilding);
        showDeleteMenuIcon();
    }

    @Override
    public void onOptionsItemSelected(int menuItemId) {
        switch (menuItemId) {
            case R.id.action_confirm:
                confirmAction();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu() {
        super.onCreateOptionsMenu();
        showConfirmMenuIcon();
        if (forWizard) hideAllMenuIcon();
    }

    public void confirmAction() {
        if (checkForEnabled()) {
            prepareRequest();
        } else {
            Toast.makeText(getActivity(), "Заполните все поля", Toast.LENGTH_SHORT).show();
        }
    }

    public void prepareRequest() {
        FlatsEntity entity = null;
        Reference areaReference = (Reference) areaSpinner.getSelectedItem();
        flat.setDistrict(districtSpinner.getSelectedItem().toString().trim())
                .setArea(areaReference.getLabel())
                .setStreet(street.getText().toString().trim())
                .setBuilding(building.getText().toString().trim())
                .setAreaId(areaReference.getValue())
                .setBuildingId(USER_ID);
        if (flat.isRegistration()) {
            FlatsEntity.RegistrationEntity registrationEntity = new FlatsEntity.RegistrationEntity(USER_ID
                    , building.getText().toString().trim()
                    , street.getText().toString().trim()
                    , areaReference.getValue());
            if (!TextUtils.isEmpty(flat.getFlatId()))
                registrationEntity.setFlat_id(flat.getFlatId());
            entity = new FlatsEntity(registrationEntity);
        }
        if (flat.isResidence()) {
            FlatsEntity.ResidenceEntity residenceEntity = new FlatsEntity.ResidenceEntity(USER_ID
                    , building.getText().toString().trim()
                    , street.getText().toString().trim()
                    , areaReference.getValue());
            if (!TextUtils.isEmpty(flat.getFlatId()))
                residenceEntity.setFlat_id(flat.getFlatId());
            entity = new FlatsEntity(residenceEntity);
        }
        if (flat.isWork()) {
            FlatsEntity.WorkEntity workEntity = new FlatsEntity.WorkEntity(USER_ID
                    , building.getText().toString().trim()
                    , street.getText().toString().trim()
                    , areaReference.getValue());
            if (!TextUtils.isEmpty(flat.getFlatId()))
                workEntity.setFlat_id(flat.getFlatId());
            entity = new FlatsEntity(workEntity);
        }
        sendFlat(new ProfileSet.Request(entity));
    }

    /**
     * Отправляем адрес
     */
    public void sendFlat(ProfileSet.Request request) {
        HandlerApiResponseSubscriber<ProfileSet.Response.Result> handler
                = new HandlerApiResponseSubscriber<ProfileSet.Response.Result>(getActivity(), getComponent(ProgressableUIComponent.class)) {
            @Override
            protected void onResult(ProfileSet.Response.Result result) {
                Flat newFlat = null;
                int percent = 0;
                if (result != null) percent = result.getPercentFillProfile();
                if (result.getFlats().getRegistration() != null) {
                    newFlat = result.getFlats().getRegistration();
                    newFlat.setType(Flat.Type.REGISTRATION);
                }
                if (result.getFlats().getResidence() != null) {
                    newFlat = result.getFlats().getResidence();
                    newFlat.setType(Flat.Type.RESIDENCE);
                }
                if (result.getFlats().getWork() != null) {
                    newFlat = result.getFlats().getWork();
                    newFlat.setType(Flat.Type.WORK);
                }
                if (newFlat != null) {
                    newFlat.setEnable(!newFlat.isEnable());    //костыля потому что в FLAT result.enable = !flatJson.optBoolean("editing_blocked"); а GSON парсит в   @SerializedName("editing_blocked")
                    newFlat.save(getActivity());
                }
                AgUser.setPercentFillProfile(getActivity(), percent);
                if (!forWizard) {
                    showDeleteMenuIcon();
                    EditProfileFragmentVM.sendBroadcastReLoadBadges(getActivity());
                    AGApplication.bus().send(new Events.ProfileEvents(Events.ProfileEvents.UPDATE_FLAT, newFlat));
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                } else {
                    int wizardType = 0;
                    switch (flatType) {
                        case NewFlatFragmentVM.FLAT_TYPE_REGISTRATION:
                            wizardType = Events.WizardEvents.WIZARD_REGISTRATION;
                            break;
                        case NewFlatFragmentVM.FLAT_TYPE_RESIDENCE:
                            wizardType = Events.WizardEvents.WIZARD_RESIDENCE;
                            break;
                        case NewFlatFragmentVM.FLAT_TYPE_WORK:
                            wizardType = Events.WizardEvents.WIZARD_WORK;
                            break;
                    }
                    AGApplication.bus().send(new Events.WizardEvents(wizardType, percent));
                    disableView();
                }
            }
        };
        Observable<ProfileSet.Response> responseObservabl =
                AGApplication.api.setProfile(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservabl.subscribeWith(handler));
    }

    public void disableView() {
        areaSpinner.setEnabled(false);
        districtSpinner.setEnabled(false);
        street.setEnabled(false);
        building.setEnabled(false);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setListeners();
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().with(new ProgressableUIComponent()).build();
    }

    public void setListeners() {
        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkForEnabled();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    areaSpinner.setClickable(true);
                    arealayout.setVisibility(View.VISIBLE);
                    if (districtReference != null) {
                        for (Reference reference : districtReference) {
                            if (districtList.get(position).equals(reference.getLabel())) {
                                requestAreaList(reference.getValue());
                            }
                        }
                    }
                } else {
                    areaSpinner.setClickable(false);
                    areaReference = new ArrayList<Reference>();
                    refreshAreas();
                    arealayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public boolean checkForEnabled() {
        return street.getText().toString().trim().length() > 0
                && building.getText().toString().trim().length() > 0
                && areaSpinner.getSelectedItemPosition() != 0
                && districtSpinner.getSelectedItemPosition() != 0;
    }

    private void sortReference(List<Reference> referenceList) {
        Collections.sort(referenceList, new Comparator<Reference>() {
            public int compare(Reference s1, Reference s2) {
                return s1.getValue().compareToIgnoreCase(s2.getValue());
            }
        });
    }

    private void requestAreaList(String value) {
        FlatApiController.ReferenceListener districtListener = new FlatApiController.ReferenceListener() {
            @Override
            public void onLoaded(List<Reference> referenceList) {
                if (referenceList != null) {
                    areaReference = referenceList;
                }
                refreshAreas();
            }

            @Override
            public void onError(VolleyError volleyError) {
                refreshAreas();
            }
        };
        FlatApiController.getReference((BaseActivity) getActivity(), districtListener, value);
    }

    private void refreshAreas() {
        sortReference(areaReference);
        setAreaSpinnerAdapter();
    }

    private void setAreaSpinnerAdapter() {
        ArrayAdapter spinnerArrayAdapter = Reference.getAdapter(getActivity(), areaReference);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(spinnerArrayAdapter);
    }

    private void setDistrictSpinnerAdapter() {
        if (districtList != null) {
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), R.layout.layout_spinner_view, districtList);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            districtSpinner.setAdapter(spinnerArrayAdapter);
        }
    }

    public void showConfirmMenuIcon() {
        getFragment().hideMenuItem(R.id.action_delet_flat);
        getFragment().showMenuItem(R.id.action_confirm);
    }

    public void showDeleteMenuIcon() {
        getFragment().hideMenuItem(R.id.action_confirm);
        getFragment().showMenuItem(R.id.action_delet_flat);
    }

    public void hideAllMenuIcon() {
        getFragment().hideMenuItem(R.id.action_confirm);
        getFragment().hideMenuItem(R.id.action_delet_flat);
    }

    private void requestDistrictList() {
        FlatApiController.ReferenceListener districtListener = new FlatApiController.ReferenceListener() {
            @Override
            public void onLoaded(List<Reference> referenceList) {
                districtReference = referenceList;
                getDistrictList();
                setDistrictSpinnerAdapter();
            }

            @Override
            public void onError(VolleyError volleyError) {
                getDistrictList();
                setDistrictSpinnerAdapter();
            }
        };
        FlatApiController.getReference((BaseActivity) getActivity(), districtListener, null);
    }

    private void getDistrictList() {
        districtList = new ArrayList<>();
        sortReference(districtReference);
        if (districtReference != null) {
            for (Reference refence : districtReference) {
                districtList.add(refence.getLabel());
            }
        }
    }
}
