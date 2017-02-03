package ru.mos.polls;


import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.Map;

import ru.mos.elk.ElkTextUtils;

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
            sendEvent(ElkTextUtils.isEmpty(error) ? "Ok" : error);
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
        public void auth(String login, boolean isSuccess) {
            String event = "Auth_e-mail";
            if (isAuthFromPhone(login)) {
                event = "Auth_tel_number";
            }
            Map<String, String> params = new HashMap<String, String>();
            params.put("success", String.valueOf(isSuccess));
            GoogleStatistics.sendEvent(getCategory(), getCategory(), event, params);
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
            sendEvent(CATEGORY, "Vosstanovit", ElkTextUtils.isEmpty(error) ? "Ok" : error);
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
            GoogleStatistics.sendEvent("Obratnaya_Svyaz", subject, ElkTextUtils.isEmpty(error) ? "Ok" : error);
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
        public void deleteSurveyHearing() {
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
            GoogleStatistics.sendEvent(CATEGORY, CATEGORY, "Otkritie_Oprosa", params);
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
}
