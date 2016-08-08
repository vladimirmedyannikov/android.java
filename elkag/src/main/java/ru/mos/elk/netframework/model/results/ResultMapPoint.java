package ru.mos.elk.netframework.model.results;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Александр Свиридов
 *
 */
public class ResultMapPoint extends ResultText {
	private static final long serialVersionUID = -4266010712414678934L;
	
	private double longitude;
    private double latitude;

    public ResultMapPoint(String style) {
        super(ResultType.MAPPOINT, style);
    }

    @Override
    public void fill(JSONObject json) throws JSONException {
        super.fill(json);

        JSONObject data = json.optJSONObject("data");
        if (data != null) {
            latitude = data.optDouble("lat");
            longitude = data.optDouble("long");
        }
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return super.toString() + " coordinates: " + latitude + ", " + longitude;
    }
}
