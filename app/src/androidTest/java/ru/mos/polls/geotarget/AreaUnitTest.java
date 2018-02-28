package ru.mos.polls.geotarget;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.geotarget.model.Area;

public class AreaUnitTest extends BaseUnitTest {

    @Test
    public void parseTest() {
        List<Area> list = Area.from(fromTestRawAsJsonArray("geotarget_area.json"));
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() > 0);
    }
}
