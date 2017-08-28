package ru.mos.polls.social;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.R;
import ru.mos.polls.social.model.AppSocial;
import ru.mos.polls.social.storable.AppStorable;
import ru.mos.social.model.Configurator;

/**
 * Created by Trunks on 13.03.2017.
 */

public class SocialUnitTest extends BaseUnitTest {
    Context appContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void jsonObject() {
        AppSocial test = new AppSocial("test", fromTestRawAsJson("social.json"));
        Assert.assertNotNull(test.tokenDataAsJson());
        Assert.assertNotNull(test.asKillJson());
        Assert.assertNotNull(test.asNull());
        Assert.assertEquals(test.getToken().isEmpty(), false);
    }

    @Test
    public void othertest() {
        AppSocial test = new AppSocial("test", fromTestRawAsJson("social.json"));
        AppSocial test2 = new AppSocial();
        test2.copy(test);
        Assert.assertEquals(test2.getToken().getExpireTime(), test.getToken().getExpireTime());
        Assert.assertEquals(test2.getStringIcon(), test.getIcon());
        Assert.assertEquals(test2.getSocialId(), test.getSocialId());
        Assert.assertEquals(test2.getToken(), test.getToken());
        Assert.assertEquals(test2.isLogon(), test.isLogon());
        Assert.assertEquals(true, test.equals(test2));

        test.setIsLogin(false);
        Assert.assertEquals(false, test.isLogon());
        test.setIcon("test");
        Assert.assertEquals("test", test.getStringIcon());
        Assert.assertEquals(-1, test.getSocialId());
    }

    @Test
    public void getList() {
        List<AppSocial> list = ((AppStorable) Configurator.getInstance(appContext).getStorable()).getAll();
        Assert.assertNotNull(list);
        Assert.assertEquals(4, list.size());

        AppSocial fbs = AppSocial.findFbSocial(list);
        Assert.assertEquals(fbs.getSocialId(), AppSocial.ID_FB);


        AppSocial vk = AppSocial.findVkSocial(list);
        Assert.assertEquals(vk.getSocialId(), AppSocial.ID_VK);

        AppSocial ok = AppSocial.findOkSocial(list);
        Assert.assertEquals(ok.getSocialId(), AppSocial.ID_OK);

        AppSocial tw = AppSocial.findTwSocial(list);
        Assert.assertEquals(tw.getSocialId(), AppSocial.ID_TW);

        AppSocial tw2 = AppSocial.findSocial(list, AppSocial.ID_TW);
        Assert.assertEquals(tw2.getSocialId(), AppSocial.ID_TW);

        Assert.assertEquals(true, AppSocial.isEquals(list, list));
    }

    @Test
    public void fromPref() {
        AppSocial test = AppSocial.fromPreference(appContext, AppSocial.ID_FB);
        Assert.assertNotNull(test);

    }

    @Test
    public void getSocialIconId() {
        int vkIcon = AppSocial.getSocialIcon(AppSocial.ID_VK);
        Assert.assertEquals(R.drawable.vk, vkIcon);

        int fbIcon = AppSocial.getSocialIcon(AppSocial.ID_FB);
        Assert.assertEquals(R.drawable.fb, fbIcon);

        int twIcon = AppSocial.getSocialIcon(AppSocial.ID_TW);
        Assert.assertEquals(R.drawable.tw, twIcon);

        int okIcon = AppSocial.getSocialIcon(AppSocial.ID_OK);
        Assert.assertEquals(R.drawable.odnklsnk, okIcon);

        int gpIcon = AppSocial.getSocialIcon(AppSocial.ID_GP);
        Assert.assertEquals(R.drawable.google, gpIcon);

        int defaultRed = AppSocial.getSocialIcon(0);
        Assert.assertEquals(-1, defaultRed);
    }
}
