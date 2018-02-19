package ru.mos.polls.survey.hearing.service;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * Created by Trunks on 19.02.2018.
 */

public class HearingCheck {
    public static class Request extends AuthRequest {
        @SerializedName("hearing_id")
        long hearingId;
        @SerializedName("meeting_id")
        long meetingId;

        public Request(long hearingId, long meetingId) {
            this.hearingId = hearingId;
            this.meetingId = meetingId;
        }
    }

    public static class Response extends GeneralResponse<HearingCheck.Response.Result> {

        public static class Result {
            @SerializedName("message")
            QuestMessage message;

            public QuestMessage getMessage() {
                return message;
            }

        }
    }
}
