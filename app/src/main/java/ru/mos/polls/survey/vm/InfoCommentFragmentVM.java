package ru.mos.polls.survey.vm;

import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.databinding.FragmentInfoCommentBinding;
import ru.mos.polls.survey.ui.InfoCommentFragment;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.questions.CheckboxSurveyQuestion;
import ru.mos.polls.survey.variants.InputSurveyVariant;
import ru.mos.polls.survey.variants.values.CharVariantValue;

/**
 * Created by Trunks on 06.12.2017.
 */

public class InfoCommentFragmentVM extends UIComponentFragmentViewModel<InfoCommentFragment, FragmentInfoCommentBinding> {

    TextInputEditText numberEditText, commentEditText;
    Survey survey;
    AppCompatTextView infoCommentDescription;
    CheckboxSurveyQuestion checkboxSurveyQuestion;

    public InfoCommentFragmentVM(InfoCommentFragment fragment, FragmentInfoCommentBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentInfoCommentBinding binding) {
        numberEditText = binding.infoNumberApp;
        commentEditText = binding.infoComment;
        infoCommentDescription = binding.infoCommentDescription;
        checkSurveyNull();
        if (survey != null) {
            checkboxSurveyQuestion = (CheckboxSurveyQuestion) survey.getQuestionsList().get(1);
            InputSurveyVariant numberSurveyVariant = (InputSurveyVariant) checkboxSurveyQuestion.getVariantsList().get(0);
            InputSurveyVariant commentSurveyVariant = (InputSurveyVariant) checkboxSurveyQuestion.getVariantsList().get(1);

            numberEditText.setText(numberSurveyVariant.getText() != null ? numberSurveyVariant.getText() : "");
            commentEditText.setText(commentSurveyVariant.getText() != null ? commentSurveyVariant.getText() : "");
        }
    }

    private void checkSurveyNull() {
        if (survey == null) {
            setSurvey(getFragment().getSurvey());
        }
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        checkSurveyNull();
        getActivity().setTitle(getActivity().getString(R.string.comment));
        setSurveyAnswer();
        setCommentDescVisibility();
    }

    public void setCommentDescVisibility() {
        if (survey.isOld() || survey.isPassed()) infoCommentDescription.setVisibility(View.GONE);
    }

    public void setSurveyAnswer() {
        setAnswer((InputSurveyVariant) checkboxSurveyQuestion.getVariantsList().get(0), numberEditText);
        setAnswer((InputSurveyVariant) checkboxSurveyQuestion.getVariantsList().get(1), commentEditText);
        if (survey.isOld() || survey.isPassed()) {
            numberEditText.setEnabled(false);
            commentEditText.setEnabled(false);
        }
    }

    @Override
    public void onCreateOptionsMenu() {
        super.onCreateOptionsMenu();
        if (survey.isOld() || survey.isPassed()) {
            getFragment().hideMenuItem(R.id.action_confirm);
        }
    }

    public void setAnswer(InputSurveyVariant surveyVariant, TextInputEditText editText) {
        if (surveyVariant.isChecked()) {
            CharVariantValue charVariantValue = (CharVariantValue) surveyVariant.input;
            if (!charVariantValue.isEmpty()) {
                editText.setText(charVariantValue.getValue());
            }
        }
    }


    @Override
    protected UIComponentHolder createComponentHolder() {
        return null;
    }

    @Override
    public void onOptionsItemSelected(int menuItemId) {
        confirmAction();
    }

    public boolean isDataChanged() {
        if (survey.isPassed() || survey.isOld()) return false;
        return (numberEditText.getText().toString().trim().length() > 0 || commentEditText.getText().toString().trim().length() > 0);
    }

    public void confirmAction() {
        AGApplication.bus().send(new Events.InfoSurveyEvents(commentEditText.getText().toString(), numberEditText.getText().toString()));
        getFragment().getFragmentManager().popBackStackImmediate();
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }
}
