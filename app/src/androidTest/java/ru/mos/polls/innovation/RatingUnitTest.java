package ru.mos.polls.innovation;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.innovation.model.Rating;

/**
 */

public class RatingUnitTest extends BaseUnitTest {
    @Test
    public void parse() {
        Rating rating = new Rating(fromTestRawAsJson("rating.json"));
        Assert.assertNotNull(rating);
        Assert.assertEquals(8, rating.getFullCount());
        Assert.assertEquals(4.4, rating.getFullRating(), 0);
        Assert.assertEquals(1, rating.getUserRating());
        Assert.assertNotNull(rating.getCounts());
        Assert.assertEquals(5, rating.getCounts().length);
        Assert.assertEquals(5, rating.getMaxCount());
    }
}
