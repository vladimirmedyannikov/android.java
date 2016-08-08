package ru.mos.polls.survey.variants.values;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.mos.polls.R;

public class DateVariantValue implements VariantValue {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy");
    private static final SimpleDateFormat JSON_SDF = new SimpleDateFormat("dd.MM.yyyy");

    private String title;
    private Date value;
    private boolean constraint;
    private Date min;
    private Date max;

    public DateVariantValue(Date min, Date max) {
        super();
        constraint = true;
        this.min = min;
        this.max = max;
    }

    public DateVariantValue() {
        super();
        constraint = false;
    }

    private int callTime = 0;

    @Override
    public void setTitle(String s) {
        title = s;
    }

    @Override
    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public String asString() {
        return value == null ? "null" : SDF.format(value);
    }

    @Override
    public void showEditor(final Context context, final Listener listener) {
        callTime = 0;
        final Calendar calendar = Calendar.getInstance();
        if (value != null) {
            calendar.setTime(value);
        }

        final AtomicBoolean done = new AtomicBoolean(false);
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (callTime > 0) {
                    return;
                }
                callTime++;
                done.set(true);
                calendar.set(year, monthOfYear, dayOfMonth);
                Date val = calendar.getTime();
                if (constraint) {
                    if ((min == null && val.compareTo(max) <= 0)                                      //задана только верхняя граница
                            || (min != null && val.compareTo(min) >= 0 && val.compareTo(max) <= 0)) { //заданы обе границы
                        commit(val);
                    } else {
                        if (min == null) {
                            Toast.makeText(context, String.format(context.getString(R.string.date_variant_value_toast_msg), SDF.format(max), SDF.format(val)), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, String.format(context.getString(R.string.date_variant_value_toast_interval_msg), SDF.format(min), SDF.format(max), SDF.format(val)), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    commit(val);
                }
            }

            private void commit(Date val) {
                value = val;
                listener.onEdited();
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(context, onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.setTitle(title);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!done.get()) {
                    listener.onCancel();
                }
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public void putValueInJson(String title, JSONObject jsonObject) throws JSONException {
        if (value != null) {
            String s = JSON_SDF.format(value);
            jsonObject.put(title, s);
        }
    }

    @Override
    public void getValueFromJson(String title, JSONObject jsonObject) throws JSONException {
        String val = jsonObject.getString(title);
        try {
            value = JSON_SDF.parse(val);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(VariantValue another) {
        int result = 0;
        if (another instanceof DateVariantValue) {
            Date thisValue = value;
            Date anotherValue = ((DateVariantValue) another).value;
            if (anotherValue != null && thisValue != null) {
                result = thisValue.compareTo(anotherValue);
            }
        }
        return result;
    }

    public static class Factory extends VariantValue.Factory {

        private static Date parseDate(String s) {
            try {
                Date result = SDF.parse(s);
                return result;
            } catch (ParseException e) {
                return null;
            }
        }

        @Override
        protected VariantValue onCreate(String hint, String kind, JSONObject constrainsJsonObject) {
            final VariantValue result;
            if (constrainsJsonObject != null) {
                String minStr = constrainsJsonObject.optString("min");
                String maxStr = constrainsJsonObject.optString("max");
                Date min = parseDate(minStr);
                Date max = parseDate(maxStr);
                result = new DateVariantValue(min, max);
            } else {
                result = new DateVariantValue();
            }
            return result;
        }
    }

}