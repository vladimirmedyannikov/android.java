package ru.mos.polls;


import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.Map;

import ru.mos.polls.subscribes.model.Channel;
import ru.mos.polls.subscribes.model.Subscription;
import ru.mos.polls.util.AgTextUtil;

import static ru.mos.polls.Statistics.isAuthFromPhone;

/**
 * Инкапсулирует методы Google Analytics
 * Возможно, со временем заменит Flurry аналитику {@link ru.mos.polls.Statistics}
 *
 * @since 1.9
 */
public abstract class GoogleStatistics {
    private static final String CHECK_YES = "Chek_Yes";
    private static final String CHECK_NO = "Chek_No";

    /**
     * Базовый класс для экрана регисрации и авторизации
     */
    public static abstract class BaseAuth {
        abstract String getCategory();

        abstract String getAction();

        protected void sendEvent(String label) {
            GoogleStatistics.sendEvent(getCategory(), getAction(), label);
        }

        /**
         * Проверка аторизации
         *
         * @param
         * @param isLogon прошла ли авторизация
         */
        public void check(boolean isLogon) {
            sendEvent(isLogon ? CHECK_YES : CHECK_NO);
        }

        /**
         * клик по оферте
         *
         * @param
         */
        public void offerClick() {
            sendEvent("Uslovie_Oferti");
        }

        /**
         * произошла ошибка
         *
         * @param
         * @param error отправляем текст ошибки, если передаем пустую строку или null, то отправится "Ок"
         */
        public void errorOccurs(String error) {
            sendEvent(AgTextUtil.isEmpty(error) ? "Ok" : error);
        }

        /**
         * клик по меню "как это работает"
         *
         * @param
         */
        public void howItWorksClick() {
            GoogleStatistics.sendEvent(getCategory(), "Nuzhna_Pomosh", "Kak_Eto_Rabotaet");
        }

        /**
         * клик по меню "Восстановление пароля"
         *
         * @param
         */
        public void recoveryClick() {
            GoogleStatistics.sendEvent(getCategory(), "Nuzhna_Pomosh", "Vosstanovlenie_Parolya");
        }

        /**
         * клик по меню "Обратная связь"
         *
         * @param
         */
        public void feedbackClick() {
            GoogleStatistics.sendEvent(getCategory(), "Nuzhna_Pomosh", "Obratnaya_Svyaz");
        }


        /**
         * клик по кнопе "нужна помощь?"
         *
         * @param
         */
        public void helpClick() {
            GoogleStatistics.sendEvent(getCategory(), "Nuzhna_Pomosh", "Nuzhna_Pomosh");
        }
    }

    /**
     * События для экрана авторизации
     */
    public static class Auth extends BaseAuth {
        private static final String CATEGORY = "Vhod";
        private static final String ACTION = "Forma_Vhoda";


        @Override
        String getCategory() {
            return CATEGORY;
        }

        @Override
        String getAction() {
            return ACTION;
        }

        /**
         * клик по кнопке "Регистрация"
         *
         * @param
         */
        public void registryClick() {
            GoogleStatistics.sendEvent(getCategory(), "Registracia", "Registracia");
        }

