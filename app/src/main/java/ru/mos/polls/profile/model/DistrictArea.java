package ru.mos.polls.profile.model;


import org.json.JSONObject;

/**
 * Класс для ответа по запросу по запросу area_id
 *
 * @since 2.0.0
 */
public class DistrictArea {
    public String district;
    public String area;


    public DistrictArea(String district, String area) {
        this.district = district;
        this.area = area;
    }

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
