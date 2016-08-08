package ru.mos.polls.survey.source;

import ru.mos.polls.social.model.SocialPostValue;
import ru.mos.polls.survey.Survey;

public interface SurveyDataSource {

    void load(long surveyId, final boolean isHearing, final LoadListener listener);

    void save(Survey survey, SaveListener listener, boolean isInterrupted);

    interface LoadListener {

        void onLoaded(Survey survey);

        void onError(String message);

    }

    interface SaveListener {

        void onSaved(int price, int currentPoints, SocialPostValue socialPOstValue);

        void onPguAuthError(String message);

        void onError(int code, String message);

        void onNoDataToSave();
    }

}
