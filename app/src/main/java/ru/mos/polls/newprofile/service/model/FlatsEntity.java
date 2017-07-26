package ru.mos.polls.newprofile.service.model;



/**
 * Created by Trunks on 20.07.2017.
 */


public class FlatsEntity {

    private RegistrationEntity registration;
    private WorkEntity work;
    private ResidenceEntity residence;

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

    }

    public static class WorkEntity extends BaseFlat {
        public WorkEntity(String building_id) {
            super(building_id);
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

    }

     static abstract class BaseFlat {
        public BaseFlat(String building_id) {
            this.building_id = building_id;
        }

        public BaseFlat(String flat_id, String building_id) {
            this.flat_id = flat_id;
            this.building_id = building_id;
        }

        /**
         * flat_id : 266574
         * building_id : 29419-2
         */

        private String flat_id;
        private String building_id;

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
    }
}

