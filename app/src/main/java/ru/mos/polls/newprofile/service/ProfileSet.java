package ru.mos.polls.newprofile.service;


import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.newprofile.service.model.FlatsEntity;
import ru.mos.polls.newprofile.service.model.Personal;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * Created by wlTrunks on 14.07.2017.
 */

public class ProfileSet extends AuthRequest {

    public static class Request extends AuthRequest {

        Personal personal;
        FlatsEntity flats;

        public Request(FlatsEntity flats) {
            this.flats = flats;
        }

        public Request(Personal personal) {
            this.personal = personal;
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            private FlatsEntity flats;
            private StatusEntity status;

            public FlatsEntity getFlats() {
                return flats;
            }

            public void setFlats(FlatsEntity flats) {
                this.flats = flats;
            }

            public StatusEntity getStatus() {
                return status;
            }

            public void setStatus(StatusEntity status) {
                this.status = status;
            }


            public static class FlatsEntity {
                /**
                 * registration : {"flat":null,"street":"Краснодонская ул.","building":"д. 2, корпус 1","area":"Люблино","district":"Юго-Восточный","city":null,"flat_id":266623,"editing_blocked":false,"building_id":"11552-1"}
                 */

                private RegistrationEntity registration;
                private WorkEntity work;
                private ResidenceEntity residence;

                public void setWork(WorkEntity work) {
                    this.work = work;
                }

                public void setResidence(ResidenceEntity residence) {
                    this.residence = residence;
                }

                public RegistrationEntity getRegistration() {
                    return registration;
                }

                public void setRegistration(RegistrationEntity registration) {
                    this.registration = registration;
                }

                public WorkEntity getWork() {
                    return work;
                }

                public ResidenceEntity getResidence() {
                    return residence;
                }

                public static class RegistrationEntity extends Flat {

                }

                public static class WorkEntity extends Flat {
                }

                public static class ResidenceEntity extends Flat {

                }

            }

            public static class StatusEntity {
                /**
                 * burnt_points : 0
                 * spent_points : 0
                 * all_points : 20
                 * current_points : 20
                 */

                private int burnt_points;
                private int spent_points;
                private int all_points;
                private int current_points;

                public int getBurnt_points() {
                    return burnt_points;
                }

                public void setBurnt_points(int burnt_points) {
                    this.burnt_points = burnt_points;
                }

                public int getSpent_points() {
                    return spent_points;
                }

                public void setSpent_points(int spent_points) {
                    this.spent_points = spent_points;
                }

                public int getAll_points() {
                    return all_points;
                }

                public void setAll_points(int all_points) {
                    this.all_points = all_points;
                }

                public int getCurrent_points() {
                    return current_points;
                }

                public void setCurrent_points(int current_points) {
                    this.current_points = current_points;
                }
            }

        }
    }
}
