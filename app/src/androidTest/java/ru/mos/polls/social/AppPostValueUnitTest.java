package ru.mos.polls.social;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.R;
import ru.mos.polls.social.model.AppPostItem;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.social.model.AppSocial;

/**
 * Created by Trunks on 21.03.2017.
 */

public class AppPostValueUnitTest extends BaseUnitTest {

    public static final String LINK = "link";
    public static final String TEXT = "text";
    public static final String IMAGE = "image";
    Context appContext = InstrumentationRegistry.getTargetContext();
    @Test
    public void createOk() {
        JSONObject jsonObject = fromTestRawAsJson("socialpostvalue.json");
        AppPostValue ok = AppPostItem.getOkSocialPostValue(jsonObject, AppPostValue.Type.POLL, 122);
        ok.setSocialId(AppSocial.NAME_OK);
        ok.setSocialName(AppSocial.NAME_OK);
        Assert.assertEquals(ok.getSocialId(), AppSocial.ID_OK);
        Assert.assertEquals(ok.getSocialName(), AppSocial.NAME_OK);
        Assert.assertEquals(ok.getMaxSymbolsInPost(), AppPostValue.MAX_OK_POST_LENGTH);
        ok.setType(AppPostValue.Type.POLL);
        Assert.assertEquals(ok.getType(), AppPostValue.Type.POLL);
        Assert.assertNotEquals(true, ok.isForTask());

        Assert.assertEquals(255, AppPostValue.MAX_OK_POST_LENGTH);
        Assert.assertEquals(115, AppPostValue.MAX_TWEET_POST_LENGTH);
        Assert.assertEquals(255, ok.getMaxSymbolsInPost());
//        JSONObject testObj = ok.getOkAttachmentsJson();
//        Assert.assertNotNull(testObj);
//        Assert.assertNotNull(ok.prepareTwPost());
//        Assert.assertNotNull(ok.preparePost());


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
        Assert.assertEquals(ok.hasType(AppPostValue.Type.POLL), true);

        String warning = String.format(appContext.getString(R.string.warning_post_mutch_long), String.valueOf(ok.getMaxSymbolsInPost()));
        Assert.assertEquals(warning, ok.getWarningTitle(appContext));

        AppPostValue vk = AppPostItem.getVkSocialPostValue(jsonObject, AppPostValue.Type.ACHIEVEMENT, 222);
        Assert.assertEquals(ok.equals(vk), false);
        Assert.assertEquals(ok.equals(ok), true);

        Assert.assertNotNull(ok.prepareFbPost());
    }
}
