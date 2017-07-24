package ru.mos.polls.newprofile.vm;

import android.animation.Animator;
import android.os.Bundle;
import android.text.Editable;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.elk.profile.flat.Value;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutNewFlatBinding;
import ru.mos.polls.newprofile.base.vm.MenuFragmentVM;
import ru.mos.polls.newprofile.ui.fragment.NewFlatFragment;
import ru.mos.polls.profile.controller.FlatApiController;
import ru.mos.polls.profile.gui.fragment.location.BuildingWatcher;
import ru.mos.polls.profile.gui.fragment.location.FlatsFragment;
import ru.mos.polls.profile.gui.fragment.location.StreetWatcher;
import ru.mos.polls.profile.model.DistrictArea;

/**
 * Created by Trunks on 23.07.2017.
 */

public class NewFlatFragmentVM extends MenuFragmentVM<NewFlatFragment, LayoutNewFlatBinding> {
    public static final int FLAT_TYPE_REGISTRATION = 12234;
    public static final int FLAT_TYPE_RESIDENCE = 11223;
    public static final int FLAT_TYPE_WORK = 11132;
    private static final int ANIMATION_DURATION_MILLS = 300;
    int flatType;
    Flat flat;
    AutoCompleteTextView etStreet;
    AutoCompleteTextView etBuilding;
    View streetNotFoundView;
    View buildingNotFoundView;
    TextView areaFlat;
    TextView districtFlat;
    @BindView(R.id.tvWarningEditingBlocked)
    TextView tvWarningEditingBlocked;
    @BindView(R.id.tvErrorEditingBlocked)
    TextView tvErrorEditingBlocked;
    LinearLayout areaAndDistrictLayout;
    private boolean isAddressSelected;

    public NewFlatFragmentVM(NewFlatFragment fragment, LayoutNewFlatBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutNewFlatBinding binding) {
        Bundle extras = getFragment().getArguments();
        flatType = extras.getInt(NewFlatFragment.ARG_FLAT_TYPE);
        flat = (Flat) extras.get(NewFlatFragment.ARG_FLAT);
        etStreet = binding.etStreet;
        etBuilding = binding.etBuilding;
        streetNotFoundView = binding.streetNotFoundContainer;
        buildingNotFoundView = binding.buildingNotFoundContainer;
        areaFlat = binding.areaFlat;
        districtFlat = binding.districtFlat;
        areaAndDistrictLayout = binding.areaAndDistrictLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        configViews();
    }

    private void configViews() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        /**
         * Если адрес проживания совпадает с адресом регистрации
         * то не отображаем адрес проживания, очищаем поля
         */
//        if (!flat.isEmpty()) {
//            setupViewIfNotEmpty();
//        }

//        etStreet.setText(flat.getStreet());
//        etBuilding.setText(flat.getBuilding());
//        districtFlat.setText(flat.getDistrict());
//        areaFlat.setText(flat.getArea());

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
//                    processEnableSave();
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
//                processEnableSave();
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
//                processEnableSave();
            }
        });
//        setDelete();
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
//        save.setVisibility(View.GONE);
//        delete.setVisibility(View.GONE);
//        changeFlat.setVisibility(View.VISIBLE);
    }

    private void setEditingBlocked() {
        boolean isHideWarnings = getActivity().getIntent().getBooleanExtra(FlatsFragment.EXTRA_HIDE_WARNING_FOR_ADD_FLATS, false);
        if (!isHideWarnings) {
            LinearLayout actionContainer = ButterKnife.findById(getFragment().getView(), R.id.actionContainer);
            if (flat != null && !flat.isEmpty() && !flat.isEnable()) {
                tvWarningEditingBlocked.setText(getFragment().getString(ru.mos.polls.R.string.error_full_editing_blocked));
                tvErrorEditingBlocked.setVisibility(View.VISIBLE);
                etStreet.setEnabled(false);
                etBuilding.setEnabled(false);
                actionContainer.setVisibility(View.GONE);
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        } else {
            getActivity().findViewById(R.id.warningContainer).setVisibility(View.GONE);
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
