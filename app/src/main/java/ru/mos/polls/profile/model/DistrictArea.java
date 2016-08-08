package ru.mos.polls.profile.model;

import org.json.JSONObject;

/**
 * Класс для ответа по запросу по запросу area_id
 *
 * @since 2.0.0
 */
public class DistrictArea {
    private String district;
    private String area;

    public DistrictArea(JSONObject jsonObject) {
        district = jsonObject.optString("district_label");
        area = jsonObject.optString("area_label");
    }

    public String getDistrict() {
        return district;
    }

    public String getArea() {
        return area;
    }
}
