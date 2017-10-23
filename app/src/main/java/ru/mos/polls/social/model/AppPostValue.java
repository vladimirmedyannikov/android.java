package ru.mos.polls.social.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONObject;

import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.social.model.PostValue;

import static ru.mos.polls.social.model.AppPostValue.Type.ACHIEVEMENT;
import static ru.mos.polls.social.model.AppPostValue.Type.CHECK_IN;
import static ru.mos.polls.social.model.AppPostValue.Type.HEARING;
import static ru.mos.polls.social.model.AppPostValue.Type.NOVELTY;
import static ru.mos.polls.social.model.AppPostValue.Type.POLL;
import static ru.mos.polls.social.model.AppPostValue.Type.TASK;


public class AppPostValue extends PostValue {
    private static final String URL_OK_WIDGET_MEDIATOPIC_POST = "http://connect.ok.ru/dk?st.cmd=WidgetMediatopicPost&st.app=%s&st.attachment=%s&st.signature=%s&st.popup=on&st.utext=on&st.access_token=%s";
    /**
     * @see <a href="http://www.geek.com/news/twitter-now-limits-tweets-to-117-characters-if-you-include-a-link-1540340/">
     * Почему 115? чтоб навреняка! =)</a>
     */
    public static final int MAX_TWEET_POST_LENGTH = 115;
    public static final int MAX_OK_POST_LENGTH = 255;

    private int socialId;
    private String socialName;
    private String image;
    private boolean isMustServerNotified;
    private boolean enable;
    private Type type;
    /**
     * Для статистики  требуется идентифиактор события или опроса,
     * будем хранить их здесь
     */
    private Object id;

    public AppPostValue() {
    }

    public AppPostValue(AppPostValue appPostValue, String socialName) {
        if (appPostValue != null) {
            setSocialName(socialName);
            setSocialId(socialName);
            setText(appPostValue.getText());
            setLink(appPostValue.getLink());
            setImage(appPostValue.getImage());
            setNotify(appPostValue.isMustServerNotified());
            setType(appPostValue.getType());
            setEnable(appPostValue.isEnable());
            setId(appPostValue.getId());
        }
    }

    public AppPostValue(JSONObject socialJson, Type type) {
        this("", socialJson, type, -1);
    }

