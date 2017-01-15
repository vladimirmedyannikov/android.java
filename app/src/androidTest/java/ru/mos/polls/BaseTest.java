package ru.mos.polls;

import android.support.test.InstrumentationRegistry;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 */

public class BaseTest {

    protected JSONObject fromTestRawAsJson(String fileName) {
        JSONObject result = null;
        try {
            InputStream is = InstrumentationRegistry.getContext().getAssets().open(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                fileContent.append(line).append('\n');
            }
            result = new JSONObject(fileContent.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected void assertNotNullOrEmpty(String value) {
        Assert.assertNotNull(value);
        Assert.assertNotEquals("", value);
    }
}
