package ru.mos.polls.quests.model.quest;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.quests.model.QuestFamilyElement;
import ru.mos.polls.quests.vm.QuestsFragmentVM;

public class ProfileQuest extends DetailsQuest {
    public static final String TYPE_PROFILE = "profile";
    public static final String ID_UPDATE_PERSONAL = "updatePersonal";
    public static final String ID_UPDATE_LOCATION = "updateLocation";
    public static final String ID_UPDATE_SOCIAL = "updateSocial";
    public static final String ID_UPDATE_EMAIL = "updateEmail";
    public static final String ID_UPDATE_EXTRA_INFO = "updateExtraInfo";
    public static final String ID_UPDATE_FAMILY_INFO = "updateFamilyInfo";
    public static final String ID_BIND_TO_PGU = "bindToPGU";
    public static final String ID_PERSONAL_WIZARD = "personalWizard";

    @SerializedName("sub_ids")
    public List<String> idsList;
    @SerializedName("percent_fill_profile")
    public int percent;

    public ProfileQuest(long innerId, QuestFamilyElement questFamilyElement) {
        super(innerId, questFamilyElement);
        idsList = questFamilyElement.getIdsList();
        percent = questFamilyElement.getPercent();
    }

    @Override
    public void onClick(Context context, QuestsFragmentVM.Listener listener) {
        if (ID_PERSONAL_WIZARD.equals(getId())) {
            listener.onWizardProfile(idsList, percent);
        } else if (ID_UPDATE_PERSONAL.equals(getId())) {
            listener.onUpdatePersonal();
        } else if (ID_UPDATE_LOCATION.equals(getId())) {
            listener.onUpdateLocation();
        } else if (ID_UPDATE_SOCIAL.equals(getId())) {
            listener.onUpdateSocial();
        } else if (ID_UPDATE_EMAIL.equals(getId())) {
            listener.onUpdateEmail();
        } else if (ID_UPDATE_EXTRA_INFO.equals(getId())) {
            listener.onUpdateExtraInfo();
        } else if (ID_UPDATE_FAMILY_INFO.equals(getId())) {
            listener.onUpdateFamilyInfo();
        } else if (ID_BIND_TO_PGU.equals(getId())) {
            listener.onBindToPgu();
        }
    }
}
