package ru.mos.polls.flat;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.profile.model.Reference;

public class ReferenceUnitTest extends BaseUnitTest {

    @Test
    public void parse() {
        List<Reference> list = Reference.fromJsonArray(fromTestRawAsJsonArray("list_reference.json"));
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        Assert.assertNotNull(list.get(0));
        Assert.assertNotNull(list.get(1));
    }

    @Test
    public void testObject() {
        List<Reference> list = Reference.fromJsonArray(fromTestRawAsJsonArray("list_reference.json"));
        Reference test = list.get(0);
        Assert.assertNotNull(test.getLabel());
        Assert.assertNotNull(test.getValue());
        Assert.assertEquals(test.toString(), test.getLabel());
    }
}
