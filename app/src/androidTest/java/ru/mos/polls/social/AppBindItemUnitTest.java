package ru.mos.polls.social;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.social.model.AppBindItem;

public class AppBindItemUnitTest extends BaseUnitTest {

    @Test
    public void createitem() {
        AppBindItem fb = AppBindItem.FB;
        Assert.assertEquals(fb.getSocialId(), AppBindItem.FB.getSocialId());
        Assert.assertEquals(fb.getUnBindResId(), AppBindItem.FB.getUnBindResId());
        Assert.assertEquals(fb.getBindResId(), AppBindItem.FB.getBindResId());
        Assert.assertEquals(fb.getResTitle(), AppBindItem.FB.getResTitle());

        AppBindItem vk = AppBindItem.getItem(AppBindItem.VK.getSocialId());
        Assert.assertEquals(vk.getSocialId(), AppBindItem.VK.getSocialId());
        Assert.assertEquals(vk.getUnBindResId(), AppBindItem.VK.getUnBindResId());
        Assert.assertEquals(vk.getBindResId(), AppBindItem.VK.getBindResId());
        Assert.assertEquals(vk.getResTitle(), AppBindItem.VK.getResTitle());

        AppBindItem tw = AppBindItem.getItem(AppBindItem.TW.getSocialId());
        Assert.assertEquals(tw.getSocialId(), AppBindItem.TW.getSocialId());
        Assert.assertEquals(tw.getUnBindResId(), AppBindItem.TW.getUnBindResId());
        Assert.assertEquals(tw.getBindResId(), AppBindItem.TW.getBindResId());
        Assert.assertEquals(tw.getResTitle(), AppBindItem.TW.getResTitle());

        AppBindItem ok = AppBindItem.getItem(AppBindItem.OK.getSocialId());
        Assert.assertEquals(ok.getSocialId(), AppBindItem.OK.getSocialId());
        Assert.assertEquals(ok.getUnBindResId(), AppBindItem.OK.getUnBindResId());
        Assert.assertEquals(ok.getBindResId(), AppBindItem.OK.getBindResId());
        Assert.assertEquals(ok.getResTitle(), AppBindItem.OK.getResTitle());
    }
}
