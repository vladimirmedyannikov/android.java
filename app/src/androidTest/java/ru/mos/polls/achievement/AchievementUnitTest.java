package ru.mos.polls.achievement;


import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import ru.mos.elk.profile.Achievements;
import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.newprofile.vm.AchievementVM;
import ru.mos.polls.profile.model.Achievement;


public class AchievementUnitTest extends BaseUnitTest {

    @Test
    public void parse() {
        Achievement test = getTestObject();
        Assert.assertEquals(true, test.isNext());
        assertNotNullOrEmpty(test.getId());
        assertNotNullOrEmpty(test.getBody());
        assertNotNullOrEmpty(test.getDescription());
        assertNotNullOrEmpty(test.getTitle());
        assertNotNullOrEmpty(test.getImageUrl());
        Assert.assertEquals(false, test.isNeedHideTask());

    }

    @Test
    public void parseList() {
        List<Achievement> testList = Achievement.fromJsonArray(fromTestRawAsJsonArray("list_achievement.json"));
        junit.framework.Assert.assertNotNull(testList);
        junit.framework.Assert.assertEquals(2, testList.size());
        junit.framework.Assert.assertNotNull(testList.get(0));
        junit.framework.Assert.assertNotNull(testList.get(1));
    }

    @Test
    public void testUrl() {
        Achievement test = getTestObject();
        boolean isURL = test.getImageUrl().startsWith("http://") || test.getImageUrl().startsWith("https://");
        Assert.assertEquals(true, isURL);
        Assert.assertEquals("http://img0.joyreactor.cc/pics/post/full/warhammer-40000-фэндомы-orks-1793330.jpeg", test.getImageUrl());

    }

    private Achievement getTestObject() {
        return new Achievement(fromTestRawAsJson("achievement.json"));
    }

    @Test
    public void testAchievementVM() {
        Achievements achievements = (Achievements) mockObj("achievement.json", Achievements.class);
        Assert.assertNotNull(achievements);
        AchievementVM achievementVM = new AchievementVM(achievements);
        Assert.assertNotNull(achievementVM.getModel());
        Assert.assertEquals(achievements, achievementVM.getModel());
        Assert.assertNotNull(achievementVM.getBody());
        Assert.assertNotNull(achievementVM.getDescription());
        Assert.assertNotNull(achievementVM.getId());
        Assert.assertNotNull(achievementVM.getImageUrl());
        Assert.assertNotNull(achievementVM.getTitle());
        Assert.assertNotNull(achievementVM.getVariableId());
        Assert.assertNotNull(achievementVM.getViewType());
        Assert.assertEquals(true, achievementVM.isNext());
        Assert.assertEquals(false, achievementVM.isNeedHideTask());
    }
}
