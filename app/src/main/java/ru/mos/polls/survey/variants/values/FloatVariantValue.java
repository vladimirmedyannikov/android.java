package ru.mos.polls.survey.variants.values;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.polls.R;

public class FloatVariantValue implements VariantValue {

    private boolean changed = false;
    private double value;
    private String title;
    private boolean constraint;
    private double min;
    private double max;

    public FloatVariantValue() {
        super();
        constraint = false;
    }

    public FloatVariantValue(double min, double max) {
        super();
        constraint = true;
        this.min = min;
        this.max = max;
    }

    @Override
    public void setTitle(String s) {
        title = s;
    }

    @Override
    public boolean isEmpty() {
        return !changed;
    }

    @Override
    public String asString() {
        return Double.toString(value);
    }

    @Override
    public void putValueInJson(String title, JSONObject jsonObject) throws JSONException {
        jsonObject.put(title, value);
    }

    @Override
    public void getValueFromJson(String title, JSONObject jsonObject) throws JSONException {
        changed = jsonObject.has(title);
        value = jsonObject.optDouble(title);
    }

    @Override
    public void showEditor(final Context context, final Listener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        final EditText inputEditText = new EditText(context);
        inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (changed) {
            String stringValue = Double.toString(value);
            inputEditText.setText(stringValue);
            inputEditText.setSelection(stringValue.length());
        }
        builder.setView(inputEditText);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(inputEditText.getText().toString())) {
                    listener.onEdited();
                    return;
                }
                double val = Double.parseDouble(inputEditText.getText().toString());
                if (constraint) {
                    if (min <= val && val <= max) {
                        commit(val);
                    } else {
                        Toast.makeText(context, String.format(context.getString(R.string.float_variant_value_toast_msg), min, max), Toast.LENGTH_SHORT).show();
                        showEditor(context, listener);
                    }
                } else {
                    commit(val);
                }
            }

            private void commit(double val) {
                value = val;
                changed = true;
                listener.onEdited();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog d = builder.create();
        inputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        d.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                listener.onCancel();
            }
        });
        d.setCanceledOnTouchOutside(true);
        d.show();
    }

    @Override
    public int compareTo(VariantValue another) {
        int result = 0;
        if (another instanceof DateVariantValue) {
            double anotherValue = ((FloatVariantValue) another).value;
            result = Double.valueOf(value).compareTo(Double.valueOf(anotherValue));
        }
        return result;
    }

    public static class Factory extends VariantValue.Factory {

        @Override
        protected VariantValue onCreate(String hint, String kind, JSONObject constrainsJsonObject) {
            final VariantValue result;
            if (constrainsJsonObject != null) {
                double min = constrainsJsonObject.optDouble("min");
                double max = constrainsJsonObject.optDouble("max");
                result = new FloatVariantValue(min, max);
            } else {
                result = new FloatVariantValue();
            }
            return result;
        }

    }
}
