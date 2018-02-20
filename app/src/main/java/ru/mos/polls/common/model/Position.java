package ru.mos.polls.common.model;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

import ru.mos.polls.R;

/**
 * Хранение географических координат
 * @since 1.7
 */
public class Position implements Serializable {
    @SerializedName("lat")
    private double lat;
    @SerializedName("long")
    private double lon;
    private String name;
    private String address;

    public Position() {
    }

    public Position(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
        }
    }

    public Position(JSONObject positionJson) {
        lat = positionJson.optDouble("lat");
        lon = positionJson.optDouble("long");
        name = positionJson.optString("name");
        address = positionJson.optString("address");
    }

    public Position(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getAddress() {
        return address == null ? "" : address;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public JSONObject asJson() {
        JSONObject positionJson = new JSONObject();
        try {
            positionJson.put("lat", lat);
            positionJson.put("long", lon);
        } catch (JSONException ignored) {
        }

        return positionJson;
    }

    public JSONObject asJsonMyPosition() {
        JSONObject positionJson = new JSONObject();
        try {
            positionJson.put("my_lat", lat);
            positionJson.put("my_lng", lon);
        } catch (JSONException ignored) {
        }

        return positionJson;
    }

    public boolean isEmpty() {
        return lat == 0 && lon == 0;
    }

    /**
     * Открыть приложение с картой дял просмотра местоположения
     * @param context
     */
    public void goToMap(Context context, String description) {
        String geo = String.format(Locale.ENGLISH, "geo:%f,%f", lat, lon);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geo));
        try {
            context.startActivity(intent);
        } catch (Exception ignored) {
            Toast.makeText(context, context.getString(R.string.error_open_map), Toast.LENGTH_SHORT).show();
        }
    }

    public void goToGoogleMapsOnly(Context context, String description) {
        String geo = String.format(Locale.ENGLISH, "geo:%f,%f", lat, lon);
        String query = String.format(Locale.ENGLISH, "%f,%f(%s)", lat, lon, description);
        String result = geo + "?q=" + Uri.encode(query);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        try {
            context.startActivity(intent);
        } catch (Exception ignored) {
            Toast.makeText(context, context.getString(R.string.error_open_map), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Расчет расстояния между двумя георграфическими точками в метрах
     * http://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
     *
     * @param begin - начальная точка
     * @param end - конечная точка
     * @return - расстояние, окргуленное до целого значения
     */
    public static int distance(Position begin, Position end) {
        double result = Math.sin(Math.toRadians(begin.getLat())) * Math.sin(Math.toRadians(end.getLat()))
                + Math.cos(Math.toRadians(begin.getLat())) * Math.cos(Math.toRadians(end.getLat()))
                * Math.cos(Math.toRadians(begin.getLon() - end.getLon()));
        result = Math.acos(result);
        result = Math.toDegrees(result);
        result = result * 60 * 1.1515;
        result = result * 1.609344 * 1000; // *1000 - перевод км в м
        return Math.abs((int) result);
    }
}
