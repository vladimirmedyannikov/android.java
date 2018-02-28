package ru.mos.polls.event.controller;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.common.model.Message;
import ru.mos.polls.common.model.PageInfo;
import ru.mos.polls.common.model.Position;
import ru.mos.polls.event.controller.service.CheckinEvent;
import ru.mos.polls.event.controller.service.GetEvent;
import ru.mos.polls.event.controller.service.GetEventCommentsList;
import ru.mos.polls.event.controller.service.GetEventsList;
import ru.mos.polls.event.controller.service.UpdateEventComment;
import ru.mos.polls.event.model.CommentPageInfo;
import ru.mos.polls.event.model.EventComment;
import ru.mos.polls.event.model.EventFromList;
import ru.mos.polls.event.model.EventRX;
import ru.mos.polls.event.model.Filter;
import ru.mos.polls.mypoints.model.Status;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;


public class EventApiControllerRX {

    private static final int DEFAULT_COUNT_PER_PAGE_COMMENT = 10;
    private static final int DEFAULT_PAGE_NUMBER_COMMENT = 1;

    /**
     * Проверка наличия активных мероприятий
     *
     * @param listener callback для обработки присутсвия/отсутсвия мероприятий
     */
    public static void hasCurrentEvents(CompositeDisposable disposable, final CheckHasCurrentEventsListener listener) {
        EventsListener eventsListener = new EventsListener() {
            @Override
            public void onLoad(List<EventFromList> events, Filter filter, Status userStatus, PageInfo pageInfo) {
                boolean hasEvents = events.size() != 0;
                if (listener != null) {
                    listener.hasCurrentEvents(hasEvents);
                }
            }

            @Override
            public void onError() {
                if (listener != null) {
                    listener.hasCurrentEvents(false);
                }
            }
        };
        loadCurrentEvents(disposable, null, new PageInfo(), eventsListener);
    }

    /**
     * Получение текущих мероприятий/событий
     *
     * @param currentPosition
     * @param pageInfo
     * @param listener
     */
    public static void loadCurrentEvents(CompositeDisposable disposable, Position currentPosition, final PageInfo pageInfo, final EventsListener listener) {
        loadEvents(disposable, currentPosition, Filter.CURRENT, pageInfo, listener);
    }

    /**
     * Получение посещенных мероприятий
     *
     * @param currentPosition
     * @param pageInfo
     * @param listener
     */
    public static void loadVisitedEvents(CompositeDisposable disposable, Position currentPosition, final PageInfo pageInfo, final EventsListener listener) {
        loadEvents(disposable, currentPosition, Filter.VISITED, pageInfo, listener);
    }

    /**
     * Получение пропущенных мероприятий
     *
     * @param currentPosition
     * @param pageInfo
     * @param listener
     */
    public static void loadPastEvents(CompositeDisposable disposable, Position currentPosition, final PageInfo pageInfo, final EventsListener listener) {
        loadEvents(disposable, currentPosition, Filter.PAST, pageInfo, listener);
    }

