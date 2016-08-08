package ru.mos.polls.profile.gui.fragment.location;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley2.VolleyError;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.elk.ElkTextUtils;
import ru.mos.elk.actionmode.ActionDescription;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.elk.profile.flat.Value;
import ru.mos.polls.AbstractActivity;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.profile.controller.FlatApiController;
import ru.mos.polls.profile.model.DistrictArea;


public class NewAddressActivity extends ToolbarAbstractActivity {
    private static final int ANIMATION_DURATION_MILLS = 300;
    public static final String EXTRA_FLAT = "extra_flat";
    public static final int REQUEST_FLAT = 1001;

    private static final int MENU_ITEM_DELETE = 2;
    private static final int MENU_ITEM_SAVE = 1;

    public static void startActivity(Fragment fragment, Flat flat) {
        startActivity(fragment, flat, false);
    }

    public static void startActivity(Fragment fragment, Flat flat, boolean isHideWarnings) {
        Intent start = new Intent(fragment.getActivity(), NewAddressActivity.class);
        start.putExtra(EXTRA_FLAT, flat);
        start.putExtra(FlatsFragment.EXTRA_HIDE_WARNING_FOR_ADD_FLATS, isHideWarnings);
        fragment.startActivityForResult(start, REQUEST_FLAT);
    }

    @BindView(R.id.etStreet)
    AutoCompleteTextView etStreet;
    @BindView(R.id.etBuilding)
    AutoCompleteTextView etBuilding;
    @BindView(R.id.street_not_found_container)
    View streetNotFoundView;
    @BindView(R.id.building_not_found_container)
    View buildingNotFoundView;
    @BindView(R.id.areaFlat)
    TextView areaFlat;
    @BindView(R.id.districtFlat)
    TextView districtFlat;
    @BindView(R.id.save)
    View save;
    @BindView(R.id.delete)
    View delete;
    @BindView(R.id.tvWarningEditingBlocked)
    TextView tvWarningEditingBlocked;
    @BindView(R.id.tvErrorEditingBlocked)
    TextView tvErrorEditingBlocked;
    @BindView(R.id.areaAndDistrictLayout)
    LinearLayout areaAndDistrictLayout;
    @BindView(R.id.changeFlat)
    Button changeFlat;
    private Flat flat;

