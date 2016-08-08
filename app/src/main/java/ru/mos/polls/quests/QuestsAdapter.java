package ru.mos.polls.quests;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.mos.polls.R;
import ru.mos.polls.quests.quest.AchievementQuest;
import ru.mos.polls.quests.quest.AdvertisementQuest;
import ru.mos.polls.quests.quest.BackQuest;
import ru.mos.polls.quests.quest.EventQuest;
import ru.mos.polls.quests.quest.FavoriteSurveysQuest;
import ru.mos.polls.quests.quest.NewsQuest;
import ru.mos.polls.quests.quest.NoveltyQuest;
import ru.mos.polls.quests.quest.OtherQuest;
import ru.mos.polls.quests.quest.ProfileQuest;
import ru.mos.polls.quests.quest.Quest;
import ru.mos.polls.quests.quest.RateAppQuest;
import ru.mos.polls.quests.quest.ResultsQuest;
import ru.mos.polls.quests.quest.SocialQuest;

public class QuestsAdapter extends BaseAdapter {

    private static final int PROFILE = 0;
    private static final int SOCIAL = 1;
    private static final int FAVORITE_SURVEYS = 2;
    private static final int EVENT = 3;
    private static final int NEWS = 4;
    private static final int RESULTS = 5;
    private static final int OTHER = 6;
    private static final int BANNER = 7;
    private static final int RATE_APP = 8;
    private static final int NOVELTY = 9;
    private static final int ACHIEVEMENT = 10;

    private static final Map<Class<? extends Quest>, Integer> CLASSES = new HashMap<Class<? extends Quest>, Integer>();

    static {
        CLASSES.put(ProfileQuest.class, PROFILE);
        CLASSES.put(SocialQuest.class, SOCIAL);
        CLASSES.put(FavoriteSurveysQuest.class, FAVORITE_SURVEYS);
        CLASSES.put(EventQuest.class, EVENT);
        CLASSES.put(NewsQuest.class, NEWS);
        CLASSES.put(ResultsQuest.class, RESULTS);
        CLASSES.put(OtherQuest.class, OTHER);
        CLASSES.put(AdvertisementQuest.class, BANNER);
        CLASSES.put(RateAppQuest.class, RATE_APP);
        CLASSES.put(NoveltyQuest.class, NOVELTY);
        CLASSES.put(AchievementQuest.class, ACHIEVEMENT);
    }

    private Context context;
    private List<Quest> quests;
    private HideListener hideListener = HideListener.STUB;

    public QuestsAdapter(Context context, List<Quest> quests) {
        this.context = context;
        this.quests = quests;
    }

    public void setHideListener(HideListener hideListener) {
        if (hideListener != null) {
            this.hideListener = hideListener;
        }
    }

    @Override
    public int getViewTypeCount() {
        return CLASSES.size();
    }

    @Override
    public int getItemViewType(int position) {
        final Quest quest = getItem(position);
        final int result = CLASSES.get(quest.getClass());
        return result;
    }

    @Override
    public int getCount() {
        return quests.size();
    }

    @Override
    public Quest getItem(int position) {
        return quests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Quest quest = getItem(position);
        View result = quest.inflate(context, convertView);
        setBackViewCallbacks(result, position, parent);
        processVisibility(result, quest);
        return result;
    }

    private void processVisibility(View result, Quest quest) {
        int visibility = View.VISIBLE;
        if (quest instanceof BackQuest
                && ((BackQuest) quest).isHidden()) {
            visibility = View.GONE;
        }
        result.setVisibility(visibility);
    }

    private void setBackViewCallbacks(View view, final int position, ViewGroup parent) {
        if (view != null && parent != null && parent instanceof SwipeListView) {
            ((SwipeListView) parent).recycle(view, position);

            TextView cancel = (TextView) view.findViewById(R.id.cancel);
            if (cancel != null) {
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideListener.onCancel(position);
                    }
                });
            }
            TextView delete = (TextView) view.findViewById(R.id.delete);
            if (delete != null) {
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideListener.onDelete(position);
                    }
                });
            }
        }
    }

    public interface HideListener {
        HideListener STUB = new HideListener() {
            @Override
            public void onCancel(int position) {
            }

            @Override
            public void onDelete(int position) {
            }
        };

        void onCancel(int position);

        void onDelete(int position);
    }
}
