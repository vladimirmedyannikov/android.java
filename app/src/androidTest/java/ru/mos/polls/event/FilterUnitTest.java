package ru.mos.polls.event;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.event.model.Filter;

/**
 * Created by matek3022 on 12.09.17.
 */

public class FilterUnitTest extends BaseUnitTest {
    @Test
    public void compare() {
        Assert.assertEquals(Filter.CURRENT, Filter.fromFilter("current"));
        Assert.assertEquals(Filter.VISITED, Filter.fromFilter("visited"));
        Assert.assertEquals(Filter.PAST, Filter.fromFilter("past"));
    }
}
