package ru.mos.polls.social;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.social.manager.SocialManager;
import ru.mos.polls.social.model.SocialPostItem;
import ru.mos.polls.social.model.SocialPostValue;

/**
 * Created by Trunks on 21.03.2017.
 */

public class SocialPostValueUnitTest extends BaseUnitTest {

    @Test
    public void createOk() {
        JSONObject jsonObject = fromTestRawAsJson("socialpostvalue.json");
        SocialPostValue ok = SocialPostItem.getOkSocialPostValue(jsonObject, SocialPostValue.Type.POLL, 122);
        ok.setSocialId(SocialManager.SOCIAL_NAME_OK);
        ok.setSocialName(SocialManager.SOCIAL_NAME_OK);
        Assert.assertEquals(ok.getSocialId(), SocialManager.SOCIAL_ID_OK);
        Assert.assertEquals(ok.getSocialName(), SocialManager.SOCIAL_NAME_OK);
        Assert.assertEquals(ok.getMaxSymbolsInPost(), SocialPostValue.MAX_OK_POST_LENGTH);
        ok.setType(SocialPostValue.Type.POLL);
        Assert.assertEquals(ok.getType(), SocialPostValue.Type.POLL);
        Assert.assertNotEquals(true, ok.isForTask());
    }
}
