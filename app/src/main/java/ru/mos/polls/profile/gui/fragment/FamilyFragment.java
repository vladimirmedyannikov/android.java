package ru.mos.polls.profile.gui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.R;
import ru.mos.polls.helpers.FragmentHelper;
import ru.mos.polls.profile.gui.activity.ChildrenBirthdaysActivity;

/**
 * Работа с семейным положением, количеством детей и списком дат рождения детей
 *
 * @since 1.9
 */
public class FamilyFragment extends AbstractProfileFragment implements UserPersonalFragment.GenderListener {
    public static AbstractProfileFragment newInstance() {
        return new FamilyFragment();
    }

    private static final int MAX_CHILDREN_COUNT = 2;
    private static final int MAX_CHILDREN_VALUE = 15;
    private static final int YEAR_SIZE = 4;

    @BindView(R.id.childContainer)
    View childContainer;
    @BindView(R.id.childrenContainer)
    View childrenContainer;
    @BindView(R.id.childrenCount)
    EditText childCount;
    @BindView(R.id.child)
    EditText child;
    @BindView(R.id.children)
    TextView children;
    @BindView(R.id.state)
    TextView state;
    @BindView(R.id.maritalStatus)
    Spinner maritalStatus;
    private AgUser.MaritalStatus.MaritalStatusAdapter maritalAdapter;
    private AgUser.MaritalStatus selectedMaritalStatus;
    private List<Long> birthdays = new ArrayList<Long>();
    private int childCountValue;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<Long> newBirthdays = ChildrenBirthdaysActivity.onResult(requestCode, resultCode, data);
        if (newBirthdays != null) {
            birthdays = newBirthdays;
            processChildBirthdaysFilled(childCountValue <= birthdays.size());
            changeListener.onChange(FAMILY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_family, null);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViews(view);
        refreshUI(new AgUser(getActivity()));
    }

    @Override
    public boolean isFilledAndChanged(AgUser old, AgUser changed) {
        boolean result = false;
        try {
            boolean isFilledFamily = !changed.isEmptyFamily();
            boolean isChangedFamily = !changed.equalsFamily(old);
            result = isFilledFamily && isChangedFamily;
        } catch (Exception ignored) {
        }
        return result;
    }

    @Override
    public void updateAgUser(AgUser agUser) {
        childCountValue = 0;
        try {
            childCountValue = Integer.parseInt(childCount.getText().toString());
        } catch (NumberFormatException ignored) {
        }
        agUser.setChildCount(childCountValue)
                .setMaritalStatus(selectedMaritalStatus);
        if (childCountValue == 1) {
            int year = 0;
            try {
                year = Integer.parseInt(child.getText().toString().trim());
            } catch (Exception ignored) {
            }
            if (Math.log10(year) >= 3) {
                agUser.clearBirthdays();
                agUser.add(String.valueOf(year));
            } else if (year == 0) {
                agUser.clearBirthdays();
            }
        }
        agUser.setChildBirthdays(birthdays);
    }

    @Override
    public void refreshUI(AgUser agUser) {
        birthdays = agUser.getChildBirthdays();
        String val = String.valueOf(agUser.getChildCount());
        childCount.setText(val);
        childCount.setSelection(val.length());
        displayMaritalStatus(agUser);

        childContainer.setVisibility(agUser.hasOneChild() ? View.VISIBLE : View.GONE);
        childrenContainer.setVisibility(agUser.hasMoreThanOneChild() ? View.VISIBLE : View.GONE);

        if (agUser.hasOneChild()) {
            child.setText(agUser.getFirstBirthday());
        }

        processChildBirthdaysFilled(agUser.isChildBirthdaysFilled());
    }

    private void processChildBirthdaysFilled(boolean isFilled) {
        state.setText(isFilled ? getActivity().getString(R.string.set) : getActivity().getString(R.string.not_set));
        int color = isFilled ? R.color.greenText : R.color.ag_red;
        state.setTextColor(getActivity().getResources().getColor(color));
    }

    @Override
    public void onChange(AgUser.Gender gender) {
        maritalAdapter.setGender(gender);
        maritalAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.childrenContainer)
    void setChildContainerListener() {
        Fragment fragment = FragmentHelper.getParentFragment(FamilyFragment.this);
        if (fragment == null) {
            fragment = FamilyFragment.this;
        }
        ChildrenBirthdaysActivity.startActivityForResult(fragment, birthdays, childCountValue);
    }

    @OnTextChanged(value = R.id.childrenCount, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void setChildCountWatcher(Editable s) {
        StringBuilder sb = new StringBuilder(s);
        int selection = childCount.getSelectionStart();

        int count = 0;
        try {
            count = Integer.parseInt(sb.toString());
        } catch (Exception ignored) {
        }
        if (count > MAX_CHILDREN_VALUE) {
            childCount.setText(String.valueOf(MAX_CHILDREN_VALUE));
            childCount.setSelection(MAX_CHILDREN_COUNT);
        }

        if (sb.length() > MAX_CHILDREN_COUNT) {
            childCount.setText(sb.subSequence(0, MAX_CHILDREN_COUNT));
            childCount.setSelection(selection >= MAX_CHILDREN_COUNT ? MAX_CHILDREN_COUNT : selection);
        }
        childContainer.setVisibility(count == 1 ? View.VISIBLE : View.GONE);
        childrenContainer.setVisibility(count > 1 ? View.VISIBLE : View.GONE);
        processChildBirthdaysFilled(count <= birthdays.size());
        childCountValue = count;
        changeListener.onChange(FAMILY_ID);
    }

    @OnTextChanged(value = R.id.child, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void setChildWathcer(Editable s) {
        StringBuilder sb = new StringBuilder(s);
        int selection = child.getSelectionStart();
        /**
         * Ввели значение превышающее 4 знака
         */
        if (sb.length() > YEAR_SIZE) {
            child.setText(sb.subSequence(0, YEAR_SIZE));
            child.setSelection(selection >= YEAR_SIZE ? YEAR_SIZE : selection);
        }
        if (sb.length() == YEAR_SIZE) {
            /**
             * Проверяем не превышает ли введенное значение
             * минимальную возможную дату рождения
             */
            Calendar c = Calendar.getInstance();
            int maxYear = c.get(Calendar.YEAR);
            int year = Integer.parseInt(sb.toString());
            if (year > maxYear) {
                year = maxYear;
                child.setText(String.valueOf(year));
                child.setSelection(YEAR_SIZE);
                return;
            }
        }
        changeListener.onChange(FAMILY_ID);
    }

    private void findViews(View v) {
    }

    private void displayMaritalStatus(final AgUser agUser) {
        maritalAdapter = (AgUser.MaritalStatus.MaritalStatusAdapter)
                AgUser.MaritalStatus.getMaritalStatusAdapter(getActivity(), agUser.getGender());
        maritalStatus.setAdapter(maritalAdapter);
        int selectedMarital = maritalAdapter.getPosition(agUser.getMaritalStatus());
        maritalStatus.setSelection(selectedMarital);
    }

    @OnItemSelected(R.id.maritalStatus)
    void martialStatus(int position) {
        selectedMaritalStatus = maritalAdapter.getItem(position);
        changeListener.onChange(FAMILY_ID);
    }
}
