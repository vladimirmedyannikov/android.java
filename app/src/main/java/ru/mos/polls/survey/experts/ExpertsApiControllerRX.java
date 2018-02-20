package ru.mos.polls.survey.experts;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.questions.SurveyQuestion;
import ru.mos.polls.survey.service.GetExpertList;

/**
 * Инкапсулирует  работу с сервисом "мнение экспертов"
 * Для запроса мнений эксперта по голосованию передавать pollId {@link Survey#getId()},
 * по вопросу - questionId {@link SurveyQuestion#getId()}<br/>
 * Для запроса мненией экспертов для публичных слушаний необходимо передать
 * признак публичного слушания{@link Kind#isHearing()}
 *
 * @since 1.8
 */

public class ExpertsApiControllerRX {

    public static void loadDetailExperts(final Context context, long pollId, long questionId, boolean isHearing, final DetailsExpertListener listener) {
        HandlerApiResponseSubscriber<GetExpertList.Response.Result> handler = new HandlerApiResponseSubscriber<GetExpertList.Response.Result>(context) {
            @Override
            protected void onResult(GetExpertList.Response.Result result) {
                List<DetailsExpert> list = new ArrayList<>();
                for (DetailsExpert detailsExpert : result.getExperts()) {
                    if (!detailsExpert.isEmpty()) list.add(detailsExpert);
                }
                if (listener != null) {
                    listener.onLoaded(list);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (listener != null) {
                    listener.onError();
                }
            }
        };
        GetExpertList.Request request = new GetExpertList.Request();
        if (pollId != 0) {
            if (isHearing) {
                request.setHearingId(pollId);
            } else {
                request.setPollId(pollId);
            }
        }
        if (questionId != 0) {
            request.setQuestionId(questionId);
        }
        AGApplication
                .api
                .getExpertsList(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }

    public interface DetailsExpertListener {
        void onLoaded(List<DetailsExpert> detailsExperts);

        void onError();
    }
}
