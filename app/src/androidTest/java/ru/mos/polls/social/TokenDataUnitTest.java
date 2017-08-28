package ru.mos.polls.social;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.social.model.Configurator;
import ru.mos.social.model.Token;

/**
 * Created by Trunks on 27.03.2017.
 */

public class TokenDataUnitTest extends BaseUnitTest {


    public static final String TEST_1 = "test1";
    public static final String TEST_2 = "test2";
    Context appContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void createObj() {
        Token td = new Token(TEST_1, TEST_2, System.currentTimeMillis() + 1000000);

        Assert.assertEquals(false, td.isEmpty());
        Assert.assertEquals(TEST_1, td.getAccess());
        Assert.assertEquals(TEST_2, td.getRefresh());
        Assert.assertEquals(true, Configurator.getInstance(appContext).getStorable().get(1).getToken().isEmpty());

    }
}
