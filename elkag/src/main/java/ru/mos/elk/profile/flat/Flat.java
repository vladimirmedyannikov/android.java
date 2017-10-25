package ru.mos.elk.profile.flat;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import ru.mos.elk.ElkTextUtils;
import ru.mos.elk.R;
import ru.mos.elk.db.UserData;
import ru.mos.elk.db.UserDataProvider;

/**
 * Структура данных для работы с данными по квартирам пользователя
 *
 * @since 1.9
 */
public class Flat implements Serializable {
    public static String FLAT_TYPE_OWN = "own";
    private static final Uri URI = UserDataProvider.getContentUri(UserData.Flats.URI_CONTENT);
    private static final String[] ARGS = new String[]{"1"};
    private static final String WHERE = "%s = ?";
    @SerializedName("flat_id")
    private String flatId;
    @SerializedName("building_id")
    private String buildingId;
    private String flat;
    private String building;
    private String street;
    private String city;
    @SerializedName("flat_type")
    private String flatType;
    private Type type;
    @SerializedName("editing_blocked")
    private boolean enable;

    public static void clearAll(Context context) {
        ContentResolver cr = context.getContentResolver();
        cr.delete(UserDataProvider.getContentUri(UserData.Flats.URI_CONTENT), null, null);
    }

    /**
     * Наименование округа
     *
     * @since 2.0.0
     */
    private String district;
    /**
     * Наименование района
     *
     * @since 2.0.0
     */
    private String area;
    /**
     * Идентификатор района
     *
     * @since 2.0.0
     */
    private String areaId;

    public Flat() {
        flatId = "";
        buildingId = "";
        flat = "";
        building = "";
        street = "";
        city = "";
        district = "";
        area = "";
        areaId = "";
    }

    /**
     * Парсинг данных квартиры по адресу регистрации
     *
     * @param flatsJson тег flats
     * @return объект квартиры с данными по адресу регистрации
     */
    public static Flat getRegistration(JSONObject flatsJson) {
        return fromJson(flatsJson, Type.REGISTRATION);
    }

    /**
     * Парсинг данных квартиры по адресу проживания
     *
     * @param flatsJson тег flats
     * @return объект квартиры с данными по адресу проживания
     */
    public static Flat getResidence(JSONObject flatsJson) {
        return fromJson(flatsJson, Type.RESIDENCE);
    }

    /**
     * Парсинг данных квартиры по адресу работы
     *
     * @param flatsJson тег flats
     * @return объект квартиры с данными по адресу работы
     */
    public static Flat getWork(JSONObject flatsJson) {
        return fromJson(flatsJson, Type.WORK);
    }

    /**
     * Объект квартиры из json ответа  сервера
     *
     * @param flatsJson тег flats, содержащий описание квартиры
     * @param type      тип квартиры
     * @return объект квартиры пользоватля вг
     */
    public static Flat fromJson(JSONObject flatsJson, Type type) {
        Flat result = new Flat();
        if (flatsJson != null) {
            JSONObject flatJson = flatsJson.optJSONObject(type.getType());
            if (flatJson != null) {
                result.flat = ElkTextUtils.getString(flatJson, "flat", "");
                result.flatId = ElkTextUtils.getString(flatJson, "flat_id", "");
                result.buildingId = ElkTextUtils.getString(flatJson, "building_id", "");
                result.building = ElkTextUtils.getString(flatJson, "building", "");
                result.street = ElkTextUtils.getString(flatJson, "street", "");
                result.city = ElkTextUtils.getString(flatJson, "city", "");
                result.enable = !flatJson.optBoolean("editing_blocked");
                if (flatJson.has("district")) {
                    result.district = ElkTextUtils.getString(flatJson, "district", "");
                }
                if (flatJson.has("area")) {
                    result.area = ElkTextUtils.getString(flatJson, "area", "");
                }
                if (flatJson.has("area_id")) {
                    result.areaId = ElkTextUtils.getString(flatJson, "area_id", "");
                }
                result.type = type;
            }
        }
        return result;
    }

    /**
     * Квартира с адресом регистрации из локальной бд
     *
     * @param context
     * @return объект квартиры регистрации
     */
    public static Flat getRegistration(Context context) {
        return fromDb(context, Type.REGISTRATION);
    }

    /**
     * Квартира с адресом проживания из локальной бд
     *
     * @param context
     * @return объект квартиры проживания
     */
    public static Flat getResidence(Context context) {
        return fromDb(context, Type.RESIDENCE);
    }

    /**
     * Квартира с адресом работы из локальной бд
     *
     * @param context
     * @return объект квартиры работы
     */
    public static Flat getWork(Context context) {
        return fromDb(context, Type.WORK);
    }

