package ru.mos.polls.social;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import ru.mos.polls.BaseUnitTest;

/**
 * Created by Trunks on 11.04.2017.
 */

public class SocialManagerUnitTest extends BaseUnitTest {

    public static final String TEST_TOKEN = "testToken";
    public static final String TEST_OAUTHTEST = "Oauthtest";
    public static final String TEST_REFRESH_TOKKEN = "test_refresh_tokken";
    public static final String TEST_OAUTH_SESSION_TOKKEN = "oauth_session_tokken";
    public static final String TEST_OAUTH_OK_SECRET_TOKKEN = "oauth_ok_secret_tokken";
    public static final String TEST_VK_ID = "test_vk_id";
    public static final long TEST_EXPIRED = -1L;
    public static final String TEST_AVATAR_URL = "test_avatar_url";
    public static final String TEST_GPNAME = "test_gpname";
    public static final String TEST_VK_KEY = "VK";
    Context appContext = InstrumentationRegistry.getTargetContext();

//    @Test
//    public void getTokken() {
//
//
//        SocialManager.setAccessToken(appContext, AppSocial.ID_VK, TEST_TOKEN);
//        Assert.assertEquals(SocialManager.getAccessToken(appContext, AppSocial.ID_VK), TEST_TOKEN);
//
//        SocialManager.setRefreshToken(appContext, AppSocial.ID_VK, TEST_REFRESH_TOKKEN);
//        Assert.assertEquals(SocialManager.getRefreshToken(appContext, AppSocial.ID_VK), TEST_REFRESH_TOKKEN);
//
//        SocialManager.setOauthOkToken(appContext, TEST_OAUTHTEST);
//        Assert.assertEquals(SocialManager.getOauthOkToken(appContext), TEST_OAUTHTEST);
//
//        SocialManager.setOauthOkSessionSecretToken(appContext, TEST_OAUTH_OK_SECRET_TOKKEN);
//        Assert.assertEquals(SocialManager.getOauthOkSessionSecretToken(appContext), TEST_OAUTH_OK_SECRET_TOKKEN);
//        Assert.assertEquals(SocialManager.isOauthOkTokensExists(appContext), true);
//
//        SocialManager.setOauthOkSessionSecretToken(appContext, TEST_OAUTH_SESSION_TOKKEN);
//        Assert.assertEquals(SocialManager.getOauthOkSessionSecretToken(appContext), TEST_OAUTH_SESSION_TOKKEN);
//
//    }
//
//    @Test
//    public void setLogon() {
//        SocialManager.setLogon(appContext, AppSocial.ID_VK, false);
//        Assert.assertEquals(SocialManager.isLogon(appContext, AppSocial.ID_VK), false);
//        SocialManager.setLogon(appContext, AppSocial.ID_VK, true);
//        Assert.assertEquals(SocialManager.isLogon(appContext, AppSocial.ID_VK), true);
//    }
//
//    @Test
//    public void removeOkTokken() {
//        SocialManager.setOauthOkToken(appContext, TEST_OAUTHTEST);
//        Assert.assertEquals(SocialManager.getOauthOkToken(appContext), TEST_OAUTHTEST);
//        SocialManager.setOauthOkSessionSecretToken(appContext, TEST_OAUTH_OK_SECRET_TOKKEN);
//        Assert.assertEquals(SocialManager.getOauthOkSessionSecretToken(appContext), TEST_OAUTH_OK_SECRET_TOKKEN);
//        SocialManager.clearOkOauthToken(appContext);
//        Assert.assertEquals(SocialManager.getOauthOkToken(appContext), "");
//        Assert.assertEquals(SocialManager.getOauthOkSessionSecretToken(appContext), "");
//    }
//
//    @Test
//    public void testVkId() {
//        SocialManager.setUserIdVk(appContext, TEST_VK_ID);
//        Assert.assertEquals(SocialManager.getUserIdVk(appContext), TEST_VK_ID);
//    }
//
//    @Test
//    public void setGetExpired() {
//        SocialManager.setExpired(appContext, AppSocial.ID_TW, TEST_EXPIRED);
//        Assert.assertEquals(SocialManager.getExpired(appContext, AppSocial.ID_TW), TEST_EXPIRED);
//        Assert.assertEquals(SocialManager.isExpired(appContext, AppSocial.ID_TW), false);
//    }
//
//    @Test
//    public void setGetAvatarUrl() {
//        SocialManager.setAvatarUrl(appContext, AppSocial.ID_TW, TEST_AVATAR_URL);
//        Assert.assertEquals(SocialManager.getAvatarUrl(appContext, AppSocial.ID_TW), TEST_AVATAR_URL);
//        Assert.assertEquals(SocialManager.getAvatarName(AppSocial.ID_TW), SocialManager.AVATAR_URL_TW);
//
//    }
//
//    @Test
//    public void setGetGP() {
//        SocialManager.setGpAccountName(appContext, TEST_GPNAME);
//        Assert.assertEquals(SocialManager.getGpAccountName(appContext), TEST_GPNAME);
//    }
//
//    @Test
//    public void clearAuth() {
//        SocialManager.setRefreshToken(appContext, AppSocial.ID_FB, TEST_REFRESH_TOKKEN);
//        Assert.assertEquals(SocialManager.getRefreshToken(appContext, AppSocial.ID_FB), TEST_REFRESH_TOKKEN);
//        SocialManager.clearAuth(appContext, AppSocial.ID_FB);
//        Assert.assertEquals(SocialManager.getRefreshToken(appContext, AppSocial.ID_FB), null);
//
//        SocialManager.clearAll(appContext);
//        Assert.assertNull(SocialManager.getAccessToken(appContext, AppSocial.ID_VK));
//    }
//
//
//    @Test
//    public void getKeys() {
//        Assert.assertEquals(SocialManager.getAccessTokenKey(AppSocial.ID_VK), SocialManager.VK_ACCESS_TOKEN);
//        Assert.assertEquals(SocialManager.getLogonKey(AppSocial.ID_VK), SocialManager.VK_IS_LOGON);
//        Assert.assertEquals(SocialManager.getStatisticsKey(AppSocial.ID_VK), TEST_VK_KEY);
//        Assert.assertEquals(SocialManager.getRefreshTokenKey(AppSocial.ID_VK), SocialManager.VK_REFRESH_TOKEN);
//        Assert.assertEquals(SocialManager.getExpireDateKey(AppSocial.ID_VK), SocialManager.VK_EXPIRE_TIME);
//        Assert.assertEquals(SocialManager.getSocialName(AppSocial.ID_VK), AppSocial.NAME_VK);
//        Assert.assertEquals(SocialManager.getId(AppSocial.NAME_VK), AppSocial.ID_VK);
//    }
}