        /**
         * Экран авторизации, авторизация по e-mail или по номеру телефона
         *
         * @param login     - логин, введенный ользователем (email или номер телефона)
         * @param isSuccess - результат авторизации
         */
        public static void auth(String login, boolean isSuccess) {
            String event = "Auth_e-mail";
            if (isAuthFromPhone(login)) {
                event = "Auth_tel_number";
            }
            Map<String, String> params = new HashMap<String, String>();
            params.put("success", String.valueOf(isSuccess));
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, event, params);
        }
    }

    /**
     * События для экрана регисрации
     */
    public static class Registry extends BaseAuth {
        private static final String CATEGORY = "Registracia";
        private static final String ACTION = "Forma_Registracii";

        @Override
        String getCategory() {
            return CATEGORY;
        }

        @Override
        String getAction() {
            return ACTION;
        }

        /**
         * клик по кнопке "Войти"
         *
         * @param
         */
        public void authClick() {
            GoogleStatistics.sendEvent(getCategory(), "Voiti", "Voiti");
        }
    }

    /**
     * События для экрана восстановления пароля
     */
    public static class Recovery {
        private static final String CATEGORY = "Vosstanovlenie_Parolya";

        public void errorOccurs(String error) {
            sendEvent(CATEGORY, "Vosstanovit", AgTextUtil.isEmpty(error) ? "Ok" : error);
        }

        public void authClick() {
            sendEvent(CATEGORY, "Otmena", "Otmena");
        }

        public void helpClick() {
            sendEvent(CATEGORY, "Nuzhna_Pomosh", "Nuzhna_Pomosh");
        }

        /**
         * клик по меню "Обратная связь"
         *
         * @param
         */
        public void feedbackClick() {
            GoogleStatistics.sendEvent(CATEGORY, "Nuzhna_Pomosh", "Obratnaya_Svyaz");
        }

        /**
         * клик по меню "как это работает"
         *
         * @param
         */
        public void howItWorksClick() {
            GoogleStatistics.sendEvent(CATEGORY, "Nuzhna_Pomosh", "Kak_Eto_Rabotaet");
        }

        public void check(boolean isLogon) {
            sendEvent(CATEGORY, "Forma_Vosstanovlenia", isLogon ? CHECK_YES : CHECK_NO);
        }
    }

    /**
     * События для экрана обратной связи
     */
    public static class Feedback {
        public void feedbackSanded(String subject) {
            sendEvent(subject, null);
        }

        public void errorOccurs(String subject, String error) {
            sendEvent(subject, error);
        }

        public void sendEvent(String subject, String error) {
            GoogleStatistics.sendEvent("Obratnaya_Svyaz", subject, AgTextUtil.isEmpty(error) ? "Ok" : error);
        }
    }

    /**
     * Базовый метод для отправки события
     *
     * @param
     * @param category категория
     * @param action   действие
     * @param label    пометка о результате действия
     */
    public static void sendEvent(String category, String action, String label) {
        Tracker tracker = AGApplication.getTracker();
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
        eventBuilder.setAction(action)
                .setCategory(category)
                .setLabel(label)
                .setValue(1);
        tracker.send(eventBuilder.build());
        Log.i("AG_Google_Analytics", "action = " + action + ", category = " + category + ", label = " + label);
    }

    /**
     * Базовый метод для отправки события c параметрами
     *
     * @param
     * @param category категория
     * @param action   действие
     * @param label    пометка о результате действия
     * @param pair
     */
    public static void sendEvent(String category, String action, String label, Map<String, String> pair) {
        Tracker tracker = AGApplication.getTracker();
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
        eventBuilder.setAction(action)
                .setCategory(category)
                .setLabel(label)
                .setValue(1)
                .setAll(pair);
        tracker.send(eventBuilder.build());
        Log.i("AG_Google_Analytics", "action = " + action + ", category = " + category + ", label = " + label + ", " + getMapLog(pair));
    }

    /**
     * Для лога
     *
     * @param pair
     * @return
     */
    public static String getMapLog(Map<String, String> pair) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : pair.entrySet()) {
            sb.append(entry.getKey() + " = " + entry.getValue() + ", ");
        }
        return sb.toString();
    }

    /**
     * Coбытия на главной ленте
     */


    public static class QuestsFragment {
        private static final String CATEGORY = "Glav_Lenta";

        /**
         * Удаление голосования на главной ленте
         *
         * @param
         */
        public static void deleteSurveyHearing() {
            GoogleStatistics.sendEvent(CATEGORY, "Udalenie_Golosovaniya", "Udalenie_Golosovaniya");
        }

        /**
         * Вход на экран
         *
         * @param
         */
        public static void enterQuestFragment() {
            GoogleStatistics.sendEvent(CATEGORY, "Main_Enter", "Main_Enter");
        }

        /**
         * Главный экран, факт перехода в "Пригласить друзей"
         */
        public static void inviteFriends() {
            GoogleStatistics.sendEvent(CATEGORY, "Invite_Friends", "Invite_Friends");
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
            GoogleStatistics.sendEvent(CATEGORY, "Otpravka_Priglasheniy_Druziam", "Otpravka_Priglasheniy_Druziam", params);
        }

        /**
         * После отправки смс сообщения пользователю
         *
         * @param isSuccess - результат отправки
         */
        public static void afterSendInviteFriends(boolean isSuccess) {
            Map<String, String> params = new HashMap<String, String>(1);
            params.put("success", String.valueOf(isSuccess));
            GoogleStatistics.sendEvent(CATEGORY, "Otpravka_Priglasheniy_Druziam", "Otpravka_Priglasheniy_Druziam", params);
        }

        /**
         * Главный экран, факт перехода в "все вопросы"
         */
        public static void enterAllPolls() {
            enterQuestFragment();
        }

        /**
         * Главный экран, шаринг в соц.сети
         *
         * @param name - имя социальной сети
         */
        public static void taskSocialSharing(String name) {
            Map<String, String> params = new HashMap<>(1);
            params.put("name", name);
            GoogleStatistics.sendEvent(CATEGORY, "Tasks_Social_Sharing", "Tasks_Social_Sharing", params);
        }

        /**
         * Нажатие кнопки "Прочитать все новости"
         */
        public static void hideAllNews() {
            GoogleStatistics.sendEvent(CATEGORY, "Spriatat_Novosti", "Spriatat_Novosti");
        }
    }

    public static class SocialSharing {
        private static final String CATEGORY = "Social_Sharing";
        private static final String ACTION_BEFORE = "Do_Sharinga";
        private static final String ACTION_AFTER = "Posle_Sharinga";

        /**
         * До шаринга о мероприятии (событии)
         *
         * @param name    - имя соц сети
         * @param eventId - идентификатор мероприятия
         */
        public static void beforeSocialEventSharing(String name, String eventId) {
            Map<String, String> params = new HashMap<>(2);
            params.put("name", name);
            params.put("event_id", eventId);
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, ACTION_BEFORE, params);
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
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, ACTION_AFTER, params);
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
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, ACTION_BEFORE, params);
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
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, ACTION_AFTER, params);
        }

        /**
         * До шаринга городской новинки
         *
         * @param name      - имя соц сети
         * @param noveltyId - идентификатор опроса
         */
        public static void beforeSocialInnovationSharing(String name, String noveltyId) {
            Map<String, String> params = new HashMap<String, String>(2);
            params.put("name", name);
            params.put("novelty_id", noveltyId);
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, ACTION_BEFORE, params);
        }

        /**
         * После шаринга результатов опроса
         *
         * @param name      - имя соцсети
         * @param noveltyId - идентифиатор опроса
         * @param isSuccess - результат шаринга
         */
        public static void afterSocialInnovationSharing(String name, String noveltyId, boolean isSuccess) {
            Map<String, String> params = new HashMap<String, String>(3);
            params.put("name", name);
            params.put("novelty_id", noveltyId);
            params.put("success", String.valueOf(isSuccess));
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, ACTION_AFTER, params);
        }


        /**
         * До шаринга достижения
         *
         * @param name    - имя соц сети
         * @param awardId - идентификатор опроса
         */
        public static void beforeSocialAchivementSharing(String name, String awardId) {
            Map<String, String> params = new HashMap<String, String>(2);
            params.put("name", name);
            params.put("award_id", awardId);
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, ACTION_BEFORE, params);
        }

        /**
         * После шаринга достижения
         *
         * @param name      - имя соцсети
         * @param awardId   - идентифиатор опроса
         * @param isSuccess - результат шаринга
         */
        public static void afterSocialAchivementSharing(String name, String awardId, boolean isSuccess) {
            Map<String, String> params = new HashMap<String, String>(3);
            params.put("name", name);
            params.put("award_id", awardId);
            params.put("success", String.valueOf(isSuccess));
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, ACTION_AFTER, params);
        }
    }

    public static class Survey {
        private static final String CATEGORY = "Golosovanie";

        /**
         * Экран список вопросов внутри опроса, факт перехода (открытие опроса)
         *
         * @param pollId
         */
        public static void enterQuestion(long pollId) {
            Map<String, String> params = new HashMap<String, String>(3);
            params.put("id", String.valueOf(pollId));
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, "Otkritie_Golosovaniya", params);
        }

        /**
         * Опросы, переход в закладку активные
         */
        public static void enterPollsActive() {
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, "Activnie_Golosovaniya");
        }


        /**
         * Опросы переход в закладку пройденные
         */
        public static void enterPollsUnactive() {
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, "Proshedshie_Golosovaniya");
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
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, "Prerivanie_Golosovaniya", params);
        }

        /**
         * опросы, изменение статуса опроса с отложенного на пройденный
         */
        public static void pollsInterruptedToPassed() {
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, "Zavershenie_Prervanogo_Golosovaniya");
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
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, "Mnenie_Expertov", params);
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
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, "Podrobno_O_Golosovanie", params);
        }
    }

    public static class BindSocialFragment {
        private static final String CATEGORY = "Socialnie_Seti";

        /**
         * Главный экран, авторизация в соц.сети
         *
         * @param name - имя социальной сети
         */
        public static void taskSocialLogin(String name, boolean isTask) {
            Map<String, String> params = new HashMap<String, String>(1);
            params.put("name", name);
            GoogleStatistics.sendEvent(CATEGORY, "Priviazka", isTask ? "Tasks_Social_Log_In" : "Profile_Social_Log_In", params);
        }
    }

    public static class Subscribe {
        private static final String CATEGORY = "Podpiski";

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
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, "Podpiski_Iz_Profile", params);
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
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, "Podpiski_Dlia_Golosovaniya", params);
        }
    }

    public static class AGNavigation {
        private static final String CATEGORY = "Menu_Navigacii";

        /**
         * Экран мои баллы, факт перехода
         */
        public static void enterBonus() {
            GoogleStatistics.sendEvent("Moi_balli", "Moi_balli", "Moi_balli");
        }

        /**
         * Магазин, клик на кнопку "Потратить баллы"
         */
        public static void shopBuy() {
            GoogleStatistics.sendEvent("Magazin", "Pokupka", "Pokupka");
        }


        /**
         * мои баллы, добавление промо-кода
         */
        public static void shopPromoCode() {
            GoogleStatistics.sendEvent("Moi_balli", "Dobavlenie_PromoCode", "Dobavlenie_PromoCode");
        }

        private static final String PROFILE_CAT = "Profile";

        /**
         * Экран профиль, факт перехода
         */
        public static void enterProfile() {
            GoogleStatistics.sendEvent(PROFILE_CAT, "Vhod_Profile", "Vhod_Profile");
        }

        /**
         * Главный экран, факт заполнения  "профиль личные данные"
         */
        public static void profileFillPersonal() {
            GoogleStatistics.sendEvent(PROFILE_CAT, "Zapolnenie_Lichnih_Dannih", "Zapolnenie_Lichnih_Dannih");
        }

        /**
         * Главный экран, факт заполнения  "адрес работы и род деятельности"
         */
        public static void taskFillProfileAddressWork() {
            GoogleStatistics.sendEvent(PROFILE_CAT, "Zapolnenie_raboti", "Zapolnenie_raboti");
        }

        private static final String NEWS = "Novosti";

        /**
         * Экран новости, факт перехода
         */
        public static void enterNews() {
            GoogleStatistics.sendEvent(NEWS, "Ekran_Hovostei", "Ekran_Hovostei");
        }

        /**
         * переход на новость
         *
         * @param id - идентификатор новости (у новости нет id, хотя в wiki прописано)
         */
        public static void readNews(long id) {
            Map<String, String> params = new HashMap<String, String>(1);
            params.put("id", String.valueOf(id));
            GoogleStatistics.sendEvent(NEWS, "Otkritie_Novosti", "Otkritie_Novosti");
        }

        /**
         * Нажатие кнопки "Поделиться новостью"
         */
        public static void shareNews() {
            GoogleStatistics.sendEvent(NEWS, "Podelitsia_Novostyu", "Podelitsia_Novostyu");
        }

        private static final String OURS_APPS = "Our_Apps";

        /**
         * Наши приложения, факт перехода
         */
        public static void ourApps() {
            GoogleStatistics.sendEvent(OURS_APPS, "Ekran_Nashi_Prilogeniya", "Ekran_Nashi_Prilogeniya");
        }

        /**
         * О приложении, факт перехода
         */
        public static void appsDescription() {
            GoogleStatistics.sendEvent(OURS_APPS, "Opisanie_Prilogeniya", "Opisanie_Prilogeniya");
        }

        /**
         * Блокировка аккаунта
         */
        public static void blockAccount() {
            GoogleStatistics.sendEvent("Blokirovka_Account", "Blokirovka_Account", "Blokirovka_Account");
        }

        /**
         * Экран настройки, факт перехода
         */
        public static void propertiesFragment() {
            GoogleStatistics.sendEvent("Nastroiki", "Nastroiki_Prilogeniya", "Nastroiki_Prilogeniya");
        }
    }

    public static class Events {
        private static final String CATEGORY = "Meropriyatie";

        /**
         * Экран мероприятия, факт перехода
         */
        public static void enterEvents() {
            GoogleStatistics.sendEvent(CATEGORY, "Ekran_Meropriyatiy", "Ekran_Meropriyatiy");
        }

        /**
         * Экран карточка мероприятия, факт перехода
         *
         * @param id - идентификатор мкроприятия
         */
        public static void enterEventTicket(long id) {
            Map<String, String> params = new HashMap<String, String>(1);
            params.put("id", String.valueOf(id));
            GoogleStatistics.sendEvent(CATEGORY, "Otkritie_Meropriyatiya", "Otkritie_Meropriyatiya", params);
        }

        /**
         * Экран комментарий, факт перехода
         *
         * @param eventId - идентификатр мероприятия , события
         */
        public static void enterEventsComments(long eventId) {
            Map<String, String> params = new HashMap<String, String>(1);
            params.put("event_id", String.valueOf(eventId));
            GoogleStatistics.sendEvent(CATEGORY, "Komentarii_Meropriyatiya", "Komentarii_Meropriyatiya", params);
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
            GoogleStatistics.sendEvent(CATEGORY, "Otmetitsia_Na_Meropriyatiye", "Otmetitsia_Na_Meropriyatiye", params);
        }

        /**
         * Отправка комментария
         *
         * @param eventId
         */
        public static void eventSendComment(long eventId) {
            Map<String, String> params = new HashMap<String, String>(1);
            params.put("event_id", String.valueOf(eventId));
            GoogleStatistics.sendEvent(CATEGORY, "Komentirovanie_Meropriyatiya", "Komentirovanie_Meropriyatiya", params);
        }

        /**
         * Отправка рейтинга из коммента без коммента
         *
         * @param eventId
         */
        public static void eventRatingSend(long eventId) {
            Map<String, String> params = new HashMap<String, String>(1);
            params.put("event_id", String.valueOf(eventId));
            GoogleStatistics.sendEvent(CATEGORY, "Reiting_Meropriyatiya", "Reiting_Meropriyatiya", params);
        }
    }

    public static class Innovation {
        private static final String CATEGORY = "Gorodskaya_Novinka";

        /**
         * Экран городские новинки, факт перехода
         */
        public static void innovationsListFragment() {
            GoogleStatistics.sendEvent(CATEGORY, "Okritie_Lenti_Novinok", "Lenta_Gorodskih_Novinok");
        }

        /**
         * Экран карточка городской новинки, факт перехода
         */
        public static void innovationsDetail() {
            GoogleStatistics.sendEvent(CATEGORY, "Okritie_Novinoki", "Okritie_Novinoki");
        }

        /**
         * Новинки, ФАКТ ПРЕрывания голосования
         */
        public static void innovationsInterrupted() {
            GoogleStatistics.sendEvent(CATEGORY, "Prerivanie_Novinoki", "Prerivanie_Novinoki");
        }
    }

    public static class Achivement {
        private static final String CATEGORY = "Dostigeniya";

        /**
         * Экран достижения, факт перехода
         */
        public static void achievementsFragment() {
            GoogleStatistics.sendEvent(CATEGORY, "Ekran_Dostigeniy", "Ekran_Dostigeniy");
        }

        /**
         * Экран достижения детали, факт перехода
         */
        public static void achievementsDetail() {
            GoogleStatistics.sendEvent(CATEGORY, "Otkritie_Detaliy_Dostigeniya", "Otkritie_Detaliy_Dostigeniya");
        }
    }
}
