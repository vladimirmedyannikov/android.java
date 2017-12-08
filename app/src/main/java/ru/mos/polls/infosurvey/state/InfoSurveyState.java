package ru.mos.polls.infosurvey.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.infosurvey.ui.InfoCommentFragment;
import ru.mos.polls.infosurvey.ui.InfoSurveyFragment;

/**
 * Created by Trunks on 04.07.2017.
 */

public class InfoSurveyState extends ContentBelowToolbarState<InfoSurveyState.InfoSurveyParams> {
    public static int TYPE_FR_INFO_SURVEY = 1;
    public static int TYPE_FR_INFO_COMMENT = 2;

    public InfoSurveyState(long pollId, int type) {
        super(new InfoSurveyParams(pollId, type));
    }

    @Override
    protected JugglerFragment onConvertContent(InfoSurveyParams params, @Nullable JugglerFragment fragment) {
//        if (params.type == TYPE_FR_INFO_SURVEY)
//            return InfoSurveyFragment.newInstance(null, params.pollid);
//        else return InfoCommentFragment.newInstance();
        return InfoCommentFragment.newInstance();
    }

    @Override
    protected JugglerFragment onConvertToolbar(InfoSurveyParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();

    }

    @Nullable
    @Override
    public String getTitle(Context context, InfoSurveyParams params) {
        if (params.type == TYPE_FR_INFO_SURVEY)
            return context.getString(R.string.info_survey_title);
        else return context.getString(R.string.comment);
    }

    static class InfoSurveyParams extends State.Params {
        long pollid;
        int type;

        public InfoSurveyParams(int pollid) {
            this.pollid = pollid;
        }

        public InfoSurveyParams(long pollid, int type) {
            this.pollid = pollid;
            this.type = type;
        }
    }
}