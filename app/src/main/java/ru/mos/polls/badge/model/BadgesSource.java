package ru.mos.polls.badge.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import ru.mos.elk.ElkTextUtils;

public class BadgesSource {

    private static BadgesSource instance = null;

    public synchronized static BadgesSource getInstance() {
        if (instance == null) {
            instance = new BadgesSource();
        }
        return instance;
    }

    /**
     * Голосования
     */
    public static final String TAG_POLLS = "polls";

    /**
     * Городские новинки
     */
    public static final String TAG_NOVELTY = "novelty";

    /**
     * Новости
     */
    public static final String TAG_NEWS = "news";

    /**
     * Друзья
     */
    public static final String TAG_FRIENDS = "friends";


    /**
     * Мои баллы
     */
    public static final String TAG_POINTS = "scores";

    private State state;
    private final List<Badge> badgeList = new ArrayList<>();

    private final Map<Badge.Type, Set<Long>> idsToSend = new HashMap<>();

    private String lastAvatartUrl = null;
    private Bitmap lastAvatarBitmap = null;

    private BadgesSource() {
    }

    public List<Badge> getBadgeList() {
        return badgeList;
    }

    @Nullable
    public String get(String bageTag) {
        final String s;
        if (bageTag == null) {
            s = null;
        } else {
            switch (bageTag) {
                case TAG_NEWS:
                    s = get(Badge.Type.NEWS);
                    break;
                case TAG_NOVELTY:
                    s = get(Badge.Type.NOVELTIES);
                    break;
                case TAG_POINTS:
                    s = getPoints();
                    break;
                case TAG_POLLS:
                    s = get(Badge.Type.POLLS);
                    break;
                case TAG_FRIENDS:
                    s = get(Badge.Type.FRIENDS);
                    break;
                default:
                    s = null;
                    break;
            }
        }
        return s;
    }

    @Nullable
    private String get(Badge.Type type) {
        String result = null;
        if (state != null) {
            for (Badge badge : state.getBadges()) {
                if (badge.getType() == type) {
                    int count = 0;
                    if (idsToSend.containsKey(type)) {
                        if (badge.getIds() != null && badge.getIds().length > 0) {
                            Set<Long> visitedIds = idsToSend.get(type);
                            for (Long currentId : badge.getIds()) {
                                if (!visitedIds.contains(currentId)) {
                                    count++;
                                }
                            }
                        }
                    } else {
                        count = badge.getCount();
                    }
                    if (count == 0) {
                        result = "";
                    } else {
                        result = String.valueOf(count);
                    }
                }
            }
        }
        return result;
    }

    @Nullable
    private String getPoints() {
        final String result;
        if (state != null) {
            int count = state.getPointsCount();
            if (count > 0) {
                result = String.format(Locale.getDefault(), "+%s", state.getPointsCount());
            } else {
                result = null;
            }
        } else {
            result = null;
        }
        return result;
    }

    public void markNewsAsReaded(long id) {
        final Set<Long> ids;
        if (idsToSend.containsKey(Badge.Type.NEWS)) {
            ids = idsToSend.get(Badge.Type.NEWS);
        } else {
            ids = new HashSet<>();
            idsToSend.put(Badge.Type.NEWS, ids);
        }
        ids.add(id);
    }

    public Badge getBadgeNews() {
        return getBadge(Badge.Type.NEWS);
    }

    public Badge getBadge(Badge.Type type) {
        Badge result = null;
        if (state != null) {
            for (Badge badge : state.getBadges()) {
                if (badge.getType() == type) {
                    result = badge;
                    break;
                }
            }
        }
        return result;
    }

    public void storeState(State state) {
        this.state = state;
        storeBadges(state.getBadges());
    }

    public long[] getReadedNewsIds() {
        final long[] result;

        if (idsToSend.containsKey(Badge.Type.NEWS)) {
            final Set<Long> ids = idsToSend.get(Badge.Type.NEWS);
            result = new long[ids.size()];
            int i = 0;
            for (Long id : ids) {
                result[i] = id;
                i++;
            }
        } else {
            result = new long[0];
        }
        return result;
    }

    private void storeBadges(List<Badge> badges) {
        badgeList.clear();
        badgeList.addAll(badges);
    }

    public State getState() {
        return state;
    }

    public void processAvatarUrl(UrlProcessor urlProcessor) {
        if (state != null) {
            if (state.getPersonal() != null) {
                String url = state.getPersonal().getIcon();
                if (ElkTextUtils.isEmpty(url)) {
                    urlProcessor.process(null);
                } else {
                    if (lastAvatartUrl == null || !url.equals(lastAvatartUrl) ) {
                        urlProcessor.process(url);
                    } else {
                        urlProcessor.process(lastAvatartUrl);
                    }
                }
            }
        }
    }

    public void setAvatar(String url, Bitmap bitmap) {
        lastAvatartUrl = url;
        if (lastAvatarBitmap != null) {
            lastAvatarBitmap.recycle();
        }
        lastAvatarBitmap = copyBitmap(bitmap);
    }

    @Nullable
    public Bitmap getAvatar() {
        if (lastAvatarBitmap != null) {
            return lastAvatarBitmap;
        } else {
            return null;
        }
    }

    public boolean compareWithLastAvatarUrl(String url) {
        return hasLastAvatarUrl() && lastAvatartUrl.equalsIgnoreCase(url);
    }

    public boolean hasLastAvatarUrl() {
        return !ElkTextUtils.isEmpty(lastAvatartUrl) && !"null".equalsIgnoreCase(lastAvatartUrl);
    }

    private Bitmap copyBitmap(Bitmap bitmap) {
        Bitmap result = null;
        if (bitmap != null) {
            result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
        return result;
    }

    public interface UrlProcessor {
        void process(@Nullable String url);
    }

}
