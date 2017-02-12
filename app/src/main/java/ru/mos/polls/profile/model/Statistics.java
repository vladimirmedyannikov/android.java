package ru.mos.polls.profile.model;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Стуктура данных для хранения информации о статистике пользователя
 * появилась вверсии 1.9
 */
public class Statistics {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    private long lastVisit;
    private int passedPolls;
    private int answeredQuestions;
    private int eventsVisited;
    private int allPoints;
    private int spentPoints;
    private int promoCodes;
    private int socialMessages;
    private long registrationDate;

    public Statistics(JSONObject statisticsJson) {
        if (statisticsJson != null) {
            lastVisit = statisticsJson.optLong("last_visit") * 1000;
            passedPolls = statisticsJson.optInt("passed_polls");
            eventsVisited = statisticsJson.optInt("events_visited");
            answeredQuestions = statisticsJson.optInt("answered_questions");
            allPoints = statisticsJson.optInt("all_points");
            spentPoints = statisticsJson.optInt("spent_points");
            promoCodes = statisticsJson.optInt("promo_codes");
            socialMessages = statisticsJson.optInt("social_messages");
            registrationDate = statisticsJson.optLong("registration_date") * 1000;
        }
    }

    public long getLastVisit() {
        return lastVisit;
    }

    public String getLastVisitFormatted() {
        return sdf.format(lastVisit);
    }

    public long getRegistrationDate() {
        return registrationDate;
    }

    public String getRegistrationDateFormatted() {
        return sdf.format(registrationDate);
    }

    public int getPassedPolls() {
        return passedPolls;
    }

    public int getAnsweredQuestions() {
        return answeredQuestions;
    }

    public int getEventsVisited() {
        return eventsVisited;
    }

    public int getAllPoints() {
        return allPoints;
    }

    public int getSpentPoints() {
        return spentPoints;
    }

    public int getPromoCodes() {
        return promoCodes;
    }

    public int getSocialMessages() {
        return socialMessages;
    }

}
