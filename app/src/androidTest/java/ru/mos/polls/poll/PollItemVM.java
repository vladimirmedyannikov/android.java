package ru.mos.polls.poll;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.R;
import ru.mos.polls.newpoll.ui.adapter.PollAdapter;
import ru.mos.polls.newpoll.vm.item.PollItemActiveVM;
import ru.mos.polls.newpoll.vm.item.PollItemOldVM;
import ru.mos.polls.poll.model.Poll;

/**
 * Created by Trunks on 02.11.2017.
 */

public class PollItemVM extends BaseUnitTest {

    @Test
    public void testPollActive() {
        Poll poll = new Poll(fromTestRawAsJson("poll_active.json"));
        PollItemActiveVM testObj = new PollItemActiveVM(poll);
        Assert.assertEquals(testObj.getViewType(), PollAdapter.Type.ITEM_ACTIVE);
        Assert.assertEquals(testObj.getLayoutId(), R.layout.item_active_poll);

    }

    @Test
    public void testPollOld() {
        Poll poll = new Poll(fromTestRawAsJson("poll_old.json"));
        PollItemOldVM testObj = new PollItemOldVM(poll);
        Assert.assertEquals(testObj.getViewType(), PollAdapter.Type.ITEM_OLD);
        Assert.assertEquals(testObj.getLayoutId(), R.layout.item_passed_poll);
    }
}