    public AppPostValue(String socialName, JSONObject socialJson, Type type, Object id) {
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

    public String getTypeNameForPostingRepeat() {
        String result = "запись";
        if (type != null) {
            switch (type) {
                case POLL:
                    result = "голосование";
                    break;
                case CHECK_IN:
                    result = "мероприятие";
                    break;
                case ACHIEVEMENT:
                    result = "достижение";
                    break;
                case NOVELTY:
                    result = "новинку";
                    break;
                case HEARING:
                    result = "публичное слушание";
                    break;
            }
        }
        return result;
    }

//    public Bundle getFbParams() {
//        Bundle result = new Bundle(2);
//        result.putString("message", text);
//        result.putString("link", link);
//        return result;
//    }
//
//    public List<NameValuePair> getVkParams(TokenData tokenData) {
//        final List<NameValuePair> result = new ArrayList<NameValuePair>();
//        result.add(new BasicNameValuePair("message", text));
//        String attms = link;
//        if (!TextUtils.isEmpty(image)) {
//            if (!TextUtils.isEmpty(attms)) {
//                attms = "," + image;
//            } else {
//                attms = image;
//            }
//        }
//        result.add(new BasicNameValuePair("attachments", attms));
//        result.add(new BasicNameValuePair("access_token", tokenData.getAccessToken()));
//        return result;
//    }
//
//    /**
//     * Формирование параметров для постинга в ОК.ru метода mediatopic.post
//     *
//     * @return
//     * @since 1.9.2
//     */
//    public Map<String, String> getOkAttachments() {
//        Map<String, String> result = new HashMap<String, String>();
//        JSONObject attachments = new JSONObject();
//        JSONArray media = new JSONArray();
//        JSONObject param = new JSONObject();
//        try {
//            /**
//             * add link
//             */
//            param.put("type", "link");
//            param.put("url", link);
//            media.put(param);
//            /**
//             * add text
//             */
//            param = new JSONObject();
//            param.put("type", "text");
//            param.put("text", text);
//            media.put(param);
//
//            attachments.put("media", media);
//        } catch (JSONException ignored) {
//        }
//        result.put("attachment", attachments.toString());
//        return result;
//    }

//    public JSONObject getOkAttachmentsJson() {
//        JSONObject attachments = new JSONObject();
//        JSONArray media = new JSONArray();
//        JSONObject param = new JSONObject();
//        try {
//            /**
//             * add link
//             */
//            param.put("type", "link");
//            param.put("url", link);
//            media.put(param);
//            /**
//             * add text
//             */
//            param = new JSONObject();
//            param.put("type", "text");
//            param.put("text", text);
//            media.put(param);
//
//            attachments.put("media", media);
//        } catch (JSONException ignored) {
//        }
//        return attachments;
//    }

//    /**
//     * Формирование параметров для постинга в ok.ru для метода share.addLink
//     * Устарел с изменением api ok.ru
//     * Вместо него теперь исопльзуется метод {@link AppPostValue#getOkAttachments()}
//     *
//     * @return
//     */
//    @Deprecated
//    public Map<String, String> getOkParams() {
//        Map<String, String> result = new HashMap<String, String>();
//        result.put("linkUrl", link);
//        result.put("comment", text);
//        return result;
//    }

//    public Bundle prepareFbPost() {
//        Bundle result = new Bundle(2);
//        result.putString("message", text);
//        result.putString("link", link);
//        return result;
//    }

//    /**
//     * @return строка для постинга в {@see <a href="httP://twitter.com">twitter</a>}
//     */
//    public String prepareTwPost() {
//        String result = "";
//        if (!TextUtils.isEmpty(text)) result += text + " ";
//        if (!TextUtils.isEmpty(link)) result += link;
//        return result;
//    }

    public int getSocialId() {
        return socialId;
    }

    public String getSocialName() {
        return socialName;
    }

    public String getTitle() {
        return super.getTitle();
    }

    public String getText() {
        return super.getText();
    }

    public String getLink() {
        return super.getLink();
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

    public AppPostValue setSocialName(String socialName) {
        this.socialName = socialName;
        return this;
    }

    public AppPostValue setImage(String image) {
        this.image = image;
        return this;
    }

    public AppPostValue setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public AppPostValue setNotify(boolean isMustServerNotified) {
        this.isMustServerNotified = isMustServerNotified;
        return this;
    }

    public AppPostValue setText(String text) {
        super.setText(text);
        return this;
    }

    public AppPostValue setLink(String link) {
        super.setLink(link);
        return this;
    }

    public AppPostValue setType(Type type) {
        this.type = type;
        return this;
    }
//
//    public AppPostValue setSocialId(int socialId) {
//        this.socialId = socialId;
//        return this;
//    }

    public AppPostValue setSocialId(int socialId) {
        this.socialId = socialId;
        return this;
    }

    public AppPostValue setSocialId(String socialName) {
        this.socialId = AppSocial.getId(socialName);
        return this;
    }

    public AppPostValue setTitle(String title) {
        super.setTitle(title);
        return this;
    }

    public AppPostValue setId(Object id) {
        this.id = id;
        return this;
    }
//
//    public String getPostingUrlForOk(Context context) {
//        String result = "";
//        if (forOk()) {
//            final String id = context.getString(R.string.ok_app_id);
//            final String secret = context.getString(R.string.ok_app_secret);
//            final String key = context.getString(R.string.ok_app_key);
//            final String attachment = getOkAttachmentsJson().toString();
//            final String returnUrl = "http://google.com";
//            final String signature = TextHelper.md5("st.attachment=" + attachment + key);
//            String accessToken = SocialManager.getAccessToken(context, socialId);
//            result = String.format(URL_OK_WIDGET_MEDIATOPIC_POST,
//                    id, attachment, signature, accessToken);
//        }
//        return result;
//    }

    @Override
    public boolean equals(Object o) {
        boolean isEquals;
        if (o instanceof AppPostValue) {
            AppPostValue other = (AppPostValue) o;
            isEquals = getText().equalsIgnoreCase(other.getText())
                    && getLink().equalsIgnoreCase(other.getLink())
                    && image.equalsIgnoreCase(other.getImage());
        } else {
            isEquals = false;
        }
        return isEquals;
    }

    public boolean isForNovelty() {
        return hasType(NOVELTY);
    }

    public boolean isForAchievement() {
        return hasType(ACHIEVEMENT);
    }

    public boolean isForPoll() {
        return hasType(POLL);
    }

    public boolean isForCheckIn() {
        return hasType(CHECK_IN);
    }

    public boolean isForTask() {
        return hasType(TASK);
    }

    public boolean isForHearing() {
        return hasType(HEARING);
    }

    public boolean hasType(Type type) {
        return this.type == type;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(getText()) && TextUtils.isEmpty(getLink()) && TextUtils.isEmpty(image);
    }

    public boolean forTwitter() {
        return socialId == AppSocial.ID_TW;
    }

    public boolean forVk() {
        return socialId == AppSocial.ID_VK;
    }

    public boolean forOk() {
        return socialId == AppSocial.ID_OK;
    }

    public boolean isPostMuchLong() {
        boolean result = false;
        switch (socialId) {
            case AppSocial.ID_TW:
                result = getText().length() > MAX_TWEET_POST_LENGTH;
                break;
            case AppSocial.ID_OK:
                result = getText().length() > MAX_OK_POST_LENGTH;
                break;
        }
        return result;
    }

    public String getWarningTitle(Context context) {
        return String.format(context.getString(R.string.warning_post_mutch_long), String.valueOf(getMaxSymbolsInPost()));
    }

    public int getMaxSymbolsInPost() {
        int result = 0;
        switch (socialId) {
            case AppSocial.ID_OK:
                result = MAX_OK_POST_LENGTH;
                break;
            case AppSocial.ID_TW:
                result = MAX_TWEET_POST_LENGTH;
                break;
        }
        return result;
    }

    public boolean forFb() {
        return socialId == AppSocial.ID_FB;
    }

    public String preparePost() {
        return getText() + " " + getLink();
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getTitle());
        dest.writeString(getText());
        dest.writeString(getLink());
        dest.writeSerializable(type);
        dest.writeInt(socialId);
        dest.writeString(socialName);
        dest.writeString(image);
        boolean [] someBoolean = {isMustServerNotified,enable};
        dest.writeBooleanArray(someBoolean);
        dest.writeString(id.toString());
    }

    protected AppPostValue(Parcel in) {
        setTitle(in.readString());
        setText(in.readString());
        setLink(in.readString());
        setType((Type)in.readSerializable());
        setSocialId(in.readInt());
        setSocialName(in.readString());
        setImage(in.readString());
        boolean [] someBoolean = new boolean[2];
        in.readBooleanArray(someBoolean);
        isMustServerNotified = someBoolean[0];
        enable = someBoolean [1];
        id = in.readString();
    }

    public static final Parcelable.Creator<AppPostValue> CREATOR = new Parcelable.Creator<AppPostValue>() {
        @Override
        public AppPostValue createFromParcel(Parcel source) {
            return new AppPostValue(source);
        }

        @Override
        public AppPostValue[] newArray(int size) {
            return new AppPostValue[size];
        }
    };
}
