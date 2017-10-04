package ru.mos.polls.newprofile.vm;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley2.VolleyError;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.elk.profile.flat.Value;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentNewFlatBinding;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.vm.MenuFragmentVM;
import ru.mos.polls.newprofile.service.ProfileSet;
import ru.mos.polls.newprofile.service.model.FlatsEntity;
import ru.mos.polls.newprofile.state.CustomFlatState;
import ru.mos.polls.newprofile.ui.fragment.CustomFlatFragment;
import ru.mos.polls.newprofile.ui.fragment.NewFlatFragment;
import ru.mos.polls.profile.controller.FlatApiController;
import ru.mos.polls.profile.gui.fragment.location.BuildingWatcher;
import ru.mos.polls.profile.gui.fragment.location.StreetWatcher;
import ru.mos.polls.profile.model.DistrictArea;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;
import ru.mos.polls.wizardprofile.vm.WizardCustomFlatListener;

/**
 * Created by Trunks on 23.07.2017.
 */

public class NewFlatFragmentVM extends MenuFragmentVM<NewFlatFragment, FragmentNewFlatBinding> {
    public static final int FLAT_TYPE_REGISTRATION = 12234;
    public static final int FLAT_TYPE_RESIDENCE = 11223;
    public static final int FLAT_TYPE_WORK = 11132;
    private static final int ANIMATION_DURATION_MILLS = 300;
    int flatType;
    Flat flat;
    AutoCompleteTextView etStreet;
    AutoCompleteTextView etBuilding;
    SwitchCompat residenceToggle;
    View streetNotFoundView;
    View buildingNotFoundView;
    View warningContainer;
    View residenceToggleLayout;
    View addFlatLayout;
    TextView areaFlat;
    TextView districtFlat;
    TextView tvWarningEditingBlocked;
    TextView tvErrorEditingBlocked;
    LinearLayout areaAndDistrictLayout;
    LinearLayout streetNotFoundContainer;
    LinearLayout buildingNotFoundContainer;
    private boolean isAddressSelected;
    boolean forWizard;
    WizardCustomFlatListener wizardCustomFlatListener;


