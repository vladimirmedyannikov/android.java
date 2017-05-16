package ru.mos.polls.survey;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.survey.parsers.SurveyQuestionFactory;
import ru.mos.polls.survey.questions.RadioboxSurveyQuestion;

/**
 * Created by Trunks on 16.05.2017.
 */

public class RadioboxSurveyQuestionUnitTest extends BaseUnitTest {

    @Test
    public void isCheckedTest() {
        RadioboxSurveyQuestion rsq = (RadioboxSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_radiobutton.json"), 0);

        Assert.assertEquals(rsq.isChecked(), false);
        rsq.getVariantsList().get(0).setChecked(true);

        Assert.assertEquals(rsq.isChecked(), true);
    }
}
