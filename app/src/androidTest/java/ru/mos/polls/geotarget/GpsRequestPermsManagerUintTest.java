package ru.mos.polls.geotarget;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.geotarget.manager.GpsRequestPermsManager;

/**
 * Created by Trunks on 23.11.2017.
 */

public class GpsRequestPermsManagerUintTest extends BaseUnitTest {

    @Test
    public void syncTest() {
        long currentTime = System.currentTimeMillis();
        if (currentTime > GpsRequestPermsManager.getSyncTime(appContext)) {
            Assert.assertFalse(GpsRequestPermsManager.isNeedRequestGps(appContext));
        } else {
            Assert.assertTrue(GpsRequestPermsManager.isNeedRequestGps(appContext));
        }
    }
}
