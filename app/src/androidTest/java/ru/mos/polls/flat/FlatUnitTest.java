package ru.mos.polls.flat;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.profile.model.flat.Flat;

public class FlatUnitTest extends BaseUnitTest {
    Context appContext = InstrumentationRegistry.getTargetContext();
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
        Assert.assertEquals(false, test.isEmpty());
    }

    @Test
    public void testType() {
        Flat registration = Flat.getRegistration(fromTestRawAsJson("flat.json"));
        Assert.assertEquals(registration.getType(), Flat.Type.REGISTRATION);
        Assert.assertEquals(true, registration.isRegistration());

        Flat residence = Flat.getResidence(fromTestRawAsJson("flat.json"));
        Assert.assertEquals(residence.getType(), Flat.Type.RESIDENCE);
        Assert.assertEquals(true, residence.isResidence());

        Flat work = Flat.getWork(fromTestRawAsJson("flat.json"));
        Assert.assertEquals(work.getType(), Flat.Type.WORK);
        Assert.assertEquals(true, work.isWork());
    }

    @Test
    public void testJson() {
        Flat testFlat = new Flat();
        JSONObject jsontest1 = testFlat.asJsonForAdd();
        Assert.assertNotNull(jsontest1);
        JSONObject jsontest2 = testFlat.asJsonForUpdate();
        Assert.assertNotNull(jsontest2);


    }

    @Test
    public void testseters() {
        Flat test = new Flat();
        test.setType(Flat.Type.REGISTRATION);
        Assert.assertEquals(Flat.Type.REGISTRATION, test.getType());
        String testString = "test";
        test.setArea(testString);
        Assert.assertEquals(testString, test.getArea());

        test.setAreaId(testString);
        Assert.assertEquals(testString, test.getAreaId());

        test.setBuilding(testString);
        Assert.assertEquals(testString, test.getBuilding());


        test.setBuildingId(testString);
        Assert.assertEquals(testString, test.getBuildingId());

        test.setCity(testString);
        Assert.assertEquals(testString, test.getCity());

        test.setDistrict(testString);
        Assert.assertEquals(testString, test.getDistrict());

        test.setEnable(true);
        Assert.assertEquals(true, test.isEnable());

        test.setStreet(testString);
        Assert.assertEquals(testString, test.getStreet());

        Assert.assertNotNull(test.getAddressTitle(appContext));
        Assert.assertNotNull(test.getViewTitle(appContext));
    }

    @Test
    public void testEqual() {
        Flat reg1 = Flat.getRegistration(fromTestRawAsJson("flat.json"));
        Flat reg2 = Flat.getRegistration(fromTestRawAsJson("flat.json"));


        Flat res1 = Flat.getResidence(fromTestRawAsJson("flat.json"));

        Flat work1 = Flat.getWork(fromTestRawAsJson("flat.json"));

        Assert.assertEquals(true, reg1.equals(reg2));
        Assert.assertEquals(false, reg1.equals(res1));

        Assert.assertEquals(true, reg1.compareByFullAddress(reg2));
        Assert.assertEquals(false, reg1.compareByFullAddress(work1));

    }
}
