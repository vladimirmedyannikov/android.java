package ru.mos.polls.variant;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.survey.variants.values.CharVariantValue;
import ru.mos.polls.survey.variants.values.DateVariantValue;
import ru.mos.polls.survey.variants.values.FloatVariantValue;
import ru.mos.polls.survey.variants.values.IntVariantValue;
import ru.mos.polls.survey.variants.values.VariantValue;

/**
 * Created by Trunks on 18.05.2017.
 */

public class VariantValueUnitTest extends BaseUnitTest {
    String title = "title";

    @Test
    public void intVariantValueTest() {

        IntVariantValue ivv1 = new IntVariantValue(1, 10);

        Assert.assertEquals(ivv1.isEmpty(), true);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(title, 10);
            ivv1.getValueFromJson(title, jsonObject);

        } catch (JSONException e) {
        }
        Assert.assertEquals(ivv1.isEmpty(), false);

        jsonObject = new JSONObject();
        IntVariantValue ivv2 = new IntVariantValue(1, 10);
        try {
            jsonObject.put(title, 9);
            ivv2.getValueFromJson(title, jsonObject);
        } catch (JSONException e) {
        }

        Assert.assertEquals(ivv1.compareTo(ivv2), 1);
        Assert.assertEquals(ivv2.compareTo(ivv1), -1);

        JSONObject putJsonObject = new JSONObject();
        try {
            ivv1.putValueInJson(title, putJsonObject);
        } catch (JSONException e) {
        }
        Assert.assertEquals(putJsonObject.has(title), true);
    }

    @Test
    public void floatVariantValueTest() {
        FloatVariantValue fvv1 = new FloatVariantValue(0.100, 0.200);
        Assert.assertEquals(fvv1.isEmpty(), true);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(title, 0.200);
            fvv1.getValueFromJson(title, jsonObject);

        } catch (JSONException e) {
        }
        Assert.assertEquals(fvv1.isEmpty(), false);

        jsonObject = new JSONObject();
        FloatVariantValue fvv2 = new FloatVariantValue(0.100, 0.200);
        try {
            jsonObject.put(title, 0.150);
            fvv2.getValueFromJson(title, jsonObject);
        } catch (JSONException e) {
        }

        Assert.assertEquals(fvv1.compareTo(fvv2), 1);
        Assert.assertEquals(fvv2.compareTo(fvv1), -1);

        JSONObject putJsonObject = new JSONObject();
        try {
            fvv1.putValueInJson(title, putJsonObject);
        } catch (JSONException e) {
        }
        Assert.assertEquals(putJsonObject.has(title), true);
    }

    @Test
    public void dateVariantValueTest() {
        String minDate = "01.01.2017";
        String maxDate = "31.01.2017";

        String date1 = "07.01.2017";
        String date2 = "06.01.2017";
        Date min = null;
        Date max = null;
        try {
            min = DateVariantValue.SDF.parse(minDate);
            max = DateVariantValue.SDF.parse(maxDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateVariantValue dvv1 = new DateVariantValue(min, max);

        Assert.assertEquals(dvv1.isEmpty(), true);
        Assert.assertEquals(dvv1.asString(), "null");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(title, date1);
            dvv1.getValueFromJson(title, jsonObject);
        } catch (JSONException e) {
        }
        Assert.assertEquals(dvv1.isEmpty(), false);
        Assert.assertEquals(dvv1.asString(), date1);

        DateVariantValue dvv2 = new DateVariantValue(min, max);

        jsonObject = new JSONObject();
        try {
            jsonObject.put(title, date2);
            dvv2.getValueFromJson(title, jsonObject);
        } catch (JSONException e) {
        }

        Assert.assertEquals(dvv1.compareTo(dvv2), 1);
        Assert.assertEquals(dvv2.compareTo(dvv1), -1);

        JSONObject putJsonObject = new JSONObject();
        try {
            dvv1.putValueInJson(title, putJsonObject);
        } catch (JSONException e) {
        }
        Assert.assertEquals(putJsonObject.has(title), true);
    }

    @Test
    public void charVariantValueTest() {
        String hint = "hint";
        String kind = "input";
        VariantValue.Factory factory = new CharVariantValue.Factory();
        JSONObject minMaxObject = new JSONObject();

        JSONObject constrainsJsonObject = new JSONObject();
        try {
            minMaxObject.put("min", 1);
            minMaxObject.put("max", 30);
            constrainsJsonObject.put("constraints", minMaxObject);
        } catch (JSONException e) {
        }
        CharVariantValue cvv1 = (CharVariantValue) factory.create(hint, "kind", constrainsJsonObject);

        Assert.assertEquals(cvv1.isEmpty(), true);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(title, "text");
            cvv1.getValueFromJson(title, jsonObject);
        } catch (JSONException e) {
        }
        Assert.assertEquals(cvv1.isEmpty(), false);

        CharVariantValue cvv2 = (CharVariantValue) factory.create(hint, kind, new JSONObject());
        jsonObject = new JSONObject();
        try {
            jsonObject.put(title, "text");
            cvv2.getValueFromJson(title, jsonObject);
        } catch (JSONException e) {
        }

        Assert.assertEquals(cvv1.compareTo(cvv2), 0);

        jsonObject = new JSONObject();
        try {
            jsonObject.put(title, "text1");
            cvv2.getValueFromJson(title, jsonObject);
        } catch (JSONException e) {
        }

        Assert.assertEquals(cvv1.compareTo(cvv2), -1);
        Assert.assertEquals(cvv2.compareTo(cvv1), 1);

        JSONObject putJsonObject = new JSONObject();
        try {
            cvv1.putValueInJson(title, putJsonObject);
        } catch (JSONException e) {
        }
        Assert.assertEquals(putJsonObject.has(title), true);
    }
}
