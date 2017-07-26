package ru.mos.polls.newprofile.service.model;



import ru.mos.elk.profile.flat.Flat;

/**
 * Created by Trunks on 20.07.2017.
 */


public class FlatsEntity {

    private RegistrationEntity registration;
    private WorkEntity work;
    private ResidenceEntity residence;

    public FlatsEntity(Flat flat, boolean update) {
        if (flat.isResidence()) {
            setResidence(new FlatsEntity.ResidenceEntity(flat, update));
        }
        if (flat.isRegistration()) {
            setRegistration(new FlatsEntity.RegistrationEntity(flat, update));
        }
        if (flat.isWork()) {
            setWork(new FlatsEntity.WorkEntity(flat, update));
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
        public RegistrationEntity(Flat flat, boolean update) {
            super(flat, update);
        }
    }

    public class WorkEntity extends BaseFlat {
        public WorkEntity(Flat flat, boolean update) {
            super(flat, update);
        }
    }


    public class ResidenceEntity extends BaseFlat {
        public ResidenceEntity(Flat flat, boolean update) {
            super(flat, update);
        }
    }

    abstract class BaseFlat {
        /**
         * flat_id : 266574
         * building_id : 29419-2
         */
        public BaseFlat(Flat flat, boolean update) {
            setBuilding_id(flat.getBuildingId());
            if (update) setFlat_id(flat.getFlatId());
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

