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
 * Вопрос, допускающий выбор одного ответа из конечного множества.
 */
public class RadioboxSurveyQuestion extends ListViewSurveyQuestion {

    public RadioboxSurveyQuestion(long id, Text question, String hint, List<SurveyVariant> variantsList, long filterId, int votersCount, int votersVariantsCount) {
        super(id, question, hint, variantsList, filterId, votersCount, votersVariantsCount);
    }

    @Override
    protected void processListView(ListView listView) {
        listView.setItemsCanFocus(true);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
    }

    @Override
    protected View getListItemView(Context context, View convertView, SurveyVariant item, StatusProcessor statusProcessor) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.survey_question_radiobox, null);
        }
        ButterKnife.bind(this, convertView);
        View buttonContainer = ButterKnife.findById(convertView, R.id.buttonContainer);
        statusProcessor.processButtonContainerVisibility(buttonContainer);
        ViewGroup container = ButterKnife.findById(convertView, R.id.container);
        container.removeAllViews();
        View innerView = item.getView(context, statusProcessor);
        container.addView(innerView);
        return convertView;
    }

    @Override
    protected void verifyCheckedCount() throws VerificationException {
        boolean b = isChecked();
        if (!b) {
            throw new VerificationException("Выберите вариант ответа");
        }
    }

    public boolean isChecked() {
        boolean result = false;
        for (SurveyVariant variant : getVariantsList()) {
            if (variant.isChecked()) {
                result = true;
                break;
            }
        }
        return result;
    }

}