    /**
     * Получение мероприятий/событий в зависимотси от фильтра
     *
     * @param currentPosition текущеее положение
     * @param filter          фильтр событий/мероприятий, возможные значения CURRENT, PAST, VISITED
     * @param pageInfo        информация для пейджинга
     * @param listener        callback для обработки полученного списка
     */
    public static void loadEvents(CompositeDisposable disposable, Position currentPosition,
                                  final Filter filter, final PageInfo pageInfo, final EventsListener listener) {
        HandlerApiResponseSubscriber<GetEventsList.Response.Result> handler = new HandlerApiResponseSubscriber<GetEventsList.Response.Result>() {
            @Override
            protected void onResult(GetEventsList.Response.Result result) {
                if (listener != null)
                    listener.onLoad(result.getListEvent(), filter, result.getStatus(), pageInfo);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (listener != null)
                    listener.onError();
            }
        };
        disposable.add(AGApplication
                .api
                .getEventsList(new GetEventsList.Request(currentPosition, filter, pageInfo))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    /**
     * Получение полной информации о мероприятии
     *
     * @param eventId       - идентификатор мероприятрия
     * @param eventListener
     */
    public static void loadEvent(CompositeDisposable disposable, long eventId, Position position, final EventListener eventListener) {
        HandlerApiResponseSubscriber<EventRX> handler = new HandlerApiResponseSubscriber<EventRX>() {
            @Override
            protected void onResult(EventRX eventRX) {
                if (eventListener != null)
                    eventListener.onGetEvent(eventRX);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (eventListener != null)
                    eventListener.onError();
            }
        };
        disposable.add(AGApplication
                .api
                .getEvent(new GetEvent.Request(eventId, position))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    /**
     * Выполнение чекина
     *
     * @param eventId         - идентификатор мероприятния, на котором выполнен чекин
     * @param position        - текущие географические координаты пользователя
     * @param checkInListener
     */
    public static void checkIn(CompositeDisposable disposable, long eventId, Position position, final CheckInListener checkInListener) {
        HandlerApiResponseSubscriber<CheckinEvent.Response.Result> handler = new HandlerApiResponseSubscriber<CheckinEvent.Response.Result>() {
            @Override
            protected void onResult(CheckinEvent.Response.Result result) {
                if (checkInListener != null)
                    checkInListener.onChecked(result.getStatus().getFreezedPoints(),
                            result.getStatus().getSpentPoints(),
                            (int) result.getStatus().getAllPoints(), // TODO: 20.02.18 убрать типы long в дальнейшем
                            result.getStatus().getCurrentPoints(),
                            result.getStatus().getState(),
                            new Message(result.getMessage().getTitle(),
                                    result.getMessage().getBody(),
                                    result.getMessage().getUrlSchemes()));
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (checkInListener != null)
                    checkInListener.onError();
            }
        };
        disposable.add(AGApplication
                .api
                .checkinEvent(new CheckinEvent.Request(eventId, position))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }


    public static void loadEventCommentList(CompositeDisposable disposable, long eventId, CommentPageInfo commentPageInfo, final EventCommentListListener listener) {
        loadEventCommentList(disposable, eventId, commentPageInfo.getCountPerPage(), commentPageInfo.getPageNumber(), listener);
    }

    public static void loadEventCommentList(CompositeDisposable disposable, long eventId, final EventCommentListListener listener) {
        loadEventCommentList(disposable, eventId, DEFAULT_COUNT_PER_PAGE_COMMENT, DEFAULT_PAGE_NUMBER_COMMENT, listener);
    }

    /**
     * Получение списка комментариев к указанному мероприятию
     *
     * @param eventId      - идентификатор мероприятрия
     * @param countPerPage - максимальное количество комментариев в ответе, по умолчанию 10
     * @param pageNumber   - номер страницы, по умолчанию 1
     * @param listener
     */
    public static void loadEventCommentList(CompositeDisposable disposable, long eventId, int countPerPage, int pageNumber, final EventCommentListListener listener) {
        HandlerApiResponseSubscriber<GetEventCommentsList.Response.Result> handler = new HandlerApiResponseSubscriber<GetEventCommentsList.Response.Result>() {
            @Override
            protected void onResult(GetEventCommentsList.Response.Result result) {
                if (listener != null)
                    listener.onGetEventCommentList(result.getMyComment(), result.getComments(), result.getPageInfo());
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (listener != null)
                    listener.onError();
            }
        };
        disposable.add(AGApplication
                .api
                .getEventCommentList(new GetEventCommentsList.Request(eventId, new PageInfo(countPerPage, pageNumber)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    /**
     * Удаление комментария, используем метод update с пустыми параметрами
     *
     * @param eventId
     * @param listener
     */
    public static void deleteComment(CompositeDisposable disposable, long eventId, UpdateEventCommentListener listener) {
        updateComment(disposable, eventId, "", "", 0, listener);
    }

    /**
     * Добавление, изменение комментария к указаному событию
     *
     * @param eventId  - идентификатор мероприятрия
     * @param title    - заголовок комментария
     * @param body     - тело комментария
     * @param rating   - райтинг пользователя
     * @param listener
     */
    public static void updateComment(CompositeDisposable disposable, long eventId, String title, String body, int rating, final UpdateEventCommentListener listener) {

        HandlerApiResponseSubscriber<Long> handler = new HandlerApiResponseSubscriber<Long>() {
            @Override
            protected void onResult(Long result) {
                if (listener != null) {
                    listener.onUpdated(true);
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
        disposable.add(AGApplication
                .api
                .updateEventComment(new UpdateEventComment.Request(eventId, title, body, rating))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    public interface EventsListener {
        void onLoad(List<EventFromList> events, Filter filter, Status userStatus, PageInfo pageInfo);

        void onError();
    }

    public interface EventListener {
        void onGetEvent(EventRX event);

        void onError();
    }

    public interface CheckInListener {
        void onChecked(int freezedPoints, int spentPoints, int allPoints, int currentPoints, String state, Message message);

        void onError();
    }

    public interface EventCommentListListener {
        void onGetEventCommentList(EventComment myComment, List<EventComment> eventComments, CommentPageInfo commentPageInfo);

        void onError();
    }

    public interface UpdateEventCommentListener {
        void onUpdated(boolean isUpdated);

        void onError();
    }

    public interface CheckHasCurrentEventsListener {
        void hasCurrentEvents(boolean hasEvents);
    }
}
