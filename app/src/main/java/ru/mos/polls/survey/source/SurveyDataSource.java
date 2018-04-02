package ru.mos.polls.survey.source;

import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.survey.Survey;

public interface SurveyDataSource {

    void load(long surveyId, final boolean isHearing, final LoadListener listener, Progressable progressable);

    void save(Survey survey, SaveListener listener, boolean isInterrupted, Progressable progressable);

    interface LoadListener {

        void onLoaded(Survey survey);

        void onError(String message);

    }

    interface SaveListener {

        void onSaved(int price, int currentPoints, AppPostValue appPOstValue);

        void onPguAuthError(String message);

        void onError(int code, String message);

        void onNoDataToSave();
    }

}
