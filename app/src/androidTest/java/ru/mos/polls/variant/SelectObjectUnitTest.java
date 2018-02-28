package ru.mos.polls.variant;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.survey.variants.select.GorodSelectActivity;
import ru.mos.polls.survey.variants.select.GorodSelectObject;
import ru.mos.polls.survey.variants.select.ServiceSelectActivity;
import ru.mos.polls.survey.variants.select.ServiceSelectObject;

public class SelectObjectUnitTest extends BaseUnitTest {

    @Test
    public void gorodSelectObjectTest() {

        String address = "address";
        String objectId = "object_id";
        String testAdd = "test_address";
        String testId = "test_id";

        GorodSelectObject gso = new GorodSelectObject(1, 1, "uid", "cat", "title", "test", "parent");

        Assert.assertEquals(gso.isSelected(), false);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(address, testAdd);
            jsonObject.put(objectId, testId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        gso.loadAnswerJson(jsonObject);


        Assert.assertEquals(gso.isSelected(), true);

        Assert.assertEquals(gso.getObjectId(), testId);
        Assert.assertEquals(gso.asString(), testAdd);

        JSONObject putJSON = new JSONObject();

        try {
            gso.processAnswerJson(putJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(putJSON.has(address), true);
        Assert.assertEquals(putJSON.has(objectId), true);

        GorodSelectObject gso2 = new GorodSelectObject(1, 1, "uid", "cat", "title", "test", "parent");
        Assert.assertEquals(gso2.isSelected(), false);

        Intent intent = new Intent();
        intent.putExtra(GorodSelectActivity.EXTRA_ADDRESS, testAdd);
        intent.putExtra(GorodSelectActivity.EXTRA_OBJECT_ID, testId);

        gso2.loadFromIntent(intent);
        Assert.assertEquals(gso2.isSelected(), true);

        Assert.assertEquals(gso2.getObjectId(), testId);
        Assert.assertEquals(gso2.asString(), testAdd);
    }


    @Test
    public void serviceSelectObjectTest() {
        String id = "id";
        String title = "title";
        String description = "description";

        long testID = 2;
        String testTitle = "test_title";
        String testDesc = "test_description";
        ServiceSelectObject sso = new ServiceSelectObject(1, 1, "uid", "cat", "title", "test", "parent");
        Assert.assertEquals(sso.isSelected(), false);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(id, testID);
            jsonObject.put(title, testTitle);
            jsonObject.put(description, testDesc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sso.loadAnswerJson(jsonObject);

        Assert.assertEquals(sso.isSelected(), true);


        Assert.assertEquals(sso.getTitle(), testTitle);
        Assert.assertEquals(sso.asString(), testTitle);
        Assert.assertEquals(sso.getId(), testID);


        JSONObject putJSON = new JSONObject();

        try {
            sso.processAnswerJson(putJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(putJSON.has(id), true);
        Assert.assertEquals(putJSON.has(title), true);
        Assert.assertEquals(putJSON.has(description), true);

        ServiceSelectObject sso1 = new ServiceSelectObject(1, 1, "uid", "cat", "title", "test", "parent");

        Intent intent = new Intent();
        intent.putExtra(ServiceSelectActivity.EXTRA_ID, testID);
        intent.putExtra(ServiceSelectActivity.EXTRA_TITLE, testTitle);
        intent.putExtra(ServiceSelectActivity.EXTRA_DESCRIPTION, testDesc);
        Assert.assertEquals(sso1.isSelected(), false);

        sso1.loadFromIntent(intent);

        Assert.assertEquals(sso1.isSelected(), true);
        Assert.assertEquals(sso1.getTitle(), testTitle);
        Assert.assertEquals(sso1.asString(), testTitle);
        Assert.assertEquals(sso1.getId(), testID);
    }
}
