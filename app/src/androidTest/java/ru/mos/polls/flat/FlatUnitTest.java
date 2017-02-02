package ru.mos.polls.flat;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.BaseUnitTest;

/**
 * Created by Trunks on 02.02.2017.
 */

public class FlatUnitTest extends BaseUnitTest {

    @Test
    public void parse() {
        Flat test = Flat.fromJson(fromTestRawAsJson("flat.json"), Flat.Type.REGISTRATION);
        Assert.assertNotNull(test.getArea());
        Assert.assertEquals(test.getType(), Flat.Type.REGISTRATION);
        Assert.assertNotNull(test.getFlatId());
        Assert.assertNotNull(test.getStreet());
        Assert.assertNotNull(test.getArea());
        Assert.assertNotNull(test.getDistrict());
        Assert.assertNotNull(test.getBuildingId());
    }

    @Test
    public void testType() {
        Flat registration = Flat.getRegistration(fromTestRawAsJson("flat.json"));
        Assert.assertEquals(registration.getType(), Flat.Type.REGISTRATION);

        Flat residence = Flat.getResidence(fromTestRawAsJson("flat.json"));
        Assert.assertEquals(residence.getType(), Flat.Type.RESIDENCE);

        Flat work = Flat.getWork(fromTestRawAsJson("flat.json"));
        Assert.assertEquals(work.getType(), Flat.Type.WORK);
    }
}
