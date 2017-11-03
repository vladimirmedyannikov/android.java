package ru.mos.polls.profile.service.model;

/**
 * Created by Trunks on 25.10.2017.
 */

public class AddressProperty {

    /**
     * kill : true
     * flat_id : 1
     */

    private Boolean kill;
    private String flat_id;
    /**
     * building_id : userid
     * flat : новая квартира
     * building : string
     * street : string
     * area_id : идентификатор района, в котором находится квартира
     */

    private String building_id;
    private String flat;
    private String building;
    private String street;
    private String area_id;

    public boolean isKill() {
        return kill;
    }

    public void setKill(boolean kill) {
        this.kill = kill;
    }

    public String getFlat_id() {
        return flat_id;
    }

    public void setFlat_id(String flat_id) {
        this.flat_id = flat_id;
    }


    public String getBuilding_id() {
        return building_id;
    }

    public void setBuilding_id(String building_id) {
        this.building_id = building_id;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }
}
