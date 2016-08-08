package ru.mos.polls.event.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.common.model.Position;

/**
 * Структура данных, которая используется как для хранения
 * элемента списка мероприятий, так и объекта с полным описанием мероприятия
 */
public class Event {
    public static final int NOT_CALCULATED_DISTANCE = -1;

    private long id;
    private double avrRating;
    private Type type;
    private String name;
    private String title;
    private String address;
    private String description;
    private Position position;
    private boolean isCheckIn;
    private List<String> imgLinks;
    private List<EventDetail> details;
    private int points;
    private int maxCheckInDistance;
    /**
     * Рассчитываемое поле, хранит расстояние в метрах от текущего пользователя до мероприятия
     */
    private int distance = NOT_CALCULATED_DISTANCE;

    private String startDate;
    private String endDate;

    /**
     * Вызывавать для создания элемента списка событий в ленте событий
     *
     * @param commonJson - json "укороченного" события в ленте событий
     * @return объект событий, содержащий неполную информацию о событии (для ленты событий)
     */
    public static Event fromJsonCommon(JSONObject commonJson) {
        Event result = getBaseEvent(commonJson);
        result.description = commonJson.optString("details");
        result.imgLinks = null;
        result.details = null;
        return result;
    }

    /**
     * Вызывать для создания объекта с полной информацией о мероприятии
     *
     * @param fullJson - json, полностью описывающий объект мероприятия
     * @return - объект мероприятия, содежащий полное описание
     */
    public static Event fromJsonFull(JSONObject fullJson) {
        JSONObject commonJson = fullJson.optJSONObject("common");
        Event result = getBaseEvent(commonJson);
        result.points = fullJson.optInt("points");
        result.startDate = fullJson.optString("start_date");
        result.endDate = fullJson.optString("end_date");
        result.details = getDetails(fullJson.optJSONArray("details"));
        result.imgLinks = getImgLinks(fullJson.optJSONArray("img_links"));
        return result;
    }

    public long getId() {
        return id;
    }

    public double getAvrRating() {
        return avrRating;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isCheckIn() {
        return isCheckIn;
    }

    public List<String> getImgLinks() {
        return imgLinks;
    }

    public List<EventDetail> getDetails() {
        return details;
    }

    public int getPoints() {
        return points;
    }

    public String getTitle() {
        return title;
    }

    public void setDistance(Position currentPosition) {
        distance = Position.distance(currentPosition, position);
    }

    public int getDistance() {
        return distance;
    }

    public int getMaxCheckInDistance() {
        return maxCheckInDistance;
    }

    public void setChecked() {
        this.isCheckIn = true;
    }

    /**
     * Проверяем, действительно ли опльзователь находится в в нужном для чекина месте
     *
     * @param position - текущее местоположение пользователя
     * @return
     */
    public boolean isCheckInEnable(Position position) {
        boolean result;
        if (maxCheckInDistance == 0) {
            /**
             * если не передавали тот параметр, то проверку отключаем
             */
            result = true;
        } else {
            int distance = Position.distance(this.position, position);
            result = distance < maxCheckInDistance;
        }

        return result;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public boolean hasPastDate() {
        return isCurrentTimeMore(endDate);
    }

    public boolean isEventYetGoing() {
        return isCurrentTimeMore(startDate);
    }

    private boolean isCurrentTimeMore(String formattedTime) {
        boolean result = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long end = sdf.parse(formattedTime).getTime();
            long current = System.currentTimeMillis();
            result = current >= end;
        } catch (ParseException ignored) {
        }
        return result;
    }

    public long getMillsOfStartDate() {
        return getMills(startDate);
    }

    public long getMillsOfEndDate() {
        return getMills(endDate);
    }

    private long getMills(String formattedTime) {
        long result = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            result = sdf.parse(formattedTime).getTime();
        } catch (ParseException ignored) {
        }
        return result;
    }

    /**
     * Метод, создающии базовую часть как для объекта мероприятия с кратким описанием, так и для объекта мероприятия с полным описанием
     *
     * @param commonJson - любой тип json: или json для объекта списка ленты мероприятий. или json для объекта детализации мероприятия
     * @return - объект мероприятия с основными заполненными полями
     */
    private static Event getBaseEvent(JSONObject commonJson) {
        Event result = new Event();
        result.id = commonJson.optInt("id"); // в тестовых данных при запросе полного id возвращается ti
        result.avrRating = commonJson.optDouble("avg_rating");
        result.type = Type.parse(commonJson.optString("type", "event"));
        result.name = commonJson.optString("name");
        result.title = commonJson.optString("title");
        result.address = commonJson.optString("address");
        result.points = commonJson.optInt("points");
        result.startDate = commonJson.optString("start_date");
        result.endDate = commonJson.optString("end_date");
        result.distance = commonJson.optInt("distance");
        try {
            result.position = new Position(commonJson.getJSONObject("position"));
        } catch (JSONException e) {
            result.position = new Position();
        }
        int checkIn = commonJson.optInt("checkin");
        result.isCheckIn = checkIn == 1;
        result.maxCheckInDistance = commonJson.optInt("max_checkin_distance");
        return result;
    }

    /**
     * Список детализации мероприятия
     *
     * @param detailsArray
     * @return
     */
    private static List<EventDetail> getDetails(JSONArray detailsArray) {
        List<EventDetail> result = new ArrayList<EventDetail>();
        for (int i = 0; i < detailsArray.length(); ++i) {
            JSONObject detailJson = detailsArray.optJSONObject(i);
            EventDetail eventDetail = new EventDetail(detailJson);
            result.add(eventDetail);
        }
        return result;
    }

    /**
     * Список url
     *
     * @param detailsArray
     * @return
     */
    private static List<String> getImgLinks(JSONArray detailsArray) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < detailsArray.length(); ++i) {
            String imgLink = detailsArray.optString(i);
            result.add(imgLink);
        }
        return result;
    }

    /**
     * Тип события
     */
    public static enum Type {
        PLACE("Место"),
        EVENT("Мероприятие");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public static Type parse(String type) {
            Type result = null;
            if ("event".equalsIgnoreCase(type)) {
                result = EVENT;
            } else if ("place".equalsIgnoreCase(type)) {
                result = PLACE;
            }
            return result;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}