package ru.mos.polls.friends;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.friend.model.Friend;

/**
 * Created by Trunks on 15.11.2017.
 */

public class FriendUnitTest extends BaseUnitTest {

    @Test
    public void getters() {
        Friend f1 = mockObj("friends_my.json", Friend.class);
        Assert.assertNotNull(f1);

        Assert.assertNotNull(f1.getAvatar());
        Assert.assertNotNull(f1.getStatus());
        Assert.assertNotNull(f1.getName());
        Assert.assertNotNull(f1.getPhone());
        Assert.assertNotNull(f1.getSurname());
        Assert.assertNotEquals(f1.getId(), 0);
    }
}
