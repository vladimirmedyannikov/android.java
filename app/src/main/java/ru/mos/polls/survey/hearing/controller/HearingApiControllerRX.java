package ru.mos.polls.survey.hearing.controller;

import android.content.Context;
import android.support.annotation.NonNull;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.netframework.request.Session;
import ru.mos.polls.AGApplication;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.survey.hearing.service.AuthPGU;
import ru.mos.polls.survey.hearing.service.HearingCheck;

public class HearingApiControllerRX {
    /**
     * Возможные коды ошибок возникающие при вызове метода hearingCheck()
     * <p/>
     * 1) Если при проверке по master_sso ПГУ ответил отрицательно (т.е. не хватает данных), вернет ошибку 100403
     * с текстом "Необходима привязка пользователя портала Гос. услуг."
     * 2) Если slave_sso не имеет права к подобным публикациям, то возвращается ошибка 100500,
     * с различными вариантами текстовых ответов, например "Привязанный пользователь имеет незаполненные поля имени и фамилии"
     */
    public static final int ERROR_CODE_NO_MASTER_SSO_ID = 5705;
    public static final int ERROR_CODE_NOT_ACCESS_FOR_SLAVE_SSO_ID = 100500;
    public static final int ERROR_FIELDS_ARE_EMPTY = 5703;
    public static final int ERROR_SESSION_EXPIRED = 5732;
    public static final int ERROR_PGU_FLAT_NOT_MATCH = 5734;
    public static final int ERROR_AG_FLAT_NOT_MATCH = 15165;
    public static final int ERROR_PGU_NOT_ATTACHED = 15167;
    public static final int ERROR_PGU_FLAT_NOT_VALID = 15163;
    public static final int ERROR_PGU_USER_DATA = 15164;
    public static final int ERROR_PGU_SESSION_EXPIRED = 15166;

    /**
     * Авторизация пользователя в pgu
     *
     * @param context
     * @param pguLogin    логин пользвоателя на сайте pgu
     * @param pguPassword пароль пользователя на сайте pgu
     * @param listener    callback обработка результата авторизации
     */
    public static void pguBind(CompositeDisposable disposable, final Context context, String pguLogin, String pguPassword, final HearingApiControllerRX.PguAuthListener listener) {
        /**
         * в ходе запроса как обычно приходит сессия,
         * но это сессия пгу, восстанавливаем сессию аг
         */
        final String agSessionId = Session.getSession(context);
        HandlerApiResponseSubscriber<AuthPGU.Response.Result> handler;
        handler = getPguHandler(context, listener, agSessionId);
        disposable.add(AGApplication
                .api
                .pguBinding(new AuthPGU.Request(new AuthPGU.Auth(pguLogin, pguPassword, agSessionId), agSessionId))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public static void pguAuth(CompositeDisposable disposable, final Context context, String pguLogin, String pguPassword, final HearingApiControllerRX.PguAuthListener listener) {
        final String agSessionId = Session.getSession(context);
        HandlerApiResponseSubscriber<AuthPGU.Response.Result> handler;
        handler = getPguHandler(context, listener, agSessionId);
        disposable.add(AGApplication
                .api
                .pguAuth(new AuthPGU.Request(new AuthPGU.Auth(pguLogin, pguPassword, agSessionId), agSessionId))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    @NonNull
    public static HandlerApiResponseSubscriber<AuthPGU.Response.Result> getPguHandler(Context context, PguAuthListener listener, String agSessionId) {
        HandlerApiResponseSubscriber<AuthPGU.Response.Result> handler = new HandlerApiResponseSubscriber<AuthPGU.Response.Result>() {
            @Override
            public void onNext(GeneralResponse<AuthPGU.Response.Result> generalResponse) {
                super.onNext(generalResponse);
                Session.setSession(agSessionId);
                if (generalResponse.hasError()) {
                    if (listener != null) {
                        listener.onError(generalResponse.getErrorMessage());
                    }
                } else {
                    onResult(generalResponse.getResult());
                }
            }

            @Override
            protected void onResult(AuthPGU.Response.Result result) {
                AgUser.setPguConnected(context);
                if (listener != null) {
                    listener.onSuccess(result.getMessage(), result.getPercentFillProfile());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (listener != null) {
                    listener.onError(throwable.getMessage());
                }
            }
        };
        return handler;
    }

    /**
     * Сообщение о желании пройти / прохождение собрания для слушания
     *
     * @param hearingId голосование слушания
     * @param meetingId идентификтаор собрания
     * @param listener  callback обработки результатов оповещения
     */
    public static void hearingCheck(CompositeDisposable disposable, long hearingId, long meetingId, final HearingApiControllerRX.HearingCheckListener listener) {
        HandlerApiResponseSubscriber<HearingCheck.Response.Result> handler = new HandlerApiResponseSubscriber<HearingCheck.Response.Result>() {
            @Override
            protected void onResult(HearingCheck.Response.Result result) {
                if (result.getMessage() != null) {
                    listener.onSuccess(result.getMessage().getTitle(), result.getMessage().getText());
                }
            }

            @Override
            public void onNext(GeneralResponse<HearingCheck.Response.Result> generalResponse) {
                super.onNext(generalResponse);
                if (generalResponse.hasError()) {
                    switch (generalResponse.getErrorCode()) {
                        case ERROR_CODE_NO_MASTER_SSO_ID:
                        case ERROR_SESSION_EXPIRED:
                        case ERROR_FIELDS_ARE_EMPTY:
                        case ERROR_PGU_FLAT_NOT_MATCH:
                        case ERROR_AG_FLAT_NOT_MATCH:
                        case ERROR_PGU_NOT_ATTACHED:
                        case ERROR_PGU_FLAT_NOT_VALID:
                        case ERROR_PGU_USER_DATA:
                        case ERROR_PGU_SESSION_EXPIRED:
                            listener.onPguAuthError(generalResponse.getErrorCode(), generalResponse.getErrorMessage());
                            break;
                        default:
                            listener.onError(generalResponse.getErrorMessage());
                            break;
                    }
                } else {
                    onResult(generalResponse.getResult());
                }
            }
        };
        disposable.add(AGApplication
                .api
                .hearingCheck(new HearingCheck.Request(hearingId, meetingId))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public interface PguAuthListener {
        void onSuccess(QuestMessage questMessage, int percent);

        void onError(String error);
    }

    public interface HearingCheckListener {
        void onSuccess(String title, String message);

        void onPguAuthError(int code, String message);

        void onError(String message);
    }
}
