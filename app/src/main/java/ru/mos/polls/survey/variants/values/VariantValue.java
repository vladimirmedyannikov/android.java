package ru.mos.polls.survey.variants.values;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public interface VariantValue extends Comparable<VariantValue> {

    void setTitle(String s);

    boolean isEmpty();

    String asString();

    void showEditor(Context context, final Listener listener);

    /**
     * Записывает в jsonObject своё значение с заголовком title.
     *
     * @param title
     * @param jsonObject
     * @throws JSONException
     */
    void putValueInJson(String title, JSONObject jsonObject) throws JSONException;

    void getValueFromJson(String title, JSONObject jsonObject) throws JSONException;

    public interface Listener {

        void onEdited();

        void onCancel();

    }

    public static abstract class Factory {

        public VariantValue create(String hint, String kind, JSONObject jsonObject) {
            final JSONObject constrainsJsonObject = jsonObject.optJSONObject("constraints");
            return onCreate(hint, kind, constrainsJsonObject);
        }

        protected abstract VariantValue onCreate(String hint, String kind, JSONObject constrainsJsonObject);

    }

}
