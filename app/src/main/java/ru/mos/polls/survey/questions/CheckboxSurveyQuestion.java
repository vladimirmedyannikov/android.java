package ru.mos.polls.survey.questions;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.List;

import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.survey.StatusProcessor;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.variants.SurveyVariant;

/**
 * Вопрос, допускающий выбор нескольких ответа из конечного множества.
 */
public class CheckboxSurveyQuestion extends ListViewSurveyQuestion {

    public static final int INT_NOT_SET = -1;

    /**
     * Минимальное количество отмеченных ответов
     */
    private int minAllowed;

    /**
     * Максимальное количество отмеченных ответов
     */
    private int maxAllowed;

    public CheckboxSurveyQuestion(long id, Text question, String hint, List<SurveyVariant> variantsList, int minAllowed, int maxAllowed, long filterId, int votersCount, int votersVariantsCount) {
        super(id, question, hint, variantsList, filterId, votersCount, votersVariantsCount);
        if (minAllowed == INT_NOT_SET) {
            this.minAllowed = 0;
        } else {
            this.minAllowed = minAllowed;
        }
        if (maxAllowed == INT_NOT_SET) {
            this.maxAllowed = variantsList.size();
        } else {
            this.maxAllowed = maxAllowed;
        }
    }

    @Override
    protected void processListView(ListView listView) {
        listView.setItemsCanFocus(true);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    protected View getListItemView(Context context, View convertView, SurveyVariant item, StatusProcessor statusProcessor) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.survey_question_checkbox, null);
        }
        ButterKnife.bind(convertView);
        ViewGroup container = ButterKnife.findById(convertView, R.id.container);
        View buttonContainer = ButterKnife.findById(convertView, R.id.buttonContainer);
        statusProcessor.processButtonContainerVisibility(buttonContainer);
        container.removeAllViews();
        View innerView = item.getView(context, statusProcessor);
        container.addView(innerView);
        return convertView;
    }

    @Override
    protected void verifyCheckedCount() throws VerificationException {
        boolean b = isChecked();
        if (!b) {
            final String text;
            if (minAllowed == maxAllowed) {
                text = "Выберите " + minAllowed + " вариантов ответов";
            } else {
                text = "Выберите от " + minAllowed + " до " + maxAllowed + " вариантов ответов.";
            }
            throw new VerificationException(text);
        }
    }

    private boolean isChecked() {
        int cc = 0;
        for (SurveyVariant variant : getVariantsList()) {
            if (variant.isChecked()) {
                cc++;
            }
        }
        boolean result = minAllowed <= cc && cc <= maxAllowed;
        return result;
    }

}
