package ru.mos.polls.survey.variants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.polls.R;
import ru.mos.polls.survey.StatusProcessor;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.variants.values.IntVariantValue;
import ru.mos.polls.survey.variants.values.VariantValue;

/**
 * Выбор интервала
 */
public class IntervalSurveyVariant extends SurveyVariant {

    private final VariantValue startVariantValue;
    private final VariantValue endVariantValue;
    private final String text;
    private final String leftText;
    private final String rightText;

    private TextView textTextView;
    private TextView stubLeftTextView;
    private TextView stubRightTextView;
    private TextView stubBothTextView;
    private TextView valueTextView;

    public IntervalSurveyVariant(String backId, long innerId, @IntRange(from = PERCENT_NOT_SET, to = 100) int percent, int voters, String text, VariantValue startVariantValue, VariantValue endVariantValue, String lt, String rt) {
        super(backId, innerId, percent, voters);
        this.text = text;
        this.leftText = lt;
        this.rightText = rt;
        this.startVariantValue = startVariantValue;
        this.endVariantValue = endVariantValue;
    }

    @Override
    protected View onGetView(Context context, StatusProcessor statusProcessor) {
        View v = View.inflate(context, R.layout.survey_variant_interval, null);

        textTextView = (TextView) v.findViewById(R.id.text);
        stubLeftTextView = (TextView) v.findViewById(R.id.stubLeft);
        stubRightTextView = (TextView) v.findViewById(R.id.stubRight);
        stubBothTextView = (TextView) v.findViewById(R.id.stubBoth);
        valueTextView = (TextView) v.findViewById(R.id.value);

        if (TextUtils.isEmpty(text)) {
            textTextView.setVisibility(View.GONE);
        } else {
            textTextView.setVisibility(View.VISIBLE);
            textTextView.setText(text);
        }

        if (startVariantValue.isEmpty() && endVariantValue.isEmpty()) {
            stubLeftTextView.setVisibility(View.GONE);
            stubRightTextView.setVisibility(View.GONE);
            stubBothTextView.setVisibility(View.VISIBLE);
            valueTextView.setVisibility(View.GONE);
        } else if (startVariantValue.isEmpty() && !endVariantValue.isEmpty()) {
            stubLeftTextView.setVisibility(View.VISIBLE);
            stubRightTextView.setVisibility(View.GONE);
            stubBothTextView.setVisibility(View.GONE);
            valueTextView.setVisibility(View.VISIBLE);
            valueTextView.setText(rightText + " " + endVariantValue.asString());
        } else if (!startVariantValue.isEmpty() && endVariantValue.isEmpty()) {
            stubLeftTextView.setVisibility(View.GONE);
            stubRightTextView.setVisibility(View.VISIBLE);
            stubBothTextView.setVisibility(View.GONE);
            valueTextView.setVisibility(View.VISIBLE);
            valueTextView.setText(leftText + " " + startVariantValue.asString());
        } else {
            stubLeftTextView.setVisibility(View.GONE);
            stubRightTextView.setVisibility(View.GONE);
            stubBothTextView.setVisibility(View.GONE);
            valueTextView.setVisibility(View.VISIBLE);
            valueTextView.setText(leftText + " " + startVariantValue.asString() + " " + rightText + " " + endVariantValue.asString());
        }
        statusProcessor.process(stubLeftTextView);
        statusProcessor.process(stubRightTextView);
        statusProcessor.process(stubBothTextView);
        statusProcessor.process(valueTextView);
        statusProcessor.processChecked(valueTextView, this);
        return v;
    }

    @Override
    public void onClick(Activity context, Fragment fragment, boolean checked) {
        if (checked) {
            showSelectDialog(context);
        }
    }

