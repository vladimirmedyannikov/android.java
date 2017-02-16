package ru.mos.polls.geotarget.manager;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.geotarget.model.Area;

/**
 * @since 2.3.0
 */

public class PrefsAreasManager implements AreasManager {
    private static final String PREFS = "areas_manager_prefs";
    private static final String AREAS = "areas";

    private SharedPreferences prefs;

    public PrefsAreasManager(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void save(List<Area> areas) {
        prefs.edit()
                .putString(AREAS, Area.asJSONArray(areas).toString())
                .apply();
    }

    public List<Area> get() {
        List<Area> result = new ArrayList<>();
        String areasString = prefs.getString(AREAS, "");
        try {
            JSONArray areasJsonArray = new JSONArray(areasString);
            result = Area.from(areasJsonArray);
        } catch (JSONException ignored) {
        }
        return result;
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
