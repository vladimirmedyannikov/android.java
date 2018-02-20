package ru.mos.polls.event.controller.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.common.model.PageInfo;
import ru.mos.polls.event.model.CommentPageInfo;
import ru.mos.polls.event.model.EventComment;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 20.02.18.
 */

public class GetEventCommentsList {
    public static class Request extends AuthRequest {
        @SerializedName("event_id")
        private long eventId;
        @SerializedName("count_per_page")
        private int countPerPage;
        @SerializedName("page_number")
        private int pageNumber;


        public Request(long eventId, final PageInfo pageInfo) {
            this.eventId = eventId;
            if (pageInfo != null) {
                this.countPerPage = pageInfo.getCountPerPage();
                this.pageNumber = pageInfo.getPageNumber();
            }
        }
    }

    public static class Response extends GeneralResponse<Response.Result> {
        public static class Result {
            @SerializedName("page_info")
            private CommentPageInfo pageInfo;
            @SerializedName("my_comment")
            private EventComment myComment;
            @SerializedName("comments")
            private List<EventComment> comments;

            public CommentPageInfo getPageInfo() {
                return pageInfo;
            }

            public EventComment getMyComment() {
                return myComment;
            }

            public List<EventComment> getComments() {
                return comments;
            }
        }
    }
}