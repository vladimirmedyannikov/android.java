package ru.mos.polls.quests;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import ru.mos.polls.quests.questviewholder.EventHolder;
import ru.mos.polls.quests.questviewholder.FavoriteSurveysHolder;
import ru.mos.polls.quests.questviewholder.NewsHolder;
import ru.mos.polls.quests.questviewholder.NoveltyHolder;
import ru.mos.polls.quests.questviewholder.OtherHolder;
import ru.mos.polls.quests.questviewholder.ProfileHolder;
import ru.mos.polls.quests.questviewholder.QuestsViewHolder;
import ru.mos.polls.quests.questviewholder.ResultsHolder;
import ru.mos.polls.quests.questviewholder.SocialHolder;

public class QuestsItemAdapter extends RecyclerView.Adapter<QuestsViewHolder> {

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
    private QuestsAdapter.HideListener hideListener = QuestsAdapter.HideListener.STUB;
    private QuestsFragment.ItemRecyclerViewListener listener;

    public QuestsItemAdapter(Context context, List<Quest> quests, QuestsFragment.ItemRecyclerViewListener listener) {
        this.context = context;
        this.quests = quests;
        this.listener = listener;
    }

    @Override
    public QuestsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case PROFILE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_profile, parent, false);
                return new ProfileHolder(view);
            case SOCIAL:
            case RATE_APP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_social, parent, false);
                return new SocialHolder(view);
            case FAVORITE_SURVEYS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_favorite_survey, parent, false);
                return new FavoriteSurveysHolder(view);
            case EVENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_event, parent, false);
                return new EventHolder(view);
            case NEWS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_news, parent, false);
                return new NewsHolder(view);
            case RESULTS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_results, parent, false);
                return new ResultsHolder(view);
            case OTHER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_other, parent, false);
                return new OtherHolder(view);
//            case BANNER:
//                layoutRes = R.layout.quest_html_banner;
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_news, parent, false);
//                return new NewsHolder(view);
//                break;
            case NOVELTY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_novelty, parent, false);
                return new NoveltyHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(QuestsViewHolder holder, int position) {
        final BackQuest quest = (BackQuest) quests.get(position);
        holder.setDataOnView(quest);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(quest);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        final Quest quest = quests.get(position);
        final int result = CLASSES.get(quest.getClass());
        return result;
    }

    @Override
    public int getItemCount() {
        return quests.size();
    }
}
