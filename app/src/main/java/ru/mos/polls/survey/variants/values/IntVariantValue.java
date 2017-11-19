package ru.mos.polls.survey.variants.values;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.polls.R;

public class IntVariantValue implements VariantValue {

    private static final int DEFAULT_MIN = 0;
    private static final int DEFAULT_MAX = 100;

    private boolean changed = false;
    public Integer value;
    private String title;
    private int min;
    private int max;

    public IntVariantValue(int min, int max) {
        super();
        this.min = min;
        this.max = max;
    }

    public IntVariantValue() {
        super();
        this.min = DEFAULT_MIN;
        this.max = DEFAULT_MAX;
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
        return value != null ? value.toString() : "";
    }

    @Override
    public void putValueInJson(String title, JSONObject jsonObject) throws JSONException {
        jsonObject.put(title, value);
    }

    @Override
    public void getValueFromJson(String title, JSONObject jsonObject) throws JSONException {
        changed = jsonObject.has(title);
        value = jsonObject.optInt(title);
    }

    @Override
    public void showEditor(Context context, final Listener listener) {

        if (!changed) {
            value = (max - min) / 2 + min;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        final View view = View.inflate(context, R.layout.survey_variant_value_int, null);
        final SeekBar seekBar = (SeekBar) view.findViewById(R.id.seek);
        final TextView minTextView = (TextView) view.findViewById(R.id.min);
        final TextView maxTextView = (TextView) view.findViewById(R.id.max);
        final TextView valueTextView = (TextView) view.findViewById(R.id.value);
        valueTextView.setText(Integer.toString(value));
        minTextView.setText(Integer.toString(min));
        maxTextView.setText(Integer.toString(max));
        seekBar.setMax(max - min);
        seekBar.setProgress(value - min);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valueTextView.setText(Integer.toString(progress + min));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changed = true;
                value = seekBar.getProgress() + min;
                listener.onEdited();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changed = false;
                value = null;
                dialog.cancel();
            }
        });
        AlertDialog d = builder.create();
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
        if (another instanceof IntVariantValue) {
            if (value != null && ((IntVariantValue) another).value != null) {
                result = value.compareTo(((IntVariantValue) another).value);
            }
        }
        return result;
    }

    public static class Factory extends VariantValue.Factory {

        @Override
        protected VariantValue onCreate(String hint, String kind, JSONObject constrainsJsonObject) {
            final VariantValue result;
            if (constrainsJsonObject != null) {
                int min = constrainsJsonObject.optInt("min");
                int max = constrainsJsonObject.optInt("max");
                result = new IntVariantValue(min, max);
            } else {
                result = new IntVariantValue();
            }
            return result;
        }

    }

}
