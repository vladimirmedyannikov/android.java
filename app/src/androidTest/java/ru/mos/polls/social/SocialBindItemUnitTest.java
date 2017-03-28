package ru.mos.polls.social;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.social.model.SocialBindItem;

/**
 * Created by Trunks on 20.03.2017.
 */

public class SocialBindItemUnitTest extends BaseUnitTest {

    @Test
    public void createitem() {
        SocialBindItem fb = SocialBindItem.FB;
        Assert.assertEquals(fb.getSocialId(), SocialBindItem.FB.getSocialId());
        Assert.assertEquals(fb.getUnBindResId(), SocialBindItem.FB.getUnBindResId());
        Assert.assertEquals(fb.getBindResId(), SocialBindItem.FB.getBindResId());
        Assert.assertEquals(fb.getResTitle(), SocialBindItem.FB.getResTitle());

        SocialBindItem vk = SocialBindItem.getItem(SocialBindItem.VK.getSocialId());
        Assert.assertEquals(vk.getSocialId(), SocialBindItem.VK.getSocialId());
        Assert.assertEquals(vk.getUnBindResId(), SocialBindItem.VK.getUnBindResId());
        Assert.assertEquals(vk.getBindResId(), SocialBindItem.VK.getBindResId());
        Assert.assertEquals(vk.getResTitle(), SocialBindItem.VK.getResTitle());

        SocialBindItem tw = SocialBindItem.getItem(SocialBindItem.TW.getSocialId());
        Assert.assertEquals(tw.getSocialId(), SocialBindItem.TW.getSocialId());
        Assert.assertEquals(tw.getUnBindResId(), SocialBindItem.TW.getUnBindResId());
        Assert.assertEquals(tw.getBindResId(), SocialBindItem.TW.getBindResId());
        Assert.assertEquals(tw.getResTitle(), SocialBindItem.TW.getResTitle());

        SocialBindItem ok = SocialBindItem.getItem(SocialBindItem.OK.getSocialId());
        Assert.assertEquals(ok.getSocialId(), SocialBindItem.OK.getSocialId());
        Assert.assertEquals(ok.getUnBindResId(), SocialBindItem.OK.getUnBindResId());
        Assert.assertEquals(ok.getBindResId(), SocialBindItem.OK.getBindResId());
        Assert.assertEquals(ok.getResTitle(), SocialBindItem.OK.getResTitle());
    }
}
