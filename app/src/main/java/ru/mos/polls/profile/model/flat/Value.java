package ru.mos.polls.profile.model.flat;

import com.google.gson.annotations.SerializedName;

public class Value {
    private String value;
    private String label;
    private String territory;
    @SerializedName("area_id")
    String areaId;

    public String getAreaId() {
        return areaId;
    }

    public Value(String value, String label) {
        this(value, label, null);
    }

    public Value(String value, String label, String territory) {
        this.value = value;
        this.label = label;
        this.territory = territory;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTerritory() {
        return territory;
    }

    public void setTerritory(String territory) {
        this.territory = territory;
    }

    @Override
    public String toString() {
        return label;
    }


}
