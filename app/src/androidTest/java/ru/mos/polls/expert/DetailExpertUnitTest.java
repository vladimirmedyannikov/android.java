package ru.mos.polls.expert;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.survey.experts.DetailsExpert;

/**
 * Created by Trunks on 27.04.2017.
 */

public class DetailExpertUnitTest extends BaseUnitTest {
    List<DetailsExpert> list;


    @Before
    public void init() {
        list = DetailsExpert.fromJson(fromTestRawAsJsonArray("expert.json"));
    }

    @Test
    public void parseList() {
        Assert.assertNotNull(list);
    }

    @Test
    public void getObjects() {
        DetailsExpert test1 = list.get(0);
        Assert.assertNotNull(test1);

        assertNotNullOrEmpty(test1.getTitle());
        assertNotNullOrEmpty(test1.getBody());
        assertNotNullOrEmpty(test1.getDescription());
        assertNotNullOrEmpty(test1.getImgUrl());
        Assert.assertNotEquals(0, test1.getId());
        Assert.assertEquals(false, test1.isEmpty());
    }

    @Test
    public void compare() {
        DetailsExpert test1 = list.get(0);
        Assert.assertNotNull(test1);

        DetailsExpert test2 = list.get(1);
        Assert.assertNotNull(test1);

        Assert.assertEquals(false, test1.compare(test2));
    }
}
