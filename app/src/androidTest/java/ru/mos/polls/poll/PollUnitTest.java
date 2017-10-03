package ru.mos.polls.poll;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.poll.model.Poll;

/**
 * Created by Trunks on 18.04.2017.
 */

public class PollUnitTest extends BaseUnitTest {

    @Test
    public void parse() {
        Poll poll = new Poll(fromTestRawAsJson("poll.json"));
        Assert.assertEquals(poll.getAuthor(),"");
        Assert.assertEquals(poll.getTitle(),"тест картинки");
        Assert.assertEquals(poll.getPoints(),12);
//        Assert.assertEquals(poll.getStatus(), Poll.Status.ACTIVE);
        Assert.assertEquals(poll.getBeginDate(), 1491000000);
        Assert.assertEquals(poll.getEndDate(), 1493000000);
        Assert.assertEquals(poll.getKind(), Kind.STANDART);
        Assert.assertEquals(poll.getId(), 1268);
        Assert.assertEquals(poll.isActive(), true);
        Assert.assertEquals(poll.isOld(), false);
        Assert.assertEquals(poll.isPassed(), false);
        Assert.assertEquals(poll.isInterrupted(), false);
    }
}