    public NewFlatFragmentVM(NewFlatFragment fragment, FragmentNewFlatBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentNewFlatBinding binding) {
        Bundle extras = getFragment().getArguments();
        if (extras != null) {
            flatType = extras.getInt(NewFlatFragment.ARG_FLAT_TYPE);
            flat = (Flat) extras.get(NewFlatFragment.ARG_FLAT);
            forWizard = extras.getBoolean(WizardProfileFragment.ARG_FOR_WIZARD);
        }
        etStreet = binding.layoutAddFlat.etStreet;
        etBuilding = binding.layoutAddFlat.etBuilding;
        streetNotFoundView = binding.layoutAddFlat.streetNotFoundContainer;
        buildingNotFoundView = binding.layoutAddFlat.buildingNotFoundContainer;
        areaFlat = binding.layoutAddFlat.areaFlat;
        districtFlat = binding.layoutAddFlat.districtFlat;
        areaAndDistrictLayout = binding.layoutAddFlat.areaAndDistrictLayout;
        tvWarningEditingBlocked = binding.layoutFlatWarningBlock.tvWarningEditingBlocked;
        tvErrorEditingBlocked = binding.layoutFlatWarningBlock.tvErrorEditingBlocked;
        warningContainer = binding.layoutFlatWarningBlock.warningContainer;
        residenceToggleLayout = binding.layoutFlatResidenceToggle.flatResidenceToggleView;
        residenceToggle = binding.layoutFlatResidenceToggle.flatResidenceToggle;
        addFlatLayout = binding.layoutAddFlat.layoutAddFlat;
        streetNotFoundContainer = binding.layoutAddFlat.streetNotFoundContainer;
        buildingNotFoundContainer = binding.layoutAddFlat.buildingNotFoundContainer;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setListener();
        AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof Events.ProfileEvents) {
                        Events.ProfileEvents action = (Events.ProfileEvents) o;
                        switch (action.getEventType()) {
                            case Events.ProfileEvents.UPDATE_FLAT:
                                Flat upDateFlat = action.getFlat();
                                if (upDateFlat != null) {
                                    flat = upDateFlat;
                                }
                                break;
                        }
                    }
                });
    }

    public void setListener() {
        streetNotFoundContainer.setOnClickListener(v -> customFlat());
        buildingNotFoundContainer.setOnClickListener(v -> customFlat());
        residenceToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                visibilityFlatInput(View.GONE);
            } else {
                visibilityFlatInput(View.VISIBLE);
            }
        });
        etStreet.addTextChangedListener(new StreetWatcher((BaseActivity) getActivity(), etStreet, getFragment().getView().findViewById(R.id.pbStreet), new StreetWatcher.Listener() {
            @Override
            public void onDataLoaded(int count) {
                buildingNotFoundView.setVisibility(View.GONE);
                if (count == 0) {
                    streetNotFoundView.setVisibility(View.VISIBLE);
                } else {
                    streetNotFoundView.setVisibility(View.GONE);
                }
            }
        }));
        etStreet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideAreaDistrict();
                    etStreet.setText("");
                    etStreet.setTag(null);
                    etBuilding.setText("");
                    etBuilding.setTag(null);
                    etBuilding.setEnabled(false);
                }
            }
        });
        etStreet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Value v = (Value) parent.getItemAtPosition(position);
                etStreet.setTag(v.getValue());
                flat.setStreet(v.getLabel());
                etBuilding.requestFocus();
                etBuilding.setEnabled(true);
                etBuilding.setAdapter((ArrayAdapter<Value>) null);
                etBuilding.setText("");
                isAddressSelected = false;
            }
        });
        etBuilding.addTextChangedListener(new BuildingWatcher((BaseActivity) getActivity(), etStreet, etBuilding, getFragment().getView().findViewById(R.id.pbBuilding), new BuildingWatcher.Listener() {
            @Override
            public void onDataLoaded(int count) {
                streetNotFoundView.setVisibility(View.GONE);
                if (count == 0 && !isAddressSelected) {
                    buildingNotFoundView.setVisibility(View.VISIBLE);
                } else {
                    buildingNotFoundView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAfterTextChanged(Editable s) {
                isAddressSelected = false;
            }
        }));
        etBuilding.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Value v = (Value) parent.getItemAtPosition(position);
                String buildingId = v.getValue();
                etBuilding.setTag(buildingId);
                flat.setBuildingId(buildingId);
                flat.setBuilding(v.getLabel());
                isAddressSelected = true;
                requestDistrictAndArea(v);
            }
        });
        etBuilding.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == ru.mos.elk.R.id.actionLogin || actionId == EditorInfo.IME_ACTION_NEXT) {
                    return true;
                }
                return false;
            }
        });
        etBuilding.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && v.getTag() == null) {
                    etBuilding.setText("");
                    /**
                     *  если после выбора строения из списка
                     *  пользователь снова
                     *  тыкает на строение стираем все то что в строении
                     *  чтоб не возвращалось то что пользователь выбрал
                     */
                } else if (hasFocus) {
                    etBuilding.setText("");
                    etBuilding.setTag(null);
                }
            }
        });
    }

    void customFlat() {
        String street = etStreet.getText().toString();
        String building = etBuilding.getText().toString();
        if (!forWizard) {
            boolean isHideWarnings = getFragment().getArguments().getBoolean(NewFlatFragment.ARG_HIDE_WARNING_FOR_ADD_FLATS, false);
            getFragment().navigateToActivityForResult(new CustomFlatState(street, building, flat, isHideWarnings, false, flatType), CustomFlatFragment.REQUEST_FLAT);
        } else {
            if (wizardCustomFlatListener != null) {
                wizardCustomFlatListener.onCustomFlatListener(street, building);
            }
        }

    }

    public void visibilityFlatInput(int gone) {
        addFlatLayout.setVisibility(gone);
        warningContainer.setVisibility(gone);
    }

    @Override
    public void onResume() {
        super.onResume();
        configViews(flatType);
    }

    @Override
    public void onCreateOptionsMenu() {
        processMenuIcon();
        if (forWizard) hideAllMenuIcon();
    }

    public void processMenuIcon() {
        if (!flat.isEmpty() && flat.isEnable()) {
            showDeleteMenuIcon();
        }
        if (!flat.isEnable()) {
            hideAllMenuIcon();
        }
        if (flat.isEmpty()) {
            showConfirmMenuIcon();
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

    @Override
    public void onOptionsItemSelected(int menuItemId) {
        switch (menuItemId) {
            case R.id.action_delet_flat:
                changeFlat();
                break;
            case R.id.action_confirm:
                confirmAction();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CustomFlatFragment.REQUEST_FLAT && resultCode == Activity.RESULT_OK) {
            if (!forWizard) {
                getActivity().finish();
            }
        }
    }

    public void setWizardCustomFlatListener(WizardCustomFlatListener wizardCustomFlatListener) {
        this.wizardCustomFlatListener = wizardCustomFlatListener;
    }

    public void confirmAction() {
        FlatsEntity entity = null;
        if (flat.isRegistration()) {
            FlatsEntity.RegistrationEntity registrationEntity = new FlatsEntity.RegistrationEntity(flat.getBuildingId());
            if (!TextUtils.isEmpty(flat.getFlatId()))// тоже почему то при первом надо только building_id, а при обновлении flat_id
                registrationEntity.setFlat_id(flat.getFlatId());
            entity = new FlatsEntity(registrationEntity);
        }
        if (flat.isResidence()) {
            FlatsEntity.ResidenceEntity residenceEntity = new FlatsEntity.ResidenceEntity();
            if (residenceToggle.isChecked()) {
                if (!TextUtils.isEmpty(flat.getFlatId())) {
                    residenceEntity.setKill(true);
                } else {
                    residenceEntity.setBuilding_id(Flat.getRegistration(getActivity()).getBuildingId());
                }
            } else {
                residenceEntity = new FlatsEntity.ResidenceEntity(flat.getBuildingId());
                if (!TextUtils.isEmpty(flat.getFlatId()))
                    residenceEntity.setFlat_id(flat.getFlatId());
            }
            entity = new FlatsEntity(residenceEntity);
        }
        if (flat.isWork()) {
            FlatsEntity.WorkEntity workEntity = new FlatsEntity.WorkEntity(flat.getBuildingId());
            if (!TextUtils.isEmpty(flat.getFlatId())) workEntity.setFlat_id(flat.getFlatId());
            entity = new FlatsEntity(workEntity);
        }
        sendFlat(new ProfileSet.Request(entity));
    }

    void changeFlat() {
        showConfirmMenuIcon();
        etBuilding.getText().clear();
        etBuilding.setEnabled(true);
        etBuilding.setTextColor(getFragment().getResources().getColor(R.color.editTextColor));
        etStreet.getText().clear();
        etStreet.setEnabled(true);
        etStreet.setTextColor(getFragment().getResources().getColor(R.color.editTextColor));
        hideAreaDistrict();
    }


    /**
     * Отправляем адрес
     */
    public void sendFlat(ProfileSet.Request request) {
        HandlerApiResponseSubscriber<ProfileSet.Response.Result> handler
                = new HandlerApiResponseSubscriber<ProfileSet.Response.Result>(getActivity(), progressable) {
            @Override
            protected void onResult(ProfileSet.Response.Result result) {
                Flat newFlat = null;
                if (result != null && result.getFlats() != null) {
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
                }
                if (flatType == FLAT_TYPE_RESIDENCE && residenceToggle.isChecked()) {
                    flat.delete(getActivity());
                }
                setupViewIfNotEmpty();
                EditProfileFragmentVM.sendBroadcastReLoadBadges(getActivity());
                if (!forWizard) {
                    showDeleteMenuIcon();
                    getActivity().finish();
                } else {
                    int wizardType = 0;
                    switch (flatType) {
                        case FLAT_TYPE_REGISTRATION:
                            wizardType = Events.WizardEvents.WIZARD_REGISTRATION;
                            break;
                        case FLAT_TYPE_RESIDENCE:
                            wizardType = Events.WizardEvents.WIZARD_RESIDENCE;
                            break;
                        case FLAT_TYPE_WORK:
                            wizardType = Events.WizardEvents.WIZARD_WORK;
                            break;
                    }
                    int percent = 0;
                    if (result != null) percent = result.getPercentFillProfile();
                    AGApplication.bus().send(new Events.WizardEvents(wizardType, percent));
                }
            }
        };
        Observable<ProfileSet.Response> responseObservabl =
                AGApplication.api.setProfile(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservabl.subscribeWith(handler));
    }

    private void configViews(int flatType) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        /**
         * Если адрес проживания совпадает с адресом регистрации
         * то не отображаем адрес проживания, очищаем поля
         */
        if (!flat.isEmpty()) {
            setupViewIfNotEmpty();
        }
        /**
         * показываем переключатель для адреса прожиывания
         */
        if (flat.isResidence()) residenceToggleLayout.setVisibility(View.VISIBLE);
        /**
         * определяем вид для заполнения адреса прохивания
         */
        switch (flatType) {
            case FLAT_TYPE_RESIDENCE:
                if (flat.isEmpty()) {
                    visibilityFlatInput(View.GONE);
                    residenceToggle.setChecked(true);
                } else {
                    visibilityFlatInput(View.VISIBLE);
                    residenceToggle.setChecked(false);
                }
                break;
        }

        etStreet.setText(flat.getStreet());
        etBuilding.setText(flat.getBuilding());
        districtFlat.setText(flat.getDistrict());
        areaFlat.setText(flat.getArea());

        setEditingBlocked();
    }

    private String clearText(String text) {
        return text.replace("?", "");
    }

    private void displayAreaDistrict() {
        if (areaAndDistrictLayout.getVisibility() == View.VISIBLE) {
            return;
        }
        Animator.AnimatorListener listener = new DefaultAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                areaAndDistrictLayout.setVisibility(View.VISIBLE);
            }
        };
        areaAndDistrictLayout.setVisibility(View.VISIBLE);
        areaAndDistrictLayout.setAlpha(0.0f);
        areaAndDistrictLayout.animate()
                .setDuration(ANIMATION_DURATION_MILLS)
                .alpha(1.0f)
                .setListener(listener);
    }

    private void requestDistrictAndArea(Value v) {
        FlatApiController.DistrictAreaListener districtAreaListener = new FlatApiController.DistrictAreaListener() {
            @Override
            public void onLoaded(DistrictArea districtArea) {
                if (districtArea != null) {
                    areaFlat.setText(clearText(districtArea.getArea()));
                    districtFlat.setText(clearText(districtArea.getDistrict()));
                    flat.setArea(clearText(districtArea.getArea()));
                    flat.setDistrict(clearText(districtArea.getDistrict()));
                    displayAreaDistrict();
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                hideAreaDistrict();
            }
        };
        FlatApiController.getDistrictByArea((BaseActivity) getActivity(), v.getValue(), districtAreaListener);
    }

    private void setupViewIfNotEmpty() {
        etStreet.setEnabled(false);
        etBuilding.setEnabled(false);
        etStreet.setTextColor(getFragment().getResources().getColor(R.color.gray_light));
        etBuilding.setTextColor(getFragment().getResources().getColor(R.color.gray_light));
        areaAndDistrictLayout.setVisibility(View.VISIBLE);
    }

    private void setEditingBlocked() {
        boolean isHideWarnings = getFragment().getArguments().getBoolean(NewFlatFragment.ARG_HIDE_WARNING_FOR_ADD_FLATS, false);
        if (isHideWarnings) {
            warningContainer.setVisibility(View.GONE);
        } else {
            if (!flat.isEmpty() && !flat.isEnable()) {
                tvWarningEditingBlocked.setText(getFragment().getString(ru.mos.polls.R.string.error_full_editing_blocked));
                tvErrorEditingBlocked.setVisibility(View.VISIBLE);
                etStreet.setEnabled(false);
                etBuilding.setEnabled(false);
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        }
    }

    private abstract class DefaultAnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }

    private void hideAreaDistrict() {
        if (areaAndDistrictLayout.getVisibility() == View.GONE) {
            return;
        }
        Animator.AnimatorListener listener = new DefaultAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                districtFlat.setText("");
                areaFlat.setText("");
                areaAndDistrictLayout.setVisibility(View.GONE);
            }
        };
        areaAndDistrictLayout.setVisibility(View.VISIBLE);
        areaAndDistrictLayout.setAlpha(1.0f);
        areaAndDistrictLayout.animate()
                .setDuration(ANIMATION_DURATION_MILLS)
                .alpha(0.0f)
                .setListener(listener);
    }
}
