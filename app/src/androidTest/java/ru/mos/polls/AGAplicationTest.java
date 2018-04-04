package ru.mos.polls;

import com.google.android.gms.analytics.Tracker;

import org.junit.Assert;
import org.junit.Test;

public class AGAplicationTest extends BaseUnitTest {

    @Test
    public void versionTrackerTest() {
        Tracker tracker = AGApplication.getTracker();
        Assert.assertEquals(BuildConfig.VERSION_NAME, tracker.get("&av"));
    }
}
