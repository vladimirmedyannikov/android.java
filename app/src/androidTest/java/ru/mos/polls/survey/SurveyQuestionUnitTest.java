package ru.mos.polls.survey;

import org.junit.Test;

import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.survey.parsers.SurveyQuestionFactory;
import ru.mos.polls.survey.questions.SimpleSurveyQuestion;
import ru.mos.polls.survey.variants.SurveyVariant;

/**
 * Created by Trunks on 04.05.2017.
 */

public class SurveyQuestionUnitTest extends BaseUnitTest {

    @Test
    public void resetAnswers() {
        SimpleSurveyQuestion sq = (SimpleSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_simple.json"), 0);
        List<SurveyVariant> variantsTestList = sq.getVariantsList();
        for (SurveyVariant surveyVariant : variantsTestList) {
            surveyVariant.setChecked(true);
        }
        for (SurveyVariant surveyVariant : variantsTestList) {
            if (surveyVariant.getBackId().equalsIgnoreCase(sq.getCheckedBackIds()[0]) && !surveyVariant.isChecked())
                throw new RuntimeException();
        }

        sq.reset();

        for (SurveyVariant surveyVariant : variantsTestList) {
            if (surveyVariant.getBackId().equalsIgnoreCase(sq.getCheckedBackIds()[0]) && surveyVariant.isChecked())
                throw new RuntimeException();
        }
    }
}
