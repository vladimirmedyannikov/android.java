package ru.mos.polls;

import android.support.test.InstrumentationRegistry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Использовать для получения солержимого файлов заглушек из assets<br/>
 * {@link #fromTestRawAsJson(String)}<br/>
 * {@link #fromTestRawAsJsonArray(String)}
 */

public class BaseUnitTest {

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

    protected JSONArray fromTestRawAsJsonArray(String fileName) {
        JSONArray result = null;
        try {
            InputStream is = InstrumentationRegistry.getContext().getAssets().open(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                fileContent.append(line).append('\n');
            }
            result = new JSONArray(fileContent.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected void assertNotNullOrEmpty(String value) {
        Assert.assertNotNull(value);
        Assert.assertNotEquals("", value);
    }

    protected <T> List<T> mockList(String fileName) {
        Gson gson = new Gson();
        List<T> content = gson.fromJson(
                fromTestRawAsJsonArray(fileName).toString(),
                new TypeToken<List<T>>() {
                }.getType()
        );
        return content;
    }

    protected <T> T mockObj(String fileName, Class<T> clazz) {
        Gson gson = new Gson();
        T content = gson.fromJson(
                fromTestRawAsJson(fileName).toString(),
                clazz
        );
        return content;
    }
}
