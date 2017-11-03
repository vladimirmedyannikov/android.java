package ru.mos.polls.profile.service.model;

import java.util.List;

/**
 * Created by Trunks on 18.07.2017.
 */

public class GetProfileResponse {

    /**
     * errorCode : 0
     * errorMessage :
     * result : {"common":{"email_confirmed":false,"is_pgu_connected":false,"is_profile_visible":true},"personal":{"surname":"фамилия","firstname":"имя","middlename":"отчество","birthday":"дата рождения, dd.mm.yyyy","sex":"male|female|null","email":"email","phone":"телефон","marital_status":"married|single|null","childrens_count":3,"childrens_birthdays":["dd.mm.yyyy","dd.mm.yyyy"],"car_exists":false,"social_status":1,"troika_card_number":"1000000000","registration_date":"дата регистрации в сервисе, dd.mm.yyyy","avatar":"/image/1.jpg"},"flats":{"registration":{"flat_id":"123","building_id":234,"flat":"string","building":"string","street":"string","district":"string","area":"string","editing_blocked":true},"work":{"flat_id":"123","building_id":234,"flat":"string","building":"string","street":"string","district":"string","area":"string","editing_blocked":false}},"statistics":{"status":"Новичок","rating":5408,"percent_fill_profile":95,"params":[{"title":"Заполненность профиля","value":"95%"},{"title":"Пройдено голосований","value":"257"},{"title":"Оценено новинок","value":"17"},{"title":"Посещено мероприятий","value":"5"},{"title":"Приглашено друзей","value":"0"},{"title":"Активность в социальных сетях","value":"0"},{"title":"Получено баллов","value":"0"},{"title":"Потрачено баллов","value":"0"}]},"professions_catalog":{"social_statuses":[{"id":1,"title":"Служащий"},{"id":2,"title":"Учащийся"},{"id":3,"title":"Студент"},{"id":4,"title":"Предприниматель"}]}}
     * achievements : {"count":10,"last":[{"id":"2015-07-01","img_url":"http://img0.joyreactor.cc/pics/post/full/warhammer-40000-фэндомы-orks-1793330.jpeg","title":"Герой. Просто Герой.","is_next":false,"description":"следующее достижение"},{"id":"2015-06-01","img_url":"http://cs623727.vk.me/v623727792/103db/u9hg8pDCc7w.jpg","title":"Активный гражданин месяца","is_next":false,"description":"получено 11.11.11"},{"id":"2015-05-01","img_url":"http://cs623727.vk.me/v623727792/103e2/OebzxL0Mjf4.jpg","title":"ГиперАктивный гражданин месяца","is_next":false,"description":"получено 12.12.12"}]}
     * session_id :
     */

