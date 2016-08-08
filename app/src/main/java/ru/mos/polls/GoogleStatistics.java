package ru.mos.polls;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import ru.mos.elk.ElkTextUtils;

/**
 * Инкапсулирует методы Google Analytics
 * Возможно, со временем заменит Flurry аналитику {@link ru.mos.polls.Statistics}
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

        protected void sendEvent(Activity activity, String label) {
            GoogleStatistics.sendEvent(activity, getCategory(), getAction(), label);
        }

        /**
         * Проверка аторизации
         * @param activity
         * @param isLogon прошла ли авторизация
         */
        public void check(Activity activity, boolean isLogon) {
            sendEvent(activity, isLogon ? CHECK_YES : CHECK_NO);
        }

        /**
         * клик по оферте
         * @param activity
         */
        public void offerClick(Activity activity) {
            sendEvent(activity, "Uslovie_Oferti");
        }

        /**
         * произошла ошибка
         * @param activity
         * @param error отправляем текст ошибки, если передаем пустую строку или null, то отправится "Ок"
         */
        public void errorOccurs(Activity activity, String error) {
            sendEvent(activity, ElkTextUtils.isEmpty(error) ? "Ok" : error);
        }

        /**
         * клик по меню "как это работает"
         * @param activity
         */
        public void howItWorksClick(Activity activity) {
            GoogleStatistics.sendEvent(activity, getCategory(), "Nuzhna_Pomosh", "Kak_Eto_Rabotaet");
        }

        /**
         * клик по меню "Восстановление пароля"
         * @param activity
         */
        public void recoveryClick(Activity activity) {
            GoogleStatistics.sendEvent(activity, getCategory(), "Nuzhna_Pomosh", "Vosstanovlenie_Parolya");
        }

        /**
         * клик по меню "Обратная связь"
         * @param activity
         */
        public void feedbackClick(Activity activity) {
            GoogleStatistics.sendEvent(activity, getCategory(), "Nuzhna_Pomosh", "Obratnaya_Svyaz");
        }


        /**
         * клик по кнопе "нужна помощь?"
         * @param activity
         */
        public void helpClick(Activity activity) {
            GoogleStatistics.sendEvent(activity, getCategory(), "Nuzhna_Pomosh", "Nuzhna_Pomosh");
        }
    }

    /**
     * События для экрана авторизации
     */
    public static class Auth  extends BaseAuth{
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
         * @param activity
         */
        public void registryClick(Activity activity) {
            GoogleStatistics.sendEvent(activity, getCategory(), "Registracia", "Registracia");
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
         * @param activity
         */
        public void authClick(Activity activity) {
            GoogleStatistics.sendEvent(activity, getCategory(), "Voiti", "Voiti");
        }
    }

    /**
     * События для экрана восстановления пароля
     */
    public static class Recovery {
        private static final String CATEGORY = "Vosstanovlenie_Parolya";

        public void errorOccurs(Activity activity, String error) {
            sendEvent(activity, CATEGORY, "Vosstanovit", ElkTextUtils.isEmpty(error) ? "Ok" : error);
        }

        public void authClick(Activity activity) {
            sendEvent(activity, CATEGORY, "Otmena", "Otmena");
        }

        public void helpClick(Activity activity) {
            sendEvent(activity, CATEGORY, "Nuzhna_Pomosh", "Nuzhna_Pomosh");
        }

        /**
         * клик по меню "Обратная связь"
         * @param activity
         */
        public void feedbackClick(Activity activity) {
            GoogleStatistics.sendEvent(activity, CATEGORY, "Nuzhna_Pomosh", "Obratnaya_Svyaz");
        }

        /**
         * клик по меню "как это работает"
         * @param activity
         */
        public void howItWorksClick(Activity activity) {
            GoogleStatistics.sendEvent(activity, CATEGORY, "Nuzhna_Pomosh", "Kak_Eto_Rabotaet");
        }

        public void check(Activity activity, boolean isLogon) {
            sendEvent(activity, CATEGORY, "Forma_Vosstanovlenia", isLogon ? CHECK_YES : CHECK_NO);
        }
    }

    /**
     * События для экрана обратной связи
     */
    public static class Feedback {
        public void feedbackSanded(Activity activity, String subject) {
            sendEvent(activity, subject, null);
        }

        public void errorOccurs(Activity activity, String subject, String error) {
            sendEvent(activity, subject, error);
        }

        public void sendEvent(Activity activity, String subject, String error) {
            GoogleStatistics.sendEvent(activity, "Obratnaya_Svyaz", subject, ElkTextUtils.isEmpty(error) ? "Ok" : error);
        }
    }

    /**
     * Базовый метод для отправки события
     * @param activity
     * @param category категория
     * @param action действие
     * @param label пометка о результате действия
     */
    public static void sendEvent(Activity activity, String category, String action, String label) {
        Tracker tracker = ((AGApplication) activity.getApplication()).getTracker();
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
        eventBuilder.setAction(action)
                .setCategory(category)
                .setLabel(label)
                .setValue(1);
        tracker.send(eventBuilder.build());
        Log.i("AG_Google_Analytics", "action = " + action + ", category = " + category + ", label = " + label);
    }
}
