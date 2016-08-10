package ru.mos.polls.social.model;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.helpers.TextHelper;
import ru.mos.polls.social.manager.SocialManager;


public class SocialPostValue implements Serializable {
    private static final String URL_OK_WIDGET_MEDIATOPIC_POST = "http://connect.ok.ru/dk?st.cmd=WidgetMediatopicPost&st.app=%s&st.attachment=%s&st.signature=%s&st.popup=on&st.utext=on&st.access_token=%s";
    /**
     * @see <a href="http://www.geek.com/news/twitter-now-limits-tweets-to-117-characters-if-you-include-a-link-1540340/">
     * Почему 115? чтоб навреняка! =)</a>
     */
    public static final int MAX_TWEET_POST_LENGTH = 115;
    public static final int MAX_OK_POST_LENGTH = 255;

    private int socialId;
    private String socialName;
    private String title;
    private String text;
    private String link;
    private String image;
    private boolean isMustServerNotified;
    private boolean enable;
    private Type type;
    /**
     * Для статистики  требуется идентифиактор события или опроса,
     * будем хранить их здесь
     */
    private Object id;

    public SocialPostValue() {
    }

    public SocialPostValue(SocialPostValue socialPostValue, String socialName) {
        if (socialPostValue != null) {
            setSocialName(socialName);
            setSocialId(socialName);
            setText(socialPostValue.getText());
            setLink(socialPostValue.getLink());
            setImage(socialPostValue.getImage());
            setNotify(socialPostValue.isMustServerNotified());
            setType(socialPostValue.getType());
            setEnable(socialPostValue.isEnable());
            setId(socialPostValue.getId());
        }
    }

    public SocialPostValue(JSONObject socialJson, Type type) {
        this("", socialJson, type, -1);
    }

    public SocialPostValue(String socialName, JSONObject socialJson, Type type, Object id) {
        if (socialJson != null) {
            setSocialName(socialName);
            setSocialId(socialName);
            setText(socialJson.optString("text"));
            setLink(socialJson.optString("url"));
            setImage(socialJson.optString("img"));
            setNotify(true);
            setType(type);
            /**
             * ограничение на постинг
             */
            boolean enable = true;
            if (AGApplication.IS_POSTING_LIMITATION) {
                enable = socialJson.optBoolean("enable");
            }
            setEnable(enable);
            setId(id);
        }
    }

    public Bundle getFbParams() {
        Bundle result = new Bundle(2);
        result.putString("message", text);
        result.putString("link", link);
        return result;
    }

    public List<NameValuePair> getVkParams(TokenData tokenData) {
        final List<NameValuePair> result = new ArrayList<NameValuePair>();
        result.add(new BasicNameValuePair("message", text));
        String attms = link;
        if (!TextUtils.isEmpty(image)) {
            if (!TextUtils.isEmpty(attms)) {
                attms = "," + image;
            } else {
                attms = image;
            }
        }
        result.add(new BasicNameValuePair("attachments", attms));
        result.add(new BasicNameValuePair("access_token", tokenData.getAccessToken()));
        return result;
    }

    /**
     * Формирование параметров для постинга в ОК.ru метода mediatopic.post
     *
     * @return
     * @since 1.9.2
     */
    public Map<String, String> getOkAttachments() {
        Map<String, String> result = new HashMap<String, String>();
        JSONObject attachments = new JSONObject();
        JSONArray media = new JSONArray();
        JSONObject param = new JSONObject();
        try {
            /**
             * add link
             */
            param.put("type", "link");
            param.put("url", link);
            media.put(param);
            /**
             * add text
             */
            param = new JSONObject();
            param.put("type", "text");
            param.put("text", text);
            media.put(param);

            attachments.put("media", media);
        } catch (JSONException ignored) {
        }
        result.put("attachment", attachments.toString());
        return result;
    }

    public JSONObject getOkAttachmentsJson() {
        JSONObject attachments = new JSONObject();
        JSONArray media = new JSONArray();
        JSONObject param = new JSONObject();
        try {
            /**
             * add link
             */
            param.put("type", "link");
            param.put("url", link);
            media.put(param);
            /**
             * add text
             */
            param = new JSONObject();
            param.put("type", "text");
            param.put("text", text);
            media.put(param);

            attachments.put("media", media);
        } catch (JSONException ignored) {
        }
        return attachments;
    }

    /**
     * Формирование параметров для постинга в ok.ru для метода share.addLink
     * Устарел с изменением api ok.ru
     * Вместо него теперь исопльзуется метод {@link SocialPostValue#getOkAttachments()}
     *
     * @return
     */
    @Deprecated
    public Map<String, String> getOkParams() {
        Map<String, String> result = new HashMap<String, String>();
        result.put("linkUrl", link);
        result.put("comment", text);
        return result;
    }

    public Bundle prepareFbPost() {
        Bundle result = new Bundle(2);
        result.putString("message", text);
        result.putString("link", link);
        return result;
    }

    /**
     * @return строка для постинга в {@see <a href="httP://twitter.com">twitter</a>}
     */
    public String prepareTwPost() {
        String result = "";
        if (!TextUtils.isEmpty(title)) result += title + "\n";
        if (!TextUtils.isEmpty(text)) result += text + " ";
        if (!TextUtils.isEmpty(link)) result += link;
        return result;
    }

