package ru.mos.polls.social;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.social.manager.SocialManager;
import ru.mos.polls.social.model.SocialPostItem;
import ru.mos.polls.social.model.SocialPostValue;

/**
 * Created by Trunks on 21.03.2017.
 */

public class SocialPostItemUnitTest extends BaseUnitTest {

    Context appContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void createSocialPostItemList() {
        JSONObject jsonObject = new JSONObject();
        long test = 100;
        List<SocialPostItem> list = SocialPostItem.createItems(appContext, jsonObject, SocialPostValue.Type.POLL, (Object) test);
        Assert.assertEquals(list.size(), 4);
    }

    @Test
    public void createSocialPostItemListPostValue() {
        SocialPostValue socialPostValue = new SocialPostValue();
        List<SocialPostItem> list = SocialPostItem.createItems(appContext, socialPostValue);
        Assert.assertEquals(list.size(), 4);
    }

    @Test
    public void createSocialPostItem() {
        String title = "test";
        SocialPostValue socialPostValue = new SocialPostValue();
        SocialPostItem socialPostItem = new SocialPostItem(1, 2, title, socialPostValue);
        Assert.assertEquals(socialPostItem.getResourceId(), 1);
        Assert.assertEquals(socialPostItem.getResourceDisableId(), 2);
        Assert.assertEquals(socialPostItem.getTitle(), title);
        Assert.assertNotNull(socialPostItem.getSocialPostValue());

    }

    @Test
    public void getSocialPostValue() {
        JSONObject jsonObject = new JSONObject();
        SocialPostValue fb = SocialPostItem.getFbSocialPostValue(jsonObject, SocialPostValue.Type.POLL, 122);
        Assert.assertNotNull(fb);

        SocialPostValue vk = SocialPostItem.getVkSocialPostValue(jsonObject, SocialPostValue.Type.POLL, 122);
        Assert.assertNotNull(vk);

        SocialPostValue tw = SocialPostItem.getTwSocialPostValue(jsonObject, SocialPostValue.Type.POLL, 122);
        Assert.assertNotNull(tw);

        SocialPostValue ok = SocialPostItem.getOkSocialPostValue(jsonObject, SocialPostValue.Type.POLL, 122);
        Assert.assertNotNull(ok);

        SocialPostValue test = SocialPostItem.getSocialPostValue(SocialManager.SOCIAL_NAME_VK, jsonObject, SocialPostValue.Type.POLL, 122);
        Assert.assertNotNull(test);
    }
}