    private void showSelectDialog(final Context context) {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);
        if (startVariantValue.isEmpty()) {
            arrayAdapter.add(TextUtils.isEmpty(leftText) ? "нажмите для выбора" : leftText);
        } else {
            arrayAdapter.add(leftText + " " + startVariantValue.asString());
        }
        if (endVariantValue.isEmpty()) {
            arrayAdapter.add(TextUtils.isEmpty(rightText) ? "нажмите для выбора" : rightText);
        } else {
            arrayAdapter.add(rightText + " " + endVariantValue.asString());
        }
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        final VariantValue.Listener listener = new VariantValue.Listener() {
            @Override
            public void onEdited() {
                showSelectDialog(context);
            }

            @Override
            public void onCancel() {
                showSelectDialog(context);
            }
        };
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        dialog.dismiss();
                        startVariantValue.showEditor(context, new StartVariantValueListener(context));
                        break;
                    case 1:
                        dialog.dismiss();
                        endVariantValue.showEditor(context, new EndVariantValueListener(context));
                        break;
                }
            }
        });
        builderSingle.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getListener().onCancel();
            }
        });
        builderSingle.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                statusProcessor.processChecked(valueTextView, IntervalSurveyVariant.this);
                getListener().onCommit();
            }
        });
        builderSingle.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                statusProcessor.processChecked(valueTextView, IntervalSurveyVariant.this);
                getListener().onCancel();
            }
        });
        AlertDialog d = builderSingle.create();
        d.setCanceledOnTouchOutside(true);
        d.show();
    }

    @Override
    public void verify() throws VerificationException {
        boolean correct = !startVariantValue.isEmpty() && !endVariantValue.isEmpty(); //TODO констрейнты
        if (!correct) {
            final String msg;
            if (startVariantValue.isEmpty() && !endVariantValue.isEmpty()) {
                msg = "Не указано начало интервала";
            } else if (!startVariantValue.isEmpty() && endVariantValue.isEmpty()) {
                msg = "Не указано окончание интервала";
            } else {
                msg = "Не указан интервал";
            }
            throw new VerificationException(msg);
        }
    }

    @Override
    protected void processAnswerJson(JSONObject jsonObject) throws JSONException {
        startVariantValue.putValueInJson("left_value", jsonObject);
        endVariantValue.putValueInJson("right_value", jsonObject);
    }

    @Override
    public void loadAnswerJson(JSONObject answerJsonObject) throws JSONException {
        startVariantValue.getValueFromJson("left_value", answerJsonObject);
        endVariantValue.getValueFromJson("right_value", answerJsonObject);
    }

    @Override
    public boolean onActivityResultOk(Intent data) {
        //не предусмотрено для данного класса
        return false;
    }

    @Override
    public boolean onActivityResultCancel(Intent data) {
        //не предусмотрено для данного класса
        return false;
    }

    @Override
    public String toString() {
        return "interval " + leftText + " " + startVariantValue.asString() + " " + rightText + " " + endVariantValue.asString() + " " + isChecked();
    }

    private abstract class VariantValueListener implements VariantValue.Listener {

        protected final Context context;

        private VariantValueListener(Context context) {
            this.context = context;
        }

        @Override
        public void onEdited() {
            if (isCorrect(startVariantValue, endVariantValue)) {
                showSelectDialog(context);
            }
        }

        protected abstract boolean isCorrect(VariantValue startVariantValue, VariantValue endVariantValue);

        @Override
        public void onCancel() {
            showSelectDialog(context);
        }
    }

    private class StartVariantValueListener extends VariantValueListener {

        private StartVariantValueListener(Context context) {
            super(context);
        }

        @Override
        protected boolean isCorrect(VariantValue startVariantValue, VariantValue endVariantValue) {
            boolean result = startVariantValue.compareTo(endVariantValue) <= 0;
            if (!result) {
                Toast.makeText(context, context.getString(R.string.start_variant_value_toast_msg), Toast.LENGTH_SHORT).show();
                startVariantValue.showEditor(context, new StartVariantValueListener(context));
            }
            return result;
        }
    }

    private class EndVariantValueListener extends VariantValueListener {

        private EndVariantValueListener(Context context) {
            super(context);
        }

        @Override
        protected boolean isCorrect(VariantValue startVariantValue, VariantValue endVariantValue) {
            boolean result = startVariantValue.compareTo(endVariantValue) <= 0;
            if (!result) {
                Toast.makeText(context, context.getString(R.string.end_variant_value_toast_msg), Toast.LENGTH_SHORT).show();
                endVariantValue.showEditor(context, new EndVariantValueListener(context));
            }
            return result;
        }
    }

    public static class Factory extends InputTypeFactory {

        @Override
        protected SurveyVariant onCreate(JSONObject jsonObject, String variantId, long innerVariantId, int percent, int voters, String inputType) {
            String text = jsonObject.optString("text");
            final String leftText = jsonObject.optString("left_text");
            final String rightText = jsonObject.optString("right_text");
            final VariantValue variantValueLeft = getVariantValue(jsonObject, inputType, leftText, "");
            final VariantValue variantValueRight = getVariantValue(jsonObject, inputType, rightText, "");
            return new IntervalSurveyVariant(variantId, innerVariantId, percent, voters, text, variantValueLeft, variantValueRight, leftText, rightText);
        }
    }

}
