package ru.mos.polls.survey.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.survey.experts.DetailsExpert;

public class GetExpertList {

    public static class Request extends AuthRequest {
        @SerializedName("hearing_id")
        Long hearingId;
        @SerializedName("poll_id")
        Long pollId;
        @SerializedName("question_id")
        Long questionId;

        public void setHearingId(Long hearingId) {
            this.hearingId = hearingId;
        }

        public void setPollId(Long pollId) {
            this.pollId = pollId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }
    }

    public static class Response extends GeneralResponse<GetExpertList.Response.Result> {
        public static class Result {
            @SerializedName("experts")
            List<DetailsExpert> experts;

            public List<DetailsExpert> getExperts() {
                return experts;
            }
        }
    }
}