    /**
     * Объект квартиы, сохраненный локально на устройстве
     *
     * @param context
     * @param type    тип кватиры {@link ru.mos.elk.profile.flat.Flat.Type}
     * @return
     */
    public static Flat fromDb(Context context, Type type) {
        Flat result = new Flat();
        String[] fields = new String[]{
                BaseColumns._ID,
                UserData.Flats.FLAT_ID,
                UserData.Flats.BUILDING_ID,
                UserData.Flats.BUILDING,
                UserData.Flats.STREET,
                UserData.Flats.FLAT,
                UserData.Flats.CITY,
                UserData.Flats.IS_RESIDENCE,
                UserData.Flats.IS_REGISTRATION,
                UserData.Flats.IS_WORK,
                UserData.Flats.EDITING_BLOCKED,
                UserData.Flats.DISTRICT,
                UserData.Flats.AREA,
                UserData.Flats.AREA_ID
        };
        Cursor cursor = context.getContentResolver().query(
                URI,
                fields,
                String.format(WHERE, type.getField()),
                ARGS,
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = new Flat(cursor, type);
            } else {
                result.setType(type);
            }
            cursor.close();
        }
        return result;
    }

    /**
     * Удаление квартиры регистрации из локальной бд
     *
     * @param context
     */
    public static void deleteRegistration(Context context) {
        delete(context, Type.REGISTRATION);
    }

    /**
     * Удаление квартиры проживания из локальной бд
     *
     * @param context
     */
    public static void deleteResidence(Context context) {
        delete(context, Type.RESIDENCE);
    }

    /**
     * Удаление адреса работы
     *
     * @param context
     */
    public static void deleteWork(Context context) {
        delete(context, Type.WORK);
    }

    /**
     * Удаление адреса из локальной бд
     *
     * @param context
     * @param type    тип кватиры {@link ru.mos.elk.profile.flat.Flat.Type}
     */
    public static void delete(Context context, Type type) {
        ContentResolver cr = context.getContentResolver();
        cr.delete(URI,
                String.format(WHERE, type.getField()),
                ARGS);
    }

    /**
     * Квартира пользователя
     *
     * @param cursor курсор таблицы flat
     * @param type   тип кватиры {@link ru.mos.elk.profile.flat.Flat.Type}
     */
    public Flat(Cursor cursor, Type type) {
        if (cursor != null) {
            this.type = type;
            flatId = cursor.getString(cursor.getColumnIndex(UserData.Flats.FLAT_ID));
            buildingId = cursor.getString(cursor.getColumnIndex(UserData.Flats.BUILDING_ID));
            flat = cursor.getString(cursor.getColumnIndex(UserData.Flats.FLAT));
            building = cursor.getString(cursor.getColumnIndex(UserData.Flats.BUILDING));
            street = cursor.getString(cursor.getColumnIndex(UserData.Flats.STREET));
            city = cursor.getString(cursor.getColumnIndex(UserData.Flats.CITY));
            enable = 1 == cursor.getInt(cursor.getColumnIndex(UserData.Flats.EDITING_BLOCKED));
            district = cursor.getString(cursor.getColumnIndex(UserData.Flats.DISTRICT));
            area = cursor.getString(cursor.getColumnIndex(UserData.Flats.AREA));
            areaId = cursor.getString(cursor.getColumnIndex(UserData.Flats.AREA_ID));
        }
    }

    /**
     * Добавление данных квартиры к телу запроса, для сохранения данных квартиры на сервере аг
     *
     * @param flatsJson тело запроса
     */
    public void addToFlatsJson(JSONObject flatsJson) {
        if (flatsJson != null) {
            try {
                JSONObject flatJson = asJsonForAdd();
                if (!ElkTextUtils.isEmpty(flatId)) {
                    flatJson = asJsonForUpdate();
                }
                flatsJson.put(type.getType(), flatJson);
            } catch (JSONException ignored) {
            }
        }
    }

    /**
     * Данные квартиры в объекте json
     *
     * @return json
     */
    public JSONObject asJsonForAdd() {
        JSONObject result = new JSONObject();
        try {
            result.put("building_id", buildingId);
            if ("userid".equalsIgnoreCase(buildingId)) {
                result.put("flat", flat);
                result.put("building", building);
                result.put("street", street);
                result.put("city", city);
                result.put("area_id", areaId);
            }
        } catch (JSONException ignored) {
        }
        return result;
    }

    public JSONObject asJsonForUpdate() {
        JSONObject result = new JSONObject();
        try {
            result.put("flat_id", flatId);
            result.put("building_id", buildingId);
            if ("userid".equalsIgnoreCase(buildingId)) {
                result.put("flat", flat);
                result.put("building", building);
                result.put("street", street);
                result.put("city", city);
                result.put("area_id", areaId);
            }
        } catch (JSONException ignored) {
        }
        return result;
    }

    /**
     * Данные квартиры для сохранения в бд
     *
     * @return объект ContentValues
     */
    public ContentValues asContentValues() {
        ContentValues result = new ContentValues();
        result.put(UserData.Flats.FLAT_ID, flatId);
        result.put(UserData.Flats.BUILDING_ID, buildingId);
        result.put(UserData.Flats.FLAT, flat);
        result.put(UserData.Flats.BUILDING, building);
        result.put(UserData.Flats.STREET, street);
        result.put(UserData.Flats.CITY, city);
        result.put(UserData.Flats.IS_REGISTRATION, isRegistration() ? 1 : 0);
        result.put(UserData.Flats.IS_RESIDENCE, isResidence() ? 1 : 0);
        result.put(UserData.Flats.IS_WORK, isWork() ? 1 : 0);
        result.put(UserData.Flats.EDITING_BLOCKED, enable ? 1 : 0);
        result.put(UserData.Flats.DISTRICT, district);
        result.put(UserData.Flats.AREA, area);
        result.put(UserData.Flats.AREA_ID, areaId);
        return result;
    }

    /**
     * Локальное сохранение данных в бд
     *
     * @param context
     * @return uri записи
     */
    public Uri save(Context context) {
        Uri result = null;
        if (!isEmpty()) {
            ContentResolver cr = context.getContentResolver();
            result = cr.insert(URI, asContentValues());
        }
        return result;
    }

    public void delete(Context context) {
        delete(context, type);
    }

    public boolean isEmpty() {
        return ElkTextUtils.isEmpty(buildingId)
//                && TextUtils.isEmpty(flat)
                && ElkTextUtils.isEmpty(building)
                && ElkTextUtils.isEmpty(street);
    }

    public String getViewTitle(Context context) {
        String result = context.getString(R.string.add_address);
        if (!isEmpty()) {
            result = String.format(context.getString(R.string.title_formatted_address), street, building);
        }
        return result;
    }

    public String getAddressTitle(Context context) {
        String result = context.getString(R.string.address_not_specified);
        if (!isEmpty()) {
            result = String.format(context.getString(R.string.title_formatted_address), street, building);
        }
        return result;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public Flat setBuildingId(String buildingId) {
        this.buildingId = buildingId;
        return this;
    }

    public String getFlat() {
        return flat;
    }

    public Flat setFlat(String flat) {
        this.flat = flat;
        return this;
    }

    public String getBuilding() {
        return building;
    }

    public Flat setBuilding(String building) {
        this.building = building;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public Flat setStreet(String street) {
        this.street = street;
        return this;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isRegistration() {
        return Type.REGISTRATION == type;
    }

    public boolean isResidence() {
        return Type.RESIDENCE == type;
    }

    public boolean isWork() {
        return Type.WORK == type;
    }

    public boolean isOwn() {
        return Type.OWN == type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public boolean compareByFullAddress(Flat other) {
        return other != null
                && street.equalsIgnoreCase(other.street)
                && building.equalsIgnoreCase(other.building);
    }

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o != null && o instanceof Flat) {
            Flat other = (Flat) o;
            result = buildingId.equalsIgnoreCase(other.buildingId)
                    && building.equalsIgnoreCase(other.building)
                    && flat.equalsIgnoreCase(other.flat)
                    && street.equalsIgnoreCase(other.street)
                    && type == other.type;

        }
        return result;
    }

    public Flat setCity(String city) {
        this.city = city;
        return this;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public Flat setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getArea() {
        return area;
    }

    public String getFlatId() {
        return flatId;
    }

    public Flat setArea(String area) {
        this.area = area;
        return this;
    }

    public String getAreaId() {
        return areaId;
    }

    public Flat setAreaId(String areaId) {
        this.areaId = areaId;
        return this;
    }

    /**
     * Тип квартиры (адреса)
     * С версии 1.9 поддерживаются адреса регистрации, проживания, работы
     */
    public enum Type {
        REGISTRATION("registration", UserData.Flats.IS_REGISTRATION),
        RESIDENCE("residence", UserData.Flats.IS_RESIDENCE),
        WORK("work", UserData.Flats.IS_WORK),
        OWN("own", "is_own");

        private String type;
        private String field;

        Type(String type, String field) {
            this.type = type;
            this.field = field;
        }

        public String getType() {
            return type;
        }

        public String getField() {
            return field;
        }

    }
}
