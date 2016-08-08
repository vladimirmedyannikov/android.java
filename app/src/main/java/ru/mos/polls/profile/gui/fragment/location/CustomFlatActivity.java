package ru.mos.polls.profile.gui.fragment.location;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley2.VolleyError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.AbstractActivity;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.profile.controller.FlatApiController;
import ru.mos.polls.profile.model.Reference;

/**
 * Сохранение кастомного адреса, которого пользователь не нашел в бд
 */
public class CustomFlatActivity extends ToolbarAbstractActivity {
    public static final String EXTRA_FLAT = "extra_flat";
    public static final String EXTRA_STREET = "extra_street";
    public static final String EXTRA_HOUSE = "extra_house";
    public static final String USER_ID = "userid";
    public static final int REQUEST_FLAT = 101;

    private List<String> districtList;
    private List<Reference> districtReference;
    private List<Reference> areaReference;

    public static void startActivity(Activity context, String street, String house, Flat flat, boolean isHideWarnings) {
        Intent start = new Intent(context, CustomFlatActivity.class);
        start.putExtra(EXTRA_FLAT, flat);
        start.putExtra(EXTRA_STREET, street);
        start.putExtra(EXTRA_HOUSE, house);
        start.putExtra(FlatsFragment.EXTRA_HIDE_WARNING_FOR_ADD_FLATS, isHideWarnings);
        context.startActivityForResult(start, REQUEST_FLAT);
    }

    @BindView(R.id.districtSpinner)
    Spinner districtSpinner;
    @BindView(R.id.areaSpinner)
    Spinner areaSpinner;
    @BindView(R.id.street)
    EditText street;
    @BindView(R.id.building)
    EditText building;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.areaLabel)
    TextView areaLabel;
    private Flat flat;

    public static Flat onResult(int requestCode, int resultCode, Intent data) {
        Flat result = null;
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_FLAT) {
            result = (Flat) data.getSerializableExtra(EXTRA_FLAT);
        }
        return result;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_address);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configViews();
        ButterKnife.bind(this);
        flat = (Flat) getIntent().getSerializableExtra(EXTRA_FLAT);
        street.setText(getIntent().getStringExtra(EXTRA_STREET));
        building.setText(getIntent().getStringExtra(EXTRA_HOUSE));
    }

    @OnTextChanged(value = {R.id.street, R.id.building},
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void setTextWatcher() {
        checkForEnabled();
    }

    private void configViews() {
        TitleHelper.setTitle(this, R.string.title_add_address);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        requestDistrictList();

        boolean isHideWarnings = getIntent().getBooleanExtra(FlatsFragment.EXTRA_HIDE_WARNING_FOR_ADD_FLATS, false);
        ButterKnife.findById(this, R.id.warningContainer).setVisibility(isHideWarnings ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.save)
    public void save() {
        addFlat();
    }

    @OnItemSelected(R.id.areaSpinner)
    public void setListenerToAreaSpinner() {
        checkForEnabled();
    }

    @OnItemSelected(R.id.districtSpinner)
    public void setListenerToDistrictSpinner(int position) {
        if (position != 0) {
            areaSpinner.setVisibility(View.VISIBLE);
            areaSpinner.setClickable(true);
            areaLabel.setVisibility(View.VISIBLE);
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
            save.setEnabled(false);
            areaLabel.setVisibility(View.GONE);
            areaSpinner.setVisibility(View.GONE);
        }
    }

    private void checkForEnabled() {
        save.setEnabled(street.getText().toString().trim().length() > 0
                && building.getText().toString().trim().length() > 0
                && areaSpinner.getSelectedItemPosition() != 0
                && districtSpinner.getSelectedItemPosition() != 0);
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
        FlatApiController.getReference(CustomFlatActivity.this, districtListener, value);
    }

    private void refreshAreas() {
        sortReference(areaReference);
        setAreaSpinnerAdapter();
    }

    private void setAreaSpinnerAdapter() {
        ArrayAdapter spinnerArrayAdapter = Reference.getAdapter(this, areaReference);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(spinnerArrayAdapter);
    }

    private void setDistrictSpinnerAdapter() {
        if (districtList != null) {
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districtList);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            districtSpinner.setAdapter(spinnerArrayAdapter);
        }
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
        FlatApiController.getReference(CustomFlatActivity.this, districtListener, null);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AbstractActivity.hideSoftInput(this, street);
    }

    private void addFlat() {
        AbstractActivity.hideSoftInput(this, street);
        Reference areaReference = (Reference) areaSpinner.getSelectedItem();
        flat.setDistrict(districtSpinner.getSelectedItem().toString().trim())
                .setArea(areaReference.getLabel())
                .setStreet(street.getText().toString().trim())
                .setBuilding(building.getText().toString().trim())
                .setAreaId(areaReference.getValue())
                .setBuildingId(USER_ID);
        Intent result = new Intent();
        result.putExtra(EXTRA_FLAT, flat);
        setResult(RESULT_OK, result);
        finish();
    }
}
