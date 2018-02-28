package ru.mos.polls.event.model;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import ru.mos.polls.common.model.Position;


public class EventRX {
    @SerializedName("common")
    private CommonBody commonBody;
    @SerializedName("points")
    private int points;
    @SerializedName("start_date")
    private String startDate = "";
    @SerializedName("end_date")
    private String endDate = "";
    @SerializedName("details")
    private List<EventDetail> details;
    @SerializedName("img_links")
    private List<String> imgLinks;

    public CommonBody getCommonBody() {
        return commonBody;
    }

    public int getPoints() {
        return points;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public List<EventDetail> getDetails() {
        return details;
    }

    public List<String> getImgLinks() {
        return imgLinks;
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

    public static class CommonBody {
        @SerializedName("id")
        private int id;
        @SerializedName("avg_rating")
        private double avgRating;
        @SerializedName("type")
        private String type;
        @SerializedName("name")
        private String name;
        @SerializedName("title")
        private String title;
        @SerializedName("position")
        private Position position;
        @SerializedName("checkin")
        private int checkin;
        @SerializedName("max_checkin_distance")
        private int maxCheckinDistance;

        public int getId() {
            return id;
        }

        public double getAvrRating() {
            return avgRating;
        }

        public int getCheckin() {
            return checkin;
        }

        public void setChecked() {
            checkin = 1;
        }

        public Type getType() {
            return Type.parse(type);
        }

        public String getName() {
            return name;
        }

        public String getTitle() {
            return title;
        }

        public Position getPosition() {
            return position;
        }

        public boolean isCheckin() {
            return checkin == 1;
        }

        public int getMaxCheckinDistance() {
            return maxCheckinDistance;
        }

        /**
         * Проверяем, действительно ли опльзователь находится в в нужном для чекина месте
         *
         * @param position - текущее местоположение пользователя
         * @return
         */
        public boolean isCheckInEnable(Position position) {
            boolean result;
            if (maxCheckinDistance == 0) {
                /**
                 * если не передавали тот параметр, то проверку отключаем
                 */
                result = true;
            } else {
                int distance = Position.distance(this.position, position);
                result = distance < maxCheckinDistance;
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
}
