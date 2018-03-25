package ru.mos.polls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import me.ilich.juggler.change.Add;
import me.ilich.juggler.states.State;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.innovations.state.InnovationState;
import ru.mos.polls.profile.state.AchievementState;
import ru.mos.polls.survey.state.SurveyState;
import ru.mos.polls.webview.state.WebViewState;

/**
 * Активность для обработки пушей
 */

public class PushProcessActivity extends BaseActivity {
    /**
     * экстра параметр, определяющий какой именно экран следует открыть
     */
    private static final String TYPE = "type";

    /**
     * для пушей AgNew
     */
    private static final String TYPE_AG_NEW = "type_ag_new";
    private static final String INFORMATION_TITLE = "information_title_extra";
    private static final String INFORMATION_URL = "information_url_extra";
    private static final String ONLY_LOAD_FIRST_URL = "only_load_first_url";
    private static final String ID = "id";
    private static final String IS_SHARE_ENABLE = "is_share_enable";

    /**
     * для пушей NewNovelty
     */
    private static final String TYPE_NEW_NOVELTY = "type_new_novelty";
    private static final String NOVELTY_ID = "novelty_id";

    /**
     * для пушей NewAchievement
     */
    private static final String TYPE_NEW_ACHIEVEMENT = "type_new_achievement";
    private static final String ACHIEVEMENT_ID = "achievement_id";

    /**
     * для пушей Голосований
     */
    private static final String TYPE_POLLS = "type_poll";
    private static final String SURVEY_ID = "survey_id";
    private static final String SURVEY_IS_HEARING = "is_hearing";

    public static Intent getIntentForAgNew(Context context, String title, String linkUrl, String id) {
        Intent intent = new Intent(context, PushProcessActivity.class);
        intent.putExtra(INFORMATION_TITLE, title);
        intent.putExtra(INFORMATION_URL, linkUrl);
        intent.putExtra(ONLY_LOAD_FIRST_URL, true);
        intent.putExtra(IS_SHARE_ENABLE, true);
        intent.putExtra(ID, id);
        /**
         * экстра параметр, определяющий какой именно экран следует открыть
         */
        intent.putExtra(TYPE, TYPE_AG_NEW);
        return intent;
    }

    public static Intent getIntentForNewNovelty(Context context, long id) {
        Intent intent = new Intent(context, PushProcessActivity.class);
        intent.putExtra(NOVELTY_ID, id);
        /**
         * экстра параметр, определяющий какой именно экран следует открыть
         */
        intent.putExtra(TYPE, TYPE_NEW_NOVELTY);
        return intent;
    }

    public static Intent getIntentForNewAchievement(Context context, String id) {
        Intent intent = new Intent(context, PushProcessActivity.class);
        intent.putExtra(ACHIEVEMENT_ID, id);
        /**
         * экстра параметр, определяющий какой именно экран следует открыть
         */
        intent.putExtra(TYPE, TYPE_NEW_ACHIEVEMENT);
        return intent;
    }

    public static Intent getIntentForPoll(Context context, long id, boolean isHearing) {
        Intent intent = new Intent(context, PushProcessActivity.class);
        intent.putExtra(TYPE, TYPE_POLLS);
        intent.putExtra(SURVEY_ID, id);
        intent.putExtra(SURVEY_IS_HEARING, isHearing);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getStringExtra(TYPE) != null) {
            State state = null;
            switch (getIntent().getStringExtra(TYPE)) {
                case TYPE_AG_NEW:
                    state = new WebViewState(
                            getIntent().getStringExtra(INFORMATION_TITLE),
                            getIntent().getStringExtra(INFORMATION_URL),
                            getIntent().getStringExtra(ID),
                            getIntent().getBooleanExtra(ONLY_LOAD_FIRST_URL, false),
                            getIntent().getBooleanExtra(IS_SHARE_ENABLE, false));
                    break;
                case TYPE_NEW_NOVELTY:
                    state = new InnovationState(
                            getIntent().getLongExtra(NOVELTY_ID, 0));
                    break;
                case TYPE_NEW_ACHIEVEMENT:
                    state = new AchievementState(getIntent().getStringExtra(ACHIEVEMENT_ID), true);
                    break;
                case TYPE_POLLS:
                    state = new SurveyState(getIntent().getLongExtra(SURVEY_ID, 0),
                            getIntent().getBooleanExtra(SURVEY_IS_HEARING, false));
            }
            if (state != null) {
                navigateTo().state(Add.newActivity(state, BaseActivity.class));
            }
        }
        finish();
    }
}