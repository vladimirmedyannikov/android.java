package ru.mos.polls.event.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Структура для хранения данных о старнице комментариев
 */
public class CommentPageInfo {
    @SerializedName("count_per_page")
    private int countPerPage;
    @SerializedName("page_number")
    private int pageNumber;
    @SerializedName("page_count")
    private int pageCount;
    @SerializedName("all_events_count")
    private int allEventsCount;

    public CommentPageInfo() {
    }

    public CommentPageInfo(JSONObject pageInfo) {
        countPerPage = pageInfo.optInt("count_per_page");
        pageNumber = pageInfo.optInt("page_number");
        pageCount = pageInfo.optInt("page_count");
        allEventsCount = pageInfo.optInt("all_events_count");
    }

    public int getCountPerPage() {
        return countPerPage;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getAllEventsCount() {
        return allEventsCount;
    }

    public void incrementPage() {
        ++pageNumber;
    }
}
