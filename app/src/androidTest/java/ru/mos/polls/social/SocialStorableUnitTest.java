package ru.mos.polls.social;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.social.model.AppSocial;
import ru.mos.polls.social.storable.AppStorable;

/**
 * Created by Trunks on 11.04.2017.
 */

public class SocialStorableUnitTest extends BaseUnitTest {

    Context appContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void saveAndGetObj() {
        AppStorable appStorable = new AppStorable(appContext);
        AppSocial vk = new AppSocial("vk", fromTestRawAsJson("social.json"));
        appStorable.save(vk);
        AppSocial savedVK = appStorable.get(AppSocial.getId("vk"));
        Assert.assertNotNull(savedVK.getToken());
        Assert.assertNotNull(savedVK.getIcon());
//        Assert.assertEquals(savedVK.isLogon(), vk.isLogon());
        Assert.assertEquals(savedVK.getToken().isEmpty(), vk.getToken().isEmpty());
        Assert.assertEquals(savedVK.getToken().getAccess(), vk.getToken().getAccess());
        Assert.assertEquals(savedVK.getToken().getRefresh(), vk.getToken().getRefresh());
        Assert.assertEquals(savedVK.getToken().getExpireTime(), vk.getToken().getExpireTime());
        Assert.assertEquals(savedVK.getIcon(), vk.getIcon());
    }

    @Test
    public void saveAndGetAllObj() {
        AppStorable appStorable = new AppStorable(appContext);
        List<AppSocial> socials = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            AppSocial s = new AppSocial(AppSocial.getSocialName(i + 1), fromTestRawAsJson("social.json"));
            socials.add(s);
            appStorable.save(s);
            AppSocial savedS = appStorable.get(i + 1);
            Assert.assertNotNull(savedS.getToken());
            Assert.assertNotNull(savedS.getIcon());
//            Assert.assertEquals(savedS.isLogon(), s.isLogon());
            Assert.assertEquals(savedS.getToken().isEmpty(), s.getToken().isEmpty());
            Assert.assertEquals(savedS.getToken().getAccess(), s.getToken().getAccess());
            Assert.assertEquals(savedS.getToken().getRefresh(), s.getToken().getRefresh());
            Assert.assertEquals(savedS.getToken().getExpireTime(), s.getToken().getExpireTime());
            Assert.assertEquals(savedS.getIcon(), s.getIcon());
        }
        List<AppSocial> savedSocials = appStorable.getAll();
        Assert.assertEquals(socials.size(), savedSocials.size());
        for (int i = 0; i < savedSocials.size(); i++) {
            AppSocial s = socials.get(i);
            AppSocial savedS = savedSocials.get(i);
            Assert.assertNotNull(savedS.getToken());
            Assert.assertNotNull(savedS.getIcon());
//            Assert.assertEquals(savedS.isLogon(), s.isLogon());
            Assert.assertEquals(savedS.getToken().isEmpty(), s.getToken().isEmpty());
            Assert.assertEquals(savedS.getToken().getAccess(), s.getToken().getAccess());
            Assert.assertEquals(savedS.getToken().getRefresh(), s.getToken().getRefresh());
            Assert.assertEquals(savedS.getToken().getExpireTime(), s.getToken().getExpireTime());
            Assert.assertEquals(savedS.getIcon(), s.getIcon());
        }
    }
}
