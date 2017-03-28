package ru.mos.polls.social;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.base.Strings;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.R;
import ru.mos.polls.social.manager.SocialManager;
import ru.mos.polls.social.model.SocialPostItem;
import ru.mos.polls.social.model.SocialPostValue;

/**
 * Created by Trunks on 21.03.2017.
 */

public class SocialPostValueUnitTest extends BaseUnitTest {

    public static final String LINK = "link";
    public static final String TEXT = "text";
    public static final String IMAGE = "image";
    Context appContext = InstrumentationRegistry.getTargetContext();
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

        Assert.assertEquals(255, SocialPostValue.MAX_OK_POST_LENGTH);
        Assert.assertEquals(115, SocialPostValue.MAX_TWEET_POST_LENGTH);
        Assert.assertEquals(255, ok.getMaxSymbolsInPost());
        JSONObject testObj = ok.getOkAttachmentsJson();
        Assert.assertNotNull(testObj);
        Assert.assertNotNull(ok.prepareTwPost());
        Assert.assertNotNull(ok.preparePost());


        Assert.assertEquals(ok.isEmpty(), true);

        Assert.assertNull(ok.getLink());
        ok.setLink(LINK);
        Assert.assertEquals(LINK, ok.getLink());

        Assert.assertNull(ok.getText());
        ok.setText(TEXT);
        Assert.assertEquals(TEXT, ok.getText());

        Assert.assertNull(ok.getImage());
        ok.setImage(IMAGE);
        Assert.assertEquals(IMAGE, ok.getImage());

        Assert.assertEquals(ok.isEmpty(), false);

        Assert.assertEquals(ok.isEnable(), false);
        ok.setEnable(true);
        Assert.assertEquals(ok.isEnable(), true);

        Assert.assertEquals(ok.isForAchievement(), false);
        Assert.assertEquals(ok.isForHearing(), false);
        Assert.assertEquals(ok.isForNovelty(), false);
        Assert.assertEquals(ok.isForTask(), false);
        Assert.assertEquals(ok.isMustServerNotified(), false);
        Assert.assertEquals(ok.forTwitter(), false);
        Assert.assertEquals(ok.forVk(), false);
        Assert.assertEquals(ok.forFb(), false);
        Assert.assertEquals(ok.forOk(), true);
        Assert.assertEquals(ok.isPostMuchLong(), false);
        Assert.assertEquals(ok.hasType(SocialPostValue.Type.POLL), true);

        String warning = String.format(appContext.getString(R.string.warning_post_mutch_long), ok.getMaxSymbolsInPost());
        Assert.assertEquals(warning, ok.getWarningTitle(appContext));

        SocialPostValue vk = SocialPostItem.getVkSocialPostValue(jsonObject, SocialPostValue.Type.ACHIEVEMENT, 222);
        Assert.assertEquals(ok.equals(vk), false);
        Assert.assertEquals(ok.equals(ok), true);

        Assert.assertNotNull(ok.prepareFbPost());
    }
}
