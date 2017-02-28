package ru.mos.polls.innovation;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.innovation.model.Innovation;
import ru.mos.polls.innovation.model.Status;

/**
 *
 */

public class InnovationUnitTest extends BaseUnitTest {
    @Test
    public void parse() {
        JSONObject jsonObject = fromTestRawAsJson("innovation.json");
        Innovation testValue = new Innovation(jsonObject);
        Assert.assertNotNull(testValue);
        Assert.assertEquals(151, testValue.getId());
        assertNotNullOrEmpty(testValue.getTitle());
        assertNotNullOrEmpty(testValue.getTextFullHtml());
        assertNotNullOrEmpty(testValue.getTextShortHtml());
        Assert.assertNotNull(testValue.getStatus());
        Assert.assertEquals(Status.OLD, testValue.getStatus());
//        Assert.assertEquals(4.4, testValue.getFullRating(), 0);
//        Assert.assertEquals(14807988000L, testValue.getBeginDate());
//        Assert.assertEquals(14831316000L, testValue.getEndDate());
        Assert.assertNotNull(testValue.getRating());
    }
}
