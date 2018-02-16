package ru.mos.polls.profile.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.R;

/**
 * Класс для ответа по запросу Округа или Района
 *
 * @since 2.0.0
 */
public class Reference {

    public static ArrayAdapter getAdapter(Context context, List<Reference> content) {
        return new Adapter(context, content);
    }

    private String value;
    private String label;

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label.replace("?", "");
    }

    public Reference(JSONObject jsonObject) {
        value = jsonObject.optString("value");
        label = jsonObject.optString("label");
        label = label.replace("?", "");
    }

    public static List<Reference> fromJsonArray(JSONArray jsonArray) {
        List<Reference> result = null;
        if (jsonArray != null) {
            result = new ArrayList<Reference>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                Reference reference = new Reference(jsonObject);
                result.add(reference);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public static class Adapter extends ArrayAdapter<Reference> {

        public Adapter(Context context, List<Reference> objects) {
            super(context, R.layout.layout_spinner_view, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.layout_spinner_view, null);
            }
            ((TextView) convertView).setText(getItem(position).getLabel());
            return convertView;
        }
    }
}
