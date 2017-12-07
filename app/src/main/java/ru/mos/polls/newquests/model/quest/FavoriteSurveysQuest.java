package ru.mos.polls.newquests.model.quest;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.newquests.model.QuestFamilyList;
import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.quests.QuestsFragment;

public class FavoriteSurveysQuest extends BackQuest {

    public static final String ID_POLL = "poll";
    public static final String ID_HEARING = "hearing";
    public static final String KIND = "kind";
    @SerializedName("kind")
    private Kind kind;

    public FavoriteSurveysQuest(long innerId, QuestFamilyList questFamilyList) {
        super(innerId, questFamilyList);
        kind = questFamilyList.getKind();
    }


    public long getSurveyId() {
        return Long.parseLong(getId());
    }

    private void onClick(QuestsFragment.Listener listener) {
        listener.onSurvey(getSurveyId());
    }

    @Override
    public String toString() {
        return "FavoriteQuest " + getId() + " " + getTitle();
    }

    public Kind getKind() {
        return kind;
    }

    public boolean isHearingPreview() {
        return kind != null && kind.isHearingPreview();
    }

    public boolean isHearing() {
        return kind != null && kind.isHearing();
    }
}