    public int getSocialId() {
        return socialId;
    }

    public String getSocialName() {
        return socialName;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getLink() {
        return link;
    }

    public String getImage() {
        return image;
    }

    public boolean isEnable() {
        return enable;
    }

    public boolean isMustServerNotified() {
        return isMustServerNotified;
    }

    public Type getType() {
        return type;
    }

    public Object getId() {
        return id;
    }

    public SocialPostValue setSocialName(String socialName) {
        this.socialName = socialName;
        return this;
    }

    public SocialPostValue setImage(String image) {
        this.image = image;
        return this;
    }

    public SocialPostValue setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public SocialPostValue setNotify(boolean isMustServerNotified) {
        this.isMustServerNotified = isMustServerNotified;
        return this;
    }

    public SocialPostValue setText(String text) {
        this.text = text;
        return this;
    }

    public SocialPostValue setLink(String link) {
        this.link = link;
        return this;
    }

    public SocialPostValue setType(Type type) {
        this.type = type;
        return this;
    }

    public SocialPostValue setSocialId(int socialId) {
        this.socialId = socialId;
        return this;
    }

    public SocialPostValue setSocialId(String socialName) {
        this.socialId = SocialManager.getSocialId(socialName);
        return this;
    }

    public SocialPostValue setTitle(String title) {
        this.title = title;
        return this;
    }

    public SocialPostValue setId(Object id) {
        this.id = id;
        return this;
    }

    public String getPostingUrlForOk(Context context) {
        String result = "";
        if (forOk()) {
            final String id = context.getString(R.string.ok_app_id);
            final String secret = context.getString(R.string.ok_app_secret);
            final String key = context.getString(R.string.ok_app_key);
            final String attachment = getOkAttachmentsJson().toString();
            final String returnUrl = "http://google.com";
            final String signature = TextHelper.md5("st.attachment=" + attachment + key);
            String accessToken = SocialManager.getAccessToken(context, socialId);
            result = String.format(URL_OK_WIDGET_MEDIATOPIC_POST,
                    id, attachment, signature, accessToken);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        boolean isEquals;
        if (o instanceof SocialPostValue) {
            SocialPostValue other = (SocialPostValue) o;
            isEquals = text.equalsIgnoreCase(other.getText())
                    && link.equalsIgnoreCase(other.getLink())
                    && image.equalsIgnoreCase(other.getImage());
        } else {
            isEquals = false;
        }
        return isEquals;
    }

    public boolean isForNovelty() {
        return hasType(Type.NOVELTY);
    }

    public boolean isForAchievement() {
        return hasType(Type.ACHIEVEMENT);
    }

    public boolean isForPoll() {
        return hasType(Type.POLL);
    }

    public boolean isForCheckIn() {
        return hasType(Type.CHECK_IN);
    }

    public boolean isForTask() {
        return hasType(Type.TASK);
    }

    public boolean isForHearing() {
        return hasType(Type.HEARING);
    }

    public boolean hasType(Type type) {
        return this.type == type;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(text) && TextUtils.isEmpty(link) && TextUtils.isEmpty(image);
    }

    public boolean forTwitter() {
        return socialId == SocialManager.SOCIAL_ID_TW;
    }

    public boolean forOk() {
        return socialId == SocialManager.SOCIAL_ID_OK;
    }

    public boolean isPostMuchLong() {
        boolean result = false;
        switch (socialId) {
            case SocialManager.SOCIAL_ID_TW:
                result = text.length() > MAX_TWEET_POST_LENGTH;
                break;
            case SocialManager.SOCIAL_ID_OK:
                result = text.length() > MAX_OK_POST_LENGTH;
                break;
        }
        return result;
    }

    public String getWarningTitle(Context context) {
        return String.format(context.getString(R.string.warning_post_mutch_long), getMaxSymbolsInPost());
    }

    public int getMaxSymbolsInPost() {
        int result = 0;
        switch (socialId) {
            case SocialManager.SOCIAL_ID_OK:
                result = MAX_OK_POST_LENGTH;
                break;
            case SocialManager.SOCIAL_ID_TW:
                result = MAX_TWEET_POST_LENGTH;
                break;
        }
        return result;
    }

    public boolean forFb() {
        return socialId == SocialManager.SOCIAL_ID_FB;
    }

    public String preparePost() {
        return text + " " + link;
    }

    public static enum Type {
        POLL,
        CHECK_IN,
        ACHIEVEMENT,
        NOVELTY,
        TASK,
        HEARING;

        public String getName() {
            String result = "";
            switch (this) {
                case POLL:
                    result = "poll_id";
                    break;
                case CHECK_IN:
                    result = "event_id";
                    break;
                case ACHIEVEMENT:
                    result = "achievement_id";
                    break;
                case NOVELTY:
                    result = "novelty_id";
                    break;
                case HEARING:
                    result = "hearing_id";
                    break;
            }
            return result;
        }

        public boolean isTask() {
            return this == TASK;
        }
    }
}
