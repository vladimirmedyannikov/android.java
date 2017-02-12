package ru.mos.polls.flat;

import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.profile.model.DistrictArea;

/**
 * Created by Trunks on 06.02.2017.
 */

public class DistrictAreaUnitTest extends BaseUnitTest{

    @Test
    public void parse() {
        DistrictArea test = new DistrictArea(fromTestRawAsJson("districtarea.json"));
        assertNotNullOrEmpty(test.getArea());
        assertNotNullOrEmpty(test.getDistrict());
    }
}
