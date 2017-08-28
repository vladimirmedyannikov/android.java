package ru.mos.polls.social;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.social.manager.SocialManager;
import ru.mos.polls.social.model.AppPostItem;
import ru.mos.polls.social.model.AppPostValue;

/**
 * Created by Trunks on 21.03.2017.
 */

public class AppPostItemUnitTest extends BaseUnitTest {

    Context appContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void createSocialPostItemList() {
        JSONObject jsonObject = new JSONObject();
        long test = 100;
        List<AppPostItem> list = AppPostItem.createItems(appContext, jsonObject, AppPostValue.Type.POLL, (Object) test);
        Assert.assertEquals(list.size(), 4);
    }

    @Test
    public void createSocialPostItemListPostValue() {
        AppPostValue appPostValue = new AppPostValue();
        List<AppPostItem> list = AppPostItem.createItems(appContext, appPostValue);
        Assert.assertEquals(list.size(), 4);
    }

    @Test
    public void createSocialPostItem() {
        String title = "test";
        AppPostValue appPostValue = new AppPostValue();
        AppPostItem appPostItem = new AppPostItem(1, 2, title, appPostValue);
        Assert.assertEquals(appPostItem.getResourceId(), 1);
        Assert.assertEquals(appPostItem.getResourceDisableId(), 2);
        Assert.assertEquals(appPostItem.getTitle(), title);
        Assert.assertNotNull(appPostItem.getAppPostValue());

    }

    @Test
    public void getSocialPostValue() {
        JSONObject jsonObject = new JSONObject();
        AppPostValue fb = AppPostItem.getFbSocialPostValue(jsonObject, AppPostValue.Type.POLL, 122);
        Assert.assertNotNull(fb);

        AppPostValue vk = AppPostItem.getVkSocialPostValue(jsonObject, AppPostValue.Type.POLL, 122);
        Assert.assertNotNull(vk);

        AppPostValue tw = AppPostItem.getTwSocialPostValue(jsonObject, AppPostValue.Type.POLL, 122);
        Assert.assertNotNull(tw);

        AppPostValue ok = AppPostItem.getOkSocialPostValue(jsonObject, AppPostValue.Type.POLL, 122);
        Assert.assertNotNull(ok);

        AppPostValue test = AppPostItem.getSocialPostValue(SocialManager.SOCIAL_NAME_VK, jsonObject, AppPostValue.Type.POLL, 122);
        Assert.assertNotNull(test);
    }
}
