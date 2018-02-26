package ru.mos.polls.subscribes.service;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.List;

import ru.mos.polls.profile.service.model.Personal;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.subscribes.model.Subscription;

/**
 * Created by Trunks on 22.02.2018.
 */

public class SubscriptionService {
    public static class RequestSetEmail extends AuthRequest {
        Personal personal;
        @SerializedName("subscriptions")
        List<Subscription> subscriptions;

        public void setPersonal(String email) {
            this.personal = new Personal();
            personal.setEmail(email);
        }

        public void setSubscriptions(List<Subscription> subscriptions) {
            this.subscriptions = subscriptions;
        }
    }

    public static class LoadSubscription extends AuthRequest {
        @SerializedName("poll_id")
        Long pollId;
        @SerializedName("event_id")
        Long eventId;
        @SerializedName("subscription_types")
        String[] subscriptionTypes;

        public void setPollId(Long pollId) {
            this.pollId = pollId;
        }

        public void setEventId(Long eventId) {
            this.eventId = eventId;
        }

        public void setSubscriptionTypes(String[] subscriptionTypes) {
            this.subscriptionTypes = subscriptionTypes;
        }
    }

    public static class SaveSubscription extends AuthRequest {
        JsonArray subscriptions;

        public void setSubscriptions(JsonArray subscriptions) {
            this.subscriptions = subscriptions;
        }
    }

    public static class Response extends GeneralResponse<Response[]> {
    }

    public static class LoadResponse extends GeneralResponse<JSONObject> {

        public static class Result {
            @SerializedName("subscriptions")
            List<Subscription> subscriptions;

            public List<Subscription> getSubscriptions() {
                return subscriptions;
            }

        }

    }
}
