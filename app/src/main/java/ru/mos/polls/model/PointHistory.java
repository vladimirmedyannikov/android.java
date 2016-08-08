package ru.mos.polls.model;

import android.content.Context;

import ru.mos.polls.R;
import ru.mos.polls.helpers.TextHelper;


public class PointHistory {

    private final String title;
    private final long writeOffDate;
    private final int points;
    private final boolean refill;
    private final Action action;

    public PointHistory(String title, long writeOffDate, int points, boolean refill, String action) {
        this.title = title;
        this.writeOffDate = writeOffDate;
        this.points = points;
        this.refill = refill;
        this.action = Action.getAction(action);
    }

    public String getTitle() {
        return title;
    }

    public long getWriteOffDate() {
        return writeOffDate;
    }

    public int getPoints() {
        return points;
    }

    @Deprecated // начиная с версии api 4 не используется
    public boolean isRefill() {
        return refill;
    }

    public Action getAction() {
        return action;
    }

    /**
     * Структура для типа баллов пользователя
     */
    public enum Action {
        DEBIT("debit"),
        CREDIT("credit"),
        BLOCKED("blocked"),
        ALL("all");

        private String action;

        Action(String action) {
            this.action = action;
        }

        public static Action getAction(String action) {
            Action result = ALL;
            if ("debit".equalsIgnoreCase((action))) {
                result = DEBIT;
            } else if ("credit".equalsIgnoreCase(action)) {
                result = CREDIT;
            } else if ("blocked".equalsIgnoreCase(action)) {
                result = BLOCKED;
            }
            return result;
        }

        /**
         * Формирует человеческое русскоязычное описание типа баллов
         *
         * @param context
         * @param action
         * @return
         */
        public static String getDescription(Context context, Action action) {
            int result = R.string.action_all_points;
            if ("debit".equalsIgnoreCase((action.toString()))) {
                result = R.string.action_current_points;
            } else if ("credit".equalsIgnoreCase(action.toString())) {
                result = R.string.action_credit_points;
            } else if ("blocked".equalsIgnoreCase(action.toString())) {
                result = R.string.action_blocked_points;
            }
            return TextHelper.capitalizeFirstLatter(context.getString(result));
        }

        @Override
        public String toString() {
            return action;
        }

        public boolean isAll() {
            return "all".equalsIgnoreCase(action);
        }
    }

}
