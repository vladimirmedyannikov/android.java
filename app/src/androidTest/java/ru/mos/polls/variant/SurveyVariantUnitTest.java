package ru.mos.polls.variant;

import android.content.Intent;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.variants.ActionSurveyVariant;
import ru.mos.polls.survey.variants.IntervalSurveyVariant;
import ru.mos.polls.survey.variants.SelectSurveyVariant;
import ru.mos.polls.survey.variants.select.GorodSelectActivity;
import ru.mos.polls.survey.variants.select.GorodSelectObject;
import ru.mos.polls.survey.variants.values.IntVariantValue;

/**
 * Created by Trunks on 17.05.2017.
 */

public class SurveyVariantUnitTest extends BaseUnitTest {

    String UID = "uid";
    long INNER_ID = 0;
    int PERCENT = 100;
    int VOTERS = 100;

    @Test
    public void actionSurveyVariantTest() {

        ActionSurveyVariant asv = new ActionSurveyVariant(UID, INNER_ID, PERCENT, VOTERS, "action", "action");
        Assert.assertEquals(asv.isChecked(), false);
        try {
            asv.verify();
        } catch (Exception e) {
            if (!(e instanceof VerificationException)) throw new RuntimeException();
        }
    }

    @Test
    public void selectSurveyVariantTest() {
        GorodSelectObject gso = new GorodSelectObject(0, 0, "uid", "cat", "title", "test", "parent");
        SelectSurveyVariant ssv = new SelectSurveyVariant(UID, INNER_ID, PERCENT, VOTERS, "action", gso, "gorod");
        try {
            ssv.verify();
        } catch (Exception e) {
            if (!(e instanceof VerificationException)) throw new RuntimeException();
        }

        Assert.assertEquals(ssv.getSelectObject(), gso);
        Assert.assertEquals(ssv.getSelectObject().isSelected(), false);

        Intent intent = new Intent();
        intent.putExtra(GorodSelectActivity.EXTRA_ADDRESS, "new_address");
        intent.putExtra(GorodSelectActivity.EXTRA_OBJECT_ID, "new_id");
        ssv.onActivityResultOk(intent);

        Assert.assertEquals(ssv.getSelectObject().isSelected(), true);
    }

    @Test
    public void intervalSurveyVariantTest() {
        IntervalSurveyVariant isv = new IntervalSurveyVariant(UID, INNER_ID, PERCENT, VOTERS, "text", new IntVariantValue(1, 10), new IntVariantValue(10, 20), "3", "15");

    }
}
