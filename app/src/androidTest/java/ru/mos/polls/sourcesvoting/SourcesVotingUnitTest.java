package ru.mos.polls.sourcesvoting;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.sourcesvoting.model.SourcesVoting;

public class SourcesVotingUnitTest extends BaseUnitTest {

    @Test
    public void gettersTest() {
        SourcesVoting votingSet = mockObj("sourcesvoting.json", SourcesVoting.class);
        Assert.assertNotEquals(votingSet.getId(), 0);
        Assert.assertNotNull(votingSet.getTitle());
        Assert.assertTrue(votingSet.isEditable());
        Assert.assertTrue(votingSet.isEnable());
    }
}
