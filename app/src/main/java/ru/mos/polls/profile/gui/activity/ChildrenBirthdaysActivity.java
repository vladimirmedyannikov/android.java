package ru.mos.polls.profile.gui.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.polls.AbstractActivity;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.helpers.TitleHelper;

/**
 * Экран для отображения/заполнения дат рождения детей
 *
 * @since 1.9
 */
public class ChildrenBirthdaysActivity extends ToolbarAbstractActivity {
    public static final int REQUEST_CHILD = 10;
    public static final String EXTRA_CHILD_COUNT = "extra_child_count";
    public static final String EXTRA_CHILD = "extra_child";
    private static final int YEAR_SIZE = 4;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

    public static void startActivityForResult(Fragment fragment, List<Long> child, int childCount) {
        Intent intent = new Intent(fragment.getActivity(), ChildrenBirthdaysActivity.class);
        intent.putExtra(EXTRA_CHILD, (Serializable) child);
        intent.putExtra(EXTRA_CHILD_COUNT, childCount);
        fragment.startActivityForResult(intent, REQUEST_CHILD);
    }

    public static List<Long> onResult(int requestCode, int resultCode, Intent intent) {
        List<Long> result = null;
        if (resultCode == RESULT_OK && requestCode == REQUEST_CHILD && intent != null && intent.getExtras() != null) {
            result = (List<Long>) intent.getExtras().getSerializable(EXTRA_CHILD);
        }
        return result;
    }

    @BindView(R.id.container)
    LinearLayout container;

    private List<Long> child;
    private String[] hints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_birthdays);
        TitleHelper.setTitle(this, getString(R.string.title_child));
        ButterKnife.bind(this);
        fillViews();
        AbstractActivity.hideSoftInput(this, container);
    }

    @Override
    protected void findViews() {
        processSave();
    }

    private void displayBirthdays() {
        container.removeAllViews();
        for (int i = 0; i < child.size(); ++i) {
            View childView = getView(child.get(i), i);
            container.addView(childView);
        }
    }

    private void processSave() {
        Button save = ButterKnife.findById(this, R.id.save);
        save.setEnabled(true);
        save.setText(R.string.ready);
    }

    @OnClick(R.id.save)
    void done() {
        setResult();
    }

    @Override
    public void onBackPressed() {
        setResult();
    }

    private List<Long> prepareBirthdays() {
        List<Long> result = new ArrayList<Long>();
        for (long birthday : child) {
            if (birthday != 0) {
                result.add(Long.valueOf(birthday));
            }
        }
        return result;
    }

    private void fillViews() {
        init();
        displayBirthdays();
    }

    private void init() {
        hints = getResources().getStringArray(R.array.child_birthdays_hint);
        child = (List<Long>) getIntent().getSerializableExtra(EXTRA_CHILD);
        if (child == null) {
            child = new ArrayList<Long>();
        }
        int childCount = getIntent().getIntExtra(EXTRA_CHILD_COUNT, 0);
        int childSize = child.size();
        if (childCount >= childSize) {
            childSize = childCount - childSize;
            for (int i = 0; i < childSize; ++i) {
                child.add(Long.valueOf(0));
            }
        } else {
            for (int i = childSize - 1; i > childCount - 1; --i) {
                child.remove(i);
            }
        }
    }

    private void setResult() {
        AbstractActivity.hideSoftInput(this, container);
        child = prepareBirthdays();
        Intent result = new Intent();
        result.putExtra(EXTRA_CHILD, (Serializable) child);
        setResult(RESULT_OK, result);
        finish();
    }

    public View getView(Long birthday, int position) {
        View v = getLayoutInflater().inflate(R.layout.layout_item_child_birthday, null);
        displayBirthday(v, birthday, position);
        return v;
    }

    /**
     * Отображения поля ввода для даты рождения ребенка
     *
     * @param v
     * @param birthday
     * @param position
     */
    private void displayBirthday(View v, long birthday, final int position) {
        final EditText item = ButterKnife.findById(v, R.id.child);
//                (EditText) v.findViewById(R.id.child);
        item.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                StringBuilder sb = new StringBuilder(s);
                int selection = item.getSelectionStart();
                /**
                 * Если вводим дату рождения более 4 знаков, то уменьшаем до 4-х знаков
                 */
                if (sb.length() > YEAR_SIZE) {
                    item.setText(sb.subSequence(0, YEAR_SIZE));
                    item.setSelection(selection >= YEAR_SIZE ? YEAR_SIZE : selection);
                }
                if (sb.length() == YEAR_SIZE) {
                    /**
                     * Ввели год целиком
                     */
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                    long year = 0;
                    try {
                        year = sdf.parse(sb.toString()).getTime();
                        /**
                         * Ограничение на минимальную дату рождения
                         * согласно требованиям минимальная дата рождения не может быть
                         * меньше текущий год минус 10 лет
                         */
                        Calendar c = Calendar.getInstance();
                        String yearString = String.valueOf(c.get(Calendar.YEAR));
                        long maxYear = sdf.parse(yearString).getTime();
                        if (year > maxYear) {
                            year = maxYear;
                            item.setText(sdf.format(year));
                            item.setSelection(YEAR_SIZE);
                            return;
                        }
                    } catch (ParseException ignored) {
                    }
                    /**
                     * Сохраняем в коллекции ввдеенный год рождения
                     */
                    if (year != 0) {
                        child.set(position, Long.valueOf(year));
                    }
                } else if (sb.length() == 0 || sb.length() < YEAR_SIZE) {
                    /**
                     * Если год рождения не указан до конца или вообще не указан
                     */
                    child.set(position, Long.valueOf(0));
                }
            }
        });
        if (birthday != 0) {
            item.setText(sdf.format(birthday));
        }
        if (position < hints.length) {
            item.setHint(hints[position]);
        }
    }

    /**
     * По тз предполагается ввод даты не через диалог с барабаном ввода,
     * а с клавиатуры с возможными ограничениями {@link #displayBirthdays()}
     * возможно, со временем будет изменен вариант ввода даты
     */
    @Deprecated
    public static class DatePickerFragment extends DialogFragment {
        public static final String YEAR = "year";
        public static final String MONTH = "month";
        public static final String DAY = "day";

        private DateSetListener dateSetListener;

        private DatePickerDialog.OnDateSetListener dataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calender = Calendar.getInstance();
                calender.set(Calendar.YEAR, year);
                calender.set(Calendar.MONTH, monthOfYear);
                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                long date = calender.getTimeInMillis();
                if (dateSetListener != null) {
                    dateSetListener.onSet(date);
                }
            }
        };

        public void setDateSetListener(DateSetListener dateListener) {
            this.dataSetListener = dataSetListener;
        }

        private int year, month, day;

        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            year = args.getInt(YEAR);
            month = args.getInt(MONTH);
            day = args.getInt(DAY);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getActivity(), dataSetListener, year, month, day);
        }

        public interface DateSetListener {
            void onSet(long dateInMills);
        }
    }
}
