package ru.mos.polls.about;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.R;
import ru.mos.polls.about.model.AboutItem;

public class AboutItemUnitTest extends BaseUnitTest {

    @Test
    public void gettersTest() {
        Assert.assertNotNull(AboutItem.ITEMS);
        Assert.assertNotNull(AboutItem.getAdapter((id, position) -> {

        }));
    }

    @Test
    public void aboutItemGettersTest() {
        AboutItem[] items = AboutItem.ITEMS;

        AboutItem item0 = items[0];
        Assert.assertEquals(AboutItem.ABOUT_PROJECT, item0.getId());
        Assert.assertEquals(R.string.title_about_project, item0.getTitle());

        AboutItem item1 = items[1];
        Assert.assertEquals(AboutItem.USER_GUIDE, item1.getId());
        Assert.assertEquals(R.string.title_user_guide, item1.getTitle());

        AboutItem item2 = items[2];
        Assert.assertEquals(AboutItem.OUR_APPS, item2.getId());
        Assert.assertEquals(R.string.our_apps, item2.getTitle());


        AboutItem item3 = items[3];
        Assert.assertEquals(AboutItem.OFFER, item3.getId());
        Assert.assertEquals(R.string.title_offer, item3.getTitle());

        AboutItem item4 = items[4];
        Assert.assertEquals(AboutItem.SHARE_SOCIAL, item4.getId());
        Assert.assertEquals(R.string.title_tell_to_friends, item4.getTitle());

        AboutItem item5 = items[5];
        Assert.assertEquals(AboutItem.RATE_APP, item5.getId());
        Assert.assertEquals(R.string.title_rate_this_app, item5.getTitle());

        AboutItem item6 = items[6];
        Assert.assertEquals(AboutItem.FEEDBACK, item6.getId());
        Assert.assertEquals(R.string.feedback, item6.getTitle());
    }
}
