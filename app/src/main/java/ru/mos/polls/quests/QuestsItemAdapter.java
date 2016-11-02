package ru.mos.polls.quests;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
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
import ru.mos.polls.quests.view.questviewholder.AchievementHolder;
import ru.mos.polls.quests.view.questviewholder.EventHolder;
import ru.mos.polls.quests.view.questviewholder.FavoriteSurveysHolder;
import ru.mos.polls.quests.view.questviewholder.NewsHolder;
import ru.mos.polls.quests.view.questviewholder.NoveltyHolder;
import ru.mos.polls.quests.view.questviewholder.OtherHolder;
import ru.mos.polls.quests.view.questviewholder.ProfileHolder;
import ru.mos.polls.quests.view.questviewholder.QuestsViewHolder;
import ru.mos.polls.quests.view.questviewholder.ResultsHolder;
import ru.mos.polls.quests.view.questviewholder.SocialHolder;

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

    private List<String> questForRemoveList = new ArrayList<>();

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


    private List<Quest> quests;
    private QuestsFragment.ItemRecyclerViewListener listener;

    public QuestsItemAdapter(List<Quest> quests, QuestsFragment.ItemRecyclerViewListener listener) {
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
            case BANNER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_news, parent, false);
                return new NewsHolder(view);
            case NOVELTY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_novelty, parent, false);
                return new NoveltyHolder(view);
            case ACHIEVEMENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_achievement, parent, false);
                return new AchievementHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final QuestsViewHolder holder, int position) {
        final BackQuest quest = (BackQuest) quests.get(position);
        final String id = quest.getId();
        holder.setDataOnView(quest);
        if (questForRemoveList.contains(id)) {
            holder.itemView.setOnClickListener(null);
            holder.swipableView.setVisibility(View.INVISIBLE);
            holder.backView.setVisibility(View.VISIBLE);
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyItemChanged(quests.indexOf(quest));
                    questForRemoveList.remove(id);
                    listener.onCancel(holder);
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(holder.getAdapterPosition());
                    listener.onDelete(quest, holder.getAdapterPosition());
                }
            });
        } else {
            holder.swipableView.setVisibility(View.VISIBLE);
            holder.cancel.setOnClickListener(null);
            holder.delete.setOnClickListener(null);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(quest);
                }
            });
        }
    }

    public void removeItem(int position) {
        if (quests.size() > 0 && position >= 0) {
            quests.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        final Quest quest = quests.get(position);
        final int result = CLASSES.get(quest.getClass());
        return result;
    }

    public void redrawViewHolder(int position) {
        BackQuest quest = (BackQuest) quests.get(position);
        String id = quest.getId();
        if (!questForRemoveList.contains(id)) {
            questForRemoveList.add(id);
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemCount() {
        return quests.size();
    }
}
