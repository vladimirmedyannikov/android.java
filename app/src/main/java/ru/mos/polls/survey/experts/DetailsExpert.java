package ru.mos.polls.survey.experts;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.helpers.TextHelper;

/**
 * Структура данных,описывающая мнение эксперта
 *
 * @since 1.8
 */
public class DetailsExpert implements Serializable {
    private long id;
    @SerializedName("img_url")
    private String imgUrl;
    private String description;
    private String title;
    private String body;

    public static List<DetailsExpert> fromJson(JSONArray jsonArray) {
        List<DetailsExpert> result = null;
        if (jsonArray != null) {
            result = new ArrayList<DetailsExpert>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); ++i) {
                DetailsExpert detailsExpert = new DetailsExpert(jsonArray.optJSONObject(i));
                result.add(detailsExpert);
            }
        }
        return result;
    }

    public DetailsExpert(JSONObject expertDetailJson) {
        if (expertDetailJson != null) {
            id = expertDetailJson.optInt("id");
            imgUrl = TextHelper.getString(expertDetailJson, "img_url", "");
            description = TextHelper.getString(expertDetailJson, "description", "");
            title = TextHelper.getString(expertDetailJson, "title", "");
            body = TextHelper.getString(expertDetailJson, "body", "");
        }
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public boolean isEmpty() {
        return isEmpty(imgUrl) || isEmpty(description) || isEmpty(title);
    }

    public boolean isEmpty(String target) {
        return TextUtils.isEmpty(target) || "null".equalsIgnoreCase(target);
    }

    public boolean compare(DetailsExpert other) {
        boolean result = false;
        if (other != null) {
            result = id == other.id;
        }
        return result;
    }

    public long getId() {
        return id;
    }
}
