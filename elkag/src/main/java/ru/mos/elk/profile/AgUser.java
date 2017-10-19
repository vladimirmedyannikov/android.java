package ru.mos.elk.profile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ru.mos.elk.Constants;
import ru.mos.elk.ElkTextUtils;
import ru.mos.elk.profile.flat.Flat;

/**
 * Структура данных для работы с данными пользователя АГ
 *
 * @since 1.9
 */
public class AgUser implements Serializable {
    public static final String PREFS = "ru.mos.elk.PREFS";

    public static final String PERSONAL_EXPIRED = "personal_expired";

    public static final String FIRSTNAME = "firstname";
    public static final String LASTNAME = "lastName";
    public static final String MIDDLENAME = "middlename";
    public static final String BIRTHDATE = "birthdate";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String IS_EMAIL_CONFIRMED = "is_email_confirmed";
    public static final String SEX = "sex";
    public static final String MARITAL_STATUS = "marital_status";
    public static final String CHILDRENS_COUNT = "childrens_count";
    public static final String IS_PGU_CONNECTED = "is_pgu_connected";
    public static final String IS_PROFILE_VISIBLE = "is_profile_visible";
    public static final String HAS_CAR = "has_car";
    public static final String CHILDREN_BIRTHDAYS = "children_birthdays";
    public static final String SOCIAL_STATUS = "social_status";
    public static final String STATUS = "status";
    public static final String RATING = "rating";
    public static final String PERCENT_FILL_PROFILE = "percent_fill_profile";
    public static final String ACHIEVEMENTS_COUNT = "achievements_count";
    public static final String REGISTRATION_DATE = "registration_date";
    public static final String AVATAR = "avatar";
    private String surname, firstName, middleName;
    private String birthday;
    private Gender gender;
    private String email;
    private String phone;
    private MaritalStatus maritalStatus;
    private int childCount;
    private List<Long> childBirthdays;
    private int agSocialStatus;
    private boolean isCarExist, isEmailConfirmed, isPguConnected, isProfileVisible;
    private Flat registrationFlat;
    private Flat residenceFlat;
    private Flat workFlat;
    private String status;
    private long rating;
    private int percentFillProfile;
    private int count;
    private String registrationDate;
    private String avatar;

    public static boolean isPguConnected(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(AgUser.PREFS, Activity.MODE_PRIVATE);
        return prefs.getBoolean(AgUser.IS_PGU_CONNECTED, false);
    }

    public static void setPguConnected(Context context) {
        setPguConnected(context, true);
    }

    public static void setPguDisconnected(Context context) {
        setPguConnected(context, false);
    }

    private static void setPguConnected(Context context, boolean isConnected) {
        SharedPreferences prefs = context.getSharedPreferences(AgUser.PREFS, Activity.MODE_PRIVATE);
        prefs.edit().putBoolean(AgUser.IS_PGU_CONNECTED, isConnected).apply();
    }

    public static void setPercentFillProfile(Context context, int percentFillProfile) {
        SharedPreferences prefs = context.getSharedPreferences(AgUser.PREFS, Activity.MODE_PRIVATE);
        prefs.edit().putInt(AgUser.PERCENT_FILL_PROFILE, percentFillProfile).apply();
    }

    public static void setProfileVisible(Context context, boolean isConnected) {
        SharedPreferences prefs = context.getSharedPreferences(AgUser.PREFS, Activity.MODE_PRIVATE);
        prefs.edit().putBoolean(AgUser.IS_PROFILE_VISIBLE, isConnected).apply();
    }

    public static String getPhone(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(AgUser.PREFS, Activity.MODE_PRIVATE);
        return prefs.getString(AgUser.PHONE, null);
    }

    private static void setPhone(Context context, String phone) {
        SharedPreferences prefs = context.getSharedPreferences(AgUser.PREFS, Activity.MODE_PRIVATE);
        prefs.edit().putString(AgUser.PHONE, phone).apply();
    }

