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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.elk.profile.flat.Value;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutNewFlatBinding;
import ru.mos.polls.newprofile.base.vm.MenuFragmentVM;
import ru.mos.polls.newprofile.service.ProfileSet;
import ru.mos.polls.newprofile.service.model.FlatsEntity;
import ru.mos.polls.newprofile.ui.fragment.NewFlatFragment;
import ru.mos.polls.profile.controller.FlatApiController;
import ru.mos.polls.profile.gui.fragment.location.BuildingWatcher;
import ru.mos.polls.profile.gui.fragment.location.StreetWatcher;
import ru.mos.polls.profile.model.DistrictArea;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;

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
    View warningContainer;
    TextView areaFlat;
    TextView districtFlat;
    TextView tvWarningEditingBlocked;
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
        tvWarningEditingBlocked = binding.layoutFlatWarningBlock.tvWarningEditingBlocked;
        tvErrorEditingBlocked = binding.layoutFlatWarningBlock.tvErrorEditingBlocked;
        warningContainer = binding.layoutFlatWarningBlock.warningContainer;
    }

    @Override
    public void onResume() {
        super.onResume();
        configViews();
    }

    @Override
    public void onCreateOptionsMenu() {
        System.out.println("flat is enable = " + flat.isEnable());
        processMenuIcon();
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
                confirmaAction();
                break;
        }
    }

    public void confirmaAction() {
        sendFlat(new ProfileSet.Request(new FlatsEntity(flat, !flat.isEmpty())));// тоже почему то при первом надо только building_id, а при обновлении flat_id
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
                Flat flat = null;
                if (result.getFlats().getRegistration() != null) {
                    flat = result.getFlats().getRegistration();
                    flat.setType(Flat.Type.REGISTRATION);
                }
                if (result.getFlats().getResidence() != null) {
                    flat = result.getFlats().getResidence();
                    flat.setType(Flat.Type.RESIDENCE);
                }
                if (result.getFlats().getWork() != null) {
                    flat = result.getFlats().getWork();
                    flat.setType(Flat.Type.WORK);
                }
                if (flat != null) {
                    flat.setEnable(!flat.isEnable());    //костыля потому что в FLAT result.enable = !flatJson.optBoolean("editing_blocked"); а GSON парсит в   @SerializedName("editing_blocked")

                    System.out.println("flat = " + flat.isWork());
                    System.out.println("flat = " + flat.isResidence());
                    System.out.println("flat = " + flat.isRegistration());
                    System.out.println("flat = " + flat.getArea());
                    System.out.println("flat = " + flat.getDistrict());
                    System.out.println("flat = " + flat.getBuilding());
                    System.out.println("flat = " + flat.getAreaId());
                    System.out.println("flat = " + flat.getViewTitle(getActivity().getBaseContext()));
                    System.out.println("flat = " + flat.isEnable());
                    flat.save(getActivity());
                }
                showDeleteMenuIcon();
//                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_BAGES_FROM_SERVER));
//                SharedPreferences prefs = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
//                prefs.edit().putLong(TIME_SYNQ, System.currentTimeMillis() + INTERVAL_SYNQ).apply();
            }
        };
        Observable<ProfileSet.Response> responseObservabl =
                AGApplication.api.setProfile(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservabl.subscribeWith(handler));
    }

    private void configViews() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        /**
         * Если адрес проживания совпадает с адресом регистрации
         * то не отображаем адрес проживания, очищаем поля
         */
        if (!flat.isEmpty()) {
            setupViewIfNotEmpty();
        }

        etStreet.setText(flat.getStreet());
        etBuilding.setText(flat.getBuilding());
        districtFlat.setText(flat.getDistrict());
        areaFlat.setText(flat.getArea());

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
