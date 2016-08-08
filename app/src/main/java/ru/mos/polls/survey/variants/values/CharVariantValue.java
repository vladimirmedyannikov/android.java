package ru.mos.polls.survey.variants.values;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.polls.R;
import ru.mos.polls.helpers.TextHelper;

public class CharVariantValue implements VariantValue {

    private String value;
    private String title;
    private boolean constraint;
    private int min;
    private int max;
    private String hint;
    private int charsCount;
    private TextView charsCountTxt;
    private EditText inputEditText;
    private Context mContext;

    public CharVariantValue(String hint) {
        super();
        constraint = false;
        this.hint = hint;
    }

    public CharVariantValue(String hint, int min, int max) {
        super();
        constraint = true;
        this.hint = hint;
        this.min = min;
        this.max = max;
    }

    @Override
    public void setTitle(String s) {
        title = s;
    }

    @Override
    public boolean isEmpty() {
        return TextUtils.isEmpty(value);
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public void putValueInJson(String title, JSONObject jsonObject) throws JSONException {
        jsonObject.put(title, value);
    }

    @Override
    public void getValueFromJson(String title, JSONObject jsonObject) throws JSONException {
        value = jsonObject.optString(title);
    }

    @Override
    public void showEditor(final Context context, final Listener listener) {
        mContext = context;
        doShowEditor(context, listener, value);
    }

    private void doShowEditor(final Context context, final Listener listener, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        View innerView = View.inflate(context, R.layout.survey_variant_value_char, null);
        inputEditText = (EditText) innerView.findViewById(R.id.value);
        charsCountTxt = (TextView) innerView.findViewById(R.id.charsCountTxt);
        inputEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
        initCharsCount();
        charsCountTxt.setText(TextHelper.Constraints.getCharsCount(context, charsCount, max));
        inputEditText.setHint(hint);
        final TextView hintTextView = (TextView) innerView.findViewById(R.id.hint);
        inputEditText.addTextChangedListener(textWatcher);
        if (constraint) {
            final String hint;
            if (min == max) {
                hint = TextHelper.Constraints.getHintOnlyOne(context, min); //"Строго " + min + " символов.";
            } else {
                hint = TextHelper.Constraints.getHintMinMax(context, min, max);//"От " + min + " до " + max + " символов";
            }
        } else {
            hintTextView.setVisibility(View.GONE);
        }
        inputEditText.setText(text);
        builder.setView(innerView);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String val = inputEditText.getText().toString();
                if (constraint) {
                    if (min <= val.length() && val.length() <= max) {
                        commit(val);
                    } else {
                        if (min == max) {
                            String hint = TextHelper.Constraints.getHintTextOnlyOne(context, min, val.length()); //"Длина текста должна быть " + min + " символов (cейчас " + val.length() + ")"
                            Toast.makeText(context, hint, Toast.LENGTH_SHORT).show();
                        } else {
                            String hint = TextHelper.Constraints.getHintTextMinMax(context, min, max, val.length()); //"Длина текста должна быть от " + min + " до " + max + " символов (cейчас " + val.length() + ")"
                            Toast.makeText(context, hint, Toast.LENGTH_SHORT).show();
                        }
                        doShowEditor(context, listener, inputEditText.getText().toString());
                    }
                } else {
                    commit(val);
                }
            }

            private void commit(String val) {
                value = val;
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

    /**
     * инициализация кол-ва букв в тексте
     */

    private void initCharsCount() {
        if (value != null) {
            charsCount = value.length();

            if (charsCount > max) charsCountTxt.setTextColor(Color.RED);
        }
    }

    private String setCharsCountTxt(int value) {
        return value + "/" + max;
    }

    /**
     * счетчик букв в тексте
     */
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //nothing here
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            charsCountTxt.setText(TextHelper.Constraints.getCharsCount(mContext, max - s.length(), max));
            if (s.length() > max) {
                charsCountTxt.setTextColor(Color.RED);
            } else if (s.length() <= max) {
                charsCountTxt.setTextColor(Color.GRAY);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // here too
        }
    };

    @Override
    public int compareTo(VariantValue another) {
        int result = 0;
        if (another instanceof DateVariantValue) {
            String anotherValue = ((CharVariantValue) another).value;
            if (anotherValue != null && value != null) {
                result = value.compareTo(anotherValue);
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
                result = new CharVariantValue(hint, min, max);
            } else if ("input".equalsIgnoreCase(kind)) {
                result = new CharVariantValue(hint, 0, 500);
            } else {
                result = new CharVariantValue(hint);
            }
            return result;
        }
    }

}