    /**
     * Пользователь, сохраненный локально в SharedPreferences
     *
     * @param context
     */
    public AgUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        surname = prefs.getString(LASTNAME, "");
        firstName = prefs.getString(FIRSTNAME, "");
        middleName = prefs.getString(MIDDLENAME, "");
        birthday = prefs.getString(BIRTHDATE, "");
        gender = Gender.parse(prefs.getString(SEX, "null"));
        email = prefs.getString(EMAIL, "");
        phone = prefs.getString(PHONE, "");
        maritalStatus = MaritalStatus.parse(prefs.getString(MARITAL_STATUS, "null"));
        childCount = prefs.getInt(CHILDRENS_COUNT, 0);
        childBirthdays = childBirthdaysFromPrefs(prefs);
        isCarExist = prefs.getBoolean(HAS_CAR, false);
        isEmailConfirmed = prefs.getBoolean(IS_EMAIL_CONFIRMED, false);
        isPguConnected = prefs.getBoolean(IS_PGU_CONNECTED, false);
        isProfileVisible = prefs.getBoolean(IS_PROFILE_VISIBLE, false);
        registrationFlat = Flat.getRegistration(context);
        residenceFlat = Flat.getResidence(context);
        workFlat = Flat.getWork(context);
        agSocialStatus = prefs.getInt(SOCIAL_STATUS, 0);
        status = prefs.getString(STATUS, "");
        rating = prefs.getLong(RATING, 0);
        percentFillProfile = prefs.getInt(PERCENT_FILL_PROFILE, 0);
        count = prefs.getInt(ACHIEVEMENTS_COUNT, 0);
        registrationDate = prefs.getString(REGISTRATION_DATE, "0");
    }

    /**
     * Данные пользователь из ответа сервиса
     *
     * @param context
     * @param json    должен содержать данные по пользователю аг: personal, common, flats
     */
    public AgUser(Context context, JSONObject json) {
        if (json != null) {
            /**
             * Вместе с данными по профилю к нам приходит список специальностей
             * Его мы тут парсим и сохраняем.
             */
            AgSocialStatus.save(context, json);
            /**
             * Данные пользователя
             */
            parseCommon(json);
            parsePersonal(json);
            parseFlats(context, json);
            parseStatistic(context, json);
            parseAchievements(context, json);
        }
    }

    public boolean isEmptyPersonal() {
        return ElkTextUtils.isEmpty(surname) || ElkTextUtils.isEmpty(firstName)
                || ElkTextUtils.isEmpty(birthday) || gender.isEmpty() /*|| TextUtils.isEmpty(email)*/
                || ElkTextUtils.isEmpty(phone);
    }

    public boolean isEmptyFlats() {
        return registrationFlat.isEmpty() || residenceFlat.isEmpty() || workFlat.isEmpty();
    }

    public boolean isEmptyFamily() {
        return maritalStatus.isEmpty()
                /*|| (childCount > 0 && childBirthdays.size() == 0) */;
    }

    public boolean isFilledAndChangedPersonal(AgUser changedAgUser, boolean isTask) {
        return isFilledAndChanged(surname, changedAgUser.surname, isTask)
                || isFilledAndChanged(firstName, changedAgUser.firstName, isTask)
                || isFilledAndChanged(middleName, changedAgUser.middleName, isTask)
                || isFilledAndChanged(birthday, changedAgUser.birthday, isTask)
                || ((isTask || !changedAgUser.gender.isEmpty()) && !gender.equals(changedAgUser.gender))
                || isFilledAndChanged(email, changedAgUser.email, isTask)
                || ((isTask || changedAgUser.agSocialStatus != 0) && agSocialStatus != changedAgUser.agSocialStatus)
                || (isPguConnected != changedAgUser.isPguConnected);
    }

    public boolean isFilledAndChangedFamily(AgUser changedAgUser, boolean isTask) {
        return ((isTask || !changedAgUser.maritalStatus.isEmpty()) && maritalStatus != changedAgUser.maritalStatus)
                || isFilledAndChanged(childCount, changedAgUser.childCount)
                || !isEqualsBirthdays(changedAgUser.childBirthdays);

    }

    public boolean isFilledAndChangedFlats(AgUser changedAgUser) {
        return isFilledAndChanged(registrationFlat, changedAgUser.registrationFlat)
                || isDeleted(registrationFlat, changedAgUser.registrationFlat)
                || isFilledAndChanged(residenceFlat, changedAgUser.residenceFlat)
                || isDeleted(residenceFlat, changedAgUser.residenceFlat)
                || isFilledAndChanged(workFlat, changedAgUser.workFlat)
                || isDeleted(workFlat, changedAgUser.workFlat);
    }

    public boolean isFilledAndChanged(AgUser changedAgUser, boolean isTask) {
        return isFilledAndChangedPersonal(changedAgUser, isTask)
                || isFilledAndChangedFamily(changedAgUser, isTask)
                || isFilledAndChangedFlats(changedAgUser);
    }

    public boolean isEmptyWork() {
        return agSocialStatus == 0;
    }

    public boolean equalsPersonal(AgUser other) {
        boolean result = false;
        if (other != null) {
            result = surname.equalsIgnoreCase(other.surname)
                    && middleName.equalsIgnoreCase(other.middleName)
                    && firstName.equalsIgnoreCase(other.firstName)
                    && birthday.equalsIgnoreCase(birthday)
                    && gender.equals(gender)
                    && email.equalsIgnoreCase(other.email);
        }
        return result;
    }

    public boolean equalsFamily(AgUser other) {
        boolean result = false;
        if (other != null) {
            result = maritalStatus.equals(other.maritalStatus)
                    && childCount == other.childCount;
        }
        return result;
    }

    public boolean equalsWork(AgUser other) {
        boolean result = false;
        if (other != null) {
            result = agSocialStatus == other.agSocialStatus;
        }
        return result;
    }

    public boolean isChildBirthdaysFilled() {
        if (childBirthdays == null) {
            childBirthdays = new ArrayList<Long>();
        }
        return childCount == childBirthdays.size();
    }

    public boolean hasOneChild() {
        return childCount == 1;
    }

    public boolean hasMoreThanOneChild() {
        return childCount > 1;
    }

    /**
     * Сохранение пользователя локально на устройстве
     *
     * @param context
     */
    public void save(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        /**
         * личные данные
         */
        editor.putString(FIRSTNAME, firstName);
        editor.putString(LASTNAME, surname);
        editor.putString(MIDDLENAME, middleName);
        editor.putString(BIRTHDATE, birthday);
        editor.putString(PHONE, phone);
        editor.putString(EMAIL, email);
        editor.putInt(CHILDRENS_COUNT, childCount);
        editor.putBoolean(HAS_CAR, isCarExist);
        editor.putString(CHILDREN_BIRTHDAYS, childBirthdaysAsJsonArray().toString());
        editor.putLong(PERSONAL_EXPIRED, System.currentTimeMillis() + Constants.MINUTE * 10);
        editor.putBoolean(IS_EMAIL_CONFIRMED, isEmailConfirmed);
        editor.putBoolean(IS_PGU_CONNECTED, isPguConnected);
        editor.putBoolean(IS_PROFILE_VISIBLE, isProfileVisible);
        editor.putString(SEX, gender.getValue());
        editor.putString(MARITAL_STATUS, maritalStatus.getValue());
        editor.putInt(SOCIAL_STATUS, agSocialStatus);
        editor.putString(STATUS, status);
        editor.putLong(RATING, rating);
        editor.putInt(ACHIEVEMENTS_COUNT, count);
        editor.putInt(PERCENT_FILL_PROFILE, percentFillProfile);
        editor.putString(REGISTRATION_DATE, registrationDate);
        editor.putString(AVATAR, avatar);
        editor.apply();

        /**
         * данные по квартирам и адресам проживания
         */
        Flat.clearAll(context);
        registrationFlat.save(context);
        residenceFlat.save(context);
        workFlat.save(context);
    }

    public JSONObject getRequestBody(AgUser agUser) {
        return getRequestBody(agUser, false, true);
    }

    public JSONObject getRequestBody(AgUser changedAgUser, boolean isNeedChildCount, boolean isTask) {
        JSONObject result = new JSONObject();
        JSONObject personal = new JSONObject();
        JSONObject flats = new JSONObject();
        try {
            /**
             * Добавление данных по профилю
             */
            if (isPguConnected != changedAgUser.isPguConnected) {
                personal.put("is_pgu_connected", changedAgUser.isPguConnected);
            }
            if (isFilledAndChanged(surname, changedAgUser.surname, isTask)) {
                personal.put("surname", changedAgUser.surname);
            }
            if (isFilledAndChanged(firstName, changedAgUser.firstName, isTask)) {
                personal.put("firstname", changedAgUser.firstName);
            }
            if (isFilledAndChanged(middleName, changedAgUser.middleName, isTask)) {
                personal.put("middlename", changedAgUser.middleName);
            }
            if (isFilledAndChanged(birthday, changedAgUser.birthday, isTask)) {
                personal.put("birthday", changedAgUser.birthday);
            }
            if ((isTask || !changedAgUser.gender.isEmpty()) && !gender.equals(changedAgUser.gender)) {
                personal.put("sex", changedAgUser.gender == Gender.NULL ? JSONObject.NULL : changedAgUser.gender.getValue());
            }
            if (isFilledAndChanged(email, changedAgUser.email, isTask)) {
                personal.put("email", changedAgUser.email);
            }
            if (isFilledAndChanged(phone, changedAgUser.phone, isTask)) {
                personal.put("phone", changedAgUser.phone);
            }
            if ((isTask || !changedAgUser.maritalStatus.isEmpty()) && maritalStatus != changedAgUser.maritalStatus) {
                personal.put("marital_status", changedAgUser.maritalStatus == MaritalStatus.NULL ? JSONObject.NULL : changedAgUser.maritalStatus.getValue());
            }
            if ((isTask || changedAgUser.agSocialStatus != 0) && agSocialStatus != changedAgUser.agSocialStatus) {
                personal.put("social_status", changedAgUser.agSocialStatus == 0 ? "" : changedAgUser.agSocialStatus);
            }

            if (isFilledAndChanged(childCount, changedAgUser.childCount)) {
                personal.put("childrens_count", changedAgUser.childCount);
            } else if (isNeedChildCount) {
                personal.put("childrens_count", 0);
            }

            if (changedAgUser.childCount > 0) {
                if (!isEqualsBirthdays(changedAgUser.childBirthdays)) {
                    personal.put("childrens_birthdays", changedAgUser.childBirthdaysAsJsonArray());
                }
            }

            if (changedAgUser.agSocialStatus != 0 && agSocialStatus < changedAgUser.agSocialStatus) {
                personal.put("social_status", changedAgUser.agSocialStatus);
            }
            result.put("personal", personal);

            /**
             * Добавление квартир
             */
            if (isFilledAndChanged(registrationFlat, changedAgUser.registrationFlat)) {
                changedAgUser.registrationFlat.addToFlatsJson(flats);
                if (registrationFlat.compareByFullAddress(residenceFlat)) {
                    changedAgUser.residenceFlat.addToFlatsJson(flats);
                }
            } else if (isDeleted(registrationFlat, changedAgUser.registrationFlat)) {
                JSONObject kill = new JSONObject();
                kill.put("kill", true);
                flats.put("registration", kill);
            }
            if (isFilledAndChanged(residenceFlat, changedAgUser.residenceFlat)) {
                changedAgUser.residenceFlat.addToFlatsJson(flats);
            } else if (isDeleted(residenceFlat, changedAgUser.residenceFlat)) {
                JSONObject kill = new JSONObject();
                kill.put("kill", true);
                flats.put("residence", kill);
            }
            if (isFilledAndChanged(workFlat, changedAgUser.workFlat)) {
                changedAgUser.workFlat.addToFlatsJson(flats);
            } else if (isDeleted(workFlat, changedAgUser.workFlat)) {
                JSONObject kill = new JSONObject();
                kill.put("kill", true);
                flats.put("work", kill);
            }
            result.put("flats", flats);
        } catch (JSONException ignored) {
        }
        return result;
    }

    private boolean isEqualsBirthdays(List<Long> other) {
        boolean result = false;
        if (childBirthdays != null && other != null) {
            if (childBirthdays.size() == other.size()) {
                result = true;
                for (int i = 0; i < childBirthdays.size(); ++i) {
                    if (!childBirthdays.get(i).equals(other.get(i))) {
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private boolean isFilledAndChanged(String old, String changed, boolean isTask) {
        return (isTask || !ElkTextUtils.isEmpty(changed)) && !old.equalsIgnoreCase(changed);
    }

    private boolean isFilledAndChanged(int old, int changed) {
        return changed > -1
                && old != changed;
    }

    private boolean isFilledAndChanged(Flat old, Flat changed) {
        return !changed.isEmpty() && !old.compareByFullAddress(changed);
    }

    private boolean isDeleted(Flat old, Flat edited) {
        return !old.isEmpty() && edited.isEmpty();
    }

    /**
     * Добавления личных данных пользователя у телу запроса
     *
     * @param requestBody json тело запроса
     */
    public void addPersonal(JSONObject requestBody) {
        if (requestBody != null) {
            JSONObject personalJson = new JSONObject();
            try {
                personalJson.put("surname", surname);
                personalJson.put("firstname", firstName);
                personalJson.put("middlename", middleName);
                personalJson.put("birthday", birthday);
                personalJson.put("sex", gender.getValue());
                personalJson.put("email", email);
                personalJson.put("phone", phone);
                personalJson.put("marital_status", maritalStatus.getValue());
                personalJson.put("childrens_count", childCount);
                personalJson.put("childrens_birthdays", childBirthdaysAsJsonArray());
//                personalJson.put("car_exists", isCarExist);
                personalJson.put("social_status", agSocialStatus);
                requestBody.put("personal", personalJson);
            } catch (JSONException ignored) {
            }
        }
    }

    /**
     * Добавление данных по квартирам к телу запроса
     *
     * @param requestBody json тело запроса
     */
    public void addFlats(JSONObject requestBody) {
        if (requestBody != null) {
            JSONObject flatsJson = new JSONObject();
            try {
                registrationFlat.addToFlatsJson(flatsJson);
                residenceFlat.addToFlatsJson(flatsJson);
                workFlat.addToFlatsJson(flatsJson);
                requestBody.put("flats", flatsJson);
            } catch (JSONException ignored) {
            }
        }
    }

    public AgUser setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public String getSurname() {
        return surname;
    }

    public String getSurnameAndFirstName() {
        String surAndFis = String.format("%s %s", surname, firstName);
        return surAndFis.equalsIgnoreCase("") ? "Имя не заполнено" : surAndFis;
    }

    public String getFullUserName() {
        if (ElkTextUtils.isEmpty(surname) && ElkTextUtils.isEmpty(firstName) && ElkTextUtils.isEmpty(middleName)) {
            return "";
        }
        return String.format("%s %s %s", surname, firstName, middleName);
    }

    public String getFirstName() {
        return firstName;
    }

    public AgUser setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getMiddleName() {
        return middleName;
    }

    public AgUser setMiddleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getRegistrationDate() {
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy");
        String regDate = "на проекте с ";
        try {
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru"));
            regDate = regDate + sdf2.format(sdf1.parse(registrationDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return regDate;
    }

    public AgUser setBirthday(String birthday) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMMM yyyy г.р.", new Locale("ru"));
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy");
        try {
            birthday = sdf2.format(sdf1.parse(birthday));
        } catch (ParseException ignored) {
        }
        this.birthday = birthday;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public AgUser setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public AgUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public AgUser setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public AgUser setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
        return this;
    }

    public boolean isProfileVisible() {
        return isProfileVisible;
    }

    public void setProfileVisible(boolean profileVisible) {
        isProfileVisible = profileVisible;
    }

    public int getChildCount() {
        return childCount;
    }

    public AgUser setChildCount(int childCount) {
        this.childCount = childCount;
        return this;
    }

    public String getFirstBirthday() {
        String result = null;
        if (childBirthdays != null && childBirthdays.size() > 0) {
            result = birthdayToStringForView(childBirthdays.get(0));
        }
        return result;
    }

    public List<Long> getChildBirthdays() {
        return childBirthdays;
    }

    public AgUser setChildBirthdays(List<Long> childBirthdays) {
        this.childBirthdays = childBirthdays;
        return this;
    }

    public AgUser add(String birthday) {
        if (childBirthdays == null) {
            childBirthdays = new ArrayList<Long>();
        }
        if (!TextUtils.isEmpty(birthday)) {
            childBirthdays.add(birthdayToLongFromView(birthday));
        }
        return this;
    }

    public AgUser remove(String birthday) {
        if (childBirthdays != null) {
            childBirthdays.remove(new Long(birthdayToLongFromView(birthday)));
        }
        return this;
    }

    public long birthdayToLongFromView(String birthday) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        long result = 0;
        try {
            result = sdf.parse(birthday).getTime();
        } catch (ParseException ignored) {
        }
        return result;
    }

    private long birthdayToLong(String birthday) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        long result = 0;
        try {
            result = sdf.parse(birthday).getTime();
        } catch (ParseException ignored) {
        }
        return result;
    }

    public AgUser clearBirthdays() {
        if (childBirthdays != null) {
            childBirthdays.clear();
        }
        return this;
    }

    public boolean isCarExist() {
        return isCarExist;
    }

    public AgUser setCarExist(boolean isCarExist) {
        this.isCarExist = isCarExist;
        return this;
    }

    public Flat getRegistration() {
        return registrationFlat;
    }

    public AgUser setRegistrationFlat(Flat registrationFlat) {
        this.registrationFlat = registrationFlat;
        return this;
    }

    public Flat getResidence() {
        return residenceFlat;
    }

    public AgUser setResidenceFlat(Flat residenceFlat) {
        this.residenceFlat = residenceFlat;
        return this;
    }

    public Flat getWork() {
        return workFlat;
    }

    public AgUser setWorkFlat(Flat workFlat) {
        this.workFlat = workFlat;
        return this;
    }

    public String getStatus() {
        return status.equalsIgnoreCase("") ? "Новичок" : status;
    }

    public String getRating() {
        return String.valueOf(rating);
    }

    public int getPercentFillProfile() {
        return percentFillProfile;
    }

    public void setPercentFillProfile(int percentFillProfile) {
        this.percentFillProfile = percentFillProfile;
    }

    public int getCount() {
        return count;
    }

    public String getAchievementsCount() {
        return String.format("+%d", getCount());
    }

    public String getAvatar() {
        return avatar;
    }

    public boolean isPguConnected() {
        return isPguConnected;
    }

    public AgUser setPguConnected(boolean isPguConnected) {
        this.isPguConnected = isPguConnected;
        return this;
    }

    public boolean isEmailConfirmed() {
        return isEmailConfirmed;
    }

    public AgUser setEmailConfirmed(boolean isEmailConfirmed) {
        this.isEmailConfirmed = isEmailConfirmed;
        return this;
    }

    /**
     * Конвертация дат рождения детей
     *
     * @param jsonObject тег personal, содержащий описание личных данных пользователя и тег childrens_birthdays
     * @return список дат рождения
     */
    private List<Long> childBirthdaysFromJson(JSONObject jsonObject) {
        List<Long> result = new ArrayList<Long>();
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.optJSONArray("childrens_birthdays");
            result = childBirthdaysFromJson(jsonArray);
        }
        return result;
    }

    /**
     * Конвертация списка дат родения, сохраненного в SharedPreference
     *
     * @param prefs объект SharedPreferences для личных данных пользователя аг
     * @return список дат рождения детей
     */
    private List<Long> childBirthdaysFromPrefs(SharedPreferences prefs) {
        List<Long> result = new ArrayList<Long>();
        try {
            JSONArray jsonArray = new JSONArray(prefs.getString(CHILDREN_BIRTHDAYS, ""));
            result = childBirthdaysFromJson(jsonArray);
        } catch (Exception ignored) {
        }

        return result;
    }

    private List<Long> childBirthdaysFromJson(JSONArray jsonArray) {
        List<Long> result = new ArrayList<Long>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); ++i) {
                if (!TextUtils.isEmpty(jsonArray.optString(i))) {
                    Long birthday = birthdayToLong(jsonArray.optString(i));
                    result.add(birthday);
                }
            }
        }
        return result;
    }

    /**
     * Конвертация дат рождения детей JSONArray для сохрания в SharedPrefernses
     *
     * @return
     */
    private JSONArray childBirthdaysAsJsonArray() {
        JSONArray result = new JSONArray();
        for (int i = 0; i < childBirthdays.size(); ++i) {
            if (childBirthdays.get(i) != 0) {
                result.put(birthdayToString(childBirthdays.get(i)));
            }
        }
        return result;
    }

    public List<String> childBirthdaysAsList() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < childBirthdays.size(); ++i) {
            if (childBirthdays.get(i) != 0) {
                list.add(birthdayToString(childBirthdays.get(i)));
            }
        }
        return list;
    }

    private String birthdayToString(long birthday) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(birthday);
    }

    private String birthdayToStringForView(long birthday) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(birthday);
    }

    public void parseAchievements(Context context, JSONObject json) {
        JSONObject achievements = json.optJSONObject("achievements");
        if (achievements != null) {
            count = achievements.optInt("count");
            saveJsonArray(context, achievements, Achievements.LAST_ACHIEVEMENTS);
        }
    }

    public void parseStatistic(Context context, JSONObject json) {
        JSONObject statistics = json.optJSONObject("statistics");
        if (statistics != null) {
            status = ElkTextUtils.getString(statistics, "status", "Новичок");
            rating = statistics.optLong("rating");
            percentFillProfile = statistics.optInt("percent_fill_profile");
            saveJsonArray(context, statistics, Statistics.STATISTICS_PARAMS);
        }
    }


    public static void saveJsonArray(Context context, JSONObject json, String params) {
        if (json != null) {
            JSONArray array = json.optJSONArray(params);
            saveToSharedPreferences(context, params, array.toString());
        }
    }

    public static void saveToSharedPreferences(Context context, String content, String params) {
        SharedPreferences prefs = context.getSharedPreferences(AgUser.PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(params, content).apply();
    }

    public List<Statistics> getStatisticList(Context context) {
        return getListFromPreferences(context, Statistics.STATISTICS_PARAMS, Statistics[].class);
    }

    public List<Achievements> getAchievementsList(Context context) {
        return getListFromPreferences(context, Achievements.LAST_ACHIEVEMENTS, Achievements[].class);
    }


    public static <T> List<T> getListFromPreferences(Context context, String params, Class<T[]> clazz) {
        List<T> result = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(AgUser.PREFS, Context.MODE_PRIVATE);
        try {
            JSONArray array = new JSONArray(prefs.getString(params, ""));
            result.addAll(getDataList(array.toString(), clazz));
        } catch (JSONException ignored) {
        }
        return result;
    }

    /**
     * Парсинг тега common данных по пользователю аг
     *
     * @param json тег common
     */
    private void parseCommon(JSONObject json) {
        JSONObject commonJson = json.optJSONObject("common");
        if (commonJson != null) {
            isEmailConfirmed = commonJson.optBoolean("email_confirmed");
            isPguConnected = commonJson.optBoolean("is_pgu_connected");
            isProfileVisible = commonJson.optBoolean("is_profile_visible");
        }
    }

    /**
     * Парсинг основных данных по пользователю аг
     *
     * @param json тег personal
     */
    private void parsePersonal(JSONObject json) {
        JSONObject personalJson = json.optJSONObject("personal");
        if (personalJson != null) {
            surname = ElkTextUtils.getString(personalJson, "surname", "");
            firstName = ElkTextUtils.getString(personalJson, "firstname", "");
            middleName = ElkTextUtils.getString(personalJson, "middlename", "");
            birthday = ElkTextUtils.getString(personalJson, "birthday", "");
            gender = Gender.parse(personalJson.optString("sex"));
            email = ElkTextUtils.getString(personalJson, "email", "");
            phone = ElkTextUtils.getString(personalJson, "phone", "");
            maritalStatus = MaritalStatus.parse(personalJson.optString("marital_status"));
            childCount = personalJson.optInt("childrens_count");
            childBirthdays = childBirthdaysFromJson(personalJson);
            isCarExist = personalJson.optBoolean("car_exist");
            agSocialStatus = personalJson.optInt("social_status");
            registrationDate = ElkTextUtils.getString(personalJson, "registration_date", "");
            avatar = ElkTextUtils.getString(personalJson, "avatar", "");
        }
    }

    /**
     * Парсинг квартир пользователя аг
     *
     * @param json тег flats
     */
    private void parseFlats(Context context, JSONObject json) {
        JSONObject flatsJson = json.optJSONObject("flats");
        registrationFlat = Flat.getRegistration(context);
        residenceFlat = Flat.getResidence(context);
        workFlat = Flat.getWork(context);
        if (flatsJson != null) {
            registrationFlat = Flat.getRegistration(flatsJson);
            residenceFlat = Flat.getResidence(flatsJson);
            workFlat = Flat.getWork(flatsJson);
        }
    }

    public int getAgSocialStatus() {
        return agSocialStatus;
    }

    public AgUser setAgSocialStatus(int agSocialStatus) {
        this.agSocialStatus = agSocialStatus;
        return this;
    }

    /**
     * Пол
     */
    public enum Gender {
        NULL("null", "Не установлено"),
        MALE("male", "Мужской"),
        FEMALE("female", "Женский"),
        HINT("null", "Пол");

        private String value, label;

        Gender(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public static Gender parse(String value) {
            Gender result = NULL;
            if (MALE.getValue().equalsIgnoreCase(value)) {
                result = MALE;
            } else if (FEMALE.getValue().equalsIgnoreCase(value)) {
                result = FEMALE;
            }
            return result;
        }

        public static Gender parseLabel(String value) {
            Gender result = NULL;
            if (MALE.toString().equalsIgnoreCase(value)) {
                result = MALE;
            } else if (FEMALE.toString().equalsIgnoreCase(value)) {
                result = FEMALE;
            }
            return result;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return label;
        }

        public boolean equals(Gender other) {
            return other != null && value.equalsIgnoreCase(other.value);
        }

        public static Gender[] getGenderItems() {
            return new Gender[]{NULL, MALE, FEMALE};
        }

        public boolean isEmpty() {
            return NULL == this;
        }

        /**
         * Адаптер для отображения списка выбора пола
         *
         * @param context
         * @return
         */
        public static ArrayAdapter getSexAdapter(Context context) {
            ArrayAdapter result = new GenderAdapter(context);
            result.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            return result;
        }

        /**
         * Адаптер для отображения списка выбора пола
         * Обычно выбор пола представлен выпадающим списком
         */
        public static class GenderAdapter extends ArrayAdapter<Gender> {

            public GenderAdapter(Context context) {
                this(context, Gender.getGenderItems());
            }

            public GenderAdapter(Context context, Gender[] objects) {
                super(context, -1, objects);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = View.inflate(getContext(), android.R.layout.simple_spinner_item, null);
                }
                displayGender(convertView, getItem(position));
                return convertView;
            }

            private void displayGender(View v, Gender gender) {
                TextView title = (TextView) v.findViewById(android.R.id.text1);
                title.setText(gender.toString());
            }
        }
    }

    /**
     * Семеной положение
     * Если задать пол (метод setGender(Gender gender)),
     * то на контроле выведется соотвествующая полу метка
     */
    public enum MaritalStatus {
        NULL("null", "Не установлено", "Не установлено", "Не установлено"),
        MARRIED("married", "Женат", "Замужем", "Женат/замужем"),
        SINGLE("single", "Не женат", "Не замужем", "Не женат/не замужем");

        private String value, labelMale, labelFemale, label;
        private Gender gender = Gender.NULL;

        MaritalStatus(String value, String labelMale, String labelFemale, String label) {
            this.value = value;
            this.labelMale = labelMale;
            this.labelFemale = labelFemale;
            this.label = label;
        }

        public String getLabelMale() {
            return labelMale;
        }

        public String getLabelFemale() {
            return labelFemale;
        }

        public static MaritalStatus parse(String value) {
            MaritalStatus result = NULL;
            if (MARRIED.getValue().equalsIgnoreCase(value)) {
                result = MARRIED;
            } else if (SINGLE.getValue().equalsIgnoreCase(value)) {
                result = SINGLE;
            }
            return result;
        }

        public static MaritalStatus parseLabel(String value) {
            MaritalStatus result = NULL;
            if (SINGLE.labelMale.equalsIgnoreCase(value) || SINGLE.labelFemale.equalsIgnoreCase(value) || SINGLE.labelMale.equalsIgnoreCase(value)) {
                result = SINGLE;
            } else if (MARRIED.labelMale.equalsIgnoreCase(value) || MARRIED.labelFemale.equalsIgnoreCase(value) || MARRIED.labelMale.equalsIgnoreCase(value)) {
                result = MARRIED;
            }
            return result;
        }

        public String getValue() {
            return value;
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

        public boolean equals(MaritalStatus other) {
            return other != null && value.equalsIgnoreCase(other.value);
        }

        /**
         * Если пол не указан, то выведется
         * общая метка без учета пола
         *
         * @return
         */
        @Override
        public String toString() {
            String result = label;
            if (Gender.MALE == gender) {
                result = labelMale;
            } else if (Gender.FEMALE == gender) {
                result = labelFemale;
            }
            return result;
        }

        public String toString(Gender gender) {
            String result = label;
            if (Gender.MALE == gender) {
                result = labelMale;
            } else if (Gender.FEMALE == gender) {
                result = labelFemale;
            }
            return result;
        }

        public boolean isEmpty() {
            return NULL == this;
        }

        public static MaritalStatus[] getMaritalStatusItems() {
            return new MaritalStatus[]{NULL, MARRIED, SINGLE};
        }

        /**
         * Адаптер для выбора семейного положения
         *
         * @param context
         * @param gender
         * @return
         */
        public static ArrayAdapter getMaritalStatusAdapter(Context context, Gender gender) {
            ArrayAdapter result = new MaritalStatusAdapter(context, gender);
            result.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            return result;
        }

        /**
         * Адаптер для выбора семейного положения
         * Обычно выбор семейного положения представлен выпадающим списком
         */
        public static class MaritalStatusAdapter extends ArrayAdapter<MaritalStatus> {
            private MaritalStatus[] statuses;

            public MaritalStatusAdapter(Context context, Gender gender) {
                this(context, MaritalStatus.getMaritalStatusItems(), gender);
            }

            public MaritalStatusAdapter(Context context, MaritalStatus[] objects, Gender gender) {
                super(context, -1, objects);
                statuses = objects;
                setGender(gender);
            }

            public void setGender(Gender gender) {
                for (MaritalStatus status : statuses) {
                    status.setGender(gender);
                }
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = View.inflate(getContext(), android.R.layout.simple_spinner_item, null);
                }
                displayMaritalStatus(convertView, getItem(position));
                return convertView;
            }

            private void displayMaritalStatus(View v, MaritalStatus maritalStatus) {
                TextView title = (TextView) v.findViewById(android.R.id.text1);
                title.setText(maritalStatus.toString());
            }
        }
    }

    public static <T> List<T> getDataList(String result, Class<T[]> clazz) {
        T[] jsonToObject = new Gson().fromJson(result, clazz);
        return new ArrayList<>(Arrays.asList(jsonToObject));
    }
}
