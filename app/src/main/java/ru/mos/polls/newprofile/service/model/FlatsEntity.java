package ru.mos.polls.newprofile.service.model;


import ru.mos.elk.profile.flat.Flat;

/**
 * Created by Trunks on 20.07.2017.
 */


public class FlatsEntity {

    private RegistrationEntity registration;
    private WorkEntity work;
    private ResidenceEntity residence;

    public FlatsEntity(Flat flat) {
        if (flat.isResidence()) {
            setResidence(new FlatsEntity.ResidenceEntity(flat));
        }
        if (flat.isRegistration()) {
            setRegistration(new FlatsEntity.RegistrationEntity(flat));
        }
        if (flat.isWork()) {
            setWork(new FlatsEntity.WorkEntity(flat));
        }
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

    public class RegistrationEntity extends BaseFlat {
        public RegistrationEntity(Flat flat) {
            super(flat);
        }
    }

    public class WorkEntity extends BaseFlat {
        public WorkEntity(Flat flat) {
            super(flat);
        }
    }


    public class ResidenceEntity extends BaseFlat {
        public ResidenceEntity(Flat flat) {
            super(flat);
        }
    }

    abstract class BaseFlat {
        /**
         * flat_id : 266574
         * building_id : 29419-2
         */
        public BaseFlat(Flat flat) {
            setBuilding_id(flat.getBuildingId());
            setFlat_id(flat.getFlatId());
        }

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

