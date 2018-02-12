package ru.mos.polls.quests.model.quest;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.quests.model.QuestFamilyElement;
import ru.mos.polls.quests.vm.QuestsFragmentVM;
import ru.mos.polls.poll.model.Kind;

public class FavoriteSurveysQuest extends BackQuest {

    public static final String ID_POLL = "poll";
    public static final String ID_HEARING = "hearing";
    public static final String KIND = "kind";
    @SerializedName("kind")
    private Kind kind;

    public FavoriteSurveysQuest(long innerId, QuestFamilyElement questFamilyElement) {
        super(innerId, questFamilyElement);
        kind = questFamilyElement.getKind();
    }


    public long getSurveyId() {
        return Long.parseLong(getId());
    }

    private void onClick(QuestsFragmentVM.Listener listener) {
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
