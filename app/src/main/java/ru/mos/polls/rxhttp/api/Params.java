package ru.mos.polls.rxhttp.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.mos.polls.rxhttp.Page;
import ru.mos.polls.rxhttp.api.session.Session;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 06.06.17 12:49.
 */

public class Params {
    public static Params init() {
        return new Params();
    }

    private Map<String, Object> values;

    public Params() {
        values = new HashMap<>();
    }

    public Params useAuth() {
        JSONObject session = new JSONObject();
        try {
            session.put("session_id", Session.get().getSession());
        } catch (JSONException ignored) {
        }
        return add("auth", session);
    }

    public Params usePage(Page page) {
        return add("count_per_page", page.getSize())
                .add("page_number", page.getNum());
    }

    public Params add(String tag, Object value) {
        values.put(tag, value);
        return this;
    }

    public Map<String, Object> get() {
        return values;
    }

}