    public static Flat onResult(int requestCode, int resultCode, Intent data) {
        Flat result = null;
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_FLAT) {
            result = (Flat) data.getSerializableExtra(EXTRA_FLAT);
            result.setEnable(true);
        }
        return result;
    }

    private boolean isAddressSelected;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        addSaveMenuItem(menu);
        addDeleteMenuItem(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ru.mos.polls.R.layout.activity_newflat);
        ButterKnife.bind(this);
        getFlat();
        configViews();
        int title = R.string.title_add_address;
        if (flat != null) {
            if (flat.isRegistration()) {
                title = R.string.title_registration;
            } else if (flat.isResidence()) {
                title = R.string.title_residance;
            } else if (flat.isWork()) {
                title = R.string.title_work;
            }
        }
        TitleHelper.setTitle(this, title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Flat newFlat = CustomFlatActivity.onResult(requestCode, resultCode, data);
        if (newFlat != null) {
            Intent result = new Intent();
            result.putExtra(EXTRA_FLAT, newFlat);
            setResult(RESULT_OK, result);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AbstractActivity.hideSoftInput(this, etStreet);
    }

    private void getFlat() {
        flat = (Flat) getIntent().getSerializableExtra(EXTRA_FLAT);
        if (flat == null) {
            finish();
        }
    }

    /**
     * Ранее кнопка сохранения была в экшен баре
     * Пока оставим данный метод
     *
     * @param menu
     */
    @Deprecated
    private void addSaveMenuItem(Menu menu) {
        ActionDescription save = new ActionDescription() {
            @Override
            public void act(int[] selectedItems) {
                AbstractActivity.hideSoftInput(NewAddressActivity.this, etStreet);
                if (!ElkTextUtils.isEmpty(flat.getBuildingId())) {
                    Intent result = new Intent();
                    result.putExtra(EXTRA_FLAT, flat);
                    setResult(RESULT_OK, result);
                    finish();
                } else {
                    Toast.makeText(NewAddressActivity.this,
                            ru.mos.elk.R.string.elk_not_filled_flat,
                            Toast.LENGTH_SHORT).show();
                }

            }
        };
        save.setTitle(ru.mos.elk.R.string.elk_menu_save);
        save.setIconId(ru.mos.elk.R.drawable.elk_save);
        save.setId(MENU_ITEM_SAVE);
        save.setOrder(MENU_ITEM_SAVE);
//        addActionItem(menu, save);
    }

    /**
     * Ранее кнопка сохранения была в экшен баре
     * Пока оставим данный метод
     *
     * @param menu
     */
    @Deprecated
    private void addDeleteMenuItem(Menu menu) {
        if (!flat.isEmpty()) {
            ActionDescription delete = new ActionDescription() {
                @Override
                public void act(int[] selectedItems) {
                    AbstractActivity.hideSoftInput(NewAddressActivity.this, etStreet);
                    if (flat.isRegistration()) {
                        Flat.deleteResidence(NewAddressActivity.this);
                    }
                    flat.delete(NewAddressActivity.this);
                    setResult(RESULT_CANCELED);
                    finish();
                }
            };
            delete.setTitle(ru.mos.elk.R.string.elk_menu_delete);
            delete.setIconId(ru.mos.elk.R.drawable.elk_delete);
            delete.setId(MENU_ITEM_DELETE);
            delete.setOrder(MENU_ITEM_DELETE);
        }
    }

    private void configViews() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

        etStreet.addTextChangedListener(new StreetWatcher(this, etStreet, findViewById(ru.mos.elk.R.id.pbStreet), new StreetWatcher.Listener() {
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
                    processEnableSave();
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
        etBuilding.addTextChangedListener(new BuildingWatcher(this, etStreet, etBuilding, findViewById(ru.mos.elk.R.id.pbBuilding), new BuildingWatcher.Listener() {
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
                processEnableSave();
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
                processEnableSave();
            }
        });
        setDelete();
        setEditingBlocked();
    }

    @OnClick({R.id.building_not_found_container, R.id.street_not_found_container})
    void customFlatListener() {
        boolean isHideWarnings = getIntent().getBooleanExtra(FlatsFragment.EXTRA_HIDE_WARNING_FOR_ADD_FLATS, false);
        String street = etStreet.getText().toString();
        String house = etBuilding.getText().toString();
        CustomFlatActivity.startActivity(NewAddressActivity.this, street, house, flat, isHideWarnings);
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
        FlatApiController.getDistrictByArea(NewAddressActivity.this, v.getValue(), districtAreaListener);
    }

    private String clearText(String text) {
        return text.replace("?", "");
    }

    private void setupViewIfNotEmpty() {
        etStreet.setEnabled(false);
        etBuilding.setEnabled(false);
        etStreet.setTextColor(getResources().getColor(R.color.gray_light));
        etBuilding.setTextColor(getResources().getColor(R.color.gray_light));
        areaAndDistrictLayout.setVisibility(View.VISIBLE);
        save.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        changeFlat.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.save)
    void setSaveListener() {
        AbstractActivity.hideSoftInput(NewAddressActivity.this, etStreet);
        if (!ElkTextUtils.isEmpty(flat.getBuildingId())) {
            flat.setEnable(true);
            Intent result = new Intent();
            result.putExtra(EXTRA_FLAT, flat);
            setResult(RESULT_OK, result);
            finish();
        } else {
            Toast.makeText(NewAddressActivity.this,
                    ru.mos.elk.R.string.elk_not_filled_flat,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.delete)
    void setDeleteListener() {
        AbstractActivity.hideSoftInput(NewAddressActivity.this, etStreet);
        if (flat.isRegistration()) {
            Flat.deleteResidence(NewAddressActivity.this);
        }
        flat.delete(NewAddressActivity.this);
        setResult(RESULT_CANCELED);
        finish();
    }

    private void setDelete() {
        boolean enable = flat != null && !flat.isEmpty();
        delete.setEnabled(enable);
    }

    private void setEditingBlocked() {
        boolean isHideWarnings = getIntent().getBooleanExtra(FlatsFragment.EXTRA_HIDE_WARNING_FOR_ADD_FLATS, false);
        if (!isHideWarnings) {
            LinearLayout actionContainer = ButterKnife.findById(this, R.id.actionContainer);
            if (flat != null && !flat.isEmpty() && !flat.isEnable()) {
                tvWarningEditingBlocked.setText(getString(ru.mos.polls.R.string.error_full_editing_blocked));
                tvErrorEditingBlocked.setVisibility(View.VISIBLE);
                etStreet.setEnabled(false);
                etBuilding.setEnabled(false);
                actionContainer.setVisibility(View.GONE);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        } else {
            findViewById(R.id.warningContainer).setVisibility(View.GONE);
        }
    }

    private void processEnableSave() {
        String iniAddress = flat.getStreet();
        if (ElkTextUtils.isEmpty(iniAddress)) {
            iniAddress = "";
        }
        boolean enable = etBuilding.getTag() != null && !iniAddress.equals(etStreet.getText().toString() + etBuilding.getText().toString());
        save.setEnabled(enable);
    }

    @OnClick(R.id.changeFlat)
    void changeFlat() {
        if (flat.isRegistration()) {
            Flat.deleteResidence(NewAddressActivity.this);
        }
        flat.delete(NewAddressActivity.this);
        delete.setVisibility(View.VISIBLE);
        save.setVisibility(View.VISIBLE);
        changeFlat.setVisibility(View.GONE);
        etBuilding.getText().clear();
        etBuilding.setEnabled(true);
        etBuilding.setTextColor(getResources().getColor(R.color.editTextColor));
        etStreet.getText().clear();
        etStreet.setEnabled(true);
        etStreet.setTextColor(getResources().getColor(R.color.editTextColor));
        hideAreaDistrict();
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
}