    private int errorCode;
    private String errorMessage;
    private ResultEntity result;
    private AchievementsEntity achievements;
    private String session_id;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ResultEntity getResult() {
        return result;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public AchievementsEntity getAchievements() {
        return achievements;
    }

    public void setAchievements(AchievementsEntity achievements) {
        this.achievements = achievements;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public static class ResultEntity {
        /**
         * common : {"email_confirmed":false,"is_pgu_connected":false,"is_profile_visible":true}
         * personal : {"surname":"фамилия","firstname":"имя","middlename":"отчество","birthday":"дата рождения, dd.mm.yyyy","sex":"male|female|null","email":"email","phone":"телефон","marital_status":"married|single|null","childrens_count":3,"childrens_birthdays":["dd.mm.yyyy","dd.mm.yyyy"],"car_exists":false,"social_status":1,"troika_card_number":"1000000000","registration_date":"дата регистрации в сервисе, dd.mm.yyyy","avatar":"/image/1.jpg"}
         * flats : {"registration":{"flat_id":"123","building_id":234,"flat":"string","building":"string","street":"string","district":"string","area":"string","editing_blocked":true},"work":{"flat_id":"123","building_id":234,"flat":"string","building":"string","street":"string","district":"string","area":"string","editing_blocked":false}}
         * statistics : {"status":"Новичок","rating":5408,"percent_fill_profile":95,"params":[{"title":"Заполненность профиля","value":"95%"},{"title":"Пройдено голосований","value":"257"},{"title":"Оценено новинок","value":"17"},{"title":"Посещено мероприятий","value":"5"},{"title":"Приглашено друзей","value":"0"},{"title":"Активность в социальных сетях","value":"0"},{"title":"Получено баллов","value":"0"},{"title":"Потрачено баллов","value":"0"}]}
         * professions_catalog : {"social_statuses":[{"id":1,"title":"Служащий"},{"id":2,"title":"Учащийся"},{"id":3,"title":"Студент"},{"id":4,"title":"Предприниматель"}]}
         */

        private CommonEntity common;
        private PersonalEntity personal;
        private FlatsEntity flats;
        private StatisticsEntity statistics;
        private ProfessionsCatalogEntity professions_catalog;

        public CommonEntity getCommon() {
            return common;
        }

        public void setCommon(CommonEntity common) {
            this.common = common;
        }

        public PersonalEntity getPersonal() {
            return personal;
        }

        public void setPersonal(PersonalEntity personal) {
            this.personal = personal;
        }

        public FlatsEntity getFlats() {
            return flats;
        }

        public void setFlats(FlatsEntity flats) {
            this.flats = flats;
        }

        public StatisticsEntity getStatistics() {
            return statistics;
        }

        public void setStatistics(StatisticsEntity statistics) {
            this.statistics = statistics;
        }

        public ProfessionsCatalogEntity getProfessions_catalog() {
            return professions_catalog;
        }

        public void setProfessions_catalog(ProfessionsCatalogEntity professions_catalog) {
            this.professions_catalog = professions_catalog;
        }

        public static class CommonEntity {
            /**
             * email_confirmed : false
             * is_pgu_connected : false
             * is_profile_visible : true
             */

            private boolean email_confirmed;
            private boolean is_pgu_connected;
            private boolean is_profile_visible;

            public boolean isEmail_confirmed() {
                return email_confirmed;
            }

            public void setEmail_confirmed(boolean email_confirmed) {
                this.email_confirmed = email_confirmed;
            }

            public boolean isIs_pgu_connected() {
                return is_pgu_connected;
            }

            public void setIs_pgu_connected(boolean is_pgu_connected) {
                this.is_pgu_connected = is_pgu_connected;
            }

            public boolean isIs_profile_visible() {
                return is_profile_visible;
            }

            public void setIs_profile_visible(boolean is_profile_visible) {
                this.is_profile_visible = is_profile_visible;
            }
        }

        public static class PersonalEntity {
            /**
             * surname : фамилия
             * firstname : имя
             * middlename : отчество
             * birthday : дата рождения, dd.mm.yyyy
             * sex : male|female|null
             * email : email
             * phone : телефон
             * marital_status : married|single|null
             * childrens_count : 3
             * childrens_birthdays : ["dd.mm.yyyy","dd.mm.yyyy"]
             * car_exists : false
             * social_status : 1
             * troika_card_number : 1000000000
             * registration_date : дата регистрации в сервисе, dd.mm.yyyy
             * avatar : /image/1.jpg
             */

            private String surname;
            private String firstname;
            private String middlename;
            private String birthday;
            private String sex;
            private String email;
            private String phone;
            private String marital_status;
            private int childrens_count;
            private boolean car_exists;
            private int social_status;
            private String troika_card_number;
            private String registration_date;
            private String avatar;
            private List<String> childrens_birthdays;

            public String getSurname() {
                return surname;
            }

            public void setSurname(String surname) {
                this.surname = surname;
            }

            public String getFirstname() {
                return firstname;
            }

            public void setFirstname(String firstname) {
                this.firstname = firstname;
            }

            public String getMiddlename() {
                return middlename;
            }

            public void setMiddlename(String middlename) {
                this.middlename = middlename;
            }

            public String getBirthday() {
                return birthday;
            }

            public void setBirthday(String birthday) {
                this.birthday = birthday;
            }

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getMarital_status() {
                return marital_status;
            }

            public void setMarital_status(String marital_status) {
                this.marital_status = marital_status;
            }

            public int getChildrens_count() {
                return childrens_count;
            }

            public void setChildrens_count(int childrens_count) {
                this.childrens_count = childrens_count;
            }

            public boolean isCar_exists() {
                return car_exists;
            }

            public void setCar_exists(boolean car_exists) {
                this.car_exists = car_exists;
            }

            public int getSocial_status() {
                return social_status;
            }

            public void setSocial_status(int social_status) {
                this.social_status = social_status;
            }

            public String getTroika_card_number() {
                return troika_card_number;
            }

            public void setTroika_card_number(String troika_card_number) {
                this.troika_card_number = troika_card_number;
            }

            public String getRegistration_date() {
                return registration_date;
            }

            public void setRegistration_date(String registration_date) {
                this.registration_date = registration_date;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public List<String> getChildrens_birthdays() {
                return childrens_birthdays;
            }

            public void setChildrens_birthdays(List<String> childrens_birthdays) {
                this.childrens_birthdays = childrens_birthdays;
            }
        }

        public static class FlatsEntity {
            /**
             * registration : {"flat_id":"123","building_id":234,"flat":"string","building":"string","street":"string","district":"string","area":"string","editing_blocked":true}
             * work : {"flat_id":"123","building_id":234,"flat":"string","building":"string","street":"string","district":"string","area":"string","editing_blocked":false}
             */

            private RegistrationEntity registration;
            private WorkEntity work;

            public RegistrationEntity getRegistration() {
                return registration;
            }

            public void setRegistration(RegistrationEntity registration) {
                this.registration = registration;
            }

            public WorkEntity getWork() {
                return work;
            }

            public void setWork(WorkEntity work) {
                this.work = work;
            }

            public static class RegistrationEntity {
                /**
                 * flat_id : 123
                 * building_id : 234
                 * flat : string
                 * building : string
                 * street : string
                 * district : string
                 * area : string
                 * editing_blocked : true
                 */

                private String flat_id;
                private int building_id;
                private String flat;
                private String building;
                private String street;
                private String district;
                private String area;
                private boolean editing_blocked;

                public String getFlat_id() {
                    return flat_id;
                }

                public void setFlat_id(String flat_id) {
                    this.flat_id = flat_id;
                }

                public int getBuilding_id() {
                    return building_id;
                }

                public void setBuilding_id(int building_id) {
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

                public String getDistrict() {
                    return district;
                }

                public void setDistrict(String district) {
                    this.district = district;
                }

                public String getArea() {
                    return area;
                }

                public void setArea(String area) {
                    this.area = area;
                }

                public boolean isEditing_blocked() {
                    return editing_blocked;
                }

                public void setEditing_blocked(boolean editing_blocked) {
                    this.editing_blocked = editing_blocked;
                }
            }

            public static class WorkEntity {
                /**
                 * flat_id : 123
                 * building_id : 234
                 * flat : string
                 * building : string
                 * street : string
                 * district : string
                 * area : string
                 * editing_blocked : false
                 */

                private String flat_id;
                private int building_id;
                private String flat;
                private String building;
                private String street;
                private String district;
                private String area;
                private boolean editing_blocked;

                public String getFlat_id() {
                    return flat_id;
                }

                public void setFlat_id(String flat_id) {
                    this.flat_id = flat_id;
                }

                public int getBuilding_id() {
                    return building_id;
                }

                public void setBuilding_id(int building_id) {
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

                public String getDistrict() {
                    return district;
                }

                public void setDistrict(String district) {
                    this.district = district;
                }

                public String getArea() {
                    return area;
                }

                public void setArea(String area) {
                    this.area = area;
                }

                public boolean isEditing_blocked() {
                    return editing_blocked;
                }

                public void setEditing_blocked(boolean editing_blocked) {
                    this.editing_blocked = editing_blocked;
                }
            }
        }

        public static class StatisticsEntity {
            /**
             * status : Новичок
             * rating : 5408
             * percent_fill_profile : 95
             * params : [{"title":"Заполненность профиля","value":"95%"},{"title":"Пройдено голосований","value":"257"},{"title":"Оценено новинок","value":"17"},{"title":"Посещено мероприятий","value":"5"},{"title":"Приглашено друзей","value":"0"},{"title":"Активность в социальных сетях","value":"0"},{"title":"Получено баллов","value":"0"},{"title":"Потрачено баллов","value":"0"}]
             */

            private String status;
            private int rating;
            private int percent_fill_profile;
            private List<ParamsEntity> params;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public int getRating() {
                return rating;
            }

            public void setRating(int rating) {
                this.rating = rating;
            }

            public int getPercent_fill_profile() {
                return percent_fill_profile;
            }

            public void setPercent_fill_profile(int percent_fill_profile) {
                this.percent_fill_profile = percent_fill_profile;
            }

            public List<ParamsEntity> getParams() {
                return params;
            }

            public void setParams(List<ParamsEntity> params) {
                this.params = params;
            }

            public static class ParamsEntity {
                /**
                 * title : Заполненность профиля
                 * value : 95%
                 */

                private String title;
                private String value;

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }

        public static class ProfessionsCatalogEntity {
            private List<SocialStatusesEntity> social_statuses;

            public List<SocialStatusesEntity> getSocial_statuses() {
                return social_statuses;
            }

            public void setSocial_statuses(List<SocialStatusesEntity> social_statuses) {
                this.social_statuses = social_statuses;
            }

            public static class SocialStatusesEntity {
                /**
                 * id : 1
                 * title : Служащий
                 */

                private int id;
                private String title;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }
            }
        }
    }

    public static class AchievementsEntity {
        /**
         * count : 10
         * last : [{"id":"2015-07-01","img_url":"http://img0.joyreactor.cc/pics/post/full/warhammer-40000-фэндомы-orks-1793330.jpeg","title":"Герой. Просто Герой.","is_next":false,"description":"следующее достижение"},{"id":"2015-06-01","img_url":"http://cs623727.vk.me/v623727792/103db/u9hg8pDCc7w.jpg","title":"Активный гражданин месяца","is_next":false,"description":"получено 11.11.11"},{"id":"2015-05-01","img_url":"http://cs623727.vk.me/v623727792/103e2/OebzxL0Mjf4.jpg","title":"ГиперАктивный гражданин месяца","is_next":false,"description":"получено 12.12.12"}]
         */

        private int count;
        private List<LastEntity> last;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<LastEntity> getLast() {
            return last;
        }

        public void setLast(List<LastEntity> last) {
            this.last = last;
        }

        public static class LastEntity {
            /**
             * id : 2015-07-01
             * img_url : http://img0.joyreactor.cc/pics/post/full/warhammer-40000-фэндомы-orks-1793330.jpeg
             * title : Герой. Просто Герой.
             * is_next : false
             * description : следующее достижение
             */

            private String id;
            private String img_url;
            private String title;
            private boolean is_next;
            private String description;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getImg_url() {
                return img_url;
            }

            public void setImg_url(String img_url) {
                this.img_url = img_url;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public boolean isIs_next() {
                return is_next;
            }

            public void setIs_next(boolean is_next) {
                this.is_next = is_next;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }
    }
}
