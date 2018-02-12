package ru.mos.polls.profile.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Структура данных для хранения профессии
 * @since 1.9
 */
public class AgSocialStatus implements Serializable {
    public static final String NOTHING_SELECT_TEXT = "Не указан";

    private static final String PREFS = "ag_social_status_prefs";
    private static final String SOCIAL_STATUSES = "social_statuses";

    private int id;
    private String title;

    public AgSocialStatus(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public static List<AgSocialStatus> fromJson(JSONObject json) {
        List<AgSocialStatus> result = new ArrayList<AgSocialStatus>();
        if (json != null) {
            json = json.optJSONObject("professions_catalog");
            if (json != null) {
                JSONArray array = json.optJSONArray(SOCIAL_STATUSES);
                result = getAgSocialStatuses(array);
            }
        }
        return result;
    }

    public static List<AgSocialStatus> fromPreferences(Context context) {
        List<AgSocialStatus> result = new ArrayList<AgSocialStatus>();
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        try {
            JSONArray array = new JSONArray(prefs.getString(SOCIAL_STATUSES, ""));
            result = getAgSocialStatuses(array);
            result.add(0, new AgSocialStatus(0, NOTHING_SELECT_TEXT));
        } catch (JSONException ignored) {
        }
        return result;
    }

    public static void save(Context context, JSONObject json) {
        if (json != null) {
            json = json.optJSONObject("professions_catalog");
            if (json != null) {
                JSONArray array = json.optJSONArray(SOCIAL_STATUSES);
                SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                prefs.edit().putString(SOCIAL_STATUSES, array.toString()).apply();
            }
        }
    }

    public static ArrayAdapter getAdapter(Context context) {
        List<AgSocialStatus> agSocialStatuses = fromPreferences(context);
        Adapter result = new Adapter(context, agSocialStatuses);
        result.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return result;
    }

    public AgSocialStatus(JSONObject socialStatusJson) {
        if (socialStatusJson != null) {
            id = socialStatusJson.optInt("id");
            title = socialStatusJson.optString("title");
        }
    }

    public AgSocialStatus(Context context, int id) {
        List<AgSocialStatus> socialStatuses = fromPreferences(context);
        if (socialStatuses != null) {
            for (int i = 0; i < socialStatuses.size(); ++i) {
                if (id == socialStatuses.get(i).id) {
                    this.id = socialStatuses.get(i).id;
                    this.title = socialStatuses.get(i).title;
                    break;
                }
            }
        }
    }

    @Override
    public String toString() {
        return getTitle();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    private static List<AgSocialStatus> getAgSocialStatuses(JSONArray array) {
        List<AgSocialStatus> result = new ArrayList<AgSocialStatus>();
        if (array != null) {
            for (int i = 0; i < array.length(); ++i) {
                AgSocialStatus agSocialStatus = new AgSocialStatus(array.optJSONObject(i));
                result.add(agSocialStatus);
            }
        }
        return result;
    }

    public static class Adapter extends ArrayAdapter<AgSocialStatus> {

        public Adapter(Context context, List<AgSocialStatus> objects) {
            super(context, -1, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(),android.R.layout.simple_spinner_item, null);
            }
            AgSocialStatus socialStatus = getItem(position);
            displayTitle(convertView, socialStatus);
            return convertView;
        }

        private void displayTitle(View v, AgSocialStatus socialStatus) {
            TextView title = (TextView) v.findViewById(android.R.id.text1);
            title.setText(socialStatus.getTitle());
        }
    }
}
