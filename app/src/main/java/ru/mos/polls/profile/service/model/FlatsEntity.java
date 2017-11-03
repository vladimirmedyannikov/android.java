package ru.mos.polls.profile.service.model;


import java.util.List;


/**
 * Created by Trunks on 20.07.2017.
 */


public class FlatsEntity {

    private RegistrationEntity registration;
    private WorkEntity work;
    private ResidenceEntity residence;
    private List<BaseFlat> own;

    public void setOwn(List<BaseFlat> own) {
        this.own = own;
    }

    public FlatsEntity(List<BaseFlat> own) {
        this.own = own;
    }

    public FlatsEntity(RegistrationEntity registration) {
        this.registration = registration;
    }

    public FlatsEntity(WorkEntity work) {
        this.work = work;
    }

    public FlatsEntity(ResidenceEntity residence) {
        this.residence = residence;
    }

    public RegistrationEntity getRegistration() {
        return registration;
    }

    public void setRegistration(RegistrationEntity registration) {
        this.registration = registration;
    }

    public ResidenceEntity getResidence() {
        return residence;
    }

    public void setResidence(ResidenceEntity residence) {
        this.residence = residence;
    }

    public WorkEntity getWork() {
        return work;
    }

    public void setWork(WorkEntity work) {
        this.work = work;
    }

    public static class RegistrationEntity extends BaseFlat {
        public RegistrationEntity(String building_id) {
            super(building_id);
        }

        public RegistrationEntity(String building_id, String building, String street, String area_id) {
            super(building_id, building, street, area_id);
        }
    }

    public static class WorkEntity extends BaseFlat {
        public WorkEntity(String building_id) {
            super(building_id);
        }

        public WorkEntity(String building_id, String building, String street, String area_id) {
            super(building_id, building, street, area_id);
        }

        public WorkEntity(String flat_id, String building_id) {
            super(flat_id, building_id);
        }
    }


    public static class ResidenceEntity extends BaseFlat {
        public ResidenceEntity(String building_id) {
            super(building_id);
        }

        public ResidenceEntity(String flat_id, String building_id) {
            super(flat_id, building_id);
        }

        public ResidenceEntity(String building_id, String building, String street, String area_id) {
            super(building_id, building, street, area_id);
        }

        public ResidenceEntity() {
        }
    }

    public static class BaseFlat {
        public BaseFlat(String building_id) {
            this.building_id = building_id;
        }

        public BaseFlat() {
        }

        public BaseFlat(String flat_id, String building_id) {
            this.flat_id = flat_id;
            this.building_id = building_id;
        }

        public BaseFlat(String flat_id, Boolean kill) {
            this.flat_id = flat_id;
            this.kill = kill;
        }

        public BaseFlat(String building_id, String building, String street, String area_id) {
            this.building_id = building_id;
            this.building = building;
            this.street = street;
            this.area_id = area_id;
        }

        /**
         * flat_id : 266574
         * building_id : 29419-2
         */

        private String flat_id;
        private String building_id;
        private String flat;
        private String building;
        private String street;
        private String city;
        private String area_id;
        private Boolean kill;

        public void setKill(Boolean kill) {
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

        public void setFlat(String flat) {
            this.flat = flat;
        }

        public void setBuilding(String building) {
            this.building = building;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setArea_id(String area_id) {
            this.area_id = area_id;
        }
    }
}

