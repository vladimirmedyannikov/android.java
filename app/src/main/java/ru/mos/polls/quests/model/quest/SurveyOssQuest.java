package ru.mos.polls.quests.model.quest;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.quests.model.QuestFamilyElement;
import ru.mos.polls.quests.vm.QuestsFragmentVM;


public class SurveyOssQuest extends BackQuest {

    public static final String TYPE = "poll_oss";

    @SerializedName("kind")
    private Kind kind;

    public SurveyOssQuest(long innerId, QuestFamilyElement questFamilyElement) {
        super(innerId, questFamilyElement);
        kind = questFamilyElement.getKind();
    }

    public long getSurveyId() {
        return Long.parseLong(getId());
    }

    @Override
    public void onClick(Context context, QuestsFragmentVM.Listener listener) {
        listener.onSurvey(getSurveyId(), false);
    }

    @Override
    public String toString() {
        return "SurveyOssQuest " + getId() + " " + getTitle();
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
