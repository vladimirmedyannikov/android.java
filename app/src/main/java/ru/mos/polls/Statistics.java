package ru.mos.polls;

import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

import ru.mos.polls.subscribes.model.Channel;
import ru.mos.polls.subscribes.model.Subscription;

/**
 * Класс инкапсулирует работу с Flurry
 */
public class Statistics {

    /**
     * Экран авторизации, факт перехода в "Регистрация"
     */
    public static void authEnterRegistration() {
        FlurryAgent.logEvent("auth_enter_registration");
    }

    /**
     * Экран авторизации, факт регистрации
     */
    public static void registration() {
        FlurryAgent.logEvent("registration");
    }

    /**
     * Экран авторизации, авторизация по e-mail или по номеру телефона
     *
     * @param login     - логин, введенный ользователем (email или номер телефона)
     * @param isSuccess - результат авторизации
     */
    public static void auth(String login, boolean isSuccess) {
        String event = "auth_e-mail";
        if (isAuthFromPhone(login)) {
            event = "auth_tel_number";
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("success", String.valueOf(isSuccess));
        FlurryAgent.logEvent(event, params);
    }

    /**
     * Экран авторизации, факт перехода в "Восстановление"
     */
    public static void getPassword() {
        FlurryAgent.logEvent("get_password");
    }

    /**
     * Факт отправки на восстановление
     */
    public static void recoveryPassword() {
        FlurryAgent.logEvent("password recovery");
    }

    /**
     * Главный экран, факт перехода на главный экран
     */
    public static void mainEnter() {
        FlurryAgent.logEvent("main_enter");
    }

    /**
     * Главный экран, факт заполнения  "профиль личные данные"
     */
    public static void taskFillProfilePersonal() {
        FlurryAgent.logEvent("task_fill_profile_personal");
    }

    /**
     * Главный экран, факт заполнения  "профиль место жительства"
     */
    public static void taskFillProfileAddress() {
        FlurryAgent.logEvent("task_fill_profile_address");
    }

    /**
     * Главный экран, авторизация в соц.сети
     *
     * @param name - имя социальной сети
     */
    public static void taskSocialLogin(String name) {
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("name", name);
        FlurryAgent.logEvent("tasks_social_log_in", params);
    }

    /**
     * Социальные сети, авторизация в соц.сети
     *
     * @param name - имя социальной сети
     */
    public static void profileSocialLogin(String name) {
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("name", name);
        FlurryAgent.logEvent("profile_social_log_in", params);
    }

    /**
     * Главный экран, факт перехода в "Пригласить друзей"
     */
    public static void inviteFriends() {
        FlurryAgent.logEvent("invite_friends");
    }

    /**
     * Перед отправкой смс сообщения для одного пользователя
     */
    public static void beforeSendInviteFriends() {
        beforeSendInviteFriends(1);
    }

    /**
     * Перед отправкой СМС сообщения
     *
     * @param count - количество отправителей (на 12.09.2014 у нас вроде можно отправить
     *              только одно сообщение одному другу за раз,
     *              на ios реализовали множественный выбор получателей)
     */
    public static void beforeSendInviteFriends(int count) {
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("count", String.valueOf(count));
        FlurryAgent.logEvent("send_invite_friends", params, true);
    }


    /**
     * После отправки смс сообщения пользователю
     *
     * @param isSuccess - результат отправки
     */
    public static void afterSendInviteFriends(boolean isSuccess) {
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("success", String.valueOf(isSuccess));
        FlurryAgent.endTimedEvent("send_invite_friends", params);
    }

    /**
     * Главный экран, факт перехода в "все вопросы"
     */
    public static void enterAllPolls() {
        FlurryAgent.logEvent("enter_all_polls");
    }

    /**
     * Главный экран, шаринг в соц.сети
     *
     * @param name - имя социальной сети
     */
    public static void taskSocialSharing(String name) {
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("name", name);
        FlurryAgent.logEvent("tasks_social_sharing", params);
    }

    /**
     * До шаринга о мероприятии (событии)
     *
     * @param name    - имя соц сети
     * @param eventId - идентификатор мероприятия
     */
    public static void beforeSocialEventSharing(String name, String eventId) {
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("name", name);
        params.put("event_id", eventId);
        FlurryAgent.logEvent("social_sharing", params, true);
    }

    /**
     * После шаринга о мероприятии (событии)
     *
     * @param name      - имя соцсети
     * @param eventId   - идентифиатор мероприятия/события
     * @param isSuccess -результат шаринга
     */
    public static void afterSocialEventSharing(String name, String eventId, boolean isSuccess) {
        Map<String, String> params = new HashMap<String, String>(3);
        params.put("name", name);
        params.put("event_id", eventId);
        params.put("success", String.valueOf(isSuccess));
        FlurryAgent.endTimedEvent("social_sharing", params);
    }

    /**
     * До шаринга результатов опроса
     *
     * @param name   - имя соц сети
     * @param pollId - идентификатор опроса
     */
    public static void beforeSocialSurveySharing(String name, String pollId) {
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("name", name);
        params.put("poll_id", pollId);
        FlurryAgent.logEvent("social_sharing", params, true);
    }

    /**
     * После шаринга результатов опроса
     *
     * @param name      - имя соцсети
     * @param pollId    - идентифиатор опроса
     * @param isSuccess - результат шаринга
     */
    public static void afterSocialSurveySharing(String name, String pollId, boolean isSuccess) {
        Map<String, String> params = new HashMap<String, String>(3);
        params.put("name", name);
        params.put("poll_id", pollId);
        params.put("success", String.valueOf(isSuccess));
        FlurryAgent.endTimedEvent("social_sharing", params);
    }

    /**
     * Экран список вопросов внутри опроса, факт перехода (открытие опроса)
     *
     * @param pollId
     */
    public static void enterQuestion(long pollId) {
        Map<String, String> params = new HashMap<String, String>(3);
        params.put("id", String.valueOf(pollId));
        FlurryAgent.endTimedEvent("enter_questions", params);
    }

    /**
     * Опросы, переход в закладку активные
     */
    public static void enterPollsActive() {
        FlurryAgent.logEvent("enter_polls_active");
    }

    /**
     * Опросы переход в закладку пройденные
     */
    public static void enterPollsUnactive() {
        FlurryAgent.logEvent("enter_polls_unactive");
    }

    /**
     * Профиль: подписки
     *
     * @param subscription - тип подписки
     * @param channel      - имя канала
     */
    public static void subscriptionsProfile(Subscription subscription, Channel channel) {
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("type", subscription.getType());
        params.put("channel", channel.getName());
        FlurryAgent.logEvent("subscriptions_profile", params);
    }

    /**
     * Подписки при прохождении опроса
     *
     * @param subscription - тип подписки
     * @param channel      - имя канала
     * @param pollId       - идентификатор опроса, покоторому устанавливаем подписку
     */
    public static void subscriptionsPolls(Subscription subscription, Channel channel, long pollId) {
        Map<String, String> params = new HashMap<String, String>(3);
        params.put("type", subscription.getType());
        params.put("channel", channel.getName());
        params.put("poll_id", String.valueOf(pollId));
        FlurryAgent.logEvent("subscriptions_polls", params);
    }

    /**
     * Опросы, ФАКТ ПРЕрывания опроса
     *
     * @param pollId        - идентификатор прерванного опроса
     * @param isInterrupted - true, если прерван
     */
    public static void pollsInterrupted(long pollId, boolean isInterrupted) {
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("poll_id", String.valueOf(pollId));
        params.put("success", String.valueOf(isInterrupted));
        FlurryAgent.logEvent("polls_interrupted", params);
    }

    /**
     * опросы, изменение статуса опроса с отложенного на пройденный
     */
    public static void pollsInterruptedToPassed() {
        FlurryAgent.logEvent("polls_interrupted_to_passed");
    }

    /**
     * Мнение экспертов, факт перехода
     *
     * @param pollId     - идентификатор опроса
     * @param questionId - идентификатор вопроса
     */
    public static void pollsEnterExperts(long pollId, long questionId) {
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("poll_id", String.valueOf(pollId));
        params.put("question_id", String.valueOf(questionId));
        FlurryAgent.logEvent("polls_enter_experts", params);
    }

    /**
     * Экран подробнее, факт перехода
     *
     * @param pollId     - идентификатор опроса
     * @param questionId - идентификатор вопроса
     */
    public static void pollsEnterMoreInfo(long pollId, long questionId) {
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("poll_id", String.valueOf(pollId));
        params.put("question_id", String.valueOf(questionId));
        FlurryAgent.logEvent("polls_enter_more_info", params);
    }

    /**
     * Экран мои баллы, факт перехода
     */
    public static void enterBonus() {
        FlurryAgent.logEvent("enter_bonuses");
    }

    /**
     * Магазин, клик на кнопку "Потратить баллы"
     */
    public static void shopBuy() {
        FlurryAgent.logEvent("shop_buy");
    }

    /**
     * мои баллы, добавление промо-кода
     */
    public static void shopPromoCode() {
        FlurryAgent.logEvent("shop_promo_code");
    }

    /**
     * Экран профиль, факт перехода
     */
    public static void enterProfile() {
        FlurryAgent.logEvent("enter_profile");
    }

    /**
     * Главный экран, факт заполнения  "профиль личные данные"
     */
    public static void profileFillPersonal() {
        FlurryAgent.logEvent("profile_fill_personal");
    }

    /**
     * Главный экран, факт заполнения  "профиль место жительства"
     */
    public static void profileFillAddress() {
        FlurryAgent.logEvent("profile_fill_address");
    }

    /**
     * Экран новости, факт перехода
     */
    public static void enterNews() {
        FlurryAgent.logEvent("enter_news");
    }

    /**
     * переход на новость
     *
     * @param id - идентификатор новости (у новости нет id, хотя в wiki прописано)
     */
    public static void readNews(long id) {
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("id", String.valueOf(id));
        FlurryAgent.logEvent("read_news", params);
    }


    /**
     * Наши приложения, факт перехода
     */
    public static void ourApps() {
        FlurryAgent.logEvent("our_apps");
    }

    /**
     * О приложении, факт перехода
     */
    public static void appsDescription() {
        FlurryAgent.logEvent("apps_description");
    }

    /**
     * Экран мероприятия, факт перехода
     */
    public static void enterEvents() {
        FlurryAgent.logEvent("enter_events");
    }

    /**
     * Экран карточка мероприятия, факт перехода
     *
     * @param id - идентификатор мкроприятия
     */
    public static void enterEventTicket(long id) {
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("id", String.valueOf(id));
        FlurryAgent.logEvent("enter_events_ticket", params);
    }

    /**
     * Экран комментарий, факт перехода
     *
     * @param eventId - идентификатр мероприятия , события
     */
    public static void enterEventsComments(long eventId) {
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("event_id", String.valueOf(eventId));
        FlurryAgent.logEvent("enter_events_comments", params);
    }

    /**
     * Клик на кнопку Отметиться здесь
     *
     * @param eventId         - идентифиатор мероприятия/события
     * @param isSuccess       - результат чекина
     * @param isLocationError - сс вернул ли ошибку по местоположению
     */
    public static void enterCheckIn(long eventId, boolean isSuccess, boolean isLocationError) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("event_id", String.valueOf(eventId));
        if (isSuccess) {
            params.put("success", String.valueOf(true));
        } else {
            params.put("error", isLocationError ? "locationError" : "distanceError");
        }
        FlurryAgent.logEvent("event_check_in", params);
    }

    /**
     * Отправка комментария
     *
     * @param eventId
     */
    public static void eventSendComment(long eventId) {
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("event_id", String.valueOf(eventId));
        FlurryAgent.logEvent("event_comment_send", params);
    }

    /**
     * Блокировка аккаунта
     */
    public static void blockAccount() {
        FlurryAgent.logEvent("account_block");
    }

    /**
     * Отправка рейтинга из коммента без коммента
     *
     * @param eventId
     */
    public static void eventRatingSend(long eventId) {
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("event_id", String.valueOf(eventId));
        FlurryAgent.logEvent("event_rating_send", params);
    }

    /**
     * Проверка, является ли логин номером телефона или логин - это email
     *
     * @param login - введнный пользователем логин
     * @return -true - если номер телефона
     */
    private static boolean isAuthFromPhone(String login) {
        boolean result;
        try {
            Long.parseLong(login);
            result = true;
        } catch (NumberFormatException ignored) {
            result = false;
        }

        return result;
    }

}
