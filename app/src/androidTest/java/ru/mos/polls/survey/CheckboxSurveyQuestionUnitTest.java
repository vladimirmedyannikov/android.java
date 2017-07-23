package ru.mos.polls.survey;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.survey.parsers.SurveyQuestionFactory;
import ru.mos.polls.survey.questions.CheckboxSurveyQuestion;
import ru.mos.polls.survey.variants.SurveyVariant;

/**
 * Created by Trunks on 16.05.2017.
 */

public class CheckboxSurveyQuestionUnitTest extends BaseUnitTest {

    @Test
    public void isCheckedMinTest() {
        CheckboxSurveyQuestion csq = (CheckboxSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_checkbox.json"), 0);
        List<SurveyVariant> list = csq.getVariantsList();
        Assert.assertEquals(csq.isChecked(), false);
        csq.getVariantsList().get(0).setChecked(true);
        Assert.assertEquals(csq.isChecked(), true);
    }

    @Test
    public void isCheckedMaxTest() {
        CheckboxSurveyQuestion csq = (CheckboxSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_checkbox.json"), 0);
        List<SurveyVariant> list = csq.getVariantsList();
        for (SurveyVariant surveyVariant : list) {
            surveyVariant.setChecked(true);
        }
        Assert.assertEquals(csq.isChecked(), false);

        csq.getVariantsList().get(0).setChecked(false);
        Assert.assertEquals(csq.isChecked(), true);
    }
}
